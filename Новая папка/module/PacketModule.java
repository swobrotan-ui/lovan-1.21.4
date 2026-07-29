package module;

import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import event.UseItemEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeCategoryOptionsC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import setting.BooleanSetting;
import setting.RangeSetting;

public abstract class PacketModule extends Module {
   protected final RangeSetting eu = new RangeSetting("Задержка", "Диапазон заморозки пакетов (мс)", 200.0, 400.0, 50.0, 1000.0, 10.0, "мс", 0);
   protected final BooleanSetting showServerPosSetting = new BooleanSetting("Показать серверную позицию", "Отображает вашу модельку на сервере", true);
   private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<Packet<?>>();
   private long lastFlushTime = 0L;
   private long currentDelay = 300L;
   private boolean wasActive = false;
   protected final ki ayc = new ki();

   protected PacketModule(String s, String s1, Category category) {
      super(s, s1, category);
   }

   protected abstract boolean shouldDelay();

   protected void onFlush() {
   }

   @Override
   public void onEnable() {
      this.packetQueue.clear();
      this.lastFlushTime = System.currentTimeMillis();
      this.currentDelay = this.eu.getRandomLong();
      this.wasActive = false;
      this.ayc.h();
   }

   @Override
   public void onDisable() {
      this.flushPackets();
      this.ayc.h();
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld()) {
         boolean flag = this.shouldDelay();
         if (this.wasActive && !flag) {
            this.flushPackets();
            this.onFlush();
         }

         this.wasActive = flag;
         if (!flag) {
            this.ayc.h();
         } else {
            long i = System.currentTimeMillis();
            if (i - this.lastFlushTime >= this.currentDelay) {
               this.flushPackets();
               this.lastFlushTime = i;
               this.currentDelay = this.eu.getRandomLong();
            }
         }
      }
   }

   @Override
   public void onUseItem(UseItemEvent useitemevent) {
      this.flushPackets();
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (!this.isNotInWorld() && this.shouldDelay()) {
         if (packetevent.getType() == PacketDirection.SEND) {
            Packet packet = packetevent.getPacket();
            if (this.shouldPassthrough(packet)) {
               return;
            }

            if (!this.ayc.i() && packet instanceof PlayerMoveC2SPacket) {
               this.ayc.c(this.getClientPlayer());
            }

            this.packetQueue.add(packet);
            packetevent.setCancelled(true);
         }
      }
   }

   private boolean shouldPassthrough(Packet<?> packet) {
      if (packet instanceof PlayerInteractEntityC2SPacket
         || packet instanceof PlayerActionC2SPacket
         || packet instanceof PlayerInteractBlockC2SPacket
         || packet instanceof PlayerInteractItemC2SPacket
         || packet instanceof HandSwingC2SPacket) {
         this.flushPackets();
         return true;
      } else if (packet instanceof ClickSlotC2SPacket
         || packet instanceof CloseHandledScreenC2SPacket
         || packet instanceof ButtonClickC2SPacket
         || packet instanceof CreativeInventoryActionC2SPacket
         || packet instanceof SelectMerchantTradeC2SPacket
         || packet instanceof RenameItemC2SPacket) {
         this.flushPackets();
         return true;
      } else if (packet instanceof ChatMessageC2SPacket || packet instanceof CommandExecutionC2SPacket) {
         this.flushPackets();
         return true;
      } else if (packet instanceof BookUpdateC2SPacket || packet instanceof UpdateSignC2SPacket) {
         this.flushPackets();
         return true;
      } else if (packet instanceof ClientStatusC2SPacket || packet instanceof UpdatePlayerAbilitiesC2SPacket) {
         this.flushPackets();
         return true;
      } else if (packet instanceof SpectatorTeleportC2SPacket || packet instanceof TeleportConfirmC2SPacket) {
         this.flushPackets();
         return true;
      } else if (!(packet instanceof CraftRequestC2SPacket)
         && !(packet instanceof RecipeBookDataC2SPacket)
         && !(packet instanceof RecipeCategoryOptionsC2SPacket)) {
         return false;
      } else {
         this.flushPackets();
         return true;
      }
   }

   protected void flushPackets() {
      while (!this.packetQueue.isEmpty()) {
         Packet packet = this.packetQueue.poll();
         if (packet != null) {
            PacketEvent.sendSilent(packet);
         }
      }

      this.ayc.h();
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && this.showServerPosSetting.getValue() && this.ayc.i() && !this.getClient().options.getPerspective().isFirstPerson()) {
         this.ayc.g(worldrendercontext);
      }
   }

   @Override
   public void onServerDisconnect() {
      this.packetQueue.clear();
      this.ayc.h();
   }
}
