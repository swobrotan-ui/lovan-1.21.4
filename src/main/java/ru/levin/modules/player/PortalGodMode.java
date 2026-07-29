package ru.levin.modules.player;

import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "PortalGodMode", desc = "Бессмертие в портале: фриз движения", type = Type.Player)
public class PortalGodMode extends Function {
    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventMotion motion)) return;
        if (mc.player == null) return;
        // if (mc.player.isInPortal()) { // method not available in this version
            motion.setX(0);
            motion.setY(0);
            motion.setZ(0);
        // }
    }
}
