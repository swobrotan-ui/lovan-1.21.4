package event;

import enum.PacketDirection;
import net.CustomClientConnection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

public class PacketEvent {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private final Packet<?> packet;
   private final PacketDirection type;
   private boolean cancelled = false;

   public PacketEvent(Packet<?> packet, PacketDirection packetdirection) {
      this.packet = packetx;
      this.type = packetdirection;
   }

   public static void sendSilent(Packet<?> packet) {
      if (mc.getNetworkHandler() != null) {
         try {
            CustomClientConnection.setSendingSilent(true);
            mc.getNetworkHandler().sendPacket(packetx);
         } finally {
            CustomClientConnection.setSendingSilent(false);
         }
      }
   }

   public static void receiveSilent(Packet<?> packet) {
      if (mc.getNetworkHandler() != null) {
         try {
            CustomClientConnection.setReceivingSilent(true);
            packetx.apply(mc.getNetworkHandler());
         } finally {
            CustomClientConnection.setReceivingSilent(false);
         }
      }
   }

   public Packet<?> getPacket() {
      return this.packet;
   }

   public PacketDirection getType() {
      return this.type;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
