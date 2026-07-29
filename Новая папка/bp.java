import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.math.BlockPos;

public class bp {
   private static final MinecraftClient KF = MinecraftClient.getInstance();
   private static final float mh = -0.15F;
   private static final float Cq = -0.01F;
   private static final double CE = 0.4;
   private static final float EV = 0.8F;
   private static final float abp = 0.8F;

   public static boolean a() {
      return KF.player == null ? false : b(KF.player.getMainHandStack()) || b(KF.player.getOffHandStack());
   }

   public static boolean b(ItemStack itemstack) {
      if (itemstack.isEmpty()) {
         return false;
      } else {
         Item item = itemstack.getItem();
         return item instanceof SwordItem || item instanceof AxeItem || item instanceof TridentItem || item instanceof BowItem || item instanceof CrossbowItem;
      }
   }

   public static boolean c(String s, boolean flag) {
      return e(s, flag, 0.0F, true);
   }

   public static boolean d(String s, boolean flag, float f) {
      return e(s, flag, f, true);
   }

   public static boolean e(String s, boolean flag, float f, boolean flag1) {
      if (KF.player != null && flag) {
         float f1 = KF.player.getAttackCooldownProgress(f);
         if (f1 < 0.8F) {
            return false;
         } else {
            boolean flag2 = f();
            if ("Только криты".equals(s)) {
               return h() || flag2;
            } else if (!h() && KF.player.getVelocity().y > 0.0) {
               return false;
            } else {
               return KF.player.isOnGround() ? f1 >= 0.8F && flag1 : h() || flag2;
            }
         }
      } else {
         return false;
      }
   }

   public static boolean f() {
      if (KF.player == null) {
         return false;
      } else if (!KF.player.isOnGround() && !KF.player.isInLava() && !KF.player.isTouchingWater() && !KF.player.hasVehicle()) {
         double d0 = KF.player.getVelocity().y;
         if (g() && d0 < -0.01F) {
            double d1 = KF.player.getY() - (KF.player.getBlockPos().down().getY() + 1.0);
            if (d1 > 0.4) {
               return true;
            }
         }

         return d0 < -0.15F;
      } else {
         return false;
      }
   }

   public static boolean g() {
      if (KF.player != null && KF.world != null) {
         BlockPos blockpos = KF.player.getBlockPos().up(2);
         BlockState blockstate = KF.world.getBlockState(blockpos);
         return !blockstate.isAir() && blockstate.isFullCube(KF.world, blockpos);
      } else {
         return false;
      }
   }

   public static boolean h() {
      return KF.player == null
         ? false
         : KF.player.isTouchingWater()
            || KF.player.isSwimming()
            || KF.player.isGliding()
            || KF.player.isClimbing()
            || KF.player.isInsideWall()
            || KF.player.isInLava()
            || KF.player.hasVehicle();
   }

   public static boolean i(LivingEntity livingentity) {
      return !livingentity.isBlocking() ? false : livingentity.getActiveItem().getItem() == Items.SHIELD;
   }

   public static int j() {
      if (KF.player == null) {
         return -1;
      } else {
         for (int i = 0; i < 9; i++) {
            ItemStack itemstack = KF.player.getInventory().getStack(i);
            if (!itemstack.isEmpty() && itemstack.getItem() instanceof AxeItem) {
               return i;
            }
         }

         return -1;
      }
   }
}
