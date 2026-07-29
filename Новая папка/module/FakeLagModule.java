package module;

import enum.Category;

public class FakeLagModule extends PacketModule {
   public FakeLagModule() {
      super("ФакеЛаг", "Задерживает отправку пакетов", Category.MOVEMENT);
      this.addSettings(this.eu, this.showServerPosSetting);
   }

   @Override
   protected boolean shouldDelay() {
      return true;
   }
}
