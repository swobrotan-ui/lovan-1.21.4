package ru.levin.modules.misc;

import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.option.GraphicsMode;
import net.minecraft.particle.ParticlesMode;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.util.player.TimerUtil;

@FunctionAnnotation(name = "Optimizer", desc = "Реально оптимизирует майнкрафт, поднимает ФПС", type = Type.Misc)
public class Optimizer extends Function {

    private final BooleanSetting boostFPS = new BooleanSetting("Макс ФПС", true, "Отключает VSync и снимает лимит FPS");
    private final BooleanSetting graphics = new BooleanSetting("Быстрая графика", true, "FAST графика, без облаков");
    private final BooleanSetting lessParticles = new BooleanSetting("Меньше частиц", true, "Ставит частицы на минимум");
    private final BooleanSetting noShadows = new BooleanSetting("Убрать тени", true, "Отключает тени сущностей");
    private final BooleanSetting noBiomeBlend = new BooleanSetting("Без смешивания биомов", true, "Убирает плавные переходы биомов");
    private final BooleanSetting limitRender = new BooleanSetting("Ограничить прорисовку", true, "Понижает дальность прорисовки");
    private final SliderSetting renderDistance = new SliderSetting("Дальность прорисовки", 8, 2, 16, 1, limitRender::get);
    private final SliderSetting entityDistance = new SliderSetting("Дальность сущностей %", 75, 50, 100, 5, limitRender::get);
    private final BooleanSetting memory = new BooleanSetting("Чистить память", true, "Периодически освобождает память");

    private final TimerUtil timerHelper = new TimerUtil();

    // сохранённые оригинальные значения для восстановления при выключении
    private boolean saved = false;
    private boolean origVsync;
    private int origMaxFps;
    private CloudRenderMode origClouds;
    private GraphicsMode origGraphics;
    private ParticlesMode origParticles;
    private boolean origShadows;
    private int origBiomeBlend;
    private int origViewDistance;
    private double origEntityDistance;

    public Optimizer() {
        addSettings(boostFPS, graphics, lessParticles, noShadows, noBiomeBlend, limitRender, renderDistance, entityDistance, memory);
    }

    @Override
    public void onEnable() {
        if (mc.options == null) return;
        saveOriginals();
        apply();
        timerHelper.reset();
    }

    @Override
    public void onDisable() {
        restoreOriginals();
    }

    private void saveOriginals() {
        if (saved || mc.options == null) return;
        origVsync = mc.options.getEnableVsync().getValue();
        origMaxFps = mc.options.getMaxFps().getValue();
        origClouds = mc.options.getCloudRenderMode().getValue();
        origGraphics = mc.options.getGraphicsMode().getValue();
        origParticles = mc.options.getParticles().getValue();
        origShadows = mc.options.getEntityShadows().getValue();
        origBiomeBlend = mc.options.getBiomeBlendRadius().getValue();
        origViewDistance = mc.options.getViewDistance().getValue();
        origEntityDistance = mc.options.getEntityDistanceScaling().getValue();
        saved = true;
    }

    private void restoreOriginals() {
        if (!saved || mc.options == null) return;
        mc.options.getEnableVsync().setValue(origVsync);
        mc.options.getMaxFps().setValue(origMaxFps);
        mc.options.getCloudRenderMode().setValue(origClouds);
        mc.options.getGraphicsMode().setValue(origGraphics);
        mc.options.getParticles().setValue(origParticles);
        mc.options.getEntityShadows().setValue(origShadows);
        if (mc.options.getBiomeBlendRadius().getValue() != origBiomeBlend)
            mc.options.getBiomeBlendRadius().setValue(origBiomeBlend);
        if (mc.options.getViewDistance().getValue() != origViewDistance)
            mc.options.getViewDistance().setValue(origViewDistance);
        mc.options.getEntityDistanceScaling().setValue(origEntityDistance);
        saved = false;
    }

    private void apply() {
        if (mc.options == null) return;

        if (boostFPS.get()) {
            if (mc.options.getEnableVsync().getValue()) mc.options.getEnableVsync().setValue(false);
            if (mc.options.getMaxFps().getValue() != 260) mc.options.getMaxFps().setValue(260);
        }

        if (graphics.get()) {
            if (mc.options.getGraphicsMode().getValue() != GraphicsMode.FAST)
                mc.options.getGraphicsMode().setValue(GraphicsMode.FAST);
            if (mc.options.getCloudRenderMode().getValue() != CloudRenderMode.OFF)
                mc.options.getCloudRenderMode().setValue(CloudRenderMode.OFF);
        }

        if (lessParticles.get() && mc.options.getParticles().getValue() != ParticlesMode.MINIMAL) {
            mc.options.getParticles().setValue(ParticlesMode.MINIMAL);
        }

        if (noShadows.get() && mc.options.getEntityShadows().getValue()) {
            mc.options.getEntityShadows().setValue(false);
        }

        if (noBiomeBlend.get() && mc.options.getBiomeBlendRadius().getValue() != 0) {
            mc.options.getBiomeBlendRadius().setValue(0);
        }

        if (limitRender.get()) {
            int rd = renderDistance.get().intValue();
            // менять дальность прорисовки только при реальном изменении — иначе постоянный релоад чанков
            if (mc.options.getViewDistance().getValue() != rd) {
                mc.options.getViewDistance().setValue(rd);
            }
            double ed = entityDistance.get().doubleValue() / 100.0;
            if (Math.abs(mc.options.getEntityDistanceScaling().getValue() - ed) > 0.001) {
                mc.options.getEntityDistanceScaling().setValue(ed);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        if (mc.options == null) return;

        apply();

        if (memory.get() && timerHelper.hasTimeElapsed(180000)) {
            if (mc.player == null || mc.world == null || mc.currentScreen != null) {
                System.gc();
                Runtime.getRuntime().freeMemory();
            }
            timerHelper.reset();
        }
    }
}
