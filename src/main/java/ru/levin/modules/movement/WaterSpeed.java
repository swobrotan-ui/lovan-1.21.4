package ru.levin.modules.movement;

import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.move.MoveUtil;

@FunctionAnnotation(name = "WaterSpeed", desc = "Ускоряет передвижение в воде", type = Type.Move)
public class WaterSpeed extends Function {

    private final SliderSetting horizontal = new SliderSetting("Гор. скорость", 0.8f, 0.3f, 1.8f, 0.05f);
    private final SliderSetting vertical = new SliderSetting("Вер. ускорение", 0.06f, 0.01f, 0.2f, 0.01f);

    public WaterSpeed() {
        addSettings(horizontal, vertical);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventMotion) || mc.player == null) return;

        boolean inWater = mc.player.isTouchingWater() || mc.player.isSubmergedInWater();
        if (!inWater) return;

        double motY = mc.player.getVelocity().y;
        if (mc.options.jumpKey.isPressed()) {
            motY = Math.max(vertical.get().floatValue(), motY);
        } else if (mc.options.sneakKey.isPressed()) {
            motY = -vertical.get().floatValue();
        } else {
            motY *= 0.98;
        }

        if (MoveUtil.isMoving()) {
            float h = Math.min(horizontal.get().floatValue(), 1.4f);
            MoveUtil.setMotion(h);
        }

        mc.player.setVelocity(mc.player.getVelocity().x, motY, mc.player.getVelocity().z);
    }
}
