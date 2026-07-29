public class il {
   public static float[] a(int i, int j, float f, float f1, float f2, float f3) {
      int k = i / j;
      int l = i % j;
      float f4 = l * (f + f2);
      float f5 = k * (f1 + f3);
      return new float[]{f4, f5};
   }

   public static float b(int i, int j, float f, float f1, float f2) {
      if (i == 0) {
         return 0.0F;
      } else {
         int k = (int)Math.ceil((double)i / j);
         float f3 = k * f + (k - 1) * f1;
         return Math.max(0.0F, f3 - f2);
      }
   }

   public static boolean c(float f, float f1, float f2, float f3) {
      float f4 = f + f1;
      float f5 = f2 + f3;
      return f4 >= f2 && f <= f5;
   }
}
