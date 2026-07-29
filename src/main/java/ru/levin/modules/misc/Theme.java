package ru.levin.modules.misc;

import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.manager.Manager;
import ru.levin.manager.themeManager.Style;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;

@FunctionAnnotation(name = "Theme", desc = "Тема клиента", type = Type.Misc)
public class Theme extends Function {
    public final ModeSetting theme = new ModeSetting(
            "Тема",
            "Клиентский",
            "Клиентский",
            "Бирюзовый",
            "Осень",
            "Кислотный",
            "Океан",
            "Вишневый",
            "Аметист",
            "Лаванда",
            "Лайм",
            "Закат",
            "Небо",
            "Пламя",
            "Сакура",
            "Мята",
            "Графит"
    );
    private String lastTheme = "";

    public Theme() {
        addSettings(theme);
    }

    @Override
    public void onEnable() {
        applyTheme();
    }

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventUpdate)) return;
        applyTheme();
    }

    private void applyTheme() {
        String selected = theme.get();
        if (selected == null) return;
        selected = selected.trim();
        if (selected.equalsIgnoreCase(lastTheme)) return;

        String selectedNorm = normalizeThemeName(selected);

        for (Style style : Manager.STYLE_MANAGER.getStyles()) {
            String styleName = style.name == null ? "" : style.name.trim();
            if (styleName.equalsIgnoreCase(selected) || normalizeThemeName(styleName).equalsIgnoreCase(selectedNorm)) {
                Manager.STYLE_MANAGER.setTheme(style);
                lastTheme = selected;
                break;
            }
        }
    }

    private static String normalizeThemeName(String s) {
        if (s == null) return "";
        // normalize different dash characters and spacing
        return s.trim()
                .replace('–', '-')
                .replace('—', '-')
                .replace('‑', '-')
                .replace(" ", "");
    }
}
