package ru.levin.modules.player;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.MathHelper;
import ru.levin.events.Event;
import ru.levin.events.impl.EventPacket;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.move.NetworkUtils;
import ru.levin.util.player.TimerUtil;

import java.util.ArrayList;
import java.util.List;

@FunctionAnnotation(name = "GuiWalk", desc = "Ходьба в GUI", type = Type.Player)
public class GuiWalk extends Function {

    private final ModeSetting mode = new ModeSetting("Режим", "Автоматический", "Без обхода", "Автоматический", "Настраиваемый");
    private final ModeSetting containers = new ModeSetting(() -> mode.is("Автоматический"), "В контейнерах", "Двигаться", "Стоять", "Двигаться", "Шифт");

    private final BooleanSetting cancelClose = new BooleanSetting("Отменять закрытие", true, () -> mode.is("Автоматический"));
    private final BooleanSetting jump = new BooleanSetting("Учитывать прыжок", true, () -> mode.is("Автоматический"));

    private final ModeSetting bypass = new ModeSetting(() -> mode.is("Автоматический"), "Обход", "Замедление", "Без обхода", "Замедление", "При закрытии", "Фейк закрытие");
    private final BooleanSetting ground = new BooleanSetting("Без обхода на земле", false, () -> bypass.is("Без обхода") || mode.is("Автоматический"));
    private final SliderSetting cooldown = new SliderSetting("Задержка (ms)", 100, 50, 500, 50,
            () -> bypass.is("Без обхода") || bypass.is("Фейк") || mode.is("Автоматический"));

    private final List<ClickSlotC2SPacket> queued = new ArrayList<>();
    private final List<ClickSlotC2SPacket> queuedPickup = new ArrayList<>();

    private final TimerUtil staying = new TimerUtil();
    private final TimerUtil grounding = new TimerUtil();
    private boolean stay;
    private boolean sending;
    private int screenId;

    public GuiWalk() {
        addSettings(mode, containers, cancelClose, jump, bypass, ground, cooldown);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventUpdate) {
            if (allowMoveInCurrentScreen() && !isTyping()) {
                long windowHandle = mc.getWindow().getHandle();
                KeyBinding[] movementKeys = new KeyBinding[]{
                        mc.options.forwardKey,
                        mc.options.backKey,
                        mc.options.leftKey,
                        mc.options.rightKey,
                        mc.options.jumpKey
                };

                for (KeyBinding key : movementKeys) {
                    int keyCode = InputUtil.fromTranslationKey(key.getBoundKeyTranslationKey()).getCode();
                    key.setPressed(InputUtil.isKeyPressed(windowHandle, keyCode));
                }

                if (mc.player.getAbilities().flying) {
                    int keyCode = InputUtil.fromTranslationKey(mc.options.sneakKey.getBoundKeyTranslationKey()).getCode();
                    mc.options.sneakKey.setPressed(InputUtil.isKeyPressed(windowHandle, keyCode));
                }
            }

            if (mc.player.currentScreenHandler != null) {
                screenId = mc.player.currentScreenHandler.syncId;
            } else {
                screenId = 0;
            }

            if (bypass.is("При закрытии") && mc.currentScreen == null && !queued.isEmpty()) {
                stay = true;
            }

            if (canSend()) {
                sendQueued();
                stay = false;
            }

            if (!mc.player.isOnGround()) {
                grounding.reset();
            }

            if (mc.player.currentScreenHandler != null
                    && (mc.player.currentScreenHandler.getCursorStack().isEmpty() || mc.player.currentScreenHandler.getCursorStack() == ItemStack.EMPTY)
                    && !queuedPickup.isEmpty()
                    && !sending) {
                stay = true;
                if ((mc.player.isOnGround() && ground.get()) || staying.hasTimeElapsed((long) cooldown.get().doubleValue(), false)) {
                    sendQueuedPickup();
                }
            }
        }

