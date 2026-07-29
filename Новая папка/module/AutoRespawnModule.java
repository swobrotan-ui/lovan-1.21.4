package module;

import enum.Category;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.player.PlayerEntity;
import setting.BooleanSetting;

public class AutoRespawnModule extends Module {
   private BooleanSetting showDeathCoordsSetting = new BooleanSetting("Показывать координаты смерти", "Отображать координаты смерти", true);
   private long nR = -1L;
   private boolean ke = false;

   public AutoRespawnModule() {
      super("АутоРезпашн", "Автоматически возрождает игрока после смерти", Category.PLAYER);
      this.addSettings(this.showDeathCoordsSetting);
   }

   @Override
   public void onEnable() {
      this.h();
   }

   @Override
   public void onDisable() {
      this.f();
   }

   @Override
   public void onEndTick() {
      if (this.isEnabled() && this.hasPlayerAndWorld()) {
         this.a();
         this.b();
         this.c();
      }
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      if (this.isEnabled() && playerentity == this.getPlayer() && !this.ke) {
         this.d();
      }
   }

   private void a() {
   }

   private void b() {
      if (this.nR != -1L) {
         long i = System.currentTimeMillis();
         if (i >= this.nR) {
            this.e();
            this.f();
         }
      }
   }

   private void c() {
      PlayerEntity playerentity = this.getPlayer();
      if (playerentity != null) {
         if (playerentity.isAlive()) {
            this.ke = false;
         }
      }
   }

   private void d() {
      if (!this.ke) {
         this.ke = true;
         PlayerEntity playerentity = this.getPlayer();
         if (playerentity != null && this.showDeathCoordsSetting.getValue()) {
            int l = (int)playerentity.getX();
            int i = (int)playerentity.getZ();
            int j = (int)playerentity.getY();
            int k = l;
            this.sendChatMessage("Вы умерли на координатах: X=" + k + " Y=" + j + " Z=" + i);
         }

         this.nR = System.currentTimeMillis() + 500L;
      }
   }

   private void e() {
      try {
         if (this.getClient().getNetworkHandler() != null) {
            if (this.getScreen() instanceof DeathScreen) {
               this.getClient().setScreen(null);
            }

            PlayerEntity playerentity = this.getPlayer();
            if (playerentity != null) {
               playerentity.requestRespawn();
            }
         }
      } catch (Exception exception) {
      }
   }

   private void f() {
      this.nR = -1L;
   }

   private void h() {
      this.ke = false;
      this.f();
   }

   @Override
   public void onServerJoin() {
      this.h();
   }

   @Override
   public void onServerDisconnect() {
      this.h();
   }
}
