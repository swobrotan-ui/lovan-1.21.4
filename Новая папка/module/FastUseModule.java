package module;

import enum.Category;
import net.minecraft.client.MinecraftClient;
import util.UnsafeFieldAccessor;

public class FastUseModule extends Module {
   private UnsafeFieldAccessor<Integer> aI;

   public FastUseModule() {
      super("ФазтУзе", "Отключение задержки на пкм", Category.PLAYER);
   }

   @Override
   public void onEnable() {
      this.aI = new UnsafeFieldAccessor<Integer>(this.getClient(), MinecraftClient.class, 90);
   }

   @Override
   public void onDisable() {
      this.aI = null;
   }

   @Override
   public void onEndTick() {
      if (this.getPlayer() != null && this.aI != null) {
         this.aI.setInt(0);
      }
   }
}
