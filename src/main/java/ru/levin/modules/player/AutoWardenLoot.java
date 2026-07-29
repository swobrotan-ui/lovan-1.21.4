package ru.levin.modules.player;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.BlockHitResult;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.manager.Manager;
import ru.levin.manager.notificationManager.NotificationType;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.player.TimerUtil;

import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@FunctionAnnotation(name = "AutoWardenLoot", desc = "Авто-лут Warden сундуков по таймеру", type = Type.Player)
public class AutoWardenLoot extends Function {

    private enum State {
        SEARCHING,
        APPROACHING,
        WAITING,
        LOOTING
    }

    private static final String MINUTE_KEYWORDS = "(?:мин(?:ут(?:ы|у|я)?)?|m|min|minutes?)";
    private static final String SECOND_KEYWORDS = "(?:сек(?:унд(?:ы|у|я)?)?|s|sec|seconds?)";
    private static final Pattern TIMER_MMSS = Pattern.compile("(\\d{1,2})\\s*[:.]\\s*(\\d{1,2})");
    private static final Pattern TIMER_MINUTE_SECOND = Pattern.compile("(\\d{1,2})\\s*" + MINUTE_KEYWORDS + "\\s*(\\d{1,2})\\s*" + SECOND_KEYWORDS, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern TIMER_MINUTE_UNIT = Pattern.compile("(\\d{1,2})\\s*" + MINUTE_KEYWORDS, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern TIMER_SECOND_UNIT = Pattern.compile("(\\d{1,3})\\s*" + SECOND_KEYWORDS, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    private static final Pattern TIMER_NUMBER = Pattern.compile("(\\d{1,3})");
    private static final Set<BlockEntityType<?>> LOOT_BLOCK_TYPES = Set.of(
            BlockEntityType.CHEST,
            BlockEntityType.TRAPPED_CHEST,
            BlockEntityType.BARREL,
            BlockEntityType.SHULKER_BOX
    );
    private static final int SEARCH_RADIUS = 2;
    private static final double APPROACH_DISTANCE = 1.5;
    private static final int MAX_INITIAL_TIMER_SECONDS = 60;
    private static final int OPEN_THRESHOLD_SECONDS = 0;

    private final SliderSetting lootAnka = new SliderSetting("Анка для лута", 101, 101, 605, 1);
    private final SliderSetting dropAnka = new SliderSetting("Анка для сброса", 102, 101, 605, 1);

    private State state = State.SEARCHING;
    private boolean homeCommandSent;
    private final TimerUtil lootTick = new TimerUtil();
    private final TimerUtil lootFinish = new TimerUtil();
    private final TimerUtil homeDelay = new TimerUtil();
    private final TimerUtil approachStuck = new TimerUtil();
    private HologramTarget target;
    private Vec3d lastApproachPos;
    private double lastApproachDistance = Double.MAX_VALUE;
    private final Random roamRandom = new Random();

    public AutoWardenLoot() {
        addSettings(lootAnka, dropAnka);
    }

    @Override
    protected void onEnable() {
        state = State.SEARCHING;
        homeCommandSent = false;
        target = null;
        lootTick.reset();
        lootFinish.reset();
        homeDelay.reset();
    }

    @Override
    protected void onDisable() {
        resetTarget();
    }

    @Override
    public void onEvent(final Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.player == null || mc.world == null) return;

        if (!homeCommandSent) {
            sendHomeCommand();
            return;
        }

        if (!homeDelay.hasTimeElapsed(10000)) {
            return;
        }

        switch (state) {
            case SEARCHING -> handleSearching();
            case APPROACHING -> handleApproach();
            case WAITING -> handleWaiting();
            case LOOTING -> handleLooting();
        }
    }

    private void sendHomeCommand() {
        sendLootAnkaCommand();
        mc.player.networkHandler.sendChatMessage("/home");
        notify("AutoWardenLoot - /home отправлен", 2);
        homeCommandSent = true;
        homeDelay.reset();
    }

    private void sendLootAnkaCommand() {
        sendAnkaCommand(lootAnka);
    }

    private void sendDropAnkaCommand() {
        sendAnkaCommand(dropAnka);
    }

    private void sendAnkaCommand(SliderSetting setting) {
        if (mc.player == null || mc.player.networkHandler == null || setting == null) return;
        int number = (int) MathHelper.clamp(setting.get().doubleValue(), 101, 999);
        mc.player.networkHandler.sendChatMessage("/an" + number);
        notify("AutoWardenLoot - /an" + number + " отправлен", 1);
    }

    private void handleSearching() {
        Optional<HologramTarget> optional = findTarget();
        if (optional.isPresent()) {
            target = optional.get();
            state = State.APPROACHING;
            notify("AutoWardenLoot - цель по таймеру " + formatTimer(target.initialTimer), 2);
            resetApproachTracker();
            return;
        }
        stopMovement();
    }

    private void handleApproach() {
        if (!isTargetValid()) {
            resetTarget();
            return;
        }
        Vec3d chestCenter = target.getChestCenter();
        double currentDistance = mc.player.getPos().distanceTo(chestCenter);
        checkApproachStuck(chestCenter, currentDistance);
        if (currentDistance <= APPROACH_DISTANCE) {
            stopMovement();
            state = State.WAITING;
            return;
        }

        moveTowards(chestCenter);
    }

    private void handleWaiting() {
        if (!isTargetValid()) {
            resetTarget();
            return;
        }
        int currentTimer = parseTimer(target.hologram);
        if (currentTimer < 0) {
            resetTarget();
            return;
        }
        if (currentTimer > MAX_INITIAL_TIMER_SECONDS) {
            resetTarget();
            return;
        }
        if (currentTimer <= OPEN_THRESHOLD_SECONDS) {
            openTargetChest();
        }
    }

    private void handleLooting() {
        var handler = mc.player.currentScreenHandler;
        if (!(handler instanceof GenericContainerScreenHandler chest)) {
            if (lootFinish.hasTimeElapsed(1500)) {
                resetTarget();
            }
            return;
        }

        if (!lootTick.hasTimeElapsed(120, true)) return;

        boolean moved = collectChestItems(chest);
        if (moved) {
            lootFinish.reset();
            return;
        }

        if (lootFinish.hasTimeElapsed(500)) {
            sendDropAnkaCommand();
            dropInventoryContents();
            mc.player.closeHandledScreen();
            resetTarget();
        }
    }

    private boolean collectChestItems(GenericContainerScreenHandler handler) {
        boolean moved = false;
        for (Slot slot : handler.slots) {
            if (slot.inventory.equals(mc.player.getInventory())) continue;
            if (!slot.hasStack()) continue;
            mc.interactionManager.clickSlot(handler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, mc.player);
            moved = true;
        }
        return moved;
    }

    private void dropInventoryContents() {
        var handler = mc.player.currentScreenHandler;
        if (mc.player == null || handler == null) return;
        int syncId = handler.syncId;
        for (Slot slot : handler.slots) {
            if (!slot.inventory.equals(mc.player.getInventory())) continue;
            if (!slot.hasStack()) continue;
            if (slot.id < 0 || slot.id > 35) continue;
            mc.interactionManager.clickSlot(syncId, slot.id, 1, SlotActionType.THROW, mc.player);
        }
    }

    private void openTargetChest() {
        if (target == null || mc.player == null || mc.interactionManager == null) return;
        BlockHitResult hit = new BlockHitResult(target.getChestCenter(), Direction.UP, target.chestPos, false);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, hit);
        state = State.LOOTING;
        lootTick.reset();
        lootFinish.reset();
    }

    private void moveTowards(Vec3d targetPos) {
        facePosition(targetPos);

        float deltaYaw = MathHelper.wrapDegrees(getYawTo(targetPos) - mc.player.getYaw());
        mc.player.input.movementForward = Math.abs(deltaYaw) > 45f ? 0f : 1f;
        mc.player.input.movementSideways = 0f;

        if (targetPos.y > mc.player.getY() + 0.5 && mc.player.isOnGround()) {
            mc.player.jump();
        }
    }

    private void facePosition(Vec3d targetPos) {
        double dx = targetPos.x - mc.player.getX();
        double dy = targetPos.y - mc.player.getEyeY();
        double dz = targetPos.z - mc.player.getZ();
        double horiz = Math.sqrt(dx * dx + dz * dz);
        double len = Math.sqrt(horiz * horiz + dy * dy);
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float pitch = (float) -Math.toDegrees(Math.asin(MathHelper.clamp(dy / len, -1.0, 1.0)));
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    private float getYawTo(Vec3d targetPos) {
        double dx = targetPos.x - mc.player.getX();
        double dz = targetPos.z - mc.player.getZ();
        return (float) Math.toDegrees(Math.atan2(-dx, dz));
    }

    private void checkApproachStuck(Vec3d chestCenter, double currentDistance) {
        if (mc.player == null) return;
        Vec3d playerPos = mc.player.getPos();
        boolean movedRecently = lastApproachPos == null
                || playerPos.distanceTo(lastApproachPos) > 0.15
                || Math.abs(currentDistance - lastApproachDistance) > 0.05;

        if (movedRecently) {
            lastApproachPos = playerPos;
            lastApproachDistance = currentDistance;
            approachStuck.reset();
            return;
        }

        if (!approachStuck.hasTimeElapsed(1500)) {
            return;
        }

        lastApproachPos = playerPos;
        lastApproachDistance = currentDistance;
        approachStuck.reset();
        moveTowards(chestCenter);
    }

    private boolean isTargetValid() {
        if (target == null) return false;
        if (!target.hologram.isAlive()) return false;
        if (mc.world == null) return false;
        return isChestPresent(target.chestPos);
    }

    private boolean isChestPresent(BlockPos pos) {
        if (mc.world == null) return false;
        BlockEntity entity = mc.world.getBlockEntity(pos);
        return entity != null && LOOT_BLOCK_TYPES.contains(entity.getType());
    }

    private void resetTarget() {
        target = null;
        state = State.SEARCHING;
        stopMovement();
        resetApproachTracker();
    }

    private void stopMovement() {
        if (mc.player == null) return;
        mc.player.input.movementForward = 0f;
        mc.player.input.movementSideways = 0f;
    }

    private void resetApproachTracker() {
        lastApproachPos = null;
        lastApproachDistance = Double.MAX_VALUE;
        approachStuck.reset();
    }

    private Optional<HologramTarget> findTarget() {
        Vec3d playerPos = mc.player.getPos();
        return StreamSupport.stream(mc.world.getEntities().spliterator(), false)
                .filter(this::isHologramEntity)
                .filter(Entity::isCustomNameVisible)
                .map(this::buildTarget)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(candidate -> candidate.initialTimer <= MAX_INITIAL_TIMER_SECONDS)
                .min(Comparator.<HologramTarget>comparingInt(t -> t.initialTimer)
                        .thenComparingDouble(t -> playerPos.distanceTo(t.getChestCenter())));
    }

    private Optional<HologramTarget> buildTarget(Entity entity) {
        int timer = parseTimer(entity);
        if (timer < 0) return Optional.empty();
        BlockPos chest = findChestNear(entity.getBlockPos());
        if (chest == null) return Optional.empty();
        return Optional.of(new HologramTarget(entity, chest, timer));
    }

    private boolean isHologramEntity(Entity entity) {
        return entity instanceof ArmorStandEntity;
    }

    private BlockPos findChestNear(BlockPos center) {
        if (mc.world == null || center == null) return null;
        for (int x = -SEARCH_RADIUS; x <= SEARCH_RADIUS; x++) {
            for (int y = -2; y <= 1; y++) {
                for (int z = -SEARCH_RADIUS; z <= SEARCH_RADIUS; z++) {
                    BlockPos pos = center.add(x, y, z);
                    BlockEntity entity = mc.world.getBlockEntity(pos);
                    if (entity == null) continue;
                    if (LOOT_BLOCK_TYPES.contains(entity.getType())) {
                        return pos;
                    }
                }
            }
        }
        return null;
    }

    private int parseTimer(Entity entity) {
        Text customName = entity.getCustomName();
        if (customName == null) return -1;
        String text = flattenText(customName);
        if (text.isEmpty()) return -1;
        return parseTimerFromText(text);
    }

    private String flattenText(Text text) {
        if (text == null) return "";
        StringBuilder collector = new StringBuilder();
        collectText(text, collector);
        return collector.toString().trim();
    }

    private void collectText(Text text, StringBuilder collector) {
        if (text == null) return;
        if (collector.length() > 0) {
            collector.append(' ');
        }
        collector.append(text.getString());
        for (Text sibling : text.getSiblings()) {
            collectText(sibling, collector);
        }
    }

    private int parseTimerFromText(String raw) {
        String sanitized = sanitizeTimerText(raw);
        if (sanitized.isEmpty()) return -1;

        Matcher matcher = TIMER_MMSS.matcher(sanitized);
        if (matcher.find()) {
            int minutes = clampInteger(matcher.group(1), 0, 99);
            int seconds = clampInteger(matcher.group(2), 0, 59);
            return minutes * 60 + seconds;
        }

        matcher = TIMER_MINUTE_SECOND.matcher(sanitized);
        if (matcher.find()) {
            int minutes = clampInteger(matcher.group(1), 0, 99);
            int seconds = clampInteger(matcher.group(2), 0, 59);
            return minutes * 60 + seconds;
        }

        matcher = TIMER_MINUTE_UNIT.matcher(sanitized);
        if (matcher.find()) {
            int minutes = clampInteger(matcher.group(1), 0, 99);
            return minutes * 60;
        }

        matcher = TIMER_SECOND_UNIT.matcher(sanitized);
        if (matcher.find()) {
            return clampInteger(matcher.group(1), 0, 599);
        }

        matcher = TIMER_NUMBER.matcher(sanitized);
        if (matcher.find()) {
            return clampInteger(matcher.group(1), 0, 999);
        }

        return -1;
    }

    private String sanitizeTimerText(String raw) {
        if (raw == null) return "";
        String cleaned = Formatting.strip(raw);
        if (cleaned == null) return "";
        cleaned = cleaned.replace('\r', ' ').replace('\n', ' ');
        cleaned = cleaned.replaceAll("[^\\p{L}\\p{N}:\\.]+", " ").trim().toLowerCase(Locale.ROOT);
        return cleaned;
    }

    private int clampInteger(String raw, int min, int max) {
        try {
            return MathHelper.clamp(Integer.parseInt(raw), min, max);
        } catch (NumberFormatException ignored) {
            return min;
        }
    }

    private static String formatTimer(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void notify(String text, int seconds) {
        Manager.NOTIFICATION_MANAGER.add(NotificationType.INFO, "AutoWardenLoot", text, seconds);
    }

    private static class HologramTarget {
        final Entity hologram;
        final BlockPos chestPos;
        final int initialTimer;

        HologramTarget(Entity hologram, BlockPos chestPos, int initialTimer) {
            this.hologram = hologram;
            this.chestPos = chestPos;
            this.initialTimer = initialTimer;
        }

        Vec3d getChestCenter() {
            return chestPos.toCenterPos();
        }
    }
}
