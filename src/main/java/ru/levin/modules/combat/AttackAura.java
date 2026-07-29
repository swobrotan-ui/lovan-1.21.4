package ru.levin.modules.combat;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.input.EventMouse;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.events.impl.input.EventKeyBoard;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.events.impl.player.EventSprint;
import ru.levin.manager.Manager;
import ru.levin.mixin.iface.ClientPlayerEntityAccessor;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.movement.ElytraTarget;
import ru.levin.modules.render.littlePet.GhostWolfEntity;
import ru.levin.modules.setting.BindBooleanSetting;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.math.RayTraceUtil;
import ru.levin.util.move.MoveUtil;
import ru.levin.util.player.AuraUtil;
import ru.levin.util.math.PerlinNoise;
import ru.levin.util.player.GCDUtil;
import ru.levin.util.render.Render3DUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

import static net.minecraft.util.Hand.MAIN_HAND;

@SuppressWarnings("All")
@FunctionAnnotation(name = "AttackAura", keywords = {"Пиздить","Хуячить","KillAura"}, desc = "Ебашит бошки всем вокруг", type = Type.Combat)
public class AttackAura extends Function {

    private static final long MIN_HIT_INTERVAL_MS = 40L;

    private final ModeSetting mode = new ModeSetting("Мод",
            "HolyWorld",
            "HolyWorld",
            "AI",
            "SpookyTime",
            "FunTime snap",
            "FunTime",
            "ReallyWorld",
            "1.8.9"
    );

    private final MultiSetting targets = new MultiSetting(
            "Кого атаковать",
            Collections.emptyList(),
            new String[]{"Игроков", "Мобов", "Жителей", "Невидимых", "Голых"}
    );

    private final ModeSetting sort = new ModeSetting("Приоритет по",
            "Дистанции",
            "Дистанции",
            "Здоровью",
            "Броне",
            "Поле зрения",
            "Всему сразу"
    );

    private final MultiSetting setting = new MultiSetting(
            "Настройки",
            Arrays.asList("Только критами", "Ломать щит", "Отжим щита"),
            new String[]{"Только критами", "Ломать щит", "Отжим щита"}
    );

    private final SliderSetting distance = new SliderSetting("Радиус атаки", 3.0f, 1.8f, 6f, 0.1f);
    private final SliderSetting rotateDistance = new SliderSetting("Радиус обнаружения", 5f, 0.0f, 10f, 0.1f);

    private final SliderSetting elytraDistance = new SliderSetting("Радиус на элитрах", 40f, 0f, 80f, 1f);

    private final BindBooleanSetting onlySpaceCritical = new BindBooleanSetting("Только с пробелом", false, () -> setting.get("Только критами"));
    private final ModeSetting critMode = new ModeSetting(() -> setting.get("Только критами"), "Режим критов",
            "Только криты",
            "Только криты",
            "Только криты умные"
    );
    private final BooleanSetting noAttackIfEat = new BooleanSetting("Не бить если ешь", false);
    private final BooleanSetting raycast = new BooleanSetting("Проверять наведение", false);
    private final BooleanSetting wallAttack = new BooleanSetting("Бить через стены", false);

    private final SliderSetting aiReactionMin = new SliderSetting(
            "AI реакция мин.",
            55f,
            0f,
            350f,
            5f,
            () -> mode.is("AI")
    );
    private final SliderSetting aiReactionMax = new SliderSetting(
            "AI реакция макс.",
            120f,
            0f,
            600f,
            5f,
            () -> mode.is("AI")
    );
    private final SliderSetting aiPredict = new SliderSetting(
            "AI предикт",
            2.2f,
            0f,
            6f,
            0.1f,
            () -> mode.is("AI")
    );

    public final BooleanSetting correction = new BooleanSetting("Коррекция", true);
    public final ModeSetting correctionType = new ModeSetting(() -> correction.get(), "Коррекция движения", "Свободная", "Свободная", "Преследование", "Таргетный");
    private final ModeSetting sprintreset = new ModeSetting("Тип спринта", "FunTime", "FunTime", "Legit", "None");

    public LivingEntity target = null;
    private long cpsLimit = 0L;
    private long lastHitMs = 0L;
    private long nextClickTime = 0L;
    private boolean skipNextClick = false;
    private int preSprintTicks = 0;

    private int sprintStartDelayTicks = 0;
    private boolean sprintStartPlanned = false;
    private boolean sprintWasActiveBeforeHit = false;

    private boolean critLocked = false;

    private int aiLastTargetId = -1;
    private long aiReactUntilMs = 0L;
    private boolean aiSideRight = true;
    private long aiLastSideSwitchMs = 0L;

    private boolean spookySideRight = true;
    private long spookyLastSideSwitchMs = 0L;

    private boolean aiLearning = false;
    private final AimStats aiStats = new AimStats();

    private static final File AI_DIR = new File(mc.runDirectory, "files\\ai");
    private final Gson aiGson = new GsonBuilder().setPrettyPrinting().create();
    private String aiProfileName = null;
    private AimProfile aiProfile = null;

    private static final int AI_TRAIN_MAX_HITS = 200;
    private static final double AI_TRAIN_RADIUS = 3.0;
    private static final int AI_TRAIN_TARGETS = 3;
    private final List<Box> aiTrainBoxes = new ArrayList<>();
    private int aiTrainHits = 0;
    private boolean aiAutoEnabled = false;

    private float aiLastPlayerYaw = 0f;
    private float aiLastPlayerPitch = 0f;
    private boolean aiHasLastPlayerRot = false;

    private float funtimeSwingPhase = 0f;
    private int funtimeAimTicks = 0;

    private float funtimeLastPlayerYaw = 0f;
    private float funtimeLastPlayerPitch = 0f;

    private int funtimeLastSwingSign = 0;
    private float funtimePitchDrop = 0f;

    private boolean funtimeRestorePending = false;
    private float funtimeReturnYaw = 0f;
    private float funtimeReturnPitch = 0f;

    private int funtimePrevTargetId = -1;
    private int funtimeSwitchTicks = 0;
    private long funtimeReactionDelayMs = 0L;
    private long funtimeAwarenessDelayMs = 0L;

    private int funtimeConsecutiveHits = 0;
    private long funtimePostMaxCooldownMs = 0L;
    private long funtimeLastHitTimeMs = 0L;
    private static final int FUNTIME_MAX_CONSECUTIVE_HITS = 8;
    private static final long FUNTIME_POST_MAX_COOLDOWN_MS = 20L;
    private static final float FUNTIME_CRIT_BASE_INTERVAL_MS = 90f;
    private static final float FUNTIME_CRIT_MIN_INTERVAL_MS = 35f;
    private static final float FUNTIME_CRIT_INTERVAL_DECAY = 8f;

    private float funtimeAimSpeedYaw = 0f;
    private float funtimeAimSpeedPitch = 0f;
    private float funtimeLastTargetYaw = 0f;
    private float funtimeLastTargetPitch = 0f;

    private float funtimeSmoothYaw = 0.40f;
    private float funtimeSmoothPitch = 0.30f;
    private long funtimeLastSmoothUpdate = 0L;
    private boolean funtimeHasGroundRef = false;
    private double funtimeGroundBodyY = 0.0d;
    private int funtimeBodyPointIndex = 0;
    private int funtimeBodyPointTicks = 0;
    private int funtimeScanTicks = 0;
    private int funtimeSnapTicks = 0;
    private int funtimeAfkTicks = 0;
    private float funtimePostHitDriftYaw = 0f;
    private float funtimePostHitDriftPitch = 0f;
    private float funtimeBreathPhase = 0f;
    private double funtimeLastDist = 0d;

    // Neuro fields for SpookyTime
    private PerlinNoise neuroNoise;
    private long neuroStartTime;
    private float neuroFactor;

    private float spookyAimY = 0.55f;
    private float spookyAimYTarget = 0.55f;
    private long spookyAimNextSwitchMs = 0L;

    private float spookySwingPhase = 0f;
    private int spookyLastSwingSign = 0;
    private float spookyPitchDrop = 0f;

    private float spookySunriseYawStatic = 0f;
    private float spookySunrisePitchStatic = 0f;

    public AttackAura() {
        addSettings(
                mode,
                targets,
                sort,
                setting,
                distance,
                rotateDistance,
                elytraDistance,
                correction,
                correctionType,
                sprintreset,
                onlySpaceCritical,
                critMode,
                noAttackIfEat,
                raycast,
                wallAttack,
                aiReactionMin,
                aiReactionMax,
                aiPredict
        );
    }

