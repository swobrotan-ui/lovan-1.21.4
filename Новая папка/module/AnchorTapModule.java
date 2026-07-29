package module;

import enum.Category;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import setting.SliderSetting;

public class AnchorTapModule extends Module {
   public SliderSetting delaySetting = new SliderSetting("Задержка", "", 1.0, 0.0, 5.0, 1.0);
   private int xp = 0;
   private int sn = -1;
   private int GC = 0;
   private BlockPos ats;

   public AnchorTapModule() {
      super("АнсхорТап", "автоматически заряжает и взрывает якорь", Category.COMBAT);
      this.addSettings(this.delaySetting);
   }

   private int a() {
      for (int i = 0; i < 9; i++) {
         if (this.getPlayer().getInventory().getStack(i).getItem() == Items.GLOWSTONE) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public void onStartTick() {
      if (this.hasPlayerAndWorld()) {
         if (this.getClient().crosshairTarget instanceof BlockHitResult blockhitresult) {
            switch (this.xp) {
               case 0:
                  if (this.getWorld().getBlockState(blockhitresult.getBlockPos()).getBlock() != Blocks.RESPAWN_ANCHOR) {
                     return;
                  } else {
                     if (this.a() == -1) {
                        return;
                     }

                     this.ats = blockhitresult.getBlockPos();
                     this.sn = this.getPlayer().getInventory().selectedSlot;
                     this.xp = 1;
                     return;
                  }
               case 1:
                  int i = this.a();
                  if (i == -1) {
                     this.c();
                     return;
                  }

                  this.getPlayer().getInventory().selectedSlot = i;
                  this.xp = 2;
                  this.GC = 0;
                  return;
               case 2:
                  if (this.b(blockhitresult)) {
                     return;
                  }

                  this.xp = 3;
                  return;
               case 3:
                  if (this.sn != -1) {
                     this.getPlayer().getInventory().selectedSlot = this.sn;
                  }

                  this.xp = 4;
                  this.GC = 0;
                  return;
               case 4:
                  if (this.b(blockhitresult)) {
                     return;
                  } else {
                     this.c();
                  }
            }
         } else {
            if (this.xp != 0) {
               this.c();
            }
         }
      }
   }

   private boolean b(BlockHitResult blockhitresult) {
      if (this.GC < this.delaySetting.getFloatValue()) {
         this.GC++;
         return true;
      } else if (!blockhitresult.getBlockPos().equals(this.ats)) {
         this.c();
         return true;
      } else if (this.getWorld().getBlockState(this.ats).getBlock() != Blocks.RESPAWN_ANCHOR) {
         this.c();
         return true;
      } else {
         this.getInteractionManager().interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult);
         this.getClientPlayer().swingHand(Hand.MAIN_HAND);
         return false;
      }
   }

   private void c() {
      this.xp = 0;
      this.sn = -1;
      this.ats = null;
      this.GC = 0;
   }

   @Override
   public void onEnable() {
      this.c();
   }

   @Override
   public void onDisable() {
      this.c();
   }
}
