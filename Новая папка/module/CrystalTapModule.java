package module;

import enum.Category;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import setting.BooleanSetting;
import setting.SliderSetting;

public class CrystalTapModule extends Module {
   public BooleanSetting autoPlaceCrystalSetting = new BooleanSetting("Авт. ставить кристал", "", true);
   public SliderSetting placeDelaySetting = new SliderSetting("Задержка ставку", "", 0.0, 0.0, 5.0, 1.0);
   public SliderSetting attackDelaySetting = new SliderSetting("Задержка на атаку", "", 0.0, 0.0, 5.0, 1.0);
   int afb;
   int Uo;

   public CrystalTapModule() {
      super("СрйзталТап", "автоматически ставит / взрывает кристал", Category.COMBAT);
      this.addSettings(this.autoPlaceCrystalSetting, this.placeDelaySetting, this.attackDelaySetting);
   }

   private void a() {
      if (this.getClient().crosshairTarget instanceof BlockHitResult blockhitresult
         && this.getWorld().getBlockState(blockhitresult.getBlockPos()).getBlock() == Blocks.OBSIDIAN) {
         for (Entity entity : this.getClientWorld().getEntities()) {
            if (entity instanceof EndCrystalEntity endcrystalentity && endcrystalentity.getBlockPos().down().equals(blockhitresult.getBlockPos())) {
               return;
            }
         }

         Direction direction = blockhitresult.getSide();
         BlockHitResult blockhitresult1 = new BlockHitResult(blockhitresult.getPos(), direction, blockhitresult.getBlockPos(), true);
         if (this.getPlayer().getMainHandStack().getItem() != Items.END_CRYSTAL) {
            return;
         }

         if (this.Uo >= this.placeDelaySetting.getFloatValue()) {
            this.getInteractionManager().interactBlock(this.getClientPlayer(), Hand.MAIN_HAND, blockhitresult1);
            this.getClientPlayer().swingHand(Hand.MAIN_HAND);
            this.Uo = 0;
            return;
         }

         this.Uo++;
      }
   }

   @Override
   public void onStartTick() {
      if (this.autoPlaceCrystalSetting.getValue()) {
         this.a();
      }

      if (this.getClient().crosshairTarget instanceof EntityHitResult entityhitresult
         && entityhitresult.getEntity() instanceof EndCrystalEntity endcrystalentity) {
         if (endcrystalentity.isRemoved() || !endcrystalentity.isAlive()) {
            return;
         }

         if (this.afb >= this.attackDelaySetting.getFloatValue()) {
            this.getInteractionManager().attackEntity(this.getPlayer(), endcrystalentity);
            this.getClientPlayer().swingHand(Hand.MAIN_HAND);
            this.afb = 0;
            return;
         }

         this.afb++;
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }
}