    @Override
    public void onEvent(Event event) {
        ClientPlayerEntity player = mc.player;
        if (player == null || player.isDead()) {
            target = null;
            preSprintTicks = 0;
            sprintStartDelayTicks = 0;
            sprintStartPlanned = false;
            sprintWasActiveBeforeHit = false;
            funtimePrevTargetId = -1;
            funtimeSwitchTicks = 0;
            funtimeReactionDelayMs = 0L;
            funtimeAwarenessDelayMs = 0L;
            funtimeConsecutiveHits = 0;
            funtimeAimSpeedYaw = 0f;
            funtimeAimSpeedPitch = 0f;
            funtimeLastTargetYaw = 0f;
            funtimeLastTargetPitch = 0f;
            funtimeSmoothYaw = 0.40f;
            funtimeSmoothPitch = 0.30f;
            funtimeLastSmoothUpdate = 0L;
            funtimeHasGroundRef = false;
            funtimeBodyPointIndex = 0;
            funtimeBodyPointTicks = 0;
            funtimeBreathPhase = 0f;
            funtimeLastDist = 0d;
            return;
        }

        if (event instanceof EventRender3D render3D) {
            renderAITargets(render3D);
        }

        if (event instanceof EventMouse mouse) {
            onMouseAITarget(mouse);
        }

        if (event instanceof EventKeyBoard e) {
            if (correction.get()) {
                float basisYaw = Manager.ROTATION.getYaw();

                if (correctionType.is("Свободная")) {
                    MoveUtil.fixMovement(e, basisYaw);
                } else if (correctionType.is("Преследование")) {
                    if (target != null && isValidTarget(target)) {
                        float desiredYaw = getYawToTarget(target);
                        applyMovementTowardsYaw(e, basisYaw, desiredYaw, true);
                    }
                } else if (correctionType.is("Таргетный")) {
                    boolean pressingW = e.getMovementForward() > 0;
                    if (pressingW && target != null && isValidTarget(target)) {
                        float desiredYaw = getYawToTarget(target);
                        applyMovementTowardsYaw(e, basisYaw, desiredYaw, false);
                    }
                }
            }
        }

        if (event instanceof EventSprint sprint) {
            if (sprintreset.is("Legit")) {
                if (target != null && shouldAttack(target) && player.isSprinting()) {
                    sprint.setSprinting(false);
                }
            }
        }

        if (event instanceof EventUpdate) {
            trackAIMimicLearning();

            if (sprintStartPlanned) {
                if (sprintStartDelayTicks > 0) {
                    sprintStartDelayTicks--;
                }

                if (sprintStartDelayTicks <= 0) {
                    sprintStartPlanned = false;
                    if (sprintWasActiveBeforeHit) {
                        boolean canStartSprint = mc.player.input.movementForward > 0
                                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                                && !mc.player.isGliding()
                                && !mc.player.isUsingItem()
                                && !mc.player.horizontalCollision
                                && mc.player.getHungerManager().getFoodLevel() > 6
                                && !mc.player.isSneaking();

                        if (canStartSprint) {
                            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                            mc.player.setSprinting(true);
                        }
                    }
                    sprintWasActiveBeforeHit = false;
                }
            }

            // reset "one crit hit per jump" lock
            if (setting.get("Только критами")) {
                double velY = mc.player.getVelocity().y;
                if (mc.player.isOnGround() || mc.player.fallDistance <= 0.0f || velY >= 0.0) {
                    critLocked = false;
                }
            } else {
                critLocked = false;
            }

            if (target == null || !isValidTarget(target)) {
                target = findTarget();
            }

            if (target == null) {
                Manager.ROTATION.set(player.getYaw(), player.getPitch());
                cpsLimit = System.currentTimeMillis();
                return;
            }

            handleAttackAndRotation(target);
        }

        if (event instanceof EventMotion motion) {
            motion.setYaw(Manager.ROTATION.getYaw());
            motion.setPitch(Manager.ROTATION.getPitch());
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        neuroNoise = new PerlinNoise();
        neuroStartTime = System.currentTimeMillis();
        neuroFactor = 1.0f;
        funtimePrevTargetId = -1;
        funtimeSwitchTicks = 0;
        funtimeReactionDelayMs = 0L;
        funtimeAwarenessDelayMs = 0L;
        funtimeConsecutiveHits = 0;
        funtimeAimSpeedYaw = 0f;
        funtimeAimSpeedPitch = 0f;
        funtimeLastTargetYaw = 0f;
        funtimeLastTargetPitch = 0f;
        funtimeSmoothYaw = 0.40f;
        funtimeSmoothPitch = 0.30f;
        funtimeLastSmoothUpdate = 0L;
        funtimeBodyPointIndex = 0;
        funtimeBodyPointTicks = 0;
        funtimeBreathPhase = 0f;
        funtimeLastDist = 0d;
    }

    @Override
    protected void onDisable() {
        if (target != null && isValidTarget(target)) {
            if (mode.is("FunTime snap") || mode.is("HolyWorld")) {
                Manager.ROTATION.smoothReturn(350);
            } else {
                Manager.ROTATION.set(mc.player.getYaw(), mc.player.getPitch());
            }
        }

        target = null;
        cpsLimit = System.currentTimeMillis();
        aiLastTargetId = -1;
        aiReactUntilMs = 0L;
        aiSideRight = true;
        aiLastSideSwitchMs = 0L;
        aiHasLastPlayerRot = false;
        sprintStartDelayTicks = 0;
        sprintStartPlanned = false;
        sprintWasActiveBeforeHit = false;
        funtimePrevTargetId = -1;
        funtimeSwitchTicks = 0;
        funtimeReactionDelayMs = 0L;
        funtimeAwarenessDelayMs = 0L;
        funtimeConsecutiveHits = 0;
        funtimeAimSpeedYaw = 0f;
        funtimeAimSpeedPitch = 0f;
        funtimeLastTargetYaw = 0f;
        funtimeLastTargetPitch = 0f;
        funtimeSmoothYaw = 0.40f;
        funtimeSmoothPitch = 0.30f;
        funtimeLastSmoothUpdate = 0L;
        funtimeBodyPointIndex = 0;
        funtimeBodyPointTicks = 0;
        funtimeBreathPhase = 0f;
        funtimeLastDist = 0d;
        super.onDisable();
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (entity == null || entity.isDead() || !entity.isAlive() || entity == mc.player) return false;

        if (!wallAttack.get() && mc.player != null && !mc.player.canSee(entity)) return false;

        double dist = AuraUtil.getDistance(entity);
        double attackRange = distance.get().doubleValue();
        double detectRange = mc.player.isGliding() ? elytraDistance.get().doubleValue() : rotateDistance.get().doubleValue();

        if (dist > attackRange && (detectRange <= 0 || dist > detectRange)) return false;
        if (Manager.FUNCTION_MANAGER.antiBot.check(entity)) return false;

        if (entity instanceof PlayerEntity) {
            if (Manager.FRIEND_MANAGER.isFriend(entity.getName().getString())) return false;
            if (entity.isInvisible() && !targets.get("Невидимых")) return false;

            boolean naked = AuraUtil.getArmor(entity) <= 0;
            if (naked) {
                if (!targets.get("Голых")) return false;
            } else {
                if (!targets.get("Игроков")) return false;
            }
        } else if (entity instanceof VillagerEntity) {
            if (!targets.get("Жителей")) return false;
        } else if (entity instanceof MobEntity || entity instanceof AnimalEntity || entity instanceof Monster) {
            if (!targets.get("Мобов")) return false;
        }

        if (entity instanceof ArmorStandEntity) return false;
        if (Manager.FUNCTION_MANAGER.littleSnickers.state && (entity instanceof GhostWolfEntity)) return false;

        return true;
    }

    private LivingEntity findTarget() {
        List<LivingEntity> list = new ArrayList<>();
        for (Entity e : Manager.SYNC_MANAGER.getEntities()) {
            if (e instanceof LivingEntity le && isValidTarget(le)) list.add(le);
        }
        if (list.isEmpty()) return null;

        switch (sort.get()) {
            case "Здоровью":
                list.sort(Comparator.comparingDouble(LivingEntity::getHealth));
                break;
            case "Дистанции":
                list.sort(Comparator.comparingDouble(mc.player::distanceTo));
                break;
            case "Броне":
                list.sort(Comparator.comparingDouble(AuraUtil::getArmor));
                break;
            case "Поле зрения":
                list.sort(Comparator.comparingDouble(this::getFovTo));
                break;
            case "Всему сразу":
                list.sort(
                        Comparator.comparingDouble((LivingEntity e) -> mc.player.distanceTo(e))
                                .thenComparingDouble((LivingEntity e) -> e.getHealth())
                                .thenComparingDouble(AuraUtil::getArmor)
                                .thenComparingDouble(this::getFovTo)
                );
                break;
            default:
                break;
        }
        return list.get(0);
    }

    private double getFovTo(LivingEntity entity) {
        if (mc.player == null || entity == null) return Double.MAX_VALUE;
        Vec3d eyePos = mc.player.getEyePos();
        Vec3d to = entity.getPos().add(0, entity.getHeight() * 0.5, 0).subtract(eyePos);
        if (to.lengthSquared() < 1.0E-7) return 0.0;

        double yawTo = Math.toDegrees(Math.atan2(to.z, to.x)) - 90.0;
        float yaw = Manager.ROTATION.getYaw();
        return Math.abs(MathHelper.wrapDegrees((float) yawTo - yaw));
    }

    private float getYawToTarget(LivingEntity entity) {
        if (mc.player == null || entity == null) return 0f;
        Vec3d from = mc.player.getPos();
        Vec3d to = entity.getPos();
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
    }

    private Vec2f getBodyRotation(LivingEntity entity) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d pos = entity.getPos().add(0, entity.getHeight() * 0.55, 0);

        double dx = pos.x - eye.x;
        double dy = pos.y - eye.y;
        double dz = pos.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new Vec2f(pitch, yaw);
    }

