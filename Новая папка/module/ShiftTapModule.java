package module;

import enum.Category;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class ShiftTapModule extends Module {
   long arX = 0L;
   boolean agd = false;

   public ShiftTapModule() {
      super("ЗхифтТап", "Shift Tap", Category.COMBAT);
   }

   private void a() {
      this.arX = System.currentTimeMillis() + 25L;
      if (!this.agd) {
         this.getOptions().sneakKey.setPressed(true);
         this.agd = true;
      }
   }

   private void b() {
      if (this.agd) {
         this.getOptions().sneakKey.setPressed(false);
         this.agd = false;
      }
   }

   @Override
   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (this.getPlayer() != null) {
         this.a();
      }
   }

   @Override
   public void onStartTick() {
      if (this.getPlayer() != null && !this.getPlayer().isSpectator()) {
         long i = System.currentTimeMillis();
         if (this.agd && i > this.arX) {
            this.b();
         }
      } else {
         this.b();
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }
}
