package ru.levin.modules.movement;

import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.manager.ClientManager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "Timer", type = Type.Move, desc = "Ускорение игры (обход-friendly)")
public class Timer extends Function {

    private final SliderSetting timerAmount = new SliderSetting("Скорость", 1.2f, 1.0f, 5.0f, 0.01f);
    private final BooleanSetting smart = new BooleanSetting("Умный", true, "Пульсирует таймер, держа среднее значение ниже порога античита");
    private final SliderSetting boostTicks = new SliderSetting("Тиков ускорения", 6f, 1f, 20f, 1f, () -> smart.get());
    private final SliderSetting idleTicks = new SliderSetting("Тиков покоя", 10f, 1f, 40f, 1f, () -> smart.get());

    // фактический множитель, который мы выставляем в этом тике
    private int phaseCounter = 0;
    private boolean boosting = true;

    public Timer() {
        addSettings(timerAmount, smart, boostTicks, idleTicks);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;

        float target = timerAmount.get().floatValue();
        // жёстко ограничиваем пик, чтобы не вылететь на FunTime/SpookyTime
        if (target > 1.25f) target = 1.25f;

        if (!smart.get()) {
            ClientManager.TICK_TIMER = target;
            return;
        }

        int boost = (int) boostTicks.get().floatValue();
        int idle = (int) idleTicks.get().floatValue();

        if (boosting) {
            ClientManager.TICK_TIMER = target;
            phaseCounter++;
            if (phaseCounter >= boost) {
                boosting = false;
                phaseCounter = 0;
            }
        } else {
            ClientManager.TICK_TIMER = 1.0F;
            phaseCounter++;
            if (phaseCounter >= idle) {
                boosting = true;
                phaseCounter = 0;
            }
        }
    }

    @Override
    public void onDisable() {
        ClientManager.TICK_TIMER = 1.0F;
        phaseCounter = 0;
        boosting = true;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        phaseCounter = 0;
        boosting = true;
        super.onEnable();
    }
}
