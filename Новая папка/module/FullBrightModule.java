package module;

import enum.Category;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class FullBrightModule extends Module {
   public FullBrightModule() {
      super("ФуллБригхт", "Даёт вам эффект ночного виденья", Category.VISUAL);
   }

   @Override
   public void onEndTick() {
      try {
         if (this.getClient() != null && this.getClient().player != null) {
            this.getClient().player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
      try {
         if (this.getClient() != null && this.getClient().player != null) {
            this.getClient().player.removeStatusEffect(StatusEffects.NIGHT_VISION);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }
}
