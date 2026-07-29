package util;

import enum.PacketDirection;
import event.PacketEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

public class TPSTracker {
   private static final TPSTracker INSTANCE = new TPSTracker();
   private static final int MAX_SAMPLES = 20;
   private static final long DEFAULT_INTERVAL = 1000L;
   private static final float MAX_MULTIPLIER = 3.0F;
   private final ArrayDeque<Float> tpsSamples = new ArrayDeque<Float>(20);
   private long lastPacketTime;
   private long tickInterval = 1000L;
   private float currentTps = 20.0F;

   public static TPSTracker getInstance() {
      return INSTANCE;
   }

   public float getTps() {
      return round(xw.h(this.currentTps, 0.0F, 20.0F));
   }

   public float c() {
      return this.getTps();
   }

   public float d() {
      return this.getTps();
   }

   public float getTickMultiplier() {
      if (this.tickInterval <= 0L) {
         return 1.0F;
      } else {
         float f = (float)this.tickInterval / 1000.0F;
         return xw.h(f, 1.0F, 3.0F);
      }
   }

   public int adjustForTps(int i) {
      return i <= 0 ? 0 : Math.max(1, Math.round(i * this.getTickMultiplier()));
   }

   public long adjustForTps(long i) {
      return i <= 0L ? 0L : Math.max(1L, (long)Math.round((float)i * this.getTickMultiplier()));
   }

   public static float round(double d0) {
      BigDecimal bigdecimal = new BigDecimal(d0);
      bigdecimal = bigdecimal.setScale(2, RoundingMode.HALF_UP);
      return bigdecimal.floatValue();
   }

   public void onPacket(PacketEvent packetevent) {
      if (packetevent.getType() == PacketDirection.RECEIVE && packetevent.getPacket() instanceof WorldTimeUpdateS2CPacket) {
         long i = System.currentTimeMillis();
         if (this.lastPacketTime != 0L) {
            long j = i - this.lastPacketTime;
            this.tickInterval = Math.max(250L, Math.min(10000L, j));
            if (this.tpsSamples.size() >= 20) {
               this.tpsSamples.poll();
            }

            this.tpsSamples.add(20.0F * (1000.0F / (float)this.tickInterval));
            float f = 0.0F;

            for (Float f1 : this.tpsSamples) {
               f += xw.h(f1, 0.0F, 20.0F);
            }

            if (!this.tpsSamples.isEmpty()) {
               this.currentTps = f / this.tpsSamples.size();
            }
         }

         this.lastPacketTime = i;
      }
   }

   public int getPing() {
      if (MinecraftClient.getInstance().getNetworkHandler() != null && MinecraftClient.getInstance().player != null) {
         PlayerListEntry playerlistentry = MinecraftClient.getInstance().getNetworkHandler().getPlayerListEntry(MinecraftClient.getInstance().player.getUuid());
         return playerlistentry == null ? 0 : playerlistentry.getLatency();
      } else {
         return 0;
      }
   }
}
