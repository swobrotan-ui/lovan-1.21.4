package aim;

import data.Angle;
import java.security.SecureRandom;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import util.RotationUtil;

public class CakeWorldAimMode extends AimMode {
   private final Random random = new Random();
   private final SecureRandom secureRandom = new SecureRandom();
   private long lastUpdateTime = 0L;
   private float lastSpeed = 0.0F;
   private int phaseIndex = 0;
   private final float[] speedHistory = new float[5];
   private int historyIndex = 0;

   public CakeWorldAimMode() {
      super("CakeWorld");
   }

   @Override
   public Angle calculateRotation(Angle angle, Angle angle1, Vec3d vec3d, Entity entity) {
      if (this.random.nextFloat() < 0.02F) {
         return angle;
      } else {
         Angle angle2 = RotationUtil.getDeltaWithJitter(angle, angle1);
         float f = angle2.getYaw();
         float f1 = angle2.getPitch();
         float f2 = (float)Math.hypot(Math.abs(f), Math.abs(f1));
         boolean flag = entity != null && mc.player != null && mc.player.getAttackCooldownProgress(0.0F) >= 0.87F;
         float f3 = 0.0F;
         if (entity != null && mc.player != null) {
            f3 = mc.player.distanceTo(entity);
         }

         float f4 = this.calculateSpeed(f2, entity, flag, f3);
         this.speedHistory[this.historyIndex] = f4;
         this.historyIndex = (this.historyIndex + 1) % this.speedHistory.length;
         float f5 = this.getAverageSpeed();
         float f6 = this.getClampFactor(f2);
         float f7 = Math.abs(f / f2) * 180.0F * f6;
         float f8 = Math.abs(f1 / f2) * 180.0F * f6;
         float f9 = 0.0F;
         float f10 = 0.0F;
         if (!flag) {
            f9 = (float)(this.randomRange(21.0F, 28.2F) * Math.sin(System.currentTimeMillis() / 20.0));
            f10 = (float)(this.randomRange(6.0F, 23.8F) * Math.sin(System.currentTimeMillis() / 22.0));
         }

         if (entity == null) {
            f5 *= 0.85F;
            f9 = 0.0F;
            f10 = 0.0F;
         }

         float f11 = MathHelper.clamp(f, -f7, f7);
         float f12 = MathHelper.clamp(f1, -f8, f8);
         Angle angle3 = this.applyRotation(angle, f11, f12, f5, f9, f10);
         this.lastUpdateTime = System.currentTimeMillis();
         this.lastSpeed = f5;
         this.phaseIndex = (this.phaseIndex + 1) % 6;
         return angle3;
      }
   }

   private float calculateSpeed(float f, Entity entity, boolean flag, float f1) {
      long i = System.currentTimeMillis();
      long j = i - this.lastUpdateTime;

      float f2 = switch (this.phaseIndex) {
         case 0 -> 0.6F + this.random.nextFloat() * 0.4F;
         case 1 -> 0.8F + this.random.nextFloat() * 0.3F;
         case 2 -> 0.5F + this.random.nextFloat() * 0.3F;
         case 3 -> 0.7F + this.random.nextFloat() * 0.5F;
         case 4 -> 0.4F + this.random.nextFloat() * 0.4F;
         case 5 -> 0.9F + this.random.nextFloat() * 0.2F;
         default -> 0.7F + this.random.nextFloat() * 0.4F;
      };
      if (flag) {
         f2 *= 1.0F + this.random.nextFloat() * 0.15F;
      } else {
         f2 *= 0.8F + this.random.nextFloat() * 0.2F;
      }

      if (entity != null) {
         f2 *= 0.9F + this.random.nextFloat() * 0.3F;
      }

      if (f1 > 0.0F && f1 < 1.5F) {
         float f3 = MathHelper.clamp(f1 / 2.0F, 0.4F, 0.9F);
         f2 *= f3;
      }

      if (j < 45L) {
         f2 *= 0.7F + this.random.nextFloat() * 0.4F;
      } else if (j > 300L) {
         f2 *= 1.1F + this.random.nextFloat() * 0.4F;
      }

      if (f > 120.0F) {
         f2 *= 1.3F;
      } else if (f < 15.0F) {
         f2 *= 0.5F + this.random.nextFloat() * 0.4F;
      }

      if (this.lastSpeed > 0.0F) {
         float f4 = f2 - this.lastSpeed;
         if (Math.abs(f4) > 0.35F) {
            f2 = this.lastSpeed + (f4 > 0.0F ? 0.35F : -0.35F);
         }
      }

      return MathHelper.clamp(f2, 0.15F, 1.8F);
   }

   private float getAverageSpeed() {
      float f = 0.0F;
      int i = 0;

      for (float f1 : this.speedHistory) {
         if (f1 > 0.0F) {
            f += f1;
            i++;
         }
      }

      return i > 0 ? f / i : this.lastSpeed;
   }

   private float getClampFactor(float f) {
      if (f < 20.0F) {
         return 0.95F + this.random.nextFloat() * 0.1F;
      } else {
         return f > 90.0F ? 0.8F + this.random.nextFloat() * 0.3F : 0.85F + this.random.nextFloat() * 0.25F;
      }
   }

   private Angle applyRotation(Angle angle, float f, float f1, float f2, float f3, float f4) {
      Angle angle1 = new Angle(angle.getYaw(), angle.getPitch());
      switch (this.phaseIndex) {
         case 0:
            angle1.setYaw(MathHelper.lerp(f2 + this.random.nextFloat() * 0.2F - 0.1F, angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f2 + this.random.nextFloat() * 0.2F - 0.1F, angle.getPitch(), angle.getPitch() + f1) + f4);
            break;
         case 1:
            float f5 = f2 * (1.2F + this.random.nextFloat() * 0.3F);
            angle1.setYaw(MathHelper.lerp(f5, angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f5, angle.getPitch(), angle.getPitch() + f1) + f4);
            break;
         case 2:
            float f6 = f2 * (0.7F + this.random.nextFloat() * 0.3F);
            angle1.setYaw(MathHelper.lerp(f6, angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f6, angle.getPitch(), angle.getPitch() + f1) + f4);
            break;
         case 3:
            angle1.setYaw(MathHelper.lerp(f2 * (0.9F + this.random.nextFloat() * 0.3F), angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f2 * (1.1F + this.random.nextFloat() * 0.2F), angle.getPitch(), angle.getPitch() + f1) + f4);
            break;
         case 4:
            float f7 = (float)(f2 * (0.8 + Math.sin(System.currentTimeMillis() * 0.01) * 0.2));
            angle1.setYaw(MathHelper.lerp(f7, angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f7, angle.getPitch(), angle.getPitch() + f1) + f4);
            break;
         default:
            angle1.setYaw(MathHelper.lerp(f2 * (0.8F + this.random.nextFloat() * 0.4F), angle.getYaw(), angle.getYaw() + f) + f3);
            angle1.setPitch(MathHelper.lerp(f2 * (0.8F + this.random.nextFloat() * 0.4F), angle.getPitch(), angle.getPitch() + f1) + f4);
      }

      return angle1;
   }

   private float randomRange(float f, float f1) {
      return MathHelper.lerp(this.secureRandom.nextFloat(), f, f1);
   }
}
