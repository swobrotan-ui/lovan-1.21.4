package combatagentai;

import data.Angle;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class BezierRotationStrategy implements RotationStrategy {
   private float[] controlPoints = new float[4];
   private long lastUpdateTime = 0L;
   private boolean initialized = false;

   @Override
   public Angle calculateRotation(Angle current, Angle target, Random random, float speed) {
      long now = System.currentTimeMillis();
      if (!this.initialized) {
         this.initialize(current, target, random);
      }

      float deltaTime = (float) (now - this.lastUpdateTime) / 1000.0F;
      this.lastUpdateTime = now;

      float t = MathHelper.clamp(deltaTime * speed, 0.0F, 1.0F);
      float yaw = this.cubicInterpolate(this.controlPoints, t);
      float pitch = current.getPitch() + (target.getPitch() - current.getPitch()) * Math.min(t * 1.5F, 1.0F);

      this.initialized = t < 1.0F;

      return new Angle(MathHelper.wrapDegrees(yaw), MathHelper.clamp(pitch, -90.0F, 90.0F));
   }

   private void initialize(Angle current, Angle target, Random random) {
      this.controlPoints[0] = current.getYaw();
      this.controlPoints[1] = current.getYaw() + random.nextFloat() * 10.0F - 5.0F;
      this.controlPoints[2] = target.getYaw() - random.nextFloat() * 10.0F + 5.0F;
      this.controlPoints[3] = target.getYaw();
      this.lastUpdateTime = System.currentTimeMillis();
      this.initialized = true;
   }

   private float cubicInterpolate(float[] points, float t) {
      float u = 1.0F - t;
      return u * u * u * points[0] +
             3 * u * u * t * points[1] +
             3 * u * t * t * points[2] +
             t * t * t * points[3];
   }

   @Override
   public void reset() {
      this.lastUpdateTime = 0L;
      this.initialized = false;
      Arrays.fill(this.controlPoints, 0.0F);
   }
}