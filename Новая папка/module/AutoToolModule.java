package module;

import enum.Category;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import setting.BooleanSetting;

public class AutoToolModule extends Module {
   public BooleanSetting dontBreakToolSetting = new BooleanSetting("Не ломать инструмент", "", true);
   private int PI = -1;

   public AutoToolModule() {
      super("АутоТоол", "Автоматически меняет инструмент", Category.PLAYER);
      this.addSettings(this.dontBreakToolSetting);
   }

   @Override
   public void onEnable() {
      this.PI = -1;
   }

   @Override
   public void onDisable() {
      if (this.hasPlayerAndWorld() && this.PI != -1) {
         this.getInventory().selectedSlot = this.PI;
      }

      this.PI = -1;
   }

   @Override
   public void onEndTick() {
      if (!this.hasPlayerAndWorld() || this.getPlayer().isCreative()) {
         this.PI = -1;
      } else if (this.getInteractionManager().isBreakingBlock()) {
         if (this.PI == -1) {
            this.PI = this.getClientPlayer().getInventory().selectedSlot;
         }

         int i = this.a();
         if (i != -1 && i != this.getClientPlayer().getInventory().selectedSlot) {
            this.getClientPlayer().getInventory().selectedSlot = i;
         }
      } else {
         if (this.PI != -1) {
            this.getClientPlayer().getInventory().selectedSlot = this.PI;
            this.PI = -1;
         }
      }
   }

   private int a() {
      if (!this.hasPlayerAndWorld()) {
         return -1;
      } else {
         BlockHitResult blockhitresult = (BlockHitResult)this.getClient().crosshairTarget;
         if (blockhitresult instanceof BlockHitResult) {
            Block block = this.getWorld().getBlockState(blockhitresult.getBlockPos()).getBlock();
            return this.b(block);
         } else {
            return -1;
         }
      }
   }

   private int b(Block block) {
      int i = -1;
      float f = 1.0F;

      for (int j = 0; j < 9; j++) {
         ItemStack itemstack = this.getInventory().getStack(j);
         float f1 = this.c(j, block);
         if (this.dontBreakToolSetting.getValue() && itemstack.isDamageable()) {
            int k = itemstack.getMaxDamage() - itemstack.getDamage();
            if (k <= 1) {
               continue;
            }
         }

         if (f1 > f) {
            f = f1;
            i = j;
         }
      }

      return i;
   }

   private float c(int i, Block block) {
      if (!this.hasPlayerAndWorld()) {
         return 0.0F;
      } else {
         ItemStack itemstack = this.getInventory().getStack(i);
         return itemstack.getMiningSpeedMultiplier(block.getDefaultState());
      }
   }
}
