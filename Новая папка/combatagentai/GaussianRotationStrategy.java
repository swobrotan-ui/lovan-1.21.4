package combatagentai;

import data.Angle;
import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class GaussianRotationStrategy implements RotationStrategy {
   private static final float MAX_CHANGE_PER_FRAME = 15.0F;
   private static final float JITTER_AMPLITUDE = 0.15F;
   private long lastUpdateTime = 0L;

   @Override
   public Angle calculateRotation(Angle current, Angle target, Random random, float speed) {
      long now = System.currentTimeMillis();
      float deltaTime = (float) (now - this.lastUpdateTime) / 1000.0F;
      this.lastUpdateTime = now;

      float yawDelta = MathHelper.wrapDegrees(target.getYaw() - current.getYaw());
      float pitchDelta = target.getPitch() - current.getPitch();

      float maxChange = Math.min(MAX_CHANGE_PER_FRAME, speed * deltaTime * 10.0F);

      float yawChange = (float) (random.nextGaussian() * JITTER_AMPLITUDE + yawDelta * 0.15F);
      float pitchChange = (float) (random.nextGaussian() * JITTER_AMPLITUDE + pitchDelta * 0.15F);

      yawChange = MathHelper.clamp(yawChange, -maxChange, maxChange);
      pitchChange = MathHelper.clamp(pitchChange, -maxChange, maxChange);

      return new Angle(
         MathHelper.wrapDegrees(current.getYaw() + yawChange),
         MathHelper.clamp(current.getPitch() + pitchChange, -90.0F, 90.0F)
      );
   }

   @Override
   public void reset() {
      this.lastUpdateTime = 0L;
   }
}