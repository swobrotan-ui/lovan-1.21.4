import core.FriendManager;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public class xk {
   private static final MinecraftClient adE = MinecraftClient.getInstance();

   public static Stream<LivingEntity> a(double d0, boolean flag, double d1) {
      return adE.world != null && adE.player != null ? StreamSupport.<Entity>stream(adE.world.getEntities().spliterator(), false).filter(entity -> {
         return entity instanceof LivingEntity;
      }).<LivingEntity>map(entity -> {
         return (LivingEntity)entity;
      }).filter(livingentity -> {
         return b(livingentity, d0, flag, d1);
      }) : Stream.<LivingEntity>empty();
   }

   public static boolean b(LivingEntity livingentity, double d0, boolean flag, double d1) {
      if (adE.player == null) {
         return false;
      } else if (!flag) {
         return adE.player.distanceTo(livingentity) <= d0;
      } else {
         double d2 = livingentity.getX() - adE.player.getX();
         double d3 = livingentity.getY() - adE.player.getY();
         double d4 = livingentity.getZ() - adE.player.getZ();
         double d5 = d2 * d2 + d4 * d4;
         double d6 = Math.sqrt(d5);
         return d6 <= d0 && Math.abs(d3) <= d1;
      }
   }

   public static boolean c(LivingEntity livingentity, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
      if (adE.player == null
         || livingentity == null
         || livingentity == adE.player
         || livingentity.isDead()
         || !livingentity.isAlive()
         || livingentity.isSpectator()
         || livingentity.getHealth() <= 0.0F) {
         return false;
      } else if (!flag && livingentity.isInvisible()) {
         return false;
      } else if (!flag1 && livingentity.isBlocking()) {
         return false;
      } else if (flag2 && !d(livingentity)) {
         return false;
      } else {
         return livingentity instanceof PlayerEntity
            ? !FriendManager.getInstance().isFriend(livingentity.getName().getString())
            : !flag3 && livingentity instanceof MobEntity;
      }
   }

   public static boolean d(LivingEntity livingentity) {
      for (ItemStack itemstack : livingentity.getArmorItems()) {
         if (!itemstack.isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static boolean e(LivingEntity livingentity, float f) {
      if (adE.player == null) {
         return true;
      } else if (f >= 180.0F) {
         return true;
      } else {
         Vec3d vec3d = livingentity.getPos().add(0.0, livingentity.getHeight() / 2.0F, 0.0).subtract(adE.player.getEyePos()).normalize();
         double d0 = Math.max(-1.0, Math.min(1.0, adE.player.getRotationVec(1.0F).dotProduct(vec3d)));
         return Math.toDegrees(Math.acos(d0)) <= f / 2.0F;
      }
   }

   public static LivingEntity f(List<LivingEntity> list, String s, LivingEntity livingentity, int i, float f) {
      if (list.isEmpty()) {
         return null;
      } else if (adE.player == null) {
         return null;
      } else {
         switch (s) {
            case "Текущая цель":
               if (livingentity != null && i < f && list.contains(livingentity)) {
                  return livingentity;
               }

               return list.stream().min(Comparator.comparingDouble(livingentity1 -> {
                  return adE.player.distanceTo(livingentity1);
               })).orElse(null);
            case "Меньше HP":
               return list.stream().min(Comparator.comparingDouble(LivingEntity::getHealth)).orElse(null);
            default:
               return list.stream().min(Comparator.comparingDouble(livingentity1 -> {
                  return adE.player.distanceTo(livingentity1);
               })).orElse(null);
         }
      }
   }

   public static Vec3d g(LivingEntity livingentity) {
      return new Vec3d(livingentity.getX(), livingentity.getY() + livingentity.getHeight() * 0.5, livingentity.getZ());
   }

   public static boolean h(LivingEntity livingentity) {
      if (livingentity != null && adE.player != null) {
         double d0 = adE.player.distanceTo(livingentity);
         if (d0 <= 1.5) {
            return true;
         } else {
            Vec3d vec3d = livingentity.getVelocity();
            if (vec3d.length() < 0.1) {
               return true;
            } else {
               Vec3d vec3d1 = vec3d.normalize();
               Vec3d vec3d2 = adE.player.getPos().subtract(livingentity.getPos()).normalize();
               return vec3d2.dotProduct(vec3d1) > -0.3;
            }
         }
      } else {
         return false;
      }
   }
}
