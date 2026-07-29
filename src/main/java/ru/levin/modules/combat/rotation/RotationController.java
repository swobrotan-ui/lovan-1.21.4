package ru.levin.modules.combat.rotation;

import net.minecraft.util.math.MathHelper;
import org.joml.Vector2f;
import ru.levin.manager.IMinecraft;
import ru.levin.util.math.MathUtil;
import ru.levin.util.player.GCDUtil;

public final class RotationController implements IMinecraft {
    private static final RotationController I = new RotationController();
    public static RotationController get() { return I; }

    private final Vector2f rotate = new Vector2f(0f, 0f);

    private RotationController() {}

    public float getYaw() { return rotate.x; }
    public float getPitch() { return rotate.y; }

    public void set(float yaw, float pitch) {
        rotate.x = yaw;
        rotate.y = pitch;
    }

    public void setSmooth(float targetYaw, float targetPitch, float smooth, float maxYawStep, float maxPitchStep, boolean applyGcd) {

        float dYaw = MathHelper.wrapDegrees(targetYaw - rotate.x);
        float dPitch = targetPitch - rotate.y;

        float stepYaw = Math.min(Math.max(Math.abs(dYaw), 1.0F), maxYawStep);
        float stepPitch = Math.min(Math.max(Math.abs(dPitch), 1.0F), maxPitchStep);

        float ny = rotate.x + (dYaw > 0f ? stepYaw : -stepYaw) * smooth;
        float np = MathHelper.clamp(rotate.y + (dPitch > 0f ? stepPitch : -stepPitch) * smooth, -89.9f, 89.9f);

        if (applyGcd) {
            float gcd = GCDUtil.getGCDValue();
            ny -= (ny - rotate.x) % gcd;
            np -= (np - rotate.y) % gcd;
        }

        rotate.x = ny;
        rotate.y = np;
    }

    public void smoothReturn(long durationMs) {
        set(mc.player.getYaw(), mc.player.getPitch());
    }

    public void onUpdate() {}

    public boolean isControlling() {
        return false;
    }

    public void updateIfFree(float yaw, float pitch) {
        rotate.x = yaw;
        rotate.y = pitch;
    }
}
