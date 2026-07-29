package ru.levin.x2demo;

import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "X2Duplicator", desc = "Демонстрационный модуль x2", type = Type.Misc)
public class X2Module extends Function {

    public X2Module() {
    }

    @Override
    public void onEvent(Event event) {
    }
}
