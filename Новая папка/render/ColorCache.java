package render;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class ColorCache {
   private static final Map<Integer, Color> avT = new HashMap<Integer, Color>(128);
   private static final Map<Integer, ColorPair> GG = new HashMap<Integer, ColorPair>(128);
   private static final int arz = 256;

   public static Color a(int i, int j, int k, int l) {
      int i1 = l << 24 | i << 16 | j << 8 | k;
      Color color = avT.get(i1);
      if (color != null) {
         return color;
      } else {
         if (avT.size() >= 256) {
            avT.clear();
         }

         Color color1 = new Color(i, j, k, l);
         avT.put(i1, color1);
         return color1;
      }
   }

   public static Color b(int i, int j, int k) {
      return a(i, j, k, 255);
   }

   public static Color c(int i, int j, int k, float f) {
      return a(i, j, k, (int)(255.0F * f));
   }

   public static int d(int i, int j, int k, float f) {
      return a(i, j, k, (int)(255.0F * f)).getRGB();
   }

   public static ColorPair e(int i) {
      ColorPair colorpair = GG.get(i);
      if (colorpair != null) {
         return colorpair;
      } else {
         if (GG.size() >= 256) {
            GG.clear();
         }

         ColorPair colorpair1 = new ColorPair(i, i, i, i);
         GG.put(i, colorpair1);
         return colorpair1;
      }
   }

   public static void f() {
      avT.clear();
      GG.clear();
   }
}
