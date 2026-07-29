package ru.levin.modules.combat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;

import java.util.Random;

@FunctionAnnotation(name = "AimAssist", desc = "AimAssist , LeyderClient dev: slader", type = Type.Combat)
public class AimAssist extends Function {

    private final SliderSetting distanceSetting = new SliderSetting("Дистанция", 5.0f, 2.0f, 7.0f, 0.1f);
    private final SliderSetting yawSpeedSetting = new SliderSetting("Yaw Speed", 30.0f, 0.0f, 100.0f, 1.0f);
    private final SliderSetting pitchSpeedSetting = new SliderSetting("Pitch Speed", 15.0f, 0.0f, 100.0f, 1.0f); 
    private final BooleanSetting targetFriendsSetting = new BooleanSetting("Таргетить друзей", false);
    private final BooleanSetting targetInvisibleSetting = new BooleanSetting("Таргетить инвизок", false);

    private final Random random = new Random();
    private long lastAimUpdate = 0L;
    private int lastTargetId = -1;
    private net.minecraft.util.math.Vec3d aimPoint = null;

    private PlayerEntity currentTarget;

    public AimAssist() {
        addSettings(distanceSetting, yawSpeedSetting, pitchSpeedSetting, targetFriendsSetting, targetInvisibleSetting);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            onUpdate();
        }
    }

    // Adapted without EventPacket, so no setting currentTarget on attack, just find closest
    private void onUpdate() {
        if (currentTarget != null) {
            if (IMinecraft.mc.player.distanceTo(currentTarget) <= ((Number) distanceSetting.get()).floatValue()) {
                net.minecraft.util.math.Vec3d aimPoint = getAimPoint(currentTarget);
                float[] targetRotations = calculateRotations(aimPoint);
                IMinecraft.mc.player.setYaw(smoothRotation(IMinecraft.mc.player.getYaw(), targetRotations[0], ((Number) yawSpeedSetting.get()).floatValue() / 200.0f));
                IMinecraft.mc.player.setPitch(smoothRotation(IMinecraft.mc.player.getPitch(), targetRotations[1], ((Number) pitchSpeedSetting.get()).floatValue() / 200.0f)); 
            } else {
                currentTarget = null;
            }
        } else {
            currentTarget = findClosestPlayer();
        }
    }

    private PlayerEntity findClosestPlayer() {
        PlayerEntity closestPlayer = null;
        double closestDistanceSq = Double.MAX_VALUE;
        for (PlayerEntity player : IMinecraft.mc.world.getPlayers()) {
            if (player != IMinecraft.mc.player && (!Manager.FRIEND_MANAGER.isFriend(player.getName().getString()) || targetFriendsSetting.get()) && (!player.isInvisible() || targetInvisibleSetting.get())) {
                double distanceSq = IMinecraft.mc.player.squaredDistanceTo(player);
                if (distanceSq < closestDistanceSq && distanceSq <= ((Number) distanceSetting.get()).floatValue() * ((Number) distanceSetting.get()).floatValue()) {
                    closestPlayer = player;
                    closestDistanceSq = distanceSq;
                }
            }
        }
        return closestPlayer;
    }

    private net.minecraft.util.math.Vec3d getAimPoint(PlayerEntity target) {
        int id = target.getId();
        long now = System.currentTimeMillis();
        if (aimPoint == null || id != lastTargetId || now - lastAimUpdate > 200L) {
            net.minecraft.util.math.Vec3d base = target.getPos();
            double yMul = 0.05 + random.nextDouble() * 0.65;
            double y = base.y + target.getHeight() * yMul;
            double radius = (yMul > 0.6) ? target.getWidth() * 0.15 : target.getWidth() * 0.4;
            double angle = random.nextDouble() * Math.PI * 2.0;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            aimPoint = new net.minecraft.util.math.Vec3d(base.x + offsetX, y, base.z + offsetZ);
            lastAimUpdate = now;
            lastTargetId = id;
        }
        return aimPoint;
    }

    private float[] calculateRotations(net.minecraft.util.math.Vec3d pos) {
        double x = pos.x - IMinecraft.mc.player.getX();
        double y = pos.y - IMinecraft.mc.player.getY() - IMinecraft.mc.player.getEyeHeight(IMinecraft.mc.player.getPose()) + 0.3;
        double z = pos.z - IMinecraft.mc.player.getZ();
        double dist = Math.sqrt(x * x + z * z);
        float yaw = (float) ((Math.atan2(z, x) * 180 / Math.PI) - 90);
        float pitch = (float) (-(Math.atan2(y, dist) * 180 / Math.PI));
        return new float[]{yaw, pitch};
    }

    private float smoothRotation(float current, float target, float factor) {
        float delta = MathHelper.wrapDegrees(target - current);

        float minRotation = 0.01f;

        float rotation = delta * factor; // ну кароч полный кринж ,на фт кстати урон не урезает пользуйтесь

        if (Math.abs(rotation) < minRotation) {
            rotation = Math.copySign(minRotation, rotation);
        }

        return current + rotation;
    }
}
