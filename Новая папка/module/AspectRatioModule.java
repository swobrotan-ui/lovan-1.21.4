package module;

import enum.Category;
import setting.SliderSetting;

public class AspectRatioModule extends Module {
   public static SliderSetting stretchStrengthSetting = new SliderSetting("Сила растяга", "", 1.0, 0.1, 10.0, 0.1);

   public AspectRatioModule() {
      super("АзпестРатио", "Позволяет растягивать экран", Category.VISUAL);
      this.addSetting(stretchStrengthSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public static SliderSetting a() {
      return stretchStrengthSetting;
   }
}
