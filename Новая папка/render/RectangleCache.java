package render;

import java.util.HashMap;
import java.util.Map;

public class RectangleCache {
   private static final Map<Long, BuiltRectangle> aiy = new HashMap<Long, BuiltRectangle>(256);
   private static final int MS = 512;

   private static long a(float f, float f1, float f2) {
      int i = Float.floatToRawIntBits(f);
      int j = Float.floatToRawIntBits(f1);
      int k = Float.floatToRawIntBits(f2);
      return (i * 31L + j) * 31L + k;
   }

   public static BuiltRectangle b(float f, float f1, float f2) {
      long i = a(f, f1, f2);
      BuiltRectangle builtrectangle = aiy.get(i);
      if (builtrectangle != null) {
         return builtrectangle;
      } else {
         if (aiy.size() >= 512) {
            aiy.clear();
         }

         BuiltRectangle builtrectangle1 = new br().a(f, f1).b(f2).a();
         aiy.put(i, builtrectangle1);
         return builtrectangle1;
      }
   }

   public static void c() {
      aiy.clear();
   }
}
