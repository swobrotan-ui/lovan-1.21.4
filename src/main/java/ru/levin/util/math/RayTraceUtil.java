package ru.levin.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import ru.levin.manager.IMinecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class RayTraceUtil implements IMinecraft {

    private static final Map<UUID, Long> lastHitTimes = new HashMap<>();
    private static final long EFFECT_DURATION = 200;

    public static void markHit(Entity entity) {
        lastHitTimes.put(entity.getUuid(), System.currentTimeMillis());
    }
    public static float getHitProgress(Entity entity) {
        Long hitTime = lastHitTimes.get(entity.getUuid());
        if (hitTime == null) return 0f;

        long elapsed = System.currentTimeMillis() - hitTime;
        if (elapsed > EFFECT_DURATION) {
            lastHitTimes.remove(entity.getUuid());
            return 0f;
        }

        return 1f - ((float) elapsed / EFFECT_DURATION);
    }
    public static Entity getMouseOver(Entity target, float yaw, float pitch, double distance) {
        if (target == null) return null;
        EntityHitResult hit = rayCastEntity(distance, yaw, pitch,
                (e) -> e == target && !e.isSpectator() && e.canBeHitByProjectile());
        return hit != null ? hit.getEntity() : null;
    }

    public static EntityHitResult rayCastEntity(double range, float yaw, float pitch, Predicate<Entity> filter) {
        Entity entity = mc.getCameraEntity();
        if (entity == null || mc.world == null) {
            return null;
        }
        Vec3d cameraVec = entity.getCameraPosVec(1.0F);
        float pitchRad = pitch * 0.017453292F;
        float yawRad = -yaw * 0.017453292F;
        float cosPitch = (float) Math.cos(pitchRad);
        float sinPitch = (float) Math.sin(pitchRad);
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);
        Vec3d rotationVec = new Vec3d(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
        Vec3d end = cameraVec.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        Box box = entity.getBoundingBox().stretch(rotationVec.multiply(range)).expand(1.0, 1.0, 1.0);

        return ProjectileUtil.raycast(entity, cameraVec, end, box, filter, range * range);
    }
    public static BlockHitResult rayCast(double range, float yaw, float pitch, boolean includeFluids) {
        Entity entity = mc.getCameraEntity();
        if (entity == null || mc.world == null) {
            return null;
        }
        Vec3d start = entity.getCameraPosVec(1.0F);
        float pitchRad = pitch * 0.017453292F;
        float yawRad = -yaw * 0.017453292F;
        float cosPitch = (float) Math.cos(pitchRad);
        float sinPitch = (float) Math.sin(pitchRad);
        float cosYaw = (float) Math.cos(yawRad);
        float sinYaw = (float) Math.sin(yawRad);
        Vec3d rotationVec = new Vec3d(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
        Vec3d end = start.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range);
        World world = mc.world;
        RaycastContext.FluidHandling fluidHandling = includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE;
        RaycastContext context = new RaycastContext(start, end, RaycastContext.ShapeType.OUTLINE, fluidHandling, entity);

        return world.raycast(context);
    }
}