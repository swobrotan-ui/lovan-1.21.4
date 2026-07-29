package module;

import enum.Category;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import setting.SliderSetting;

public class NoWebModule extends Module {
   private SliderSetting speedSetting = new SliderSetting("Скорость", "Скорость перемещения", 0.6, 0.0, 1.0, 0.05, "", 1);

   public NoWebModule() {
      super("НоШеб", "Позволяет ходить через паутину", Category.MOVEMENT);
      this.addSetting(this.speedSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onEndTick() {
      if (this.hasPlayerAndWorld() && this.b()) {
         double[] adouble = this.a(this.speedSetting.getDoubleValue());
         this.getPlayer().setVelocity(adouble[0], 0.0, adouble[1]);
         if (this.getOptions().jumpKey.isPressed()) {
            this.getPlayer().setVelocity(this.getPlayer().getVelocity().add(0.0, this.speedSetting.getDoubleValue(), 0.0));
         }

         if (this.getOptions().sneakKey.isPressed()) {
            this.getPlayer().setVelocity(this.getPlayer().getVelocity().add(0.0, -this.speedSetting.getDoubleValue(), 0.0));
         }
      }
   }

   public double[] a(double d0) {
      float f = this.getClientPlayer().input.movementForward;
      float f1 = this.getClientPlayer().input.movementSideways;
      float f2 = this.getClientPlayer().getYaw();
      if (f != 0.0F) {
         if (f1 > 0.0F) {
            f2 += f > 0.0F ? -45.0F : 45.0F;
         } else if (f1 < 0.0F) {
            f2 += f > 0.0F ? 45.0F : -45.0F;
         }

         f1 = 0.0F;
         if (f > 0.0F) {
            f = 1.0F;
         } else if (f < 0.0F) {
            f = -1.0F;
         }
      }

      double d1 = Math.sin(Math.toRadians(f2 + 90.0F));
      double d2 = Math.cos(Math.toRadians(f2 + 90.0F));
      double d3 = f * d0 * d2 + f1 * d0 * d1;
      double d4 = f * d0 * d1 - f1 * d0 * d2;
      return new double[]{d3, d4};
   }

   public boolean b() {
      Box box = this.getPlayer().getBoundingBox();
      BlockPos blockpos = BlockPos.ofFloored(this.getPlayer().getPos());

      for (int i = blockpos.getX() - 2; i <= blockpos.getX() + 2; i++) {
         for (int j = blockpos.getY() - 1; j <= blockpos.getY() + 4; j++) {
            for (int k = blockpos.getZ() - 2; k <= blockpos.getZ() + 2; k++) {
               BlockPos blockpos1 = new BlockPos(i, j, k);
               if (box.intersects(new Box(blockpos1)) && this.getWorld().getBlockState(blockpos1).getBlock() == Blocks.COBWEB) {
                  return true;
               }
            }
         }
      }

      return false;
   }
}
