import java.awt.Color;

public final class db {
   public static int a(int i, int j, int k, int l) {
      return (l & 0xFF) << 24 | (i & 0xFF) << 16 | (j & 0xFF) << 8 | (k & 0xFF) << 0;
   }

   public static int[] b(int i) {
      return new int[]{i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF, i >> 24 & 0xFF};
   }

   public static float[] c(Color color) {
      return new float[]{color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F};
   }

   public static float[] d(int i) {
      return new float[]{(i >> 16 & 0xFF) / 255.0F, (i >> 8 & 0xFF) / 255.0F, (i & 0xFF) / 255.0F, (i >> 24 & 0xFF) / 255.0F};
   }

   public static void e(int i, float[] afloat) {
      afloat[0] = (i >> 16 & 0xFF) / 255.0F;
      afloat[1] = (i >> 8 & 0xFF) / 255.0F;
      afloat[2] = (i & 0xFF) / 255.0F;
      afloat[3] = (i >> 24 & 0xFF) / 255.0F;
   }
}
