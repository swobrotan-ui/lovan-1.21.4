package ru.levin.modules.combat;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import ru.levin.events.Event;
import ru.levin.events.impl.player.EventAttack;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;

@SuppressWarnings("All")
@FunctionAnnotation(name = "Criticals", desc = "Криты пакетами", type = Type.Combat)
public class Criticals extends Function {

    @Override
    public void onEvent(Event event) {
        if (!(event instanceof EventAttack attack)) return;
        if (mc.player == null || mc.world == null) return;
        if (attack.getAttacker() != mc.player) return;

        if (!mc.player.isOnGround()) return;
        if (mc.player.isTouchingWater() || mc.player.isInLava()) return;
        if (mc.player.isClimbing()) return;
        if (mc.player.hasVehicle()) return;

        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();

        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false, true));
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, false, false));
    }
}
