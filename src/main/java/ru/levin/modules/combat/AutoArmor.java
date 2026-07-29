package ru.levin.modules.combat;

import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;

@SuppressWarnings("All")
@FunctionAnnotation(name = "AutoArmor", desc = "Авто-надевание брони (упрощённо)", type = Type.Combat)
public class AutoArmor extends Function {

    private final SliderSetting delay = new SliderSetting("Задержка", 250, 50, 1000, 10);
    private long lastAt = 0L;

    public AutoArmor() {
        addSettings(delay);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        if (now - lastAt < delay.get().longValue()) return;
        lastAt = now;

        // Пока что оставлено как безопасный stub, чтобы проект собирался.
        // При необходимости позже реализуем полноценный выбор лучшей брони.
    }
}
