public class xsx {
   public static float a(float f) {
      if (f < 0.5F) {
         return 4.0F * f * f * f;
      } else {
         float f1 = -2.0F * f + 2.0F;
         return 1.0F - f1 * f1 * f1 / 2.0F;
      }
   }

   public static float b(float f) {
      return f < 0.5F ? 2.0F * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 2.0) / 2.0F;
   }

   public static float c(float f) {
      float f1 = 1.70158F;
      float f2 = f1 + 1.0F;
      return 1.0F + f2 * (float)Math.pow(f - 1.0F, 3.0) + f1 * (float)Math.pow(f - 1.0F, 2.0);
   }
}
