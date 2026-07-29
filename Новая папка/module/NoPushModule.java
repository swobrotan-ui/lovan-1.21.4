package module;

import core.ClientMain;
import enum.Category;

public class NoPushModule extends Module {
   public NoPushModule() {
      super("НоПузх", "Отключает коллизию", Category.PLAYER);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onPush() {
      if (this.getClientPlayer() != null && ClientMain.getInstance().getModuleManager().<NoPushModule>getModule(NoPushModule.class).isEnabled()) {
         this.getClientPlayer().noClip = true;
      }
   }
}
