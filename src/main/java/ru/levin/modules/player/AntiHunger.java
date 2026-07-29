package ru.levin.modules.player;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import ru.levin.events.Event;
import ru.levin.events.impl.EventPacket;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@FunctionAnnotation(name = "AntiHunger", desc = "Уменьшает голод: отменяет спринт-пакеты", type = Type.Player)
public class AntiHunger extends Function {
    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventPacket packetEvent)) return;
        if (!packetEvent.isSendPacket()) return;
        var packet = packetEvent.getPacket();
        if (packet instanceof ClientCommandC2SPacket cmdPacket) {
            if (cmdPacket.getMode() == ClientCommandC2SPacket.Mode.START_SPRINTING) {
                packetEvent.setCancel(true);
            }
        }
    }
}
