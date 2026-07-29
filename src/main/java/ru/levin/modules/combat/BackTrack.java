package ru.levin.modules.combat;

import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@SuppressWarnings("All")
@FunctionAnnotation(name = "BackTrack", desc = "BackTrack (заглушка)", type = Type.Combat)
public class BackTrack extends Function {
    @Override
    public void onEvent(Event event) {
        // TODO: реализовать, сейчас оставлено заглушкой чтобы проект собирался
    }
}
