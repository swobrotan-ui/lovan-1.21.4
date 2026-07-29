package ru.levin.modules.player;

import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.player.TimerUtil;

@FunctionAnnotation(name = "AutoJoin", desc = "Авто-вход через меню компаса", type = Type.Player)
public class AutoJoin extends Function {

    private final SliderSetting slot = new SliderSetting("Слот", 23.0, 0.0, 53.0, 1.0);
    private final SliderSetting useDelayMs = new SliderSetting("Задержка ПКМ (ms)", 0.0, 0.0, 2000.0, 1.0);
    private final SliderSetting clickDelayMs = new SliderSetting("Задержка клика (ms)", 150.0, 0.0, 2000.0, 1.0);
    private final BooleanSetting autoDisable = new BooleanSetting("Выключаться после", true);

    private final TimerUtil stageTimer = new TimerUtil();

    private int stage;

    public AutoJoin() {
        addSettings(slot, useDelayMs, clickDelayMs, autoDisable);
    }

    @Override
    protected void onEnable() {
        stage = 0;
        stageTimer.reset();
    }

    @Override
    protected void onDisable() {
        stage = 0;
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        if (stage == 0) {
            Hand hand = getCompassHand();
            if (hand == null) return;

            long useDelay = useDelayMs.get().longValue();
            if (useDelay <= 0L || stageTimer.hasTimeElapsed(useDelay, true)) {
                mc.interactionManager.interactItem(mc.player, hand);
                stage = 1;
                stageTimer.reset();
            }
            return;
        }

        if (stage == 1) {
            ScreenHandler handler = mc.player.currentScreenHandler;
            if (handler == null || handler.syncId == 0) {
                if (stageTimer.hasTimeElapsed(500L, true)) {
                    Hand hand = getCompassHand();
                    if (hand != null) {
                        mc.interactionManager.interactItem(mc.player, hand);
                    }
                }
                return;
            }

            long clickDelay = clickDelayMs.get().longValue();
            if (clickDelay > 0L && !stageTimer.hasTimeElapsed(clickDelay, false)) return;

            int slotId = (int) Math.round(slot.get().doubleValue());
            if (slotId < 0 || slotId >= handler.slots.size()) return;

            mc.interactionManager.clickSlot(handler.syncId, slotId, 0, SlotActionType.PICKUP, mc.player);
            stage = 2;

            if (autoDisable.get()) {
                setState(false);
            }
        }
    }

    private Hand getCompassHand() {
        if (mc.player == null) return null;
        if (mc.player.getMainHandStack() != null && mc.player.getMainHandStack().isOf(Items.COMPASS)) return Hand.MAIN_HAND;
        if (mc.player.getOffHandStack() != null && mc.player.getOffHandStack().isOf(Items.COMPASS)) return Hand.OFF_HAND;
        return null;
    }
}
