package util;

import data.Angle;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationUtil {
   private static final Random random = new Random();
   private static final MinecraftClient mc = MinecraftClient.getInstance();

   public static Angle getRotation(Vec3d vec3d) {
      double d0 = MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0);
      double d1 = MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(vec3d.y, Math.hypot(vec3d.x, vec3d.z))));
      return new Angle((float)d0, (float)d1);
   }

   public static Angle getDeltaWithJitter(Angle angle, Angle angle1) {
      float f = MathHelper.wrapDegrees(angle1.getYaw() - angle.getYaw());
      float f1 = MathHelper.wrapDegrees(angle1.getPitch() - angle.getPitch());
      if (Math.abs(f) > 5.0F || Math.abs(f1) > 5.0F) {
         float f2 = Math.min(1.0F, (Math.abs(f) + Math.abs(f1)) / 180.0F);
         f += (random.nextFloat() - 0.5F) * 0.3F * f2;
         f1 += (random.nextFloat() - 0.5F) * 0.2F * f2;
      } else if (random.nextFloat() < 0.3F) {
         f += (random.nextFloat() - 0.5F) * 0.15F;
         f1 += (random.nextFloat() - 0.5F) * 0.1F;
      }

      return new Angle(f, f1);
   }

   public static Angle getRotationTo(Vec3d vec3d) {
      if (mc.player == null) {
         return Angle.ZERO;
      } else {
         Vec3d vec3d1 = mc.player.getEyePos();
         if (random.nextFloat() < 0.2F) {
            double d0 = 0.01 + random.nextDouble() * 0.02;
            vec3d1 = vec3d1.add((random.nextDouble() - 0.5) * d0, (random.nextDouble() - 0.5) * d0, (random.nextDouble() - 0.5) * d0);
         }

         return getRotation(vec3d.subtract(vec3d1));
      }
   }

   public static Angle getPlayerRotation() {
      if (mc.player == null) {
         return Angle.ZERO;
      } else {
         float f = mc.player.getYaw();
         float f1 = mc.player.getPitch();
         if (random.nextFloat() < 0.05F) {
            f += (random.nextFloat() - 0.5F) * 0.1F;
            f1 += (random.nextFloat() - 0.5F) * 0.05F;
         }

         return new Angle(f, f1);
      }
   }
}
