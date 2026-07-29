import core.ImageCache;
import java.util.HashMap;
import java.util.Map;

public class olt {
   private static final Map<String, byte[]> fontAtlases = new HashMap<String, byte[]>();
   private static boolean initialized = false;

   private static void a() {
      b("a", "a");
      b("aa", "aa");
      b("b", "b");
      b("bb", "bb");
      b("c", "c");
      initialized = true;
   }

   private static void b(String s, String s1) {
      try {
         byte[] abyte = ImageCache.a(s1);
         if (abyte == null) {
            throw new RuntimeException("Failed to load atlas from server: " + s1);
         } else {
            fontAtlases.put(s, abyte);
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static byte[] c(String s) {
      return fontAtlases.get(s);
   }

   static {
      a();
   }
}
