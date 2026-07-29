package net;

import core.ClientMain;
import enum.PacketDirection;
import event.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import module.Module;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import util.TPSTracker;

public class CustomClientConnection extends ClientConnection {
   private static volatile boolean sendingSilent = false;
   private static volatile boolean receivingSilent = false;

   public CustomClientConnection(NetworkSide networkside) {
      super(networkside);
   }

   protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet<?> packet) {
      if (!receivingSilent) {
         PacketEvent packetevent = new PacketEvent(packet, PacketDirection.RECEIVE);
         this.fireEvent(packetevent);
         if (packetevent.isCancelled()) {
            return;
         }
      }

      super.channelRead0(channelhandlercontext, packet);
   }

   public void send(Packet<?> packet) {
      if (!sendingSilent) {
         PacketEvent packetevent = new PacketEvent(packet, PacketDirection.SEND);
         this.fireEvent(packetevent);
         if (packetevent.isCancelled()) {
            return;
         }
      }

      super.send(packet);
   }

   private void fireEvent(PacketEvent packetevent) {
      try {
         TPSTracker.getInstance().onPacket(packetevent);
         if (ClientMain.getInstance() != null && ClientMain.getInstance().getModuleManager() != null) {
            for (Module module : ClientMain.getInstance().getModuleManager().getEnabledModules()) {
               try {
                  module.onPacket(packetevent);
                  if (packetevent.isCancelled()) {
                     break;
                  }
               } catch (Exception exception) {
               }
            }
         }
      } catch (Exception exception1) {
      }
   }

   public static void setSendingSilent(boolean flag) {
      sendingSilent = flag;
   }

   public static boolean isSendingSilent() {
      return sendingSilent;
   }

   public static void setReceivingSilent(boolean flag) {
      receivingSilent = flag;
   }

   public static boolean isReceivingSilent() {
      return receivingSilent;
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected void channelRead0(ChannelHandlerContext channelhandlercontext, Object object) throws Exception {
      this.channelRead0(channelhandlercontext, (Packet<?>)object);
   }
}