    private Vec2f getSpookyBodyRotation(LivingEntity entity) {
        if (mc.player == null || entity == null) return new Vec2f(Manager.ROTATION.getPitch(), Manager.ROTATION.getYaw());

        Vec3d eye = mc.player.getEyePos();

        long now = System.currentTimeMillis();
        if (now - spookyLastSideSwitchMs > (200L + random.nextInt(150))) {
            spookyLastSideSwitchMs = now;
            spookySideRight = !spookySideRight;
        }

        Box bb = entity.getBoundingBox();
        double pad = 0.03;

        double toYaw = Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX());
        double rx = -Math.sin(toYaw);
        double rz = Math.cos(toYaw);

        double halfW = Math.max(0.01, entity.getWidth() / 2.0);
        double sideMul = spookySideRight ? 1.0 : -1.0;
        double side = sideMul * (halfW * 0.72);
        double wobble = Math.sin(now / 240.0) * (halfW * 0.10);

        double centerX = (bb.minX + bb.maxX) * 0.5;
        double centerZ = (bb.minZ + bb.maxZ) * 0.5;
        double cx = centerX + rx * (side + wobble);
        double cz = centerZ + rz * (side + wobble);

        double baseY = bb.minY + entity.getHeight() * 0.48;
        double yJit = Math.sin(now / 320.0) * (entity.getHeight() * 0.035);
        double cy = baseY + yJit;

        cx = MathHelper.clamp(cx, bb.minX + pad, bb.maxX - pad);
        cz = MathHelper.clamp(cz, bb.minZ + pad, bb.maxZ - pad);
        cy = MathHelper.clamp(cy, bb.minY + entity.getHeight() * 0.25, bb.maxY - entity.getHeight() * 0.20);

        Vec3d pos = new Vec3d(cx, cy, cz);

