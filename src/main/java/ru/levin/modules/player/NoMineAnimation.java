package ru.levin.modules.player;

import ru.levin.events.Event;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "NoMineAnimation", desc = "Не показывает анимацию добычи для других игроков", type = Type.Player)
public class NoMineAnimation extends Function {
    @Override
    public void onEvent(Event event) {
    }
}
