package module;

import enum.Category;
import setting.BooleanSetting;
import setting.SliderSetting;

public class PlayerScalerModule extends Module {
   private BooleanSetting proportionalSetting = new BooleanSetting("Пропорционально", "Масштабировать по всем осям одинаково", true);
   private SliderSetting sizeSetting = new SliderSetting("Размер", "Пропорциональный размер игрока", 0.5, 0.1, 3.0, 0.05);
   private SliderSetting sizeXSetting = new SliderSetting("Размер X", "Масштаб по оси X", 0.5, 0.1, 3.0, 0.05);
   private SliderSetting sizeYSetting = new SliderSetting("Размер Y", "Масштаб по оси Y", 0.5, 0.1, 3.0, 0.05);
   private SliderSetting sizeZSetting = new SliderSetting("Размер Z", "Масштаб по оси Z", 0.5, 0.1, 3.0, 0.05);

   public PlayerScalerModule() {
      super("ПлайерЗсалер", "Изменяет размер других игроков", Category.RENDER);
      this.sizeSetting.setVisibilitySupplier(this.proportionalSetting::getValue);
      this.sizeXSetting.setVisibilitySupplier(() -> {
         return !this.proportionalSetting.getValue();
      });
      this.sizeYSetting.setVisibilitySupplier(() -> {
         return !this.proportionalSetting.getValue();
      });
      this.sizeZSetting.setVisibilitySupplier(() -> {
         return !this.proportionalSetting.getValue();
      });
      this.addSettings(this.proportionalSetting, this.sizeSetting, this.sizeXSetting, this.sizeYSetting, this.sizeZSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public BooleanSetting a() {
      return this.proportionalSetting;
   }

   public SliderSetting b() {
      return this.sizeSetting;
   }

   public SliderSetting c() {
      return this.sizeXSetting;
   }

   public SliderSetting d() {
      return this.sizeYSetting;
   }

   public SliderSetting e() {
      return this.sizeZSetting;
   }
}
