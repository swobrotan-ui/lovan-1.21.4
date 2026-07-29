package module;

import enum.Category;
import setting.BooleanSetting;
import setting.SliderSetting;

public class TabTweaksModule extends Module {
   private BooleanSetting highlightFriendSetting = new BooleanSetting("Подсвечивать друга", "Подсвечивает друзей в табе другим цветом", true);
   private BooleanSetting optimizationSetting = new BooleanSetting("Оптимизация", "Скрывает лишнюю информацию сервера в табе для оптимизации", false);
   private BooleanSetting tabAnimationSetting = new BooleanSetting("Анимация таба", "Включает анимацию появления и обновления таба", true);
   private SliderSetting animationSpeedSetting = new SliderSetting("Скорость анимации", "Скорость проигрывания анимации таба", 2.2F, 1.0, 10.0, 0.5);
   private BooleanSetting showPingSetting = new BooleanSetting("Показывать пинг", "Показывает числовой пинг вместо иконок", false);
   private BooleanSetting customColumnsSetting = new BooleanSetting("Свои столбцы", "Использовать кастомное количество столбцов", false);
   private SliderSetting columnCountSetting = new SliderSetting("Количество столбцов", "Количество столбцов в табе", 4.0, 1.0, 10.0, 1.0);

   public TabTweaksModule() {
      super("ТабТшеакз", "Утилита для таба", Category.VISUAL);
      this.animationSpeedSetting.setVisibilitySupplier(this.tabAnimationSetting::getValue);
      this.columnCountSetting.setVisibilitySupplier(this.customColumnsSetting::getValue);
      this.addSettings(
         this.highlightFriendSetting,
         this.tabAnimationSetting,
         this.animationSpeedSetting,
         this.optimizationSetting,
         this.showPingSetting,
         this.customColumnsSetting,
         this.columnCountSetting
      );
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public BooleanSetting a() {
      return this.highlightFriendSetting;
   }

   public BooleanSetting b() {
      return this.optimizationSetting;
   }

   public BooleanSetting c() {
      return this.tabAnimationSetting;
   }

   public SliderSetting d() {
      return this.animationSpeedSetting;
   }

   public BooleanSetting e() {
      return this.showPingSetting;
   }

   public BooleanSetting f() {
      return this.customColumnsSetting;
   }

   public SliderSetting g() {
      return this.columnCountSetting;
   }
}
