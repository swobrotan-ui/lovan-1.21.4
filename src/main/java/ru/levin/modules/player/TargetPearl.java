package ru.levin.modules.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventEntitySpawn;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.player.InventoryUtil;
import ru.levin.util.player.TimerUtil;

@SuppressWarnings("All")
@FunctionAnnotation(name = "TargetPearl", desc = "Авто реакция на чужой эндер-пёрл", type = Type.Player)
public class TargetPearl extends Function {

    private final ModeSetting who = new ModeSetting("Кого", "Всех", "Всех", "Таргет");
    private final SliderSetting delayMs = new SliderSetting("Задержка", 150, 0, 1500, 10);
    private final BooleanSetting inventoryUse = new BooleanSetting("Исп. из инв", true);

    private final TimerUtil delayTimer = new TimerUtil();
    private final TimerUtil pearlDelayTimer = new TimerUtil();

    private BlockPos targetBlock;
    private int lastPearlId = -1;
    private int tick;
    private boolean shouldThrowPearl;
    private int thrownPearls;

    private float rotationYaw;
    private float rotationPitch;

    public TargetPearl() {
        addSettings(who, delayMs, inventoryUse);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventEntitySpawn spawn) {
            Entity ent = spawn.getEntity();
            if (!(ent instanceof EnderPearlEntity pearl)) return;
            if (pearl.getOwner() == mc.player) return;
            if (pearl.getId() == lastPearlId) return;

            if (who.is("Таргет")) {
                Entity auraTarget = ru.levin.manager.Manager.FUNCTION_MANAGER.attackAura.target;
                if (auraTarget == null || auraTarget != pearl.getOwner()) return;
            }

            long delay = delayMs.get().longValue();
            if (!delayTimer.hasTimeElapsed(Math.max(0L, delay), true)) return;

            BlockPos predicted = calcTrajectory(pearl);
            if (predicted == null) return;

            targetBlock = predicted;
            lastPearlId = pearl.getId();
            tick = 1;
            shouldThrowPearl = true;
            thrownPearls = 0;
            pearlDelayTimer.reset();
            return;
        }

        if (event instanceof EventUpdate) {
            if (shouldThrowPearl && tick > 0) {
                // rotate for a few ticks then throw
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotationYaw, rotationPitch, mc.player.isOnGround(), mc.player.horizontalCollision));
                tick++;
            }

            if (shouldThrowPearl && tick >= 3) {
                int pearlsToThrow = 1;
                if (thrownPearls < pearlsToThrow && pearlDelayTimer.hasTimeElapsed(100L, true)) {
                    throwPearl();
                    thrownPearls++;
                }

                if (thrownPearls >= pearlsToThrow) {
                    shouldThrowPearl = false;
                    tick = 0;
                    targetBlock = null;
                }
            }
        }
    }

    private void throwPearl() {
        if (mc.player == null || mc.world == null) return;
        if (targetBlock == null) return;

        if (mc.player.getHealth() < 5.0f) return;

        int pearlSlot = InventoryUtil.getPearls();
        if (pearlSlot == -1 && !inventoryUse.get()) return;

        Vec3d center = targetBlock.toCenterPos();
        rotationPitch = (float) (-Math.toDegrees(calcTrajectoryAngle(targetBlock)));
        rotationYaw = (float) Math.toDegrees(Math.atan2(center.z - mc.player.getZ(), center.x - mc.player.getX())) - 90.0f;

        BlockPos traced = checkTrajectory(rotationYaw, rotationPitch);
        if (traced == null) return;
        if (targetBlock.getSquaredDistance(traced) > 36.0) return;

        rotationYaw = MathHelper.wrapDegrees(rotationYaw);
        rotationPitch = MathHelper.clamp(rotationPitch, -90.0f, 90.0f);

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotationYaw, rotationPitch, mc.player.isOnGround(), mc.player.horizontalCollision));
        InventoryUtil.inventorySwapClick2(Items.ENDER_PEARL, inventoryUse.get(), true);
    }

    private float calcTrajectoryAngle(BlockPos bp) {
        double a = Math.hypot(bp.getX() + 0.5F - mc.player.getX(), bp.getZ() + 0.5F - mc.player.getZ());
        double y = 6.125 * (bp.getY() + 1.0F - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose())));
        y = 0.05F * (0.05F * (a * a) + y);
        y = Math.sqrt(9.378906F - y);
        double d = 3.0625 - y;
        y = Math.atan2(d * d + y, 0.05F * a);
        d = Math.atan2(d, 0.05F * a);
        return (float) Math.min(y, d);
    }

    private BlockPos calcTrajectory(Entity e) {
        Vec3d v = e.getVelocity();
        if (v == null) return null;
        return traceTrajectory(e.getX(), e.getY(), e.getZ(), v.x, v.y, v.z);
    }

    private BlockPos checkTrajectory(float yaw, float pitch) {
        if (Float.isNaN(pitch)) return null;

        float yawRad = yaw * (float) (Math.PI / 180.0);
        float pitchRad = pitch * (float) (Math.PI / 180.0);
        double x = mc.player.getX() - Math.cos(yawRad) * 0.16F;
        double y = mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - 0.1000000014901161;
        double z = mc.player.getZ() - Math.sin(yawRad) * 0.16F;
        double motionX = -Math.sin(yawRad) * Math.cos(pitchRad) * 0.4F;
        double motionY = -Math.sin(pitchRad) * 0.4F;
        double motionZ = Math.cos(yawRad) * Math.cos(pitchRad) * 0.4F;

        float distance = (float) Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        if (distance < 1.0E-6) return null;

        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        motionX *= 1.5;
        motionY *= 1.5;
        motionZ *= 1.5;

        if (!mc.player.isOnGround()) {
            motionY += mc.player.getVelocity().y;
        }

        return traceTrajectory(x, y, z, motionX, motionY, motionZ);
    }

    private BlockPos traceTrajectory(double x, double y, double z, double mx, double my, double mz) {
        for (int i = 0; i < 300; i++) {
            Vec3d lastPos = new Vec3d(x, y, z);
            x += mx;
            y += my;
            z += mz;
            mx *= 0.99;
            my *= 0.99;
            mz *= 0.99;
            my -= 0.03F;
            Vec3d pos = new Vec3d(x, y, z);

            BlockHitResult bhr = mc.world.raycast(new RaycastContext(lastPos, pos, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player));
            if (bhr != null && bhr.getType() == HitResult.Type.BLOCK) {
                return bhr.getBlockPos();
            }

            for (Entity ent : mc.world.getEntities()) {
                if (ent instanceof ArrowEntity) continue;
                if (ent == mc.player) continue;
                if (ent instanceof EnderPearlEntity) continue;
                if (ent.getBoundingBox().intersects(new Box(x - 0.3, y - 0.3, z - 0.3, x + 0.3, y + 0.3, z + 0.2))) {
                    return null;
                }
            }

            if (y <= -65.0) {
                break;
            }
        }

        return null;
    }
}
