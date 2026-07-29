package shader;

import java.util.HashMap;
import java.util.Map;

public class ShaderCache {
   private static final Map<String, ShaderData> cache = new HashMap<String, ShaderData>();
   private static boolean initialized = false;

   public static synchronized void a() {
      if (!initialized) {
         try {
            ShaderDataFetcher shaderdatafetcher = new ShaderDataFetcher();

            for (ShaderData shaderdata : shaderdatafetcher.a()) {
               cache.put(shaderdata.getName(), shaderdata);
            }

            initialized = true;
         } catch (Exception exception) {
         }
      }
   }

   public static ShaderData getShader(String s) {
      if (!initialized) {
         a();
      }

      return cache.get(s);
   }

   public static void clear() {
      cache.clear();
      initialized = false;
   }
}
