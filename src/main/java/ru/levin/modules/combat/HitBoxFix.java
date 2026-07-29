package ru.levin.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.render.EventRender3D;
import ru.levin.mixin.iface.MixinEntityAccessor;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.math.RayTraceUtil;
import ru.levin.util.render.Render3DUtil;

@SuppressWarnings("All")
@FunctionAnnotation(name = "HitBox", desc = "Увеличивает хитбокс энтити", type = Type.Combat)
public class HitBoxFix extends Function {

    public final SliderSetting size = new SliderSetting("Размер", 0.3, 0.0, 1.0, 0.05);

    private Entity aimEntity;
    private float aimYaw;
    private float aimPitch;
    private int aimTicks;

    public HitBoxFix() {
        addSettings(size);
    }

    @Override
    public void onEvent(Event event) {
        if (!state) return;
        if (mc == null || mc.player == null || mc.world == null) return;

        if (event instanceof EventUpdate) {
            // trigger: actual attack key press + raycast hits expanded bbox but not raw bbox
            if (aimEntity == null && mc.options != null && mc.options.attackKey != null) {
                while (mc.options.attackKey.wasPressed()) {
                    startAimIfNeeded();
                    break;
                }
            }

            if (aimEntity == null) return;
            if (aimTicks <= 0) {
                aimEntity = null;
                return;
            }

            float cy = mc.player.getYaw();
            float cp = mc.player.getPitch();

            float dy = MathHelper.wrapDegrees(aimYaw - cy);
            float dp = aimPitch - cp;

            float smooth = 0.35f;
            float maxYawStep = 18.0f;
            float maxPitchStep = 14.0f;

            float stepYaw = MathHelper.clamp(dy * smooth, -maxYawStep, maxYawStep);
            float stepPitch = MathHelper.clamp(dp * smooth, -maxPitchStep, maxPitchStep);

            mc.player.setYaw(cy + stepYaw);
            mc.player.setPitch(MathHelper.clamp(cp + stepPitch, -89.9f, 89.9f));

            if (Math.abs(dy) < 0.8f && Math.abs(dp) < 0.8f) {
                aimTicks = 0;
                aimEntity = null;
            } else {
                aimTicks--;
            }
            return;
        }

        if (event instanceof EventRender3D) {
            if (aimEntity == null) return;
            if (!(aimEntity instanceof MixinEntityAccessor acc)) return;
            Box raw = acc.sodiumextra$getRawBoundingBox();
            if (raw == null) return;
            Render3DUtil.drawBox(raw, 0xA0FFFFFF, 1.5f, true, false, false);
        }
    }

    private void startAimIfNeeded() {
        try {
            float yaw = mc.player.getYaw();
            float pitch = mc.player.getPitch();

            EntityHitResult ehr = RayTraceUtil.rayCastEntity(6.0, yaw, pitch,
                    (ent) -> ent != null && ent.getId() != mc.player.getId() && !ent.isSpectator() && ent.canHit());
            if (ehr == null) return;

            Entity e = ehr.getEntity();
            if (e == null) return;

            Box expanded = e.getBoundingBox();
            Box raw = e instanceof MixinEntityAccessor acc ? acc.sodiumextra$getRawBoundingBox() : expanded;
            if (expanded == null || raw == null) return;
            if (raw.equals(expanded)) return;

            Vec3d hitPos = ehr.getPos();
            if (hitPos == null) return;
            if (raw.contains(hitPos)) return;

            Vec3d center = raw.getCenter();
            double yLen = raw.maxY - raw.minY;
            Vec3d aim = new Vec3d(center.x, raw.minY + yLen * 0.85, center.z);

            Vec3d start = mc.player.getCameraPosVec(1.0F);
            double dx = aim.x - start.x;
            double dy = aim.y - start.y;
            double dz = aim.z - start.z;
            double distXZ = Math.sqrt(dx * dx + dz * dz);

            aimYaw = (float) (MathHelper.atan2(dz, dx) * 57.2957763671875D) - 90.0F;
            aimPitch = (float) -(MathHelper.atan2(dy, distXZ) * 57.2957763671875D);
            aimPitch = MathHelper.clamp(aimPitch, -89.9f, 89.9f);

            aimEntity = e;
            aimTicks = 8;
        } catch (Throwable ignored) {
            // ignore
        }
    }
}
