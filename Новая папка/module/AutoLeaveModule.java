package module;

import enum.Category;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import setting.BooleanSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class AutoLeaveModule extends Module {
   private final SliderSetting distanceSetting = new SliderSetting(
      "Дистанция", "Минимальная дистанция до игрока для срабатывания", 10.0, 1.0, 150.0, 1.0, " блоков", 0
   );
   private final BooleanSetting crystalsSetting = new BooleanSetting("Кристаллы", "Выходить при обнаружении кристалла рядом", false);
   private final SliderSetting crystalDistanceSetting = new SliderSetting(
      "Дист. кристаллов", "Максимальная дистанция до кристалла", 6.0, 1.0, 12.0, 0.5, " блоков", 1
   );
   private final BooleanSetting tntSetting = new BooleanSetting("ТНТ", "Выходить при обнаружении активированного ТНТ рядом", true);
   private final SliderSetting tntDistanceSetting = new SliderSetting("ТНТ дистанция", "Максимальная дистанция до ТНТ", 10.0, 1.0, 50.0, 1.0, " блоков", 0);
   private final SliderSetting tntFuseSetting = new SliderSetting(
      "ТНТ фьюз", "Максимальный оставшийся фьюз ТНТ для срабатывания", 40.0, 1.0, 80.0, 1.0, " тиков", 0
   );
   private final ListSetting exitModeSetting = new ListSetting(
      "Режим выхода", "Способ выхода с сервера", List.<String>of("Выход", "/hub"), List.<String>of("Выход"), false
   );
   private boolean Pd = false;
   private String Mr = "";

   public AutoLeaveModule() {
      super("АутоЛеаве", "Автоматически выходит с сервера при приближении игрока, кристалла или ТНТ", Category.PLAYER);
      this.crystalDistanceSetting.setVisibilitySupplier(this.crystalsSetting::getValue);
      this.tntDistanceSetting.setVisibilitySupplier(this.tntSetting::getValue);
      this.tntFuseSetting.setVisibilitySupplier(this.tntSetting::getValue);
      this.addSettings(
         this.distanceSetting,
         this.crystalsSetting,
         this.crystalDistanceSetting,
         this.tntSetting,
         this.tntDistanceSetting,
         this.tntFuseSetting,
         this.exitModeSetting
      );
   }

   @Override
   public String getDisplayName() {
      if (this.Mr.isEmpty()) {
         return super.getDisplayName();
      } else {
         String s = this.Mr;
         String s1 = super.getDisplayName();
         return s1 + " §7[" + s + "]";
      }
   }

   @Override
   public void onEnable() {
      this.Pd = false;
      this.Mr = "";
   }

   @Override
   public void onDisable() {
      this.Pd = false;
   }

   @Override
   public void onEndTick() {
      if (this.isEnabled() && this.hasPlayerAndWorld() && !this.Pd) {
         PlayerEntity playerentity = this.a();
         if (playerentity != null) {
            double d0 = this.getPlayer().distanceTo(playerentity);
            if (d0 <= this.distanceSetting.getValue()) {
               int i = (int)Math.round(d0);
               String s = playerentity.getName().getString();
               this.Mr = s + " " + i + "м";
               this.b();
               this.Pd = true;
               return;
            }
         }

         if (this.crystalsSetting.getValue() && dz.a(this.getWorld(), this.getPlayer(), this.crystalDistanceSetting.getValue())) {
            this.Mr = "Кристалл";
            this.b();
            this.Pd = true;
         } else {
            if (this.tntSetting.getValue() && dz.b(this.getWorld(), this.getPlayer(), this.tntDistanceSetting.getValue(), (int)this.tntFuseSetting.getValue())) {
               this.Mr = "ТНТ";
               this.b();
               this.Pd = true;
            }
         }
      }
   }

   private PlayerEntity a() {
      if (this.getWorld() != null && this.getPlayer() != null) {
         PlayerEntity playerentity = null;
         double d0 = Double.MAX_VALUE;

         for (PlayerEntity playerentity1 : this.getWorld().getPlayers()) {
            if (playerentity1 != this.getPlayer() && !this.isFriendPlayer(playerentity1)) {
               double d1 = this.getPlayer().distanceTo(playerentity1);
               if (d1 < d0) {
                  d0 = d1;
                  playerentity = playerentity1;
               }
            }
         }

         return playerentity;
      } else {
         return null;
      }
   }

   private void b() {
      String s = this.exitModeSetting.getFirst();
      if ("Выход".equals(s)) {
         if (this.getNetworkHandler() != null) {
            this.getNetworkHandler().getConnection().disconnect(Text.of("Авто-лив"));
         }
      } else if ("/hub".equals(s) && this.getPlayer() != null) {
         this.getNetworkHandler().sendChatCommand("hub");
      }

      this.setEnabled(false);
   }

   @Override
   public void onServerJoin() {
      this.Pd = false;
      this.Mr = "";
   }

   @Override
   public void onServerDisconnect() {
      this.Pd = false;
   }
}
