import java.util.List;
import module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class wu {
   public static boolean a(Vec3d vec3d, Camera camera, float f) {
      Vec3d vec3d1 = camera.getPos();
      Vec3d vec3d2 = vec3d.subtract(vec3d1).normalize();
      Vec3d vec3d3 = Vec3d.fromPolar(camera.getPitch(), camera.getYaw());
      double d0 = vec3d2.dotProduct(vec3d3);
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      int i = minecraftclient.getWindow().getFramebufferWidth();
      int j = minecraftclient.getWindow().getFramebufferHeight();
      double d1 = j > 0 ? (double)i / j : 1.7777777777777777;
      double d2 = Math.toRadians(f);
      double d3 = 2.0 * Math.atan(Math.tan(d2 / 2.0) * d1);
      double d4 = 2.0 * Math.atan(Math.sqrt(Math.pow(Math.tan(d3 / 2.0), 2.0) + Math.pow(Math.tan(d2 / 2.0), 2.0)));
      double d5 = d4 / 2.0 - Math.toRadians(5.0);
      return d0 >= Math.cos(d5);
   }

   public static boolean b(PlayerEntity playerentity, PlayerEntity playerentity1, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module) {
      if (playerentity == playerentity1) {
         if (!flag) {
            return false;
         }

         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.options.getPerspective() == Perspective.FIRST_PERSON) {
            return false;
         }
      }

      if (!playerentity.isAlive()) {
         return false;
      } else {
         return dr.p(playerentity.getPos(), vec3d, d0) ? false : flag1 || !module.isFriendPlayer(playerentity);
      }
   }

   public static boolean c(
      PlayerEntity playerentity, PlayerEntity playerentity1, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module, Camera camera, float f
   ) {
      if (!b(playerentity, playerentity1, vec3d, d0, flag, flag1, module)) {
         return false;
      } else if (playerentity == playerentity1) {
         return true;
      } else {
         Vec3d vec3d1 = playerentity.getPos().add(0.0, playerentity.getHeight() + 0.5, 0.0);
         Vec3d vec3d2 = playerentity.getPos().add(0.0, playerentity.getHeight() / 2.0, 0.0);
         return a(vec3d1, camera, f) || a(vec3d2, camera, f);
      }
   }

   public static boolean d(ItemEntity itementity, Vec3d vec3d, double d0, boolean flag, double d1) {
      if (itementity != null && itementity.isAlive()) {
         if (dr.p(itementity.getPos(), vec3d, d0)) {
            return false;
         } else {
            return itementity.getStack().getCount() < d1 ? false : !flag || dr.m(itementity.getStack().getItem());
         }
      } else {
         return false;
      }
   }

   public static int e(List<? extends PlayerEntity> list, PlayerEntity playerentity, Vec3d vec3d, double d0, boolean flag, boolean flag1, Module module) {
      int i = 0;

      for (PlayerEntity playerentity1 : list) {
         if (b(playerentity1, playerentity, vec3d, d0, flag, flag1, module)) {
            i++;
         }
      }

      return i;
   }
}