        double dx = pos.x - eye.x;
        double dy = pos.y - eye.y;
        double dz = pos.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        pitch = MathHelper.clamp(pitch, -89.9f, 89.9f);
        return new Vec2f(pitch, yaw);
    }

    private boolean isAimedAt(LivingEntity entity, float yaw, float pitch) {
        if (entity == null) return false;
        if (mc.player == null) return false;
        return RayTraceUtil.getMouseOver(entity, yaw, pitch, distance.get().floatValue()) == entity;
    }

    private Vec2f getFuntimeAttackRotation(LivingEntity entity) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d base = entity.getPos();

        double y = base.y + entity.getHeight() * 0.58;

        double halfWidth = entity.getWidth() / 2.0;
        double side = (((System.currentTimeMillis() / 280L) % 2L) == 0L ? 1.0 : -1.0) * (halfWidth * 0.12);

        double yawTo = Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX());
        double offX = Math.cos(yawTo + Math.PI / 2) * side;
        double offZ = Math.sin(yawTo + Math.PI / 2) * side;

        Vec3d pos = new Vec3d(base.x + offX, y, base.z + offZ);

        double dx = pos.x - eye.x;
        double dy = pos.y - eye.y;
        double dz = pos.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new Vec2f(pitch, yaw);
    }

    private Vec2f getFuntimeBodyRotation(LivingEntity entity) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d base = entity.getPos();

        float[] points = new float[]{0.66f, 0.58f, 0.52f};
        int idx = (int) ((System.currentTimeMillis() / 220L) % points.length);
        double y = base.y + entity.getHeight() * points[idx];

        y += Math.sin(System.currentTimeMillis() / 280.0) * (entity.getHeight() * 0.015);

        double halfWidth = entity.getWidth() / 2.0;
        double baseSide = (((System.currentTimeMillis() / 320L) % 2L) == 0L ? 1.0 : -1.0) * (halfWidth * 0.22);
        double sideWobble = Math.sin(System.currentTimeMillis() / 240.0) * (halfWidth * 0.08);
        double side = baseSide + sideWobble;
        double yawTo = Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX());
        double offX = Math.cos(yawTo + Math.PI / 2) * side;
        double offZ = Math.sin(yawTo + Math.PI / 2) * side;

        Vec3d pos = new Vec3d(base.x + offX, y, base.z + offZ);

        double dx = pos.x - eye.x;
        double dy = pos.y - eye.y;
        double dz = pos.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        return new Vec2f(pitch, yaw);
    }

    private void applyMovementTowardsYaw(EventKeyBoard event, float basisYaw, float desiredYaw, boolean allowAutoForward) {
        float forwardIn = event.getMovementForward();
        float strafeIn = event.getMovementStrafe();

        boolean noInput = forwardIn == 0f && strafeIn == 0f;
        if (noInput && !allowAutoForward) return;

        float bestForward = 0f;
        float bestStrafe = 0f;
        float bestDiff = Float.MAX_VALUE;

        for (float pf = -1f; pf <= 1f; pf += 1f) {
            for (float ps = -1f; ps <= 1f; ps += 1f) {
                if (pf == 0f && ps == 0f) continue;

                float predYaw = (float) Math.toDegrees(MoveUtil.direction(basisYaw, pf, ps));
                float diff = Math.abs(MathHelper.wrapDegrees(desiredYaw - predYaw));

                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestForward = pf;
                    bestStrafe = ps;
                } else if (diff == bestDiff) {
                    if (bestForward < pf) {
                        bestForward = pf;
                        bestStrafe = ps;
                    }
                }
            }
        }

        if (noInput && allowAutoForward && bestForward == 0f && bestStrafe == 0f) {
            bestForward = 1f;
        }

        event.setMovementForward(bestForward);
        event.setMovementStrafe(bestStrafe);
    }

    private float randomYawOffset = 0;
    private float randomPitchOffset = 0;
    private int randomUpdateTicks = 0;
    private float bodyYaw, bodyPitch, prevBodyYaw, prevBodyPitch;
    private float headYaw, headPitch, prevHeadYaw, prevHeadPitch;

    private final int updateInterval = 2;
    private final float maxYawShake = 0.3f;
    private final float maxPitchShake = 0.25f;
    private final Random random = new Random();


    private long shakeStartTime = 0L;
    private void handleAttackAndRotation(LivingEntity t) {
        float currYaw = Manager.ROTATION.getYaw();
        float currPitch = Manager.ROTATION.getPitch();

        boolean canAttackNow = shouldAttack(t);
        boolean passRay = !raycast.get() || RayTraceUtil.getMouseOver(t, currYaw, currPitch, distance.get().floatValue()) == t;
        boolean passVisible = wallAttack.get() || (mc.player != null && mc.player.canSee(t));
        boolean noPotion = !Manager.FUNCTION_MANAGER.autoPotion.isActivePotion;

        if (handleElytraRotation(t)) {
            if (canAttackNow && passRay && noPotion) attackTarget(mc.player);
            return;
        }

        if (mode.is("AI")) {
            long now = System.currentTimeMillis();

            int id = t.getId();
            if (id != aiLastTargetId) {
                aiLastTargetId = id;
                aiReactUntilMs = 0L;
                aiSideRight = random.nextBoolean();
                aiLastSideSwitchMs = now;
            }

            if (now - aiLastSideSwitchMs > 520L + (long) (random.nextDouble() * 420L)) {
                aiSideRight = !aiSideRight;
                aiLastSideSwitchMs = now;
            }

            Vec3d aim = getBestAIAimPoint(t);
            Vec3d eye = mc.player.getEyePos();
            double dx = aim.x - eye.x;
            double dy = aim.y - eye.y;
            double dz = aim.z - eye.z;

            float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));
            pitch = MathHelper.clamp(pitch, -89.9f, 89.9f);

            if (aiProfile != null) {
                if (aiProfile.jitterYaw > 0f) {
                    yaw += (float) (random.nextGaussian() * (double) aiProfile.jitterYaw);
                }
                if (aiProfile.jitterPitch > 0f) {
                    pitch += (float) (random.nextGaussian() * (double) aiProfile.jitterPitch);
                }
            }

            float smooth = aiProfile != null ? aiProfile.smoothFactor : 0.55f;
            float yawSpeed = aiProfile != null ? aiProfile.maxYawSpeed : 120f;
            float pitchSpeed = aiProfile != null ? aiProfile.maxPitchSpeed : 35f;
            Manager.ROTATION.setSmooth(yaw, pitch, smooth, yawSpeed, pitchSpeed, true);

            float ay = Manager.ROTATION.getYaw();
            float ap = Manager.ROTATION.getPitch();

            boolean pass;
            if (raycast.get()) {
                pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue()) == t;
            } else {
                float yawDiff = Math.abs(MathHelper.wrapDegrees(yaw - ay));
                float pitchDiff = Math.abs(pitch - ap);
                pass = yawDiff <= 3.2f && pitchDiff <= 3.2f;
            }

            if (!pass) {
                aiReactUntilMs = 0L;
                return;
            }

            boolean wantAttack = canAttackNow && noPotion && pass;
            if (!wantAttack) {
                return;
            }

            attackTarget(mc.player);
            return;
        }

        if (mode.is("SpookyTime")) {
            if (mc.player == null) return;

            if (target == null) {
                spookySunriseYawStatic = mc.player.getYaw();
                spookySunrisePitchStatic = mc.player.getPitch();
                return;
            }

            Vec2f rot = getSpookyBodyRotation(t);
            float yaw = rot.y;
            float pitch = rot.x;

            // Smooth but fast, without hard pulling to the exact center point.
            // Limit step based on current delta so it stays closer to where the view already is.
            float cy = Manager.ROTATION.getYaw();
            float cp = Manager.ROTATION.getPitch();
            float dy = Math.abs(MathHelper.wrapDegrees(yaw - cy));
            float dp = Math.abs(pitch - cp);

            float maxYawStep = MathHelper.clamp(10f + dy * 0.28f, 10f, 48f);
            float maxPitchStep = MathHelper.clamp(6f + dp * 0.26f, 6f, 26f);
            float smooth = 0.86f;

            Manager.ROTATION.setSmooth(yaw, pitch, smooth, maxYawStep, maxPitchStep, true);

            float ay = Manager.ROTATION.getYaw();
            float ap = Manager.ROTATION.getPitch();
            boolean pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue() + 0.35f) == t;

            boolean wantAttack = canAttackNow && noPotion && pass;
            if (wantAttack && canAttack()) {
                attackTarget(mc.player);
            }
            return;
        }

        if (mode.is("FunTime snap")) {
            if (funtimeRestorePending) {
                Manager.ROTATION.set(funtimeReturnYaw, funtimeReturnPitch);
                funtimeRestorePending = false;
            }

            // Пока удар не нужен — не целимся в таргет. Махаем вокруг взгляда игрока.
            if (!(canAttackNow && canAttack() && noPotion)) {
                funtimeAimTicks = 0;
                funtimeSwingPhase += 0.48f;
                float swing = (float) Math.sin(funtimeSwingPhase) * 18.0f;

                int sign = Math.abs(swing) < 0.1f ? funtimeLastSwingSign : (swing >= 0f ? 1 : -1);
                if (sign != 0 && funtimeLastSwingSign != 0 && sign != funtimeLastSwingSign) {
                    funtimePitchDrop = 3.6f;
                }
                funtimeLastSwingSign = sign;
                funtimePitchDrop = Math.max(0f, funtimePitchDrop - 0.75f);

                float baseYaw = mc.player.getYaw();
                float basePitch = mc.player.getPitch();

                float dy = Math.abs(MathHelper.wrapDegrees(baseYaw - funtimeLastPlayerYaw));
                float dp = Math.abs(basePitch - funtimeLastPlayerPitch);
                boolean playerMovingCamera = dy > 0.25f || dp > 0.25f;

                if (playerMovingCamera) {
                    Manager.ROTATION.set(baseYaw + swing, basePitch + funtimePitchDrop);
                } else {
                    Manager.ROTATION.setSmooth(baseYaw + swing, basePitch + funtimePitchDrop, 0.35f, 60f, 60f, true);
                }

                funtimeLastPlayerYaw = baseYaw;
                funtimeLastPlayerPitch = basePitch;
                return;
            }

            // Когда пора бить — тогда и наводимся в тело цели
            if (funtimeAimTicks == 0) {
                funtimeReturnYaw = mc.player.getYaw();
                funtimeReturnPitch = mc.player.getPitch();
            }
            funtimeAimTicks++;

            Vec2f attackRot = getFuntimeAttackRotation(t);
            Manager.ROTATION.setSmooth(attackRot.y, attackRot.x, 0.60f, 60f, 60f, true);

            float ay = Manager.ROTATION.getYaw();
            float ap = Manager.ROTATION.getPitch();
            boolean pass;
            if (raycast.get()) {
                pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue()) == t;
            } else {
                float yawDiff = Math.abs(MathHelper.wrapDegrees(attackRot.y - ay));
                float pitchDiff = Math.abs(attackRot.x - ap);
                pass = yawDiff <= 2.5f && pitchDiff <= 2.5f;
            }

            if (!pass && funtimeAimTicks >= 5) {
                Manager.ROTATION.set(attackRot.y, attackRot.x);
                ay = Manager.ROTATION.getYaw();
                ap = Manager.ROTATION.getPitch();
                if (raycast.get()) {
                    pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue()) == t;
                } else {
                    float yawDiff = Math.abs(MathHelper.wrapDegrees(attackRot.y - ay));
                    float pitchDiff = Math.abs(attackRot.x - ap);
                    pass = yawDiff <= 2.5f && pitchDiff <= 2.5f;
                }
            }

            if (pass) {
                attackTarget(mc.player);
                funtimeRestorePending = true;
                lastHitMs = System.currentTimeMillis();
                funtimeAimTicks = 0;
            }
            return;
        }

        if (mode.is("FunTime")) {
            long now = System.currentTimeMillis();
            int tid = t.getId();

            if (tid != funtimePrevTargetId) {
                funtimePrevTargetId = tid;
                funtimeSwitchTicks = 0;
                int baseDelay = 3 + random.nextInt(12);
                if (random.nextInt(100) < 40) baseDelay = 1 + random.nextInt(5);
                funtimeReactionDelayMs = baseDelay;
                funtimeConsecutiveHits = 0;
                funtimePostMaxCooldownMs = 0L;
                funtimeLastHitTimeMs = 0L;
                funtimeAimSpeedYaw = 0f;
                funtimeAimSpeedPitch = 0f;
                funtimeLastTargetYaw = Manager.ROTATION.getYaw();
                funtimeLastTargetPitch = Manager.ROTATION.getPitch();
                funtimeBodyPointIndex = 0;
                funtimeBodyPointTicks = 0;
                funtimeScanTicks = 0;
                funtimeAfkTicks = 0;
                funtimeBreathPhase = 0f;
                funtimeLastDist = 0d;
            }
            funtimeSwitchTicks++;

            if (funtimeScanTicks > 0) {
                funtimeScanTicks--;
            } else if (random.nextInt(120) == 0) {
                funtimeScanTicks = 4 + random.nextInt(8);
            }

            if (funtimeReactionDelayMs > 0) {
                funtimeReactionDelayMs -= 16L;
                float baseYaw = Manager.ROTATION.getYaw();
                float basePitch = Manager.ROTATION.getPitch();
                float microJitterYaw = (random.nextFloat() - 0.5f) * 6f;
                float microJitterPitch = (random.nextFloat() - 0.5f) * 3f;
                Manager.ROTATION.setSmooth(
                        baseYaw + microJitterYaw,
                        basePitch + microJitterPitch,
                        0.80f+ random.nextFloat() * 0.20f,
                        60f,
                        50f,
                        true
                );
                return;
            }

            Vec3d eye = mc.player.getEyePos();
            float playerEyeY = (float) (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));

            double distToTarget = mc.player.distanceTo(t);
            funtimeLastDist = distToTarget;

            float targetSpeed = (float) Math.sqrt(t.getVelocity().x * t.getVelocity().x + t.getVelocity().z * t.getVelocity().z);
            float smoothFactorTarget = MathHelper.clamp(1.0f - targetSpeed * 0.5f, 0.2f, 1.0f);
            float predictionTicks = 0.15f + targetSpeed * 0.2f;
            Vec3d predictedPos = t.getPos().add(t.getVelocity().x * predictionTicks, t.getVelocity().y * predictionTicks, t.getVelocity().z * predictionTicks);

            float playerSpeed = (float) Math.sqrt(mc.player.getVelocity().x * mc.player.getVelocity().x + mc.player.getVelocity().z * mc.player.getVelocity().z);
            boolean playerAlmostStill = playerSpeed < 0.08f && Math.abs(mc.player.input.movementSideways) < 0.1f;

            if (targetSpeed < 0.05f && playerAlmostStill) {
                funtimeAfkTicks = Math.min(funtimeAfkTicks + 1, 80);
            } else {
                funtimeAfkTicks = Math.max(funtimeAfkTicks - 1, 0);
            }
            boolean afkMode = funtimeAfkTicks > 30;

            funtimeBodyPointTicks--;
            if (funtimeBodyPointTicks <= 0) {
                if (afkMode) {
                    funtimeBodyPointIndex = 1 + random.nextInt(3);
                    funtimeBodyPointTicks = 2 + random.nextInt(4);
                } else {
                    int stay = random.nextInt(100) < 50 ? 0 : (random.nextBoolean() ? 1 : -1);
                    int newIndex = funtimeBodyPointIndex + stay;
                    if (newIndex < 0) newIndex = 0;
                    if (newIndex >= 5) newIndex = 4;
                    if (newIndex == funtimeBodyPointIndex && random.nextInt(100) < 50) {
                        newIndex = random.nextInt(5);
                    }
                    funtimeBodyPointIndex = newIndex;
                    funtimeBodyPointTicks = 1 + random.nextInt(2);
                }
            }
            float[] bodyPoints = afkMode ? new float[]{0.50f, 0.45f, 0.42f} : new float[]{0.55f, 0.48f, 0.42f, 0.36f, 0.30f};
            float baseYFraction = bodyPoints[Math.min(funtimeBodyPointIndex, bodyPoints.length - 1)];
            float heightRandomness = afkMode ? (random.nextFloat() - 0.5f) * 0.04f : (random.nextFloat() - 0.5f) * 0.12f * smoothFactorTarget;
            float yFraction = afkMode ? MathHelper.clamp(baseYFraction + heightRandomness, 0.38f, 0.58f) : MathHelper.clamp(baseYFraction + heightRandomness, 0.28f, 0.62f);
            double aimBaseY = predictedPos.y + t.getHeight() * yFraction;

            double halfWidth = t.getWidth() / 2.0;
            double sideOffset = afkMode ? (0.05f + random.nextFloat() * 0.10f) * halfWidth : (0.15f + random.nextFloat() * 0.25f) * halfWidth * smoothFactorTarget;
            double yawToEntity = Math.atan2(t.getZ() - mc.player.getZ(), t.getX() - mc.player.getX());
            double offX = Math.cos(yawToEntity + Math.PI / 2) * sideOffset;
            double offZ = Math.sin(yawToEntity + Math.PI / 2) * sideOffset;

            double distXZ = Math.sqrt(Math.pow(predictedPos.x + offX - mc.player.getX(), 2) + Math.pow(predictedPos.z + offZ - mc.player.getZ(), 2));
            double yDelta = aimBaseY - playerEyeY;
            float idealYaw = (float) Math.toDegrees(Math.atan2((predictedPos.z + offZ) - mc.player.getZ(), (predictedPos.x + offX) - mc.player.getX())) - 90.0F;
            float idealPitch = (float) (-Math.toDegrees(Math.atan2(yDelta, distXZ)));

            if (funtimeScanTicks > 0 && !afkMode) {
                idealYaw += (random.nextFloat() - 0.5f) * 6f;
                idealPitch += (random.nextFloat() - 0.5f) * 3f;
                idealPitch = MathHelper.clamp(idealPitch, -89.9f, 89.9f);
            }

            funtimeBreathPhase += 0.12f;
            float breathPitch = (float) Math.sin(funtimeBreathPhase) * 0.12f;
            idealPitch += breathPitch;

            float distFactor = (float) MathHelper.clamp(distToTarget / 4.0f, 0.5f, 2.0f);

            float diffYaw = MathHelper.wrapDegrees(idealYaw - funtimeLastTargetYaw);
            float diffPitch = idealPitch - funtimeLastTargetPitch;
            float startupScale = 1.0f + Math.min(Math.abs(diffYaw) * 0.012f, 1.2f) + Math.min(Math.abs(diffPitch) * 0.014f, 1.2f);
            funtimeAimSpeedYaw += diffYaw * 2.20f * startupScale;
            funtimeAimSpeedPitch += diffPitch * 2.20f * startupScale;
            funtimeAimSpeedYaw *= 0.10f;
            funtimeAimSpeedPitch *= 0.10f;

            float currentYaw = funtimeLastTargetYaw + funtimeAimSpeedYaw;
            float currentPitch = funtimeLastTargetPitch + funtimeAimSpeedPitch;
            float snapYaw = idealYaw;
            float snapPitch = idealPitch;
            float yawDelta = MathHelper.wrapDegrees(snapYaw - currentYaw);
            float pitchDelta = snapPitch - currentPitch;
            float startupBoost = Math.max(Math.abs(yawDelta), Math.abs(pitchDelta)) * 0.18f;
            float yawStep = MathHelper.clamp(yawDelta * (1.30f + startupBoost), -120f, 120f);
            float pitchStep = MathHelper.clamp(pitchDelta * (1.30f + startupBoost), -55f, 55f);
            currentYaw += yawStep;
            currentPitch += pitchStep;
            currentYaw = MathHelper.wrapDegrees(currentYaw);
            currentPitch = MathHelper.clamp(currentPitch, -89.9f, 89.9f);

            if (random.nextInt(100) < 30) {
                float overshoot = (random.nextFloat() - 0.5f) * (afkMode ? 0.8f : 3.0f);
                currentYaw = MathHelper.wrapDegrees(currentYaw + overshoot);
            }
            if (random.nextInt(100) < 25) {
                float overshoot = (random.nextFloat() - 0.5f) * (afkMode ? 0.4f : 1.8f);
                currentPitch = MathHelper.clamp(currentPitch + overshoot, -89.9f, 89.9f);
            }

            funtimeLastTargetYaw += MathHelper.wrapDegrees(idealYaw - funtimeLastTargetYaw) * 1.20f;
            funtimeLastTargetPitch += (idealPitch - funtimeLastTargetPitch) * 0.90f;

            float microJitterYaw = (float) (Math.sin(System.currentTimeMillis() / 120.0) * (afkMode ? 0.12f : 0.35f) + (random.nextFloat() - 0.5f) * (afkMode ? 0.3f : 0.6f));
            float microJitterPitch = (float) (Math.cos(System.currentTimeMillis() / 150.0) * (afkMode ? 0.06f : 0.18f) + (random.nextFloat() - 0.5f) * (afkMode ? 0.15f : 0.35f));
            currentYaw += microJitterYaw;
            currentPitch += microJitterPitch;

            float smoothFactor = afkMode ? 0.55f + (random.nextFloat() - 0.5f) * 0.10f : 0.60f + (random.nextFloat() - 0.5f) * 0.15f;
            smoothFactor = MathHelper.clamp(smoothFactor, afkMode ? 0.45f : 0.50f, afkMode ? 0.75f : 0.80f);
            Manager.ROTATION.setSmooth(currentYaw, currentPitch, smoothFactor, afkMode ? 100f : 140f, afkMode ? 40f : 60f, true);

            float ay = Manager.ROTATION.getYaw();
            float ap = Manager.ROTATION.getPitch();
            boolean pass;
            if (raycast.get()) {
                pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue()) == t;
                if (!pass) {
                    double dx = t.getX() - mc.player.getX();
                    double dy = (t.getY() + t.getHeight() * 0.5) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
                    double dz = t.getZ() - mc.player.getZ();
                    double dist = Math.sqrt(dx * dx + dz * dz);
                    float fallbackYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
                    float fallbackPitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
                    float yawDiff = Math.abs(MathHelper.wrapDegrees(fallbackYaw - ay));
                    float pitchDiff = Math.abs(fallbackPitch - ap);
                    pass = yawDiff <= 4.0f && pitchDiff <= 4.0f;
                }
            } else {
                float tolerance = 3.0f + random.nextFloat() * 2.0f;
                if (random.nextInt(100) < 20) tolerance += 1.0f;
                float yawDiff = Math.abs(MathHelper.wrapDegrees(idealYaw - ay));
                float pitchDiff = Math.abs(idealPitch - ap);
                pass = yawDiff <= tolerance && pitchDiff <= tolerance;
            }

            boolean canHitNow = canAttackNow && noPotion;
            boolean postMaxCooldown = now < funtimePostMaxCooldownMs;
            float dynamicInterval = Math.max(FUNTIME_CRIT_BASE_INTERVAL_MS - funtimeConsecutiveHits * FUNTIME_CRIT_INTERVAL_DECAY, FUNTIME_CRIT_MIN_INTERVAL_MS);
            boolean intervalOk = (now - funtimeLastHitTimeMs) >= (long) dynamicInterval;
            boolean wantAttack = canHitNow && pass && canAttack() && !postMaxCooldown && intervalOk;

            if (wantAttack) {
                attackTarget(mc.player);
                funtimeConsecutiveHits++;
                funtimeLastHitTimeMs = now;
                if (funtimeConsecutiveHits >= FUNTIME_MAX_CONSECUTIVE_HITS) {
                    funtimePostMaxCooldownMs = now + FUNTIME_POST_MAX_COOLDOWN_MS;
                    funtimeConsecutiveHits = 0;
                }
            } else if (!wantAttack && funtimeConsecutiveHits > 0 && postMaxCooldown) {
                funtimeConsecutiveHits = 0;
            }
            return;
        }

        if (mode.is("ReallyWorld")) {
            float baseYaw = mc.player.getYaw();
            float basePitch = mc.player.getPitch();
            
            float[] points = new float[]{0.58f, 0.48f, 0.38f};
            int idx = (int) ((System.currentTimeMillis() / 300L) % points.length);
            double y = t.getY() + t.getHeight() * points[idx];
            
            y += Math.sin(System.currentTimeMillis() / 500.0) * 0.02;
            
            double halfWidth = t.getWidth() / 2.0;
            double side = (((System.currentTimeMillis() / 400L) % 2L) == 0L ? 1.0 : -1.0) * (halfWidth * 0.15);
            double yawToEntity = Math.atan2(t.getZ() - mc.player.getZ(), t.getX() - mc.player.getX());
            double offX = Math.cos(yawToEntity + Math.PI / 2) * side;
            double offZ = Math.sin(yawToEntity + Math.PI / 2) * side;
            
            Vec3d pos = new Vec3d(t.getX() + offX, y, t.getZ() + offZ);
            
            double dx = pos.x - mc.player.getX();
            double dy = pos.y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
            double dz = pos.z - mc.player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            
            float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
            pitch = MathHelper.clamp(pitch, -89.9f, 89.9f);
            
            if (random.nextInt(3) == 0) {
                yaw += (random.nextFloat() - 0.5f) * 1.2f;
                pitch += (random.nextFloat() - 0.5f) * 0.8f;
            }
            
            Manager.ROTATION.setSmooth(yaw, pitch, 0.45f, 45f, 12f, true);
            
            float ay = Manager.ROTATION.getYaw();
            float ap = Manager.ROTATION.getPitch();
            boolean pass;
            if (raycast.get()) {
                pass = RayTraceUtil.getMouseOver(t, ay, ap, distance.get().floatValue()) == t;
            } else {
                float yawDiff = Math.abs(MathHelper.wrapDegrees(yaw - ay));
                float pitchDiff = Math.abs(pitch - ap);
                pass = yawDiff <= 3.0f && pitchDiff <= 3.0f;
            }
            
            if (pass && canAttack()) {
                attackTarget(mc.player);
            }
            return;
        }

        if (System.currentTimeMillis() - lastHitMs < 450) {
            funtime(t);

        }

        setRotation(t, true);
        float ay = Manager.ROTATION.getYaw();
        float ap = Manager.ROTATION.getPitch();
        boolean aimed = !raycast.get() || isAimedAt(t, ay, ap);
        if (canAttackNow && aimed && passVisible && noPotion) {
            attackTarget(mc.player);
        }
    }
    private void setRotation(LivingEntity entity, boolean applyGcd) {
        Vec3d tp = predictPos(entity);
        double dx = tp.x - mc.player.getX();
        double dy = (tp.y + entity.getEyeHeight(entity.getPose()) / 2.0) - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double dz = tp.z - mc.player.getZ();

        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.hypot(dx, dz)));
        Manager.ROTATION.setSmooth(yaw, pitch, 1.2f, 180f, 15f, applyGcd);
    }

    private void koopinVector(LivingEntity entity, boolean attackContext) {
        Vec3d head = entity.getEyePos().add(0, entity.getHeight(), 0);
        Vec3d chest = entity.getEyePos().add(0, entity.getStandingEyeHeight() / 2.0f, 0);
        Vec3d legs = entity.getEyePos().add(0, 0.05, 0);
        Vec3d[] points = new Vec3d[]{head, chest, legs};

        float bestPitchDelta = Float.MAX_VALUE;
        Vec3d best = chest;
        float currPitch = Manager.ROTATION.getPitch();
        float currYaw = Manager.ROTATION.getYaw();

        for (Vec3d p : points) {
            Vec3d eye = mc.player.getEyePos();
            double dx = p.x - eye.x;
            double dy = p.y - eye.y;
            double dz = p.z - eye.z;
            float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
            float pitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));
            float pitchDelta = Math.abs(pitch - currPitch);
            if (pitchDelta < bestPitchDelta) {
                bestPitchDelta = pitchDelta;
                best = p;
            }
        }

        Vec3d eye = mc.player.getEyePos();
        double dx = best.x - eye.x;
        double dy = best.y - eye.y;
        double dz = best.z - eye.z;
        double dst = Math.sqrt(dx * dx + dz * dz);

        float yawTo = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitchTo = (float) (-Math.toDegrees(Math.atan2(dy, dst)));

        float yawDelta = MathHelper.wrapDegrees(yawTo - currYaw);
        float pitchDelta = pitchTo - currPitch;

        float addYaw = Math.min(Math.max(Math.abs(yawDelta), 1), 80);
        if (Math.abs(addYaw) <= 3.0f) addYaw = 3.1f;

        float addPitch = Math.max(attackContext ? Math.abs(pitchDelta) : 1.0f, 2.0f);

        float ny = currYaw + (yawDelta > 0 ? addYaw : -addYaw);
        float np = MathHelper.clamp(currPitch + (pitchDelta > 0 ? addPitch : -addPitch), -90.0f, 90.0f);

        Manager.ROTATION.set(ny, np);
    }

    private boolean swingSideRight = false;
    private float jitterYaw = 0f, jitterYawTarget = 0f, jitterYawSpeed = 0f;
    private float microJitter = 0f;
    private float swayPhase = 0f;
    private float swaySpeed = 0.04f;
    private float swayAmplitude = 2.5f;
    private long lastSwitch = 0L;
    private long lastBreathChange = 0L;


    private void funtime(LivingEntity entity) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d base = entity.getPos();

        float[] points = new float[]{0.82f, 0.67f, 0.43f, 0.27f};
        float mul = points[(int) (System.currentTimeMillis() / 180 % points.length)];
        Vec3d targetPos = new Vec3d(base.x, base.y + entity.getHeight() * mul, base.z);

        double halfWidth = entity.getWidth() / 2.0;
        double sideOffset = swingSideRight ? halfWidth * 1.2f : -halfWidth * 1;

        double yawToEntity = Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX());
        double offsetX = Math.cos(yawToEntity + Math.PI / 2) * sideOffset;
        double offsetZ = Math.sin(yawToEntity + Math.PI / 2) * sideOffset;
        targetPos = targetPos.add(offsetX, 0, offsetZ);

        double dx = targetPos.x - eye.x;
        double dy = targetPos.y - eye.y;
        double dz = targetPos.z - eye.z;
        double dist = Math.sqrt(dx * dx + dz * dz);

        float baseYaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float basePitch = (float) -Math.toDegrees(Math.atan2(dy, dist));

        long now = System.currentTimeMillis();

        if (now - lastSwitch > 200 + random.nextInt(250)) {
            lastSwitch = now;
            swingSideRight = !swingSideRight;

            float distanceFactor = (float) MathHelper.clamp(dist / 6.0f, 0.4f, 1.0f);
            float maxDeviation = 4.0f * distanceFactor;

            jitterYawTarget = (swingSideRight ? maxDeviation : -maxDeviation) + (float) (random.nextGaussian() * 0.6f);
        }

        float diff = jitterYawTarget - jitterYaw;
        jitterYawSpeed += diff * 0.05f;
        jitterYawSpeed *= 0.88f;
        jitterYaw += jitterYawSpeed;
        jitterYaw *= 0.985f;

        if (now - lastBreathChange > 2000 + random.nextInt(1500)) {
            lastBreathChange = now;
            swaySpeed = 0.035f + random.nextFloat() * 0.02f;
            swayAmplitude = 2.0f + random.nextFloat() * 1.2f;
        }

        swayPhase += swaySpeed;
        float sway = (float) Math.sin(swayPhase) * swayAmplitude;
        float totalYawOffset = (float) MathHelper.clamp(jitterYaw + sway, -halfWidth * 8.5f, halfWidth * 8.5f);
        microJitter += (random.nextFloat() - 0.5f) * 0.25f;
        microJitter *= 0.85f;

        float finalYaw = baseYaw + totalYawOffset + microJitter;
        float finalPitch = basePitch + (float) Math.sin(swayPhase * 0.8f) * 0.5f;


        Manager.ROTATION.setSmooth(finalYaw, finalPitch, 1.1f, 180f, 15f, true);
    }



    private void hollyworld(LivingEntity entity, boolean force) {
        Vec3d eye = mc.player.getEyePos();
        Vec3d base = entity.getPos();
        float[] points = new float[]{0.85f, 0.65f, 0.35f, 0.25f};
        float mul = points[(int) (System.nanoTime() % points.length)];
        Vec3d aim = new Vec3d(base.x, base.y + entity.getHeight() * mul, base.z);

        double dx = aim.x - eye.x;
        double dy = aim.y - eye.y;
        double dz = aim.z - eye.z;

        double hd = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, hd));

        if (force) {
            Manager.ROTATION.set(yaw, MathHelper.clamp(pitch, -89.9f, 89.9f));
        } else {
            Manager.ROTATION.setSmooth(yaw, pitch, 0.25f, 45f, 12f, true);
        }
    }

    private boolean handleElytraRotation(LivingEntity t) {
        ElytraTarget ely = Manager.FUNCTION_MANAGER.elytraTarget;
        if (ely.state && mc.player.isGliding()) {
            if (ely.mode.is("Продвинутый")) ely.overtakingElytra(t, false);
            else ely.targetDefault(t, false);
            return true;
        }
        return false;
    }

    public void attackTarget(PlayerEntity player) {
        boolean sprintStop = false;
        boolean canStartSprint = mc.player.input.movementForward > 0
                && !mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                && !mc.player.isGliding()
                && !mc.player.isUsingItem()
                && !mc.player.horizontalCollision
                && mc.player.getHungerManager().getFoodLevel() > 6
                && !mc.player.isSneaking();

        if (setting.get("Отжим щита") && mc.player.isBlocking()) {
            mc.interactionManager.stopUsingItem(mc.player);
        }

        if (sprintreset.is("Legit")) {
            if (((ClientPlayerEntityAccessor) mc.player).getLastSprinting()) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                mc.player.setSprinting(false);
                sprintStop = true;
            }
        }

        if (sprintreset.is("FunTime")) {
            if (((ClientPlayerEntityAccessor) mc.player).getLastSprinting()) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                mc.player.setSprinting(false);
                sprintStop = true;
            }
        }
        if (setting.get("Только критами")) {
            boolean strict = critMode.is("Только криты");
            if (strict) {
                // prevent spamming multiple hits during the same long fall
                critLocked = true;
            } else {
                // Smart: lock only when we actually do a crit (airborne), so one crit per jump/fall.
                if (canCritAttack()) {
                    critLocked = true;
                }
            }
        }

        ElytraTarget elytraTarget = Manager.FUNCTION_MANAGER.elytraTarget;
        if (elytraTarget.mode.is("Продвинутый")) {
            elytraTarget.trueFireWork = true;
            if (elytraTarget.prefer.get()) elytraTarget.nextPhase(target);
        }

        if (setting.get("Ломать щит")) shieldBreaker(false);

        if ((sprintreset.is("Legit") || sprintreset.is("FunTime")) && sprintStop && canStartSprint) {
            sprintWasActiveBeforeHit = true;
            sprintStartPlanned = true;
            sprintStartDelayTicks = sprintreset.is("Legit") ? 2 : 1;
        }

        long now = System.currentTimeMillis();
        if (setting.get("Только критами")) {
            // Strict: keep old small safety interval.
            // Smart: rely on real cooldown (attackCooldownProgress), so don't add extra time gating.
            if (critMode.is("Только криты умные")) {
                cpsLimit = now;
            } else {
                cpsLimit = now + MIN_HIT_INTERVAL_MS;
            }
        } else {
            cpsLimit = mode.is("SpookyTime") ? (now + 82L) : now;
        }

        if (mode.is("FunTime") && !setting.get("Только критами")) {
            cpsLimit = now;
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(MAIN_HAND);
        lastHitMs = now;
    }

    private boolean shouldAttack(LivingEntity e) {
        long now = System.currentTimeMillis();
        if (e == null || cpsLimit > now) return false;
        
        if (mode.is("SpookyTime")) {
            long minInterval = 55L;
            if (now - lastHitMs < minInterval) return false;
        }
        if (mode.is("FunTime")) {
            if (skipNextClick) {
                skipNextClick = false;
                scheduleNextClick();
                return false;
            }
            if (now < nextClickTime) return false;
            scheduleNextClick();
        }
        if (!isWithinLegitReach(e)) return false;
        return canAttack();
    }

    private void scheduleNextClick() {
        double gaussian = Math.abs(random.nextGaussian() * 6 + 45);
        long interval = (long) MathHelper.clamp(gaussian, 35L, 100L);
        nextClickTime = System.currentTimeMillis() + interval;
        if (random.nextInt(100) < 10) {
            skipNextClick = true;
        }
    }

    private boolean isWithinLegitReach(LivingEntity e) {
        Vec3d targetPos = e.getPos();
        Vec3d velocity = e.getVelocity();
        double predictionTicks = 0.15;
        double futureX = targetPos.x + velocity.x * predictionTicks;
        double futureY = targetPos.y + velocity.y * predictionTicks;
        double futureZ = targetPos.z + velocity.z * predictionTicks;
        double dx = futureX - mc.player.getX();
        double dy = futureY - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()));
        double dz = futureZ - mc.player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        double maxReach = 3.0;
        if (setting.get("Только критами")) {
            maxReach = 2.8;
        }
        return distance <= maxReach;
    }

    private float[] getSpookySunriseRots(LivingEntity entity) {
        if (mc.player == null) return new float[]{Manager.ROTATION.getYaw(), Manager.ROTATION.getPitch()};

        double dist = getSpookySunriseDist(entity);
        double playerEyeHeight = mc.player.getEyePos().y - mc.player.getY();

        Vec3d vec = entity.getPos().add(
                0,
                MathHelper.clamp(
                        entity.getStandingEyeHeight() * (dist / (distance.get().doubleValue() + Math.max(0.001, entity.getWidth()))),
                        0.2,
                        playerEyeHeight
                ),
                0
        );

        double diffX = vec.x - mc.player.getX();
        double diffY = vec.y - mc.player.getEyePos().y;
        double diffZ = vec.z - mc.player.getZ();
        double hDist = MathHelper.sqrt((float) (diffX * diffX + diffZ * diffZ));

        float jy = GCDUtil.getSensitivity((float) (Math.sin(System.currentTimeMillis() / 30.0) * 2.0));
        float jp = GCDUtil.getSensitivity((float) (Math.cos(System.currentTimeMillis() / 30.0) * 2.0));

        float yawTo = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0) + jy;
        float pitchTo = (float) (-(Math.toDegrees(Math.atan2(diffY, hDist)))) + jp;

        yawTo = mc.player.getYaw() + GCDUtil.getSensitivity(MathHelper.wrapDegrees(yawTo - mc.player.getYaw()));
        pitchTo = mc.player.getPitch() + GCDUtil.getSensitivity(MathHelper.wrapDegrees(pitchTo - mc.player.getPitch()));
        pitchTo = MathHelper.clamp(pitchTo, -90f, 90f);

        spookySunriseYawStatic = applyGcd(spookySunriseRotate(spookySunriseYawStatic, yawTo, 55f), spookySunriseYawStatic);
        spookySunrisePitchStatic = applyGcd(spookySunriseRotate(spookySunrisePitchStatic, pitchTo, 2.2f), spookySunrisePitchStatic);
        spookySunrisePitchStatic = MathHelper.clamp(spookySunrisePitchStatic, -89.9f, 89.9f);

        return new float[]{spookySunriseYawStatic, spookySunrisePitchStatic};
    }

    private double getSpookySunriseDist(LivingEntity entity) {
        if (mc.player == null) return 0.0;

        Vec3d vec = entity.getPos().add(
                0,
                MathHelper.clamp(entity.getY() - mc.player.getY() + (mc.player.getEyePos().y - mc.player.getY()), 0, entity.getHeight()),
                0
        );
        Vec3d playerMid = mc.player.getPos().add(0, mc.player.getHeight() / 2.0, 0);
        return playerMid.distanceTo(vec);
    }

    private float spookySunriseRotate(float current, float target, float maxStep) {
        float diff = MathHelper.wrapDegrees(target - current);
        float clamped = MathHelper.clamp(diff, -maxStep, maxStep);
        return current + clamped;
    }

    private float applyGcd(float next, float prev) {
        float gcd = GCDUtil.getGCDValue();
        return next - (next - prev) % gcd;
    }

    private boolean canAttack() {
        if (noAttackIfEat.get() && mc.player.isUsingItem() && !mc.player.getActiveItem().isOf(Items.SHIELD)) return false;

        float cd = mc.player.getAttackCooldownProgress(0.5f);
        float minCd;
        if (mode.is("SpookyTime")) {
            minCd = setting.get("Только критами") ? 0.65F : 0.72F;
        } else if (mode.is("FunTime")) {
            minCd = 0.05F;
        } else {
            minCd = 0.70F;
        }
        if ((mode.is("SpookyTime") || mode.is("FunTime")) && System.currentTimeMillis() < cpsLimit) return false;
        if (cd < minCd) return false;

        boolean restrict = mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || mc.player.isInLava()
                || mc.player.inPowderSnow
                || mc.player.isClimbing()
                || mc.player.hasVehicle()
                || mc.player.getAbilities().flying
                || (mc.player.isInFluid() && !mc.options.jumpKey.isPressed());
        if (restrict) return false;

        if (mode.is("1.8.9")) {
            return true; // Allow attacks always for 1.8.9 style
        }

        if (setting.get("Только критами")) {
            boolean strict = critMode.is("Только криты");
            if (strict) {
                if (onlySpaceCritical.get() && !mc.options.jumpKey.isPressed()) return false;
            }

            boolean critOk = canCritAttack();

            // "Только криты": бьём только когда реально возможен крит
            if (strict) {
                if (critLocked) return false;
                return critOk;
            }

            // "Только криты умные" (как TriggerBot SMART):
            // - если можно критануть -> бьём (и не больше 1 раза за прыжок/падение)
            // - если на земле -> разрешаем обычный удар
            // - в воздухе, пока крит невозможен -> НЕ бьём
            if (critOk) {
                if (critLocked) return false;
                return true;
            }
            return mc.player.isOnGround();
        }

        return true;
    }

    private boolean canCritAttack() {
        if (mc.player == null) return false;

        if (mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || mc.player.isInLava()
                || mc.player.inPowderSnow
                || mc.player.isClimbing()
                || mc.player.hasVehicle()
                || mc.player.getAbilities().flying
                || mc.player.isInFluid()) {
            return false;
        }

        double velY = mc.player.getVelocity().y;
        return !mc.player.isOnGround()
                && velY < 0.0
                && mc.player.fallDistance > 0.0f;
    }

    private boolean shieldBreaker(boolean instant) {
        int axeSlot = -1;
        if (axeSlot == -1) return false;
        if (!(target instanceof PlayerEntity)) return false;
        if (!((PlayerEntity) target).isUsingItem() && !instant) return false;
        if (((PlayerEntity) target).getOffHandStack().getItem() != Items.SHIELD
                && ((PlayerEntity) target).getMainHandStack().getItem() != Items.SHIELD) return false;

        if (axeSlot >= 9) {
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, axeSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, axeSlot, mc.player.getInventory().selectedSlot, SlotActionType.SWAP, mc.player);
            mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
        } else {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(axeSlot));
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
        }
        return true;
    }

    private Vec3d predictPos(LivingEntity e) {
        Vec3d p = e.getPos();
        var ts = Manager.FUNCTION_MANAGER.targetStrafe;
        if (ts.state && ts.predictCheck.get()) {
            float pr = ts.predict.get().floatValue();
            if (pr > 0) {
                Vec3d v = e.getVelocity();
                p = p.add(v.x * pr, v.y * pr, v.z * pr);
            }
        }
        return p;
    }

    private Vec3d getBestAIAimPoint(LivingEntity e) {
        Vec3d base = e.getBoundingBox().getCenter();
        float pr = aiPredict.get().floatValue();
        if (pr > 0f) {
            Vec3d v = e.getVelocity();
            base = base.add(v.x * pr, v.y * pr, v.z * pr);
        }

        double bodyY = e.getY() + e.getHeight() * 0.40;
        if (pr > 0f) {
            bodyY += e.getVelocity().y * pr;
        }

        double halfWidth = e.getWidth() / 2.0;
        double sideMul = 0.34;
        double side = halfWidth * sideMul;

        double yawTo = Math.atan2(base.z - mc.player.getZ(), base.x - mc.player.getX());
        double rightX = Math.cos(yawTo + Math.PI / 2);
        double rightZ = Math.sin(yawTo + Math.PI / 2);

        return new Vec3d(base.x + rightX * side, bodyY, base.z + rightZ * side);
    }

    private Vec3d predictPosAI(LivingEntity e) {
        return getBestAIAimPoint(e);
    }

