package ru.levin.modules.render;

import ru.levin.events.Event;
import ru.levin.manager.Manager;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;

@FunctionAnnotation(name = "Interface", desc = "Настройки интерфейса", type = Type.Render)
public class Interface extends Function {

    public final ModeSetting style = new ModeSetting("Стиль", "Клиентский", "Клиентский", "Стеклянный");

    public Interface() {
        addSettings(style);
    }

    @Override
    public void onEvent(Event event) {
        if (Manager.FUNCTION_MANAGER != null && Manager.FUNCTION_MANAGER.clickGUI != null) {
            String v = style.get();
            if (v != null && v.equalsIgnoreCase("Стеклянный")) {
                Manager.FUNCTION_MANAGER.clickGUI.interfaceStyle.set("Liquid Glass");
            } else {
                Manager.FUNCTION_MANAGER.clickGUI.interfaceStyle.set("Клиентский");
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Manager.FUNCTION_MANAGER != null && Manager.FUNCTION_MANAGER.clickGUI != null) {
            String v = style.get();
            if (v != null && v.equalsIgnoreCase("Стеклянный")) {
                Manager.FUNCTION_MANAGER.clickGUI.interfaceStyle.set("Liquid Glass");
            } else {
                Manager.FUNCTION_MANAGER.clickGUI.interfaceStyle.set("Клиентский");
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Manager.FUNCTION_MANAGER != null && Manager.FUNCTION_MANAGER.clickGUI != null) {
            Manager.FUNCTION_MANAGER.clickGUI.interfaceStyle.set("Клиентский");
        }
    }
}
