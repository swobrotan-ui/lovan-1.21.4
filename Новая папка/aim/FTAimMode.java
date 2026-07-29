package aim;

import data.Angle;
import java.security.SecureRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import util.RotationUtil;

public class FTAimMode extends AimMode {
   private final SecureRandom secureRandom = new SecureRandom();
   private int attackCount = 0;
   private long lastAttackTime = System.currentTimeMillis();
   private boolean aggressive = false;

   public FTAimMode() {
      super("FT");
   }

   public void setAggressive(boolean flag) {
      this.aggressive = flag;
   }

   public void onAttack() {
      this.attackCount++;
      this.lastAttackTime = System.currentTimeMillis();
   }

   public void reset() {
      this.attackCount = 0;
      this.aggressive = false;
      this.lastAttackTime = System.currentTimeMillis();
   }

   @Override
   public Angle calculateRotation(Angle angle, Angle angle1, Vec3d vec3d, Entity entity) {
      Angle angle2 = RotationUtil.getDeltaWithJitter(angle, angle1);
      float f = angle2.getYaw();
      float f1 = angle2.getPitch();
      float f2 = (float)Math.hypot(Math.abs(f), Math.abs(f1));
      if (f2 < 1.0E-4F) {
         return new Angle(angle1.getYaw(), angle1.getPitch());
      } else if (entity != null) {
         float f13 = this.aggressive ? 1.0F : (this.secureRandom.nextBoolean() ? 0.4F : 0.2F);
         float f14 = Math.abs(f / f2) * 180.0F;
         float f15 = Math.abs(f1 / f2) * 180.0F;
         float f16 = MathHelper.clamp(f, -f14, f14);
         float f17 = MathHelper.clamp(f1, -f15, f15);
         Angle angle5 = new Angle(angle.getYaw(), angle.getPitch());
         angle5.setYaw(MathHelper.lerp(this.randomRange(f13, f13 + 0.2F), angle.getYaw(), angle.getYaw() + f16));
         angle5.setPitch(MathHelper.clamp(MathHelper.lerp(this.randomRange(f13, f13 + 0.2F), angle.getPitch(), angle.getPitch() + f17), -90.0F, 90.0F));
         return angle5;
      } else {
         int i = this.attackCount % 3;
         float f3 = this.hasElapsed(430L) ? (this.secureRandom.nextBoolean() ? 0.4F : 0.2F) : -0.2F;
         float f4 = (float)this.getElapsedTime() / 40.0F + this.attackCount % 6;

         Angle angle3 = switch (i) {
            case 0 -> new Angle((float)Math.cos(f4), (float)Math.sin(f4));
            case 1 -> new Angle((float)Math.sin(f4), (float)Math.cos(f4));
            case 2 -> new Angle((float)Math.sin(f4), (float)(-Math.cos(f4)));
            default -> new Angle((float)(-Math.cos(f4)), (float)Math.sin(f4));
         };
         float f5 = !this.hasElapsed(2000L) ? this.randomRange(12.0F, 24.0F) * angle3.getYaw() : 0.0F;
         float f6 = this.randomRange(0.0F, 2.0F) * (float)Math.cos(System.currentTimeMillis() / 5000.0);
         float f7 = !this.hasElapsed(2000L) ? this.randomRange(2.0F, 6.0F) * angle3.getPitch() + f6 : 0.0F;
         float f8 = Math.abs(f / f2) * 180.0F;
         float f9 = Math.abs(f1 / f2) * 180.0F;
         float f10 = MathHelper.clamp(f, -f8, f8);
         float f11 = MathHelper.clamp(f1, -f9, f9);
         float f12 = MathHelper.clamp(this.randomRange(f3, f3 + 0.2F), 0.0F, 1.0F);
         Angle angle4 = new Angle(angle.getYaw(), angle.getPitch());
         angle4.setYaw(MathHelper.lerp(f12, angle.getYaw(), angle.getYaw() + f10) + f5);
         angle4.setPitch(MathHelper.clamp(MathHelper.lerp(f12, angle.getPitch(), angle.getPitch() + f11) + f7, -90.0F, 90.0F));
         return angle4;
      }
   }

   private boolean hasElapsed(long i) {
      return System.currentTimeMillis() - i >= this.lastAttackTime;
   }

   private long getElapsedTime() {
      return System.currentTimeMillis() - this.lastAttackTime;
   }

   private float randomRange(float f, float f1) {
      return MathHelper.lerp(this.secureRandom.nextFloat(), f, f1);
   }
}