        if (event instanceof ru.levin.events.impl.input.EventKeyBoard e) {
            if (!allowMoveInCurrentScreen()) return;
            if (isTyping()) return;

            if (stay) {
                e.setMovementForward(0.0F);
                e.setMovementStrafe(0.0F);
                if (jump.get()) e.setJump(false);
            }

            if ((jump.get() && e.isJump()) || e.getMovementForward() != 0.0F || e.getMovementStrafe() != 0.0F) {
                staying.reset();
            }

            if (containers.is("Шифт") && isHandledContainer()) {
                e.setSneak(true);
            }
        }

        if (event instanceof EventPacket packetEvent) {
            if (packetEvent.isSendPacket()) {
                if (packetEvent.getPacket() instanceof ClickSlotC2SPacket click) {
                    if (shouldDeferClickSlot(click)) {
                        deferClickSlot(click);
                        packetEvent.setCancel(true);
                        return;
                    }
                }
            }

            if (packetEvent.isReceivePacket()) {
                if (cancelClose.get() && packetEvent.getPacket() instanceof CloseScreenS2CPacket) {
                    packetEvent.setCancel(true);
                }
            }
        }
    }

    private boolean isTyping() {
        return mc.currentScreen instanceof ChatScreen
                || mc.currentScreen instanceof SignEditScreen
                || mc.currentScreen instanceof AnvilScreen
                || (mc.currentScreen instanceof CreativeInventoryScreen);
    }

    private boolean isHandledContainer() {
        return mc.currentScreen instanceof GenericContainerScreen
                || mc.currentScreen instanceof InventoryScreen
                || mc.currentScreen instanceof CreativeInventoryScreen;
    }

    private boolean allowMoveInCurrentScreen() {
        if (mc.currentScreen == null) return false;

        if (isHandledContainer()) {
            if (containers.is("Стоять")) return false;
        }

        return true;
    }

    private boolean shouldDeferClickSlot(ClickSlotC2SPacket packet) {
        if (mc.currentScreen == null) return false;
        if (mode.is("Без обхода")) return false;
        if (sending) return false;

        if (!isHandledContainer()) return false;
        if (containers.is("Стоять")) return false;
        if (bypass.is("Без обхода")) return false;

        if (ground.get() && mc.player.isOnGround()) {
            grounding.reset();
            return false;
        }

        if (packet.getSyncId() != 0) return false;

        if (mc.currentScreen instanceof GenericContainerScreen screen) {
            String title = screen.getTitle().getString().toLowerCase();
            if (title.contains("выбор")) return false;
        }

        return true;
    }

    public void deferClickSlot(ClickSlotC2SPacket packet) {
        if (packet.getActionType() == SlotActionType.PICKUP
                || packet.getActionType() == SlotActionType.PICKUP_ALL
                || packet.getActionType() == SlotActionType.CLONE
                || packet.getActionType() == SlotActionType.QUICK_CRAFT) {
            queuedPickup.add(packet);
            return;
        }

        if (bypass.is("Замедление") || bypass.is("При закрытии")) {
            queued.add(packet);
            stay = true;
            return;
        }

        if (bypass.is("Фейк")) {
            queued.add(packet);
            stay = true;
            return;
        }
    }

    private boolean canSend() {
        if (!isState()) return false;
        if (!stay) return false;

        long cd = (long) MathHelper.clamp(cooldown.get().doubleValue(), 50, 500);
        return (mc.player.isOnGround() && ground.get() && grounding.hasTimeElapsed(500L, false))
                || staying.hasTimeElapsed(cd, false);
    }

    private void sendQueued() {
        if (queued.isEmpty()) return;
        sending = true;
        try {
            NetworkUtils.setSendingSilent(true);
            for (ClickSlotC2SPacket p : queued) {
                mc.player.networkHandler.sendPacket(p);
            }
            queued.clear();

            if (bypass.is("Фейк")) {
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(screenId));
            }
        } finally {
            NetworkUtils.setSendingSilent(false);
            sending = false;
        }
    }

    private void sendQueuedPickup() {
        if (queuedPickup.isEmpty()) return;
        sending = true;
        try {
            NetworkUtils.setSendingSilent(true);
            for (ClickSlotC2SPacket p : queuedPickup) {
                mc.player.networkHandler.sendPacket(p);
            }
            queuedPickup.clear();
        } finally {
            NetworkUtils.setSendingSilent(false);
            sending = false;
        }
    }
}
