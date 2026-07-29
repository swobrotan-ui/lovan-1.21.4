package module;

import enum.Category;
import net.minecraft.entity.effect.StatusEffects;
import setting.BooleanSetting;

public class NoOverlayModule extends Module {
   public BooleanSetting fireSetting = new BooleanSetting("Огонь", "Отключает наложение огня на экран", true);
   public BooleanSetting scoreboardSetting = new BooleanSetting("Таблица очков", "Отключает отображение табло справа", true);
   public BooleanSetting hurtShakeSetting = new BooleanSetting("Тряска при уроне", "Отключает тряску при уроне (NоHurtCam)", true);
   public BooleanSetting runShakeSetting = new BooleanSetting("Тряска при беге", "Отключает тряску при беге", true);
   public BooleanSetting wardenDarknessSetting = new BooleanSetting("Тьма вардена", "Отключает эффект тьмы от вардена", true);
   public BooleanSetting nauseaSetting = new BooleanSetting("Тошнота", "Отключает эффект тошноты", true);
   public BooleanSetting attackIndicatorSetting = new BooleanSetting("Индикатор атаки", "Отключает индикатор атаки", true);
   public BooleanSetting effectsSetting = new BooleanSetting("Эффекты", "Отключает отображение полосы эффектов", true);
   public BooleanSetting bossBarSetting = new BooleanSetting("Полоса босса", "Отключает отображение полосы босса", true);
   public BooleanSetting bubblesSetting = new BooleanSetting("Пузыри", "Отключает отображение пузырей", true);
   public BooleanSetting glowSetting = new BooleanSetting("Свечение", "Отключает эффект свечения у игроков. Влияет на PlаyerOutlines", false);
   public BooleanSetting rainSetting = new BooleanSetting("Дождь", "Отключает визуальный эффект дождя", false);
   public BooleanSetting vignetteSetting = new BooleanSetting("Виньетка", "Отключает визуальный эффект виньетки", false);

   public NoOverlayModule() {
      super("НоОверлай", "Отключает рендер на экране", Category.VISUAL);
      this.addSettings(
         this.fireSetting,
         this.scoreboardSetting,
         this.hurtShakeSetting,
         this.runShakeSetting,
         this.wardenDarknessSetting,
         this.nauseaSetting,
         this.attackIndicatorSetting,
         this.effectsSetting,
         this.bossBarSetting,
         this.bubblesSetting,
         this.glowSetting,
         this.rainSetting,
         this.wardenDarknessSetting
      );
   }

   @Override
   public void onRenderHand() {
      if (this.isEnabled() && this.getClientPlayer() != null) {
         if (this.fireSetting.getValue()) {
            this.getClientPlayer().setOnFire(false);
         }

         if (this.rainSetting.getValue() && this.getWorld() != null) {
            this.getWorld().setRainGradient(0.0F);
            this.getWorld().setThunderGradient(0.0F);
         }

         if (this.wardenDarknessSetting.getValue() && this.getClientPlayer().hasStatusEffect(StatusEffects.DARKNESS)) {
            this.getClientPlayer().removeStatusEffect(StatusEffects.DARKNESS);
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
