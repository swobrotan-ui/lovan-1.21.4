package font;

import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;

public final class Glyph {
   private final int code;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private final float advance;
   private final float topPosition;
   private final float width;
   private final float height;

   public Glyph(pqg pqg, float f, float f1) {
      this.code = pqg.a();
      this.advance = pqg.b();
      pu pux = pqg.d();
      if (pux != null) {
         this.minU = pux.a() / f;
         this.maxU = pux.c() / f;
         this.minV = 1.0F - pux.b() / f1;
         this.maxV = 1.0F - pux.d() / f1;
      } else {
         this.minU = this.maxU = this.minV = this.maxV = 0.0F;
      }

      pu pux = pqg.c();
      if (pux != null) {
         this.width = pux.c() - pux.a();
         this.height = pux.b() - pux.d();
         this.topPosition = pux.b();
      } else {
         this.width = this.height = this.topPosition = 0.0F;
      }
   }

   public float a(Matrix4f matrix4f, VertexConsumer vertexconsumer, float f, float f1, float f2, float f3, int i) {
      f2 -= this.topPosition * f;
      float f4 = this.width * f;
      float f5 = this.height * f;
      int j = i >> 24 & 0xFF;
      int k = j << 24 | 0 | 0;
      int l = j << 24 | 0xFF00 | 0;
      int i1 = j << 24 | 0xFF00 | 0xFF;
      int j1 = j << 24 | 0 | 0xFF;
      vertexconsumer.vertex(matrix4f, f1, f2, f3).texture(this.minU, this.minV).color(k);
      vertexconsumer.vertex(matrix4f, f1, f2 + f5, f3).texture(this.minU, this.maxV).color(l);
      vertexconsumer.vertex(matrix4f, f1 + f4, f2 + f5, f3).texture(this.maxU, this.maxV).color(i1);
      vertexconsumer.vertex(matrix4f, f1 + f4, f2, f3).texture(this.maxU, this.minV).color(j1);
      return this.advance * f;
   }

   public float b(float f) {
      return this.advance * f;
   }

   public int c() {
      return this.code;
   }
}
