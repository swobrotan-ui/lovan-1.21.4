package ru.levin.modules.render;

import net.minecraft.client.render.*;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import ru.levin.events.Event;
import ru.levin.events.impl.EventPacket;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.world.EventFog;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;

import java.awt.Color;

@SuppressWarnings("All")
@FunctionAnnotation(name = "World", desc = "Позволяет менять время суток, погоду и туман", type = Type.Render)
public class World extends Function {

    private final BooleanSetting timeBox = new BooleanSetting("Изменять время", true);
    private final ModeSetting timeMode = new ModeSetting(
            timeBox::get,
            "Время суток",
            "День", "День", "Ночь", "Утро", "Восход", "Кастомное"
    );
    private final SliderSetting customTime = new SliderSetting(
            "Кастомное время", 6000, 0, 24000, 100,
            () -> timeBox.get() && timeMode.is("Кастомное")
    );

    private final BooleanSetting weatherBox = new BooleanSetting("Изменять погоду", true);
    private final ModeSetting weatherMode = new ModeSetting(
            weatherBox::get,
            "Погода",
            "Ясно", "Ясно", "Дождь", "Гроза"
    );

    public final BooleanSetting fog = new BooleanSetting("Туман", false);
    public final ModeSetting fogColorMode = new ModeSetting(
            fog::get,
            "Цвет тумана",
            "Клиент", "Клиент", "Кастомный"
    );
    public final SliderSetting fogRed = new SliderSetting(
            "Красный", 125, 0, 255, 1,
            () -> fog.get() && fogColorMode.is("Кастомный")
    );
    public final SliderSetting fogGreen = new SliderSetting(
            "Зелёный", 110, 0, 255, 1,
            () -> fog.get() && fogColorMode.is("Кастомный")
    );
    public final SliderSetting fogBlue = new SliderSetting(
            "Синий", 255, 0, 255, 1,
            () -> fog.get() && fogColorMode.is("Кастомный")
    );
    public final SliderSetting fogAlpha = new SliderSetting(
            "Прозрачность", 0.85f, 0.0f, 1.0f, 0.01f,
            fog::get
    );
    public final SliderSetting fogStart = new SliderSetting(
            "Начало тумана", 30, 0, 500, 1,
            fog::get
    );
    public final SliderSetting fogEnd = new SliderSetting(
            "Дальность тумана", 200, 0, 500, 1, fog::get
    );

    public World() {
        addSettings(timeBox, timeMode, customTime, weatherBox, weatherMode, fog, fogColorMode, fogRed, fogGreen, fogBlue, fogAlpha, fogStart, fogEnd);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.world == null) return;
        if (event instanceof EventPacket packet && timeBox.get()) {
            if (packet.getPacket() instanceof WorldTimeUpdateS2CPacket) {
                packet.setCancel(true);
            }
        }

        if (event instanceof EventUpdate) {
            if (timeBox.get()) {
                mc.world.setTime(resolveTime(), resolveTime(), false);
            }

            if (weatherBox.get()) {
                switch (weatherMode.get()) {
                    case "Ясно" -> {
                        mc.world.setRainGradient(0f);
                        mc.world.setThunderGradient(0f);
                    }
                    case "Дождь" -> {
                        mc.world.setRainGradient(1f);
                        mc.world.setThunderGradient(0f);
                    }
                    case "Гроза" -> {
                        mc.world.setRainGradient(1f);
                        mc.world.setThunderGradient(1f);
                    }
                }
            }
        }

        if (event instanceof EventFog fogEvent && fog.get()) {
            int fogColor;
            if (fogColorMode.is("Кастомный")) {
                fogColor = new Color(
                        fogRed.get().intValue(),
                        fogGreen.get().intValue(),
                        fogBlue.get().intValue()
                ).getRGB();
            } else {
                fogColor = Manager.STYLE_MANAGER.getFirstColor();
            }
            fogEvent.r = ((fogColor >> 16) & 0xFF) / 255.0f;
            fogEvent.g = ((fogColor >> 8) & 0xFF) / 255.0f;
            fogEvent.b = (fogColor & 0xFF) / 255.0f;
            fogEvent.alpha = fogAlpha.get().floatValue();
            float end = fogEnd.get().floatValue();
            float start = Math.min(fogStart.get().floatValue(), end - 0.5f);
            fogEvent.start = Math.max(0.0f, start);
            fogEvent.end = end;
            fogEvent.shape = FogShape.SPHERE;
            fogEvent.modified = true;
        }
    }

    private long resolveTime() {
        return switch (timeMode.get()) {
            case "День" -> 1000L;
            case "Ночь" -> 13000L;
            case "Утро" -> 0L;
            case "Восход" -> 23000L;
            case "Кастомное" -> (long) customTime.get().floatValue();
            default -> 6000L;
        };
    }
}
