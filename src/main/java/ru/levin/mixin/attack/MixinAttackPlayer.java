package ru.levin.mixin.attack;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.events.Event;
import ru.levin.events.impl.player.EventAttack;
import ru.levin.manager.Manager;
import ru.levin.mixin.iface.MixinEntityAccessor;
import ru.levin.modules.combat.HitBoxSnap;
import ru.levin.util.math.RayTraceUtil;

import java.util.Optional;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinAttackPlayer {
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void attackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        HitBoxSnap hitbox = Manager.FUNCTION_MANAGER.xbox;
        if (hitbox != null && hitbox.state && hitbox.snapAttack.get() && !hitbox.isPerformingSnapAttack() && player != null && target != null && target.getId() != player.getId()) {
            try {
                Box expanded = target.getBoundingBox();
                Box raw = target instanceof MixinEntityAccessor acc ? acc.sodiumextra$getRawBoundingBox() : expanded;

                // If bbox isn't expanded or we can't access it, don't interfere.
                if (raw != null && expanded != null && !raw.equals(expanded)) {
                    Vec3d start = player.getCameraPosVec(1.0F);
                    Vec3d look = player.getRotationVec(1.0F);
                    double reach = 6.0;
                    Vec3d end = start.add(look.multiply(reach));

                    Optional<Vec3d> hitRaw = raw.raycast(start, end);
                    if (hitRaw.isEmpty()) {
                        Optional<Vec3d> hitExpanded = expanded.raycast(start, end);
                        if (hitExpanded.isPresent()) {
                            Vec3d center = raw.getCenter();
                            double yLen = raw.maxY - raw.minY;
                            double y = raw.minY + yLen * 0.85;
                            Vec3d aim = new Vec3d(center.x, y, center.z);

                            Vec3d eye = start;
                            double dx = aim.x - eye.x;
                            double dy = aim.y - eye.y;
                            double dz = aim.z - eye.z;
                            double distXZ = Math.sqrt(dx * dx + dz * dz);

                            float yaw = (float) (MathHelper.atan2(dz, dx) * 57.2957763671875D) - 90.0F;
                            float pitch = (float) -(MathHelper.atan2(dy, distXZ) * 57.2957763671875D);

                            if (!hitbox.throughWalls.get()) {
                                var blockHit = RayTraceUtil.rayCast(reach, yaw, pitch, false);
                                if (blockHit != null && blockHit.getPos() != null) {
                                    // If a block is hit before reaching the aim point, don't snap attack.
                                    double blockDistSq = start.squaredDistanceTo(blockHit.getPos());
                                    double aimDistSq = start.squaredDistanceTo(aim);
                                    if (blockDistSq + 1.0E-4 < aimDistSq) {
                                        return;
                                    }
                                }
                            }

                            // Silent snap: send rotated yaw/pitch only in packets (EventMotion),
                            // then perform attack next tick and release.
                            hitbox.queueSnapAttack(target, yaw, pitch);
                            ci.cancel();
                            return;
                        }
                    }
                }
            } catch (Throwable ignored) {
                // if anything goes wrong, don't block attacks
            }
        }

        if (Manager.FUNCTION_MANAGER.noFriendDamage.state) {
            if (Manager.FRIEND_MANAGER.isFriend(target.getName().getString())) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "attackEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", shift = At.Shift.AFTER, ordinal = 0))
    private void afterSendPacket(PlayerEntity player, Entity target, CallbackInfo ci) {
        Event.call(new EventAttack(player,target));
        RayTraceUtil.markHit(target);
    }
}