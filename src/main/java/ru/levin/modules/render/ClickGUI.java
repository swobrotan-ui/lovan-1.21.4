package ru.levin.modules.render;

import org.lwjgl.glfw.GLFW;
import ru.levin.manager.Manager;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.MultiSetting;
import ru.levin.modules.setting.SliderSetting;

import java.awt.*;
import java.util.Arrays;

@FunctionAnnotation(name = "ClickGUI" ,desc  = "Управление/Кастомизация GUI", type = Type.Render, key = GLFW.GLFW_KEY_RIGHT_SHIFT)
public class ClickGUI extends Function {
    public final ModeSetting guiType = new ModeSetting("Тип GUI", "Колонки", "Колонки", "Категории");
    public final ModeSetting colorGUI = new ModeSetting("Тема","Светло-чёрная","Светло-чёрная","Тёмная");
    public final ModeSetting interfaceStyle = new ModeSetting("Interface", "Клиентский", "Клиентский");

    // общая прозрачность GUI: диапазон сдвинут ближе к 255, чтобы GUI был почти непрозрачным
    public final SliderSetting alpha = new SliderSetting("Прозрачность",245f,230f,255f,1f);
    public final BooleanSetting blur = new BooleanSetting("Размытие",true);
    public final MultiSetting blurSetting = new MultiSetting(
            () -> blur.get(),
            "Элементы",
            Arrays.asList("Поиск","Панели"),
            new String[]{"Поиск","Темы", "Панели", "Описание","Создание темы"}
    );
    public final BooleanSetting strike = new BooleanSetting("Обводка для модулей",true);
    public final BooleanSetting filling = new BooleanSetting("Заливка для модулей",true);
    public final SliderSetting rounding = new SliderSetting("Закругление",4,0,6,1,() -> strike.get() || filling.get());
    // прозрачность блоков модулей: делаем по умолчанию более плотной и допускаем полный диапазон до 255
    public final SliderSetting alphaModules = new SliderSetting("Прозрачность модулей",190f,120f,255f,1f,() -> strike.get() || filling.get());

    public ClickGUI() {
        addSettings(guiType, colorGUI, interfaceStyle, alpha, blur, blurSetting, strike, filling, rounding, alphaModules);
    }

    public Color getGuiColor() {
        int a = Math.min(255, Math.round(alpha.get().floatValue() * 0.92f));

        var style = Manager.STYLE_MANAGER.getTheme();
        if (style != null && style.name != null && !style.name.equalsIgnoreCase("Клиентский")) {
            return new Color(18, 18, 24, a);
        }

        switch (colorGUI.get()) {
            case "Тёмная":
                return new Color(18, 16, 45, a);
            case "Светло-чёрная":
            default:
                return new Color(30, 26, 70, a);
        }
    }

    @Override
    public void onEvent(Event event) {}

    @Override
    public void onEnable() {
        setState(false);
        super.onEnable();
    }
}