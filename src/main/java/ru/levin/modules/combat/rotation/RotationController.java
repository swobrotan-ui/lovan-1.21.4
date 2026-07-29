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
    private final Vector2f lastTarget = new Vector2f(0f, 0f);

    private float yawVelocity = 0f;
    private float pitchVelocity = 0f;

    private RotationController() {}

    public float getYaw() { return rotate.x; }
    public float getPitch() { return rotate.y; }

    public void set(float yaw, float pitch) {
        rotate.x = MathHelper.wrapDegrees(yaw);
        rotate.y = MathHelper.clamp(pitch, -89.9f, 89.9f);
        yawVelocity = 0f;
        pitchVelocity = 0f;
    }

    public void setSmooth(float targetYaw, float targetPitch, float smooth, float maxYawStep, float maxPitchStep, boolean applyGcd) {
        lastTarget.x = targetYaw;
        lastTarget.y = targetPitch;

        float dYaw = MathHelper.wrapDegrees(targetYaw - rotate.x);
        float dPitch = targetPitch - rotate.y;

        float t = MathHelper.clamp(smooth, 0.01f, 1.0f);
        float eased = easeInOutQuad(t);

        float distance = MathHelper.sqrt(dYaw * dYaw + dPitch * dPitch);
        float stepScale = 0.5f + 0.5f * eased;

        float yawStep = Math.copySign(
                Math.min(Math.abs(dYaw) * eased, Math.max(1.0f, maxYawStep * stepScale)),
                dYaw
        );
        float pitchStep = Math.copySign(
                Math.min(Math.abs(dPitch) * eased, Math.max(1.0f, maxPitchStep * stepScale)),
                dPitch
        );

        yawVelocity += yawStep * 0.35f;
        pitchVelocity += pitchStep * 0.35f;
        yawVelocity *= 0.78f;
        pitchVelocity *= 0.78f;

        yawVelocity = MathHelper.clamp(yawVelocity, -maxYawStep, maxYawStep);
        pitchVelocity = MathHelper.clamp(pitchVelocity, -maxPitchStep, maxPitchStep);

        float ny = rotate.x + yawVelocity;
        float np = rotate.y + pitchVelocity;

        if (distance > 0.5f) {
            ny += MathUtil.random(-0.06f, 0.06f);
            np += MathUtil.random(-0.04f, 0.04f);
        }

        np = MathHelper.clamp(np, -89.9f, 89.9f);

        if (applyGcd) {
            float gcd = GCDUtil.getGCDValue();
            float yawDelta = MathHelper.wrapDegrees(ny - rotate.x);
            float pitchDelta = np - rotate.y;

            float roundedYaw = Math.round(yawDelta / gcd) * gcd;
            float roundedPitch = Math.round(pitchDelta / gcd) * gcd;

            ny = rotate.x + roundedYaw;
            np = rotate.y + roundedPitch;
        }

        rotate.x = MathHelper.wrapDegrees(ny);
        rotate.y = MathHelper.clamp(np, -89.9f, 89.9f);
    }

    private float easeInOutQuad(float t) {
        return t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f;
    }

    public void smoothReturn(long durationMs) {
        if (mc.player == null) return;
        setSmooth(mc.player.getYaw(), mc.player.getPitch(), 0.35f, 45f, 12f, true);
    }

    public void onUpdate() {}

    public boolean isControlling() {
        return false;
    }

    public void updateIfFree(float yaw, float pitch) {
        set(yaw, pitch);
    }
}
