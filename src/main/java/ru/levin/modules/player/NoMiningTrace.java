package ru.levin.modules.player;

import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "NoMiningTrace", desc = "Позволяет копать блоки сквозь сущностей", type = Type.Player)
public class NoMiningTrace extends Function {
    @Override
    public void onEvent(Event event) {
    }
}
