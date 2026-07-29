package ru.levin.modules.movement;

import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;

@FunctionAnnotation(name = "Jesus", desc = "Ходьба по воде", type = Type.Move)
public class Jesus extends Function {

    private final ModeSetting mode = new ModeSetting("Режим", "Solid", "Solid", "Dolphin", "GrimBypass", "FunTime", "Off");
    private final SliderSetting speed = new SliderSetting("Скорость", 1.0f, 0.1f, 3.0f, 0.1f);
    private final SliderSetting lift = new SliderSetting("Подъём", 1.0f, 0.1f, 5.0f, 0.1f);

    private int dolphinTicks = 0;
    private boolean jumpQueued = false;

    public Jesus() {
        addSettings(mode, speed, lift);
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventMotion) || mc.player == null || mc.world == null) return;
        if (mc.player.isSpectator() || mc.player.isCreative()) return;
        if ("Off".equals(mode.get())) return;

        String currentMode = mode.get();
        if ("Solid".equals(currentMode)) {
            handleSolid();
        } else if ("Dolphin".equals(currentMode)) {
            handleDolphin();
        } else if ("FunTime".equals(currentMode)) {
            handleFunTime();
        } else if ("GrimBypass".equals(currentMode)) {
            handleGrimBypass();
        }
    }

    /**
     * Solid режим: классическая ходьба по воде как по твердому блоку.
     * Устанавливаем onGround и фиксируем Y-скорость, чтобы игрок скользил по поверхности.
     */
    private void handleSolid() {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        if (!isOnWaterSurface(x, y, z)) return;

        if (mc.player.getVelocity().y < 0.0) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
        }

        double vy = getLiftVelocity(y);
        double speedMult = getSpeedMultiplier();

        mc.player.setOnGround(true);
        mc.player.setVelocity(
                mc.player.getVelocity().x * speedMult,
                vy,
                mc.player.getVelocity().z * speedMult
        );

        mc.player.fallDistance = 0;
    }

    /**
     * Dolphin режим: ритмичные прыжки по воде как дельфин.
     * Игрок автоматически прыгает при достижении определенной глубины.
     */
    private void handleDolphin() {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        if (!isInWater(x, y, z)) return;

        if (mc.player.isSneaking()) return;

        dolphinTicks++;

        boolean inWater = mc.player.isTouchingWater() || mc.player.isSubmergedInWater();
        boolean wantsToJump = mc.player.getVelocity().y < -0.05 || mc.player.getY() < (int) mc.player.getY() + 0.1;

        if (inWater && wantsToJump && !jumpQueued) {
            jumpQueued = true;
            dolphinTicks = 0;
        }

        if (jumpQueued && dolphinTicks >= 1) {
            jumpQueued = false;
            dolphinTicks = 0;

            mc.player.setVelocity(
                    mc.player.getVelocity().x,
                    0.25 + (lift.get().floatValue() * 0.05),
                    mc.player.getVelocity().z
            );
            mc.player.setOnGround(false);
            mc.player.fallDistance = 0;
        }

        double speedMult = getSpeedMultiplier();
        mc.player.setVelocity(
                mc.player.getVelocity().x * speedMult,
                mc.player.getVelocity().y,
                mc.player.getVelocity().z * speedMult
        );
    }

    /**
     * GrimBypass режим: микро-погружения для обхода строгих античитов.
     * Чередуем нахождение над водой и микро-погружение, маскируясь под плавание.
     */
    private void handleGrimBypass() {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        if (!isOnWaterSurface(x, y, z)) return;

        if (mc.player.isSneaking()) return;

        double speedMult = getSpeedMultiplier();
        double vy = mc.player.getVelocity().y;

        if (vy < 0.0) {
            mc.player.setVelocity(mc.player.getVelocity().x, 0.0, mc.player.getVelocity().z);
        }

        double time = System.currentTimeMillis() / 1000.0;
        float microDive = (float) (Math.sin(time * 3.0) * 0.0015f);

        double liftVel = getLiftVelocity(y);
        mc.player.setVelocity(
                mc.player.getVelocity().x * speedMult,
                liftVel + microDive,
                mc.player.getVelocity().z * speedMult
        );

        mc.player.setOnGround(true);
        mc.player.fallDistance = 0;
    }

    /**
     * FunTime режим: мелкие подпрыгивания на поверхности воды.
     * Имитирует серию небольших прыжков, что выглядит менее подозрительно,
     * чем зависание/полёт над водой.
     */
    private void handleFunTime() {
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        if (!isOnWaterSurface(x, y, z)) return;
        if (mc.player.isSneaking()) return;

        double speedMult = getSpeedMultiplier();
        double vy = mc.player.getVelocity().y;

        if (mc.player.isOnGround()) {
            vy = 0.20 + (lift.get().floatValue() * 0.03);
        } else if (vy < -0.1) {
            vy = -0.1;
        }

        mc.player.setVelocity(
                mc.player.getVelocity().x * speedMult,
                vy,
                mc.player.getVelocity().z * speedMult
        );
        mc.player.fallDistance = 0;
    }

    /**
     * Проверяет, находится ли игрок непосредственно над поверхностью воды.
     */
    private boolean isOnWaterSurface(double x, double y, double z) {
        int px = (int) Math.floor(x);
        int py = (int) Math.floor(y);
        int pz = (int) Math.floor(z);
        return isWaterBlock(px, py, pz) || isWaterBlock(px, py + 1, pz);
    }

    /**
     * Проверяет, находится ли игрок внутри воды (для Dolphin режима).
     */
    private boolean isInWater(double x, double y, double z) {
        int px = (int) Math.floor(x);
        int py = (int) Math.floor(y);
        int pz = (int) Math.floor(z);
        BlockPos feet = new BlockPos(px, py, pz);
        BlockPos head = new BlockPos(px, py + 1, pz);
        return isWaterBlock(feet.getX(), feet.getY(), feet.getZ()) ||
               isWaterBlock(head.getX(), head.getY(), head.getZ());
    }

    /**
     * Проверяет, является ли блок по заданным координатам водным.
     */
    private boolean isWaterBlock(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        FluidState state = mc.world.getFluidState(pos);
        return state.isIn(FluidTags.WATER);
    }

    /**
     * Вычисляет вертикальную скорость подъема над водой в зависимости от настроек.
     */
    private double getLiftVelocity(double y) {
        int intY = (int) Math.floor(y);
        boolean onWater = isWaterBlock((int) Math.floor(mc.player.getX()), intY, (int) Math.floor(mc.player.getZ()));
        if (onWater) return 0.015 + (lift.get().floatValue() * 0.005);
        return 0.002 * lift.get().floatValue();
    }

    /**
     * Возвращает множитель скорости из настроек.
     */
    private double getSpeedMultiplier() {
        return speed.get().floatValue();
    }
}
