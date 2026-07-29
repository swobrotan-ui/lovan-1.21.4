package ru.levin.modules.combat;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;

@SuppressWarnings("All")
@FunctionAnnotation(name = "HitBox", desc = "Увеличивает хитбокс энтити (snap)", type = Type.Combat)
public class HitBoxSnap extends Function {

    public final BooleanSetting expand = new BooleanSetting("Расширять", true);
    public final SliderSetting size = new SliderSetting("Размер", 0.3, 0.0, 1.5, 0.05);

    public final BooleanSetting snapAttack = new BooleanSetting("Снап-удар", true);
    public final BooleanSetting throughWalls = new BooleanSetting("Бить через стены", false);

    private Entity snapTarget;
    private float snapYaw;
    private float snapPitch;
    private boolean attackQueued;
    private boolean performingSnapAttack;

    public HitBoxSnap() {
        addSettings(expand, size, snapAttack, throughWalls);
    }

    public void queueSnapAttack(Entity target, float yaw, float pitch) {
        if (target == null) return;
        this.snapTarget = target;
        this.snapYaw = yaw;
        this.snapPitch = MathHelper.clamp(pitch, -89.9f, 89.9f);
        this.attackQueued = true;
    }

    /**
     * Must be called right after ClientPlayerEntity#sendMovementPackets().
     * At this moment the server already received our silent rotation.
     */
    public void onPostSendMovementPackets() {
        if (!state) return;
        if (mc == null || mc.player == null || mc.world == null) return;
        if (!attackQueued || snapTarget == null) return;

        try {
            if (snapTarget.isAlive()) {
                performingSnapAttack = true;
                mc.interactionManager.attackEntity(mc.player, snapTarget);
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.player.resetLastAttackedTicks();
            }
        } catch (Throwable ignored) {
            // ignore
        } finally {
            performingSnapAttack = false;
            attackQueued = false;
            snapTarget = null;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (!state) return;
        if (mc == null || mc.player == null || mc.world == null) return;

        if (event instanceof EventMotion motion) {
            if (attackQueued && snapTarget != null) {
                motion.setYaw(snapYaw);
                motion.setPitch(snapPitch);
            }
            return;
        }
    }

    public boolean isSnapping() {
        return attackQueued && snapTarget != null;
    }

    public boolean isPerformingSnapAttack() {
        return performingSnapAttack;
    }

    public Entity getSnapTarget() {
        return snapTarget;
    }

    public void clearSnap() {
        snapTarget = null;
        attackQueued = false;
    }
}
