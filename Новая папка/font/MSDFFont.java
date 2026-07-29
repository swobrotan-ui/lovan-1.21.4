package font;

import java.util.Map;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import org.joml.Matrix4f;

public final class MSDFFont {
   private final String name;
   private final AbstractTexture texture;
   private final FontAtlas atlas;
   private final FontMetrics metrics;
   private final Map<Integer, Glyph> glyphs;
   private final Map<Integer, Map<Integer, Float>> kernings;

   private MSDFFont(
      String s, AbstractTexture abstracttexture, FontAtlas fontatlas, FontMetrics fontmetrics, Map<Integer, Glyph> map, Map<Integer, Map<Integer, Float>> map1
   ) {
      this.name = s;
      this.texture = abstracttexture;
      this.atlas = fontatlas;
      this.metrics = fontmetrics;
      this.glyphs = map;
      this.kernings = map1;
   }

   public int a() {
      return this.texture.getGlId();
   }

   public void b(Matrix4f matrix4f, VertexConsumer vertexconsumer, String s, float f, float f1, float f2, float f3, float f4, float f5, int i) {
      int j = -1;

      for (int k = 0; k < s.length(); k++) {
         char c0 = s.charAt(k);
         Glyph glyph = this.glyphs.get(Integer.valueOf(c0));
         if (glyph != null) {
            Map map = this.kernings.get(j);
            if (map != null) {
               f3 += map.getOrDefault(Integer.valueOf(c0), 0.0F) * f;
            }

            f3 += glyph.a(matrix4f, vertexconsumer, f, f3, f4, f5, i) + f1 + f2;
            j = c0;
         }
      }
   }

   public float c(String s, float f) {
      int i = -1;
      float f1 = 0.0F;

      for (int j = 0; j < s.length(); j++) {
         char c0 = s.charAt(j);
         Glyph glyph = this.glyphs.get(Integer.valueOf(c0));
         if (glyph != null) {
            Map map = this.kernings.get(i);
            if (map != null) {
               f1 += map.getOrDefault(Integer.valueOf(c0), 0.0F) * f;
            }

            f1 += glyph.b(f);
            i = c0;
         }
      }

      return f1;
   }

   public FontAtlas d() {
      return this.atlas;
   }

   public FontMetrics e() {
      return this.metrics;
   }

   public String f() {
      return this.name;
   }

   public static qu g() {
      return new qu();
   }
}
