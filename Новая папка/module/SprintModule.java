package module;

import core.ClientMain;
import enum.Category;

public class SprintModule extends Module {
   private boolean sprinting = false;

   public SprintModule() {
      super("Зпринт", "Автоматически прожимает спринт", Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      this.sprinting = true;
   }

   @Override
   public void onDisable() {
      this.sprinting = false;
   }

   @Override
   public void onEndTick() {
      if (this.shouldStopSprinting()) {
         this.getOptions().sprintKey.setPressed(false);
         this.getClientPlayer().setSprinting(false);
      } else if (!this.canSprint()) {
         this.getOptions().sprintKey.setPressed(false);
         this.getClientPlayer().setSprinting(false);
      } else {
         this.getOptions().sprintKey.setPressed(true);
         this.getClientPlayer().setSprinting(true);
      }
   }

   private boolean shouldStopSprinting() {
      AuraModule auramodule = ClientMain.getInstance().getModuleManager().<AuraModule>getModule(AuraModule.class);
      TriggerBotModule triggerbotmodule = ClientMain.getInstance().getModuleManager().<TriggerBotModule>getModule(TriggerBotModule.class);
      return auramodule != null && auramodule.G() || triggerbotmodule != null && triggerbotmodule.y();
   }

   private boolean canSprint() {
      return this.hasPlayerAndWorld() && this.isSprinting() && this.getOptions().forwardKey.isPressed()
         ? this.getClientPlayer().getHungerManager().getFoodLevel() > 6 || this.getClientPlayer().getAbilities().allowFlying
         : false;
   }

   public boolean isSprinting() {
      return this.sprinting;
   }

   public void setSprinting(boolean flag) {
      this.sprinting = flag;
   }
}
