package module;

import enum.Category;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class SpiderModule extends Module {
   private float adw;
   private float apE;

   public SpiderModule() {
      super("Зпидер", "Позволяет лазить по стенам, для активации нужны блоки в слоте", Category.MOVEMENT);
   }

   @Override
   public void onEnable() {
      if (!this.isNotInWorld()) {
         BlockPos blockpos = this.a();
         if (blockpos != null) {
            Vec3d vec3d = this.getPlayer().getEyePos();
            Vec3d vec3d1 = Vec3d.ofCenter(blockpos);
            double d0 = vec3d1.x - vec3d.x;
            double d1 = vec3d1.z - vec3d.z;
            this.adw = (float)Math.toDegrees(Math.atan2(d1, d0)) - 90.0F;
            this.apE = 75.0F;
         } else {
            this.adw = this.getPlayer().getYaw();
            this.apE = 82.0F;
         }
      }
   }

   @Override
   public void onDisable() {
      if (!this.isNotInWorld()) {
         this.getOptions().jumpKey.setPressed(false);
         this.getOptions().useKey.setPressed(false);
      }
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld()) {
         this.getPlayer().setYaw(this.adw);
         this.getPlayer().setPitch(this.apE);
         this.getPlayer().prevYaw = this.adw;
         this.getPlayer().prevPitch = this.apE;
         this.getOptions().jumpKey.setPressed(true);
         this.getOptions().useKey.setPressed(true);
      }
   }

   private BlockPos a() {
      if (this.getPlayer() != null && this.getWorld() != null) {
         BlockPos blockpos = this.getPlayer().getBlockPos();
         BlockPos blockpos1 = null;
         int i = 0;

         for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {
               if (j != 0 || k != 0) {
                  int l = 0;
                  BlockPos blockpos2 = blockpos.add(j, 0, k);

                  for (int i1 = -1; i1 < 10; i1++) {
                     BlockPos blockpos3 = blockpos2.add(0, i1, 0);
                     if (this.getWorld().getBlockState(blockpos3).isAir()) {
                        break;
                     }

                     l = i1 + 1;
                  }

                  if (l > i) {
                     i = l;
                     blockpos1 = blockpos2.add(0, l - 1, 0);
                  }
               }
            }
         }

         return blockpos1;
      } else {
         return null;
      }
   }
}
