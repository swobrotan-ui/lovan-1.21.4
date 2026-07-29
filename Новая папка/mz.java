import java.util.HashMap;
import java.util.Map;
import shader.ShaderResource;

public class mz {
   private final Map<String, ShaderResource> fontAssets = new HashMap<String, ShaderResource>();
   private static final String[] FONT_NAMES = new String[]{"a", "aa", "b", "bb", "c"};

   public mz() {
      ng.a();

      for (String s : FONT_NAMES) {
         this.a(s);
      }
   }

   private void a(String s) {
      wi wi = ng.b(s);
      if (wi == null) {
         System.err.println("Font not found in cache: " + s);
      } else {
         this.fontAssets.put(s + ".json", ShaderResource.b("fonts/" + s + ".json", wi.a()));
      }
   }

   public static mz b() {
      return new mz();
   }

   public Map<String, ShaderResource> c() {
      return this.fontAssets;
   }
}
