package combatagentai;

import java.util.concurrent.ThreadLocalRandom;

public class InteractionRateController {
   private final long minDelayMs;
   private final long maxDelayMs;
   private long lastInteractionTime = 0L;
   private final long[] markovDelays = new long[]{300L, 400L, 500L, 600L, 700L, 800L, 900L};
   private int markovIndex = 0;

   private static final double SKEWED_MEAN = 500.0;
   private static final double SKEWED_STD = 150.0;
   private static final double SKEWED_SKEW = -0.5;

   public InteractionRateController(long minDelayMs, long maxDelayMs) {
      this.minDelayMs = minDelayMs;
      this.maxDelayMs = maxDelayMs;
   }

   public boolean shouldInteract() {
      long now = System.currentTimeMillis();
      if (this.lastInteractionTime == 0L) {
         this.lastInteractionTime = now;
         return true;
      }
      return now - this.lastInteractionTime >= this.calculateRequiredDelay();
   }

   public long calculateRequiredDelay() {
      return this.generateSkewedDelay();
   }

   private long generateSkewedDelay() {
      double gaussian = ThreadLocalRandom.current().nextGaussian();
      double skewed = SKEWED_MEAN + (gaussian * SKEWED_STD) * (1.0 + SKEWED_SKEW * ThreadLocalRandom.current().nextGaussian());

      return Math.max(this.minDelayMs, Math.min(this.maxDelayMs, (long) skewed));
   }

   public long generateMarkovDelay() {
      int index = this.markovIndex;
      long delay = this.markovDelays[index];

      if (ThreadLocalRandom.current().nextDouble() < 0.3) {
         index = (index + 1) % this.markovDelays.length;
      } else if (ThreadLocalRandom.current().nextDouble() < 0.3) {
         index = (index - 1 + this.markovDelays.length) % this.markovDelays.length;
      }
      this.markovIndex = index;

      float noise = (float) (ThreadLocalRandom.current().nextDouble() - 0.5) * 100.0F;
      return Math.max(this.minDelayMs, Math.min(this.maxDelayMs, delay + (long) noise));
   }

   public void onInteraction() {
      this.lastInteractionTime = System.currentTimeMillis();
   }

   public void reset() {
      this.lastInteractionTime = 0L;
      this.markovIndex = 0;
   }
}