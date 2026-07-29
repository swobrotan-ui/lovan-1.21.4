package module;

import enum.Category;
import event.SpeedEvent;
import setting.BooleanSetting;

public class NoSlowModule extends Module {
   private final BooleanSetting onlyOnGroundSetting = new BooleanSetting("Только на земле", "this", false);

   public NoSlowModule() {
      super("НоЗлош", "Отключает замедление при использовании предметов", Category.MOVEMENT);
      this.addSettings(this.onlyOnGroundSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onSpeed(SpeedEvent speedevent) {
      if (this.getClient().player != null && !this.getClient().player.isGliding()) {
         if (!this.onlyOnGroundSetting.getValue() || this.getClient().player.isOnGround()) {
            if (this.getClient().player.getItemUseTime() % 2 == 0) {
               speedevent.setCancelled(true);
            }
         }
      }
   }
}
