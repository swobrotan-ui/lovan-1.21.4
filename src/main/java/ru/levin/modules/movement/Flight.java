package ru.levin.modules.movement;

import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.move.MoveUtil;

@FunctionAnnotation(name = "Flight", desc = "Полёт", type = Type.Move)
public class Flight extends Function {
    private final ModeSetting mode = new ModeSetting("Режим", "Creative", "Creative", "Vanilla");
    private final SliderSetting hSpeed = new SliderSetting("Гориз. скорость", 1.0f, 0.0f, 5.0f, 0.1f);
    private final SliderSetting vSpeed = new SliderSetting("Вер. скорость", 1.0f, 0.0f, 5.0f, 0.1f);

    public Flight() {
        addSettings(mode, hSpeed, vSpeed);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventMotion)) return;
        if (mc.player == null || mc.world == null) return;
        if (mc.player.isSpectator() || mc.player.isCreative() || mc.player.isGliding()) return;

        double y = 0.0;
        if (mc.options.jumpKey.isPressed()) {
            y = vSpeed.get().floatValue();
        } else if (mc.options.sneakKey.isPressed()) {
            y = -vSpeed.get().floatValue();
        }

        if (MoveUtil.isMoving()) {
            MoveUtil.setMotion(hSpeed.get().floatValue());
        } else {
            mc.player.setVelocity(0, mc.player.getVelocity().y, 0);
        }

        if (y != 0.0) {
            mc.player.setVelocity(mc.player.getVelocity().x, y, mc.player.getVelocity().z);
        }

        mc.player.fallDistance = 0;
    }
}