private void trackAIMimicLearning() {
    if (!aiLearning || mc.player == null) return;

    float yaw = mc.player.getYaw();
    float pitch = mc.player.getPitch();

    if (!aiHasLastPlayerRot) {
        aiHasLastPlayerRot = true;
        aiLastPlayerYaw = yaw;
        aiLastPlayerPitch = pitch;
        return;
    }

    float dy = MathHelper.wrapDegrees(yaw - aiLastPlayerYaw);
    float dp = pitch - aiLastPlayerPitch;

    aiLastPlayerYaw = yaw;
    aiLastPlayerPitch = pitch;
    aiStats.accept(dy, dp);
}

public void toggleAILearning() {
    boolean enable = !aiLearning;
    aiLearning = enable;
    aiHasLastPlayerRot = false;

    if (aiLearning) {
        if (!this.state) {
            aiAutoEnabled = true;
            setState(true);
        } else {
            aiAutoEnabled = false;
        }
        aiStats.reset();
        aiTrainHits = 0;
        spawnAITargets();
    } else {
        aiTrainBoxes.clear();
        if (aiAutoEnabled) {
            aiAutoEnabled = false;
            setState(false);
        }
    }
}

    public boolean isAiLearning() {
        return aiLearning;
    }

    public String getAiProfileName() {
        return aiProfileName;
    }

    public boolean saveAiProfile(String name) {
        if (name == null || name.isEmpty()) return false;
        if (!AI_DIR.exists() && !AI_DIR.mkdirs()) return false;

        AimProfile profile = aiStats.toProfile();
        if (profile == null) return false;

        File f = new File(AI_DIR, name + ".json");
        try (FileWriter w = new FileWriter(f)) {
            aiGson.toJson(profile, w);
        } catch (Exception ignored) {
            return false;
        }

        aiProfileName = name;
        aiProfile = profile;
        return true;
    }

    public boolean loadAiProfile(String name) {
        if (name == null || name.isEmpty()) return false;
        File f = new File(AI_DIR, name + ".json");
        if (!f.exists()) return false;

        try (FileReader r = new FileReader(f)) {
            AimProfile p = aiGson.fromJson(r, AimProfile.class);
            if (p == null) return false;
            aiProfileName = name;
            aiProfile = p;
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    public List<String> listAiProfiles() {
        if (!AI_DIR.exists()) return Collections.emptyList();
        File[] files = AI_DIR.listFiles((dir, file) -> file.toLowerCase().endsWith(".json"));
        if (files == null || files.length == 0) return Collections.emptyList();

        List<String> out = new ArrayList<>();
        for (File f : files) {
            String n = f.getName();
            if (n.endsWith(".json")) n = n.substring(0, n.length() - 5);
            out.add(n);
        }
        out.sort(String::compareToIgnoreCase);
        return out;
    }

    public int getAiTrainHits() {
        return aiTrainHits;
    }

    public int getAiTrainMaxHits() {
        return AI_TRAIN_MAX_HITS;
    }

    private void spawnAITargets() {
        aiTrainBoxes.clear();
        if (mc.player == null) return;

        Vec3d base = mc.player.getEyePos();
        Vec3d forward = mc.player.getRotationVec(1.0F);
        Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0));
        if (right.lengthSquared() < 1.0E-4) {
            right = new Vec3d(1, 0, 0);
        } else {
            right = right.normalize();
        }
        Vec3d up = right.crossProduct(forward).normalize();

        for (int i = 0; i < AI_TRAIN_TARGETS; i++) {
            aiTrainBoxes.add(randomTrainBox(base, forward, right, up));
        }
    }

    private Box randomTrainBox(Vec3d eye, Vec3d forward, Vec3d right, Vec3d up) {
        double dist = 2.2 + random.nextDouble() * 0.6;
        double offR = (random.nextDouble() * 2.0 - 1.0) * 1.1;
        double offU = (random.nextDouble() * 2.0 - 1.0) * 0.65;

        Vec3d center = eye.add(forward.multiply(dist))
                .add(right.multiply(offR))
                .add(up.multiply(offU));

        Vec3d playerPos = mc.player.getPos();
        Vec3d delta = center.subtract(playerPos);
        double max = AI_TRAIN_RADIUS;
        if (delta.lengthSquared() > max * max) {
            center = playerPos.add(delta.normalize().multiply(max));
        }

        double size = 0.72;
        return new Box(center.x - size / 2.0, center.y - size / 2.0, center.z - size / 2.0, center.x + size / 2.0, center.y + size / 2.0, center.z + size / 2.0);
    }

