package render;

import core.ImageCache;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
   private static final Map<String, byte[]> textures = new HashMap<String, byte[]>();

   private static void a() {
      String[] astring = ImageCache.c();

      for (String s : astring) {
         b(s, s);
      }
   }

   private static void b(String s, String s1) {
      try {
         byte[] abyte = ImageCache.a(s1);
         textures.put(s, abyte);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public static byte[] getTexture(String s) {
      return textures.get(s);
   }

   public static byte[] getCapeTexture() {
      return textures.get("cape");
   }

   public static byte[] getGlowTexture() {
      return textures.get("glow");
   }

   public static byte[] getGlowPolygonTexture() {
      return textures.get("glow_polygon");
   }

   public static byte[] getGlowEllipseTexture() {
      return textures.get("glow_ellipse");
   }

   public static byte[] getGlowSquareTexture() {
      return textures.get("glow_square");
   }

   public static byte[] getGlowStarTexture() {
      return textures.get("glow_star");
   }

   public static byte[] getArrowTexture() {
      return textures.get("arrow");
   }

   public static byte[] getArrow3dTexture() {
      return textures.get("arrow_3d");
   }

   public static byte[] getGlowLineTexture() {
      return textures.get("glow_line");
   }

   public static boolean hasTexture(String s) {
      return textures.containsKey(s);
   }

   static {
      a();
   }
}
