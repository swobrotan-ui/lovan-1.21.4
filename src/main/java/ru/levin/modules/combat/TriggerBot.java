package ru.levin.modules.combat;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import ru.levin.manager.Manager;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;

@SuppressWarnings("All")
@FunctionAnnotation(name = "TriggerBot", desc = "Авто-удар по цели в прицеле", type = Type.Combat)
public class TriggerBot extends Function {

    private static final String MODE_CRITS = "Криты";
    private static final String MODE_SMART = "Умный";
    private static final String PVP_18 = "1.8";
    private static final String PVP_19 = "1.9+";

    private static final String TARGETS_ALL = "Все";
    private static final String TARGETS_PLAYERS = "Только игроки";
    private static final String TARGETS_MOBS = "Только мобы";

    private final ModeSetting targets = new ModeSetting("Цели", TARGETS_ALL, TARGETS_ALL, TARGETS_PLAYERS, TARGETS_MOBS);
    private final ModeSetting attackMode = new ModeSetting("Режим атаки", MODE_SMART, MODE_SMART, MODE_CRITS);
    private final ModeSetting pvpMode = new ModeSetting("Режим PvP", PVP_19, PVP_18, PVP_19);

    private final BooleanSetting sprintControl = new BooleanSetting("Управление спринтом", true);
    private final BooleanSetting tpsSync = new BooleanSetting("ТПС Синк", true);
    private final BooleanSetting customDelay = new BooleanSetting("Кастомная задержка", false);
    private final SliderSetting extraDelayMs = new SliderSetting("Доп. Задержка", 0.0, 0.0, 1000.0, 1.0, customDelay::get);

    private final BooleanSetting onlyWithWeapon = new BooleanSetting("Только с оружием", true);
    private final BooleanSetting hitboxSync = new BooleanSetting("Синхронизация с ХитБокс", false);
    private final SliderSetting attackDistance = new SliderSetting("Дистанция атаки", 3.0, 2.0, 10.0, 0.1, () -> !hitboxSync.get());

    private final BooleanSetting attackInvisible = new BooleanSetting("Атаковать невидимых", false);
    private final BooleanSetting invisibleWithArmor = new BooleanSetting("Невидимых с бронёй", true, "Атаковать невидимых только если на них броня", attackInvisible::get);
    private final BooleanSetting onlyArmored = new BooleanSetting("Только в броне", false);

    private final BooleanSetting lineOfSightCheck = new BooleanSetting("Проверка прямой видимости", true);
    private final BooleanSetting blockCheck = new BooleanSetting("Проверка блокирующих блоков", true);

    private final BooleanSetting noise = new BooleanSetting("Шум", false);
    private final SliderSetting noiseRangeMs = new SliderSetting("Диапазон шума", 50.0, 1.0, 200.0, 1.0, noise::get);

    private final BooleanSetting misses = new BooleanSetting("Промахи", false);
    private final SliderSetting missChance = new SliderSetting("Шанс промаха", 5.0, 1.0, 100.0, 1.0, misses::get);

    private long nextAttackTimeMs = 0L;
    private boolean sprintWasPressed;
    private boolean sprintWasSprinting;
    private boolean sprintFrozen;
    private long sprintFrozenAt;

    public TriggerBot() {
        addSettings(
                targets,
                attackMode,
                pvpMode,
                sprintControl,
                tpsSync,
                customDelay,
                extraDelayMs,
                onlyWithWeapon,
                hitboxSync,
                attackDistance,
                attackInvisible,
                invisibleWithArmor,
                onlyArmored,
                lineOfSightCheck,
                blockCheck,
                noise,
                noiseRangeMs,
                misses,
                missChance
        );
    }

    private boolean canCritAttack() {
        if (mc.player == null) return false;

        if (mc.player.hasStatusEffect(StatusEffects.BLINDNESS)
                || mc.player.hasStatusEffect(StatusEffects.LEVITATION)
                || mc.player.hasStatusEffect(StatusEffects.SLOW_FALLING)
                || mc.player.isInLava()
                || mc.player.inPowderSnow
                || mc.player.isClimbing()
                || mc.player.hasVehicle()
                || mc.player.getAbilities().flying
                || mc.player.isInFluid()) {
            return false;
        }

        double velY = mc.player.getVelocity().y;
        return !mc.player.isOnGround()
                && velY < 0.0
                && mc.player.fallDistance > 0.0f;
    }