private void renderAITargets(EventRender3D e) {
    if (!aiLearning) return;
    if (aiTrainBoxes.isEmpty()) return;

    int color = 0xAA22FF55;
    for (Box b : aiTrainBoxes) {
        Render3DUtil.drawBox(b, color, 2.0f, true, true, false);
    }
}

private void onMouseAITarget(EventMouse mouse) {
    if (!aiLearning) return;
    if (mouse.getButton() != 0) return;
    if (mc.player == null) return;
    if (aiTrainBoxes.isEmpty()) return;

    Vec3d eye = mc.player.getEyePos();
    Vec3d dir = mc.player.getRotationVec(1.0F);
    Vec3d end = eye.add(dir.multiply(AI_TRAIN_RADIUS + 0.6));

    int hitIndex = -1;
    for (int i = 0; i < aiTrainBoxes.size(); i++) {
        Box b = aiTrainBoxes.get(i);
        if (b.raycast(eye, end).isPresent()) {
            hitIndex = i;
            break;
        }
    }
    if (hitIndex == -1) return;

    aiTrainHits++;

    Vec3d base = mc.player.getEyePos();
    Vec3d forward = mc.player.getRotationVec(1.0F);
    Vec3d right = forward.crossProduct(new Vec3d(0, 1, 0));
    if (right.lengthSquared() < 1.0E-4) {
        right = new Vec3d(1, 0, 0);
    } else {
        right = right.normalize();
    }
    Vec3d up = right.crossProduct(forward).normalize();

    aiTrainBoxes.set(hitIndex, randomTrainBox(base, forward, right, up));

    if (aiTrainHits >= AI_TRAIN_MAX_HITS) {
        aiLearning = false;
        aiTrainBoxes.clear();
        if (aiAutoEnabled) {
            aiAutoEnabled = false;
            setState(false);
        }
    }
}

