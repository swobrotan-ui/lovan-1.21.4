package core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.ImageClient;

public class ImageCache {
   private static final Map<String, byte[]> imageCache = new ConcurrentHashMap<String, byte[]>();
   private static final ImageClient imageClient = new ImageClient();
   private static final CompletableFuture<Void> preloadFuture = new CompletableFuture<Void>();
   private static final String[] PRELOAD_IMAGES = new String[]{
      "a", "aa", "b", "bb", "c", "cape", "glow", "glow_polygon", "glow_ellipse", "glow_square", "glow_star", "arrow", "arrow_3d", "glow_line"
   };

   public ImageCache() {
      new Thread(() -> {
         b();
         preloadFuture.complete(null);
      }, "preload").start();
   }

   public static byte[] a(String s) {
      preloadFuture.join();
      return imageCache.get(s);
   }

   public static void b() {
      try {
         Map map = imageClient.a(PRELOAD_IMAGES);

         for (Entry entry : map.entrySet()) {
            if (entry.getValue() != null && ((byte[])entry.getValue()).length > 0) {
               imageCache.put((String)entry.getKey(), (byte[])entry.getValue());
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static String[] c() {
      return (String[])PRELOAD_IMAGES.clone();
   }
}
