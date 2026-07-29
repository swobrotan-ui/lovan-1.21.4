package module;

import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import java.util.Set;
import net.minecraft.entity.player.PlayerPosition;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public class NoServerRotateModule extends Module {
   public NoServerRotateModule() {
      super("НоЗерверРотате", "Отменяет принудительный поворот камеры от сервера", Category.PLAYER);
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (packetevent.getType() == PacketDirection.RECEIVE) {
         if (this.getClient().player != null) {
            if (packetevent.getPacket() instanceof PlayerPositionLookS2CPacket playerpositionlooks2cpacket) {
               PlayerPositionLookS2CPacket playerpositionlooks2cpacket1 = playerpositionlooks2cpacket;

               try {
                  j = playerpositionlooks2cpacket1.teleportId();
               } catch (Throwable throwable2) {
                  throw new MatchException(throwable2.toString(), throwable2);
               }

               int i = j;
               playerpositionlooks2cpacket1 = playerpositionlooks2cpacket;

               try {
                  playerposition2 = playerpositionlooks2cpacket1.change();
               } catch (Throwable throwable1) {
                  throw new MatchException(throwable1.toString(), throwable1);
               }

               PlayerPosition playerposition1 = playerposition2;
               playerpositionlooks2cpacket1 = playerpositionlooks2cpacket;

               try {
                  set1 = playerpositionlooks2cpacket1.relatives();
               } catch (Throwable throwable) {
                  throw new MatchException(throwable.toString(), throwable);
               }

               Set set = set1;
               packetevent.setCancelled(true);
               PlayerPosition playerposition = new PlayerPosition(
                  playerposition1.position(), playerposition1.deltaMovement(), this.getClient().player.getYaw(), this.getClient().player.getPitch()
               );
               PacketEvent.receiveSilent(new PlayerPositionLookS2CPacket(i, playerposition, set));
            }
         }
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }
}