private static class AimStats {
    long n;
    double sumAbsYaw;
    double sumAbsPitch;
    double sumYaw;
    double sumPitch;
    double sumYaw2;
    double sumPitch2;

    void reset() {
        n = 0;
        sumAbsYaw = 0;
        sumAbsPitch = 0;
        sumYaw = 0;
        sumPitch = 0;
        sumYaw2 = 0;
        sumPitch2 = 0;
    }

    void accept(float dy, float dp) {
        n++;
        sumAbsYaw += Math.abs(dy);
        sumAbsPitch += Math.abs(dp);
        sumYaw += dy;
        sumPitch += dp;
        sumYaw2 += (double) dy * (double) dy;
        sumPitch2 += (double) dp * (double) dp;
    }

        AimProfile toProfile() {
            if (n < 30) return null;

            double meanAbsYaw = sumAbsYaw / (double) n;
            double meanAbsPitch = sumAbsPitch / (double) n;

            double meanYaw = sumYaw / (double) n;
            double meanPitch = sumPitch / (double) n;

            double varYaw = Math.max(0.0, (sumYaw2 / (double) n) - meanYaw * meanYaw);
            double varPitch = Math.max(0.0, (sumPitch2 / (double) n) - meanPitch * meanPitch);

            double stdYaw = Math.sqrt(varYaw);
            double stdPitch = Math.sqrt(varPitch);

            AimProfile p = new AimProfile();
            p.smoothFactor = (float) MathHelper.clamp(0.35 + meanAbsYaw / 18.0, 0.30, 0.85);
            p.maxYawSpeed = (float) MathHelper.clamp(60.0 + meanAbsYaw * 45.0, 60.0, 180.0);
            p.maxPitchSpeed = (float) MathHelper.clamp(18.0 + meanAbsPitch * 35.0, 18.0, 90.0);
            p.jitterYaw = (float) MathHelper.clamp(stdYaw * 0.35, 0.0, 1.6);
            p.jitterPitch = (float) MathHelper.clamp(stdPitch * 0.35, 0.0, 1.2);
            return p;
        }
    }

    private static class AimProfile {
        float smoothFactor;
        float maxYawSpeed;
        float maxPitchSpeed;
        float jitterYaw;
        float jitterPitch;
    }
}

