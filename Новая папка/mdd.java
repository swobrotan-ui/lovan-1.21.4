public class mdd {
   public static float a(float f, float f1, float f2) {
      return f + (f1 - f) * Math.min(f2, 1.0F);
   }

   public static float b(float f, float f1, float f2) {
      float f3 = c((f2 - f) / (f1 - f), 0.0F, 1.0F);
      return f3 * f3 * (3.0F - 2.0F * f3);
   }

   public static float c(float f, float f1, float f2) {
      return Math.max(f1, Math.min(f2, f));
   }

   public static boolean d(float f, float f1, float f2) {
      return Math.abs(f1 - f) < f2;
   }
}