    private boolean hasWeapon() {
        if (mc.player == null) return false;
        ItemStack stack = mc.player.getMainHandStack();
        if (stack == null || stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item instanceof SwordItem || item instanceof AxeItem || item instanceof MaceItem;
    }

    private double getAttackDistance() {
        if (!hitboxSync.get()) return attackDistance.get().doubleValue();
        HitBoxSnap hb = Manager.FUNCTION_MANAGER == null ? null : Manager.FUNCTION_MANAGER.xbox;
        if (hb != null && hb.state && hb.expand.get()) {
            return attackDistance.get().doubleValue() + hb.size.get().doubleValue();
        }
        return attackDistance.get().doubleValue();
    }

    private boolean isValidTarget(LivingEntity target) {
        if (target == null || mc.player == null) return false;
        if (!target.isAlive()) return false;
        if (target == mc.player) return false;

        if (target instanceof PlayerEntity pe) {
            if (Manager.FRIEND_MANAGER != null && Manager.FRIEND_MANAGER.isFriend(pe.getName().getString())) return false;
        }

        String t = targets.get();
        if (TARGETS_PLAYERS.equalsIgnoreCase(t) && !(target instanceof PlayerEntity)) return false;
        if (TARGETS_MOBS.equalsIgnoreCase(t) && !(target instanceof MobEntity)) return false;

        if (onlyArmored.get() && target instanceof PlayerEntity) {
            if (target.getArmorItems() == null || !target.getArmorItems().iterator().hasNext()) return false;
            boolean anyArmor = false;
            for (var it : target.getArmorItems()) {
                if (it != null && !it.isEmpty()) {
                    anyArmor = true;
                    break;
                }
            }
            if (!anyArmor) return false;
        }

        if (target.isInvisible()) {
            if (!attackInvisible.get()) return false;
            if (invisibleWithArmor.get()) {
                boolean anyArmor = false;
                for (var it : target.getArmorItems()) {
                    if (it != null && !it.isEmpty()) {
                        anyArmor = true;
                        break;
                    }
                }
                if (!anyArmor) return false;
            }
        }

        if (lineOfSightCheck.get() && !hasLineOfSight(target)) return false;
        if (blockCheck.get() && isInBlockingBlock(target)) return false;

        return true;
    }

    private boolean hasLineOfSight(Entity target) {
        if (mc.world == null || mc.player == null || target == null) return false;
        Vec3d from = mc.player.getEyePos();
        Vec3d to = target.getBoundingBox().getCenter();
        RaycastContext ctx = new RaycastContext(from, to, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player);
        return mc.world.raycast(ctx).getType() == HitResult.Type.MISS;
    }

    private boolean isInBlockingBlock(Entity e) {
        if (mc.world == null || e == null) return false;
        BlockPos pos = e.getBlockPos();
        BlockState st = mc.world.getBlockState(pos);
        // close to original logic: if it's a full cube and not transparent -> consider blocking
        return st != null && st.isFullCube(mc.world, pos) && !st.isTransparent();
    }

    private float getTpsMultiplier() {
        if (!tpsSync.get()) return 1.0f;
        if (Manager.SYNC_MANAGER == null) return 1.0f;
        float tps = Manager.SYNC_MANAGER.tps;
        if (tps <= 0.0f) return 1.0f;
        // if tps is lower -> increase delays / cooldown requirements
        return 20.0f / Math.min(20.0f, tps);
    }

    private void freezeSprint() {
        if (mc.player == null) return;
        sprintWasPressed = mc.options.sprintKey.isPressed();
        sprintWasSprinting = mc.player.isSprinting();
        mc.options.sprintKey.setPressed(false);
        mc.player.setSprinting(false);
        sprintFrozen = true;
        sprintFrozenAt = System.currentTimeMillis();
    }

    private void restoreSprintIfNeeded() {
        if (!sprintFrozen || mc.player == null) return;
        // same idea as in provided code: restore after a short time window
        if (System.currentTimeMillis() - sprintFrozenAt < 300L) return;

        mc.options.sprintKey.setPressed(sprintWasPressed);
        mc.player.setSprinting(sprintWasSprinting || sprintWasPressed);
        sprintFrozen = false;
    }

    private boolean shouldAttackNow() {
        if (mc.player == null) return false;

        if (PVP_19.equalsIgnoreCase(pvpMode.get())) {
            float base = 0.84f;
            float tpsMul = getTpsMultiplier();
            // if server tps < 20 => require a bit more cooldown
            float required = Math.min(1.0f, base * tpsMul);
            if (mc.player.getAttackCooldownProgress(0.5f) < required) return false;
        }

        String mode = attackMode.get();
        boolean critOk = canCritAttack();
        if (MODE_CRITS.equalsIgnoreCase(mode)) {
            return critOk;
        }
        // SMART: if can crit -> prefer crit, otherwise allow normal
        return critOk || mc.player.isOnGround();
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;

        restoreSprintIfNeeded();

        if (onlyWithWeapon.get() && !hasWeapon()) return;
        if (mc.player.isUsingItem()) return;

        if (System.currentTimeMillis() < nextAttackTimeMs) return;

        LivingEntity target = null;
        if (mc.crosshairTarget instanceof EntityHitResult ehr) {
            if (ehr.getEntity() instanceof LivingEntity le) target = le;
        } else if (mc.targetedEntity instanceof LivingEntity le) {
            target = le;
        }
        if (target == null) return;
        if (!isValidTarget(target)) return;

        double dist = getAttackDistance();
        if (target.distanceTo(mc.player) > dist) return;

        if (!shouldAttackNow()) return;

        if (misses.get()) {
            float chance = missChance.get().floatValue();
            if (chance > 0.0f && mc.player.getRandom().nextFloat() * 100.0f < chance) {
                // do a fake swing
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.player.resetLastAttackedTicks();
                scheduleNextAttack();
                return;
            }
        }

        if (sprintControl.get() && !sprintFrozen) {
            freezeSprint();
        }

        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);
        scheduleNextAttack();
    }

    private void scheduleNextAttack() {
        long delay = customDelay.get() ? extraDelayMs.get().longValue() : 0L;
        if (noise.get()) {
            delay += (long) (mc.player.getRandom().nextDouble() * noiseRangeMs.get().doubleValue());
        }
        if (tpsSync.get()) {
            delay = (long) (delay * getTpsMultiplier());
        }
        nextAttackTimeMs = System.currentTimeMillis() + Math.max(0L, delay);
    }
}
