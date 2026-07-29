package module;

import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import setting.BooleanSetting;

public class AirStuckModule extends Module {
   private static AirStuckModule instance;
   private Vec3d stuckPos = Vec3d.ZERO;
   private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<Packet<?>>();
   private int tickCounter = 0;
   private BooleanSetting cancelMovementSetting = new BooleanSetting("Отменить движение", "Блокирует ввод движения игрока", true);
   private BooleanSetting delayPacketsSetting = new BooleanSetting("Задержка пакетов", "Задерживает пакеты движения для обхода античита", true);

   public AirStuckModule() {
      super("AirStuck", "Замораживает игрока в воздухе", Category.PLAYER);
      instance = this;
      this.addSetting(this.cancelMovementSetting);
      this.addSetting(this.delayPacketsSetting);
   }

   @Override
   public void onEnable() {
      if (!this.isNotInWorld()) {
         this.stuckPos = this.getPlayer().getPos();
         this.packetQueue.clear();
         this.tickCounter = 0;
      }
   }

   @Override
   public void onDisable() {
      this.stuckPos = Vec3d.ZERO;

      while (!this.packetQueue.isEmpty()) {
         Packet packet = this.packetQueue.poll();
         if (packet != null) {
            PacketEvent.sendSilent(packet);
         }
      }

      this.tickCounter = 0;
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld() && this.stuckPos != Vec3d.ZERO) {
         this.getPlayer().setPosition(this.stuckPos.x, this.stuckPos.y, this.stuckPos.z);
         this.getPlayer().setVelocity(Vec3d.ZERO);
         this.getPlayer().fallDistance = 0.0F;
         if (this.cancelMovementSetting.getValue()) {
            this.getClientPlayer().input.movementForward = 0.0F;
            this.getClientPlayer().input.movementSideways = 0.0F;
         }

         if (this.delayPacketsSetting.getValue() && !this.packetQueue.isEmpty()) {
            this.tickCounter++;
            if (this.tickCounter >= 3) {
               Packet packet = this.packetQueue.poll();
               if (packet != null) {
                  PacketEvent.sendSilent(packet);
               }

               this.tickCounter = 0;
            }
         }
      }
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (packetevent.getType() == PacketDirection.SEND) {
         if (this.delayPacketsSetting.getValue()) {
            if (packetevent.getPacket() instanceof PlayerMoveC2SPacket) {
               this.packetQueue.offer(packetevent.getPacket());
               if (this.packetQueue.size() > 20) {
                  this.packetQueue.poll();
               }

               packetevent.setCancelled(true);
            }
         }
      }
   }

   public static AirStuckModule a() {
      return instance;
   }
}
