package ru.levin.modules.render;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import ru.levin.events.Event;
import ru.levin.events.impl.player.EventAttack;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;

import java.awt.*;
import java.util.Map;
import java.util.WeakHashMap;

@FunctionAnnotation(name = "HitColor", desc = "Подсветка противника при ударе", type = Type.Render)
public class HitColor extends Function {
    public static ModeSetting hitColor = new ModeSetting("Hit Color", "Red", "Red", "Blue", "Green", "Yellow", "Purple", "Orange", "Rainbow");
    public static SliderSetting duration = new SliderSetting("Длительность", 400f, 100f, 1500f, 50f);
    public static SliderSetting intensity = new SliderSetting("Интенсивность", 0.8f, 0.1f, 1f, 0.1f);

    private static final Map<LivingEntity, Long> hitTargets = new WeakHashMap<>();

    public HitColor() {
        addSettings(hitColor, duration, intensity);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventAttack attack) {
            if (attack.getTarget() instanceof LivingEntity living) {
                hitTargets.put(living, System.currentTimeMillis());
            }
        }
    }

    public static boolean isHit(LivingEntity entity) {
        Long time = hitTargets.get(entity);
        if (time == null) return false;
        long elapsed = System.currentTimeMillis() - time;
        if (elapsed > duration.get().floatValue()) {
            hitTargets.remove(entity);
            return false;
        }
        return true;
    }

    public static float getHitRed() {
        return switch (hitColor.get()) {
            case "Blue" -> 0.2f;
            case "Green" -> 0.2f;
            case "Yellow" -> 1f;
            case "Purple" -> 0.6f;
            case "Orange" -> 1f;
            case "Rainbow" -> (float) (Math.sin(System.currentTimeMillis() / 150.0) * 0.5 + 0.5);
            default -> 1f;
        };
    }

    public static float getHitGreen() {
        return switch (hitColor.get()) {
            case "Blue" -> 0.5f;
            case "Green" -> 1f;
            case "Yellow" -> 0.9f;
            case "Purple" -> 0.2f;
            case "Orange" -> 0.5f;
            case "Rainbow" -> (float) (Math.sin(System.currentTimeMillis() / 150.0 + 2.0) * 0.5 + 0.5);
            default -> 0.2f;
        };
    }

    public static float getHitBlue() {
        return switch (hitColor.get()) {
            case "Blue" -> 1f;
            case "Green" -> 0.3f;
            case "Yellow" -> 0.2f;
            case "Purple" -> 0.8f;
            case "Orange" -> 0.1f;
            case "Rainbow" -> (float) (Math.sin(System.currentTimeMillis() / 150.0 + 4.0) * 0.5 + 0.5);
            default -> 0.2f;
        };
    }

    public static float getIntensity() {
        return intensity.get().floatValue();
    }
}
