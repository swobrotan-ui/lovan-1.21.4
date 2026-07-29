package ru.levin.modules.movement;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.move.MoveUtil;

@FunctionAnnotation(name = "Strafe", desc = "Быстрое перемещение", type = Type.Move)
public class Strafe extends Function {
    private final ModeSetting mode = new ModeSetting("Тип", "MetaHvH", "MetaHvH", "Custom", "FunTime");
    private final SliderSetting customSpeed = new SliderSetting("Custom скорость", 0.25f, 0.10f, 1.00f, 0.01f);

    public Strafe() {
        addSettings(mode, customSpeed);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventMotion && mc.player != null) {
            if (!mc.player.isGliding() && (!mc.player.isTouchingWater() || !mc.player.isSwimming())) {
                if (!MoveUtil.isMoving()) return;

                if (mode.is("MetaHvH")) {
                    float motion = 0.19f;
                    StatusEffectInstance speedEffect = mc.player.getStatusEffect(StatusEffects.SPEED);
                    if (speedEffect != null) {
                        int amplifier = speedEffect.getAmplifier();

                        switch (amplifier) {
                            case 0:
                                motion = 0.25f;
                                break;
                            case 1:
                                motion = 0.37f;
                                break;
                            case 2:
                                motion = 0.46f;
                                break;
                            case 3:
                                motion = 0.7f;
                                break;
                            default:
                                motion = 0.75f + (amplifier - 3) * 0.05f;
                                break;
                        }
                    }

                    if (mc.options.jumpKey.isPressed()) {
                        motion += 0.1f;
                    }

                    MoveUtil.setMotion(motion);
                } else if (mode.is("Custom")) {
                    MoveUtil.setMotion(customSpeed.get().floatValue());
                } else if (mode.is("FunTime")) {
                    float motion = customSpeed.get().floatValue();
                    MoveUtil.setMotion(motion);
                    if (mc.player.isOnGround() && mc.player.getVelocity().y <= 0.0) {
                        mc.player.setVelocity(mc.player.getVelocity().x, 0.42, mc.player.getVelocity().z);
                    }
                }
            }
        }
    }
}
