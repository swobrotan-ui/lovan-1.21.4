package module;

import enum.Category;
import setting.ActionKeySetting;
import setting.SliderSetting;

public class EmergencyStopModule extends Module {
   private ActionKeySetting stopKeySetting = new ActionKeySetting("Клавиша остановки", "Кнопка для остановки", 90, this::a);
   private SliderSetting changeYSetting = new SliderSetting("Менять Y", "Менять Y (Лучше не менять)", 0.0, -5.0, 5.0, 1.0);

   public EmergencyStopModule() {
      super("ЕмергенсйЗтоп", "По бинду останавливает вас.", Category.MOVEMENT);
      this.addSettings(this.stopKeySetting, this.changeYSetting);
   }

   private void a() {
      if (!this.isNotInWorld()) {
         this.getPlayer().setVelocity(0.0, this.changeYSetting.getValue(), 0.0);
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }
}
