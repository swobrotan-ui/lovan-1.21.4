import java.util.HashMap;
import java.util.Map;

public class ng {
   private static final Map<String, wi> cache = new HashMap<String, wi>();
   private static boolean initialized = false;

   public static synchronized void a() {
      if (!initialized) {
         try {
            gm gm = new gm();

            for (wi wi : gm.a()) {
               cache.put(wi.b(), wi);
            }

            initialized = true;
         } catch (Exception exception) {
         }
      }
   }

   public static wi b(String s) {
      if (!initialized) {
         a();
      }

      return cache.get(s);
   }

   public static void c() {
      cache.clear();
      initialized = false;
   }
}
