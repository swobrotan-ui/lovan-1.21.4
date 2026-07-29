package render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public class BuiltTexture implements Renderable {
   private static final ShaderProgramKey apr = ShaderUtil.a("texture", VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   private final Size aqx;
   private final RadiusConfig nj;
   private final ColorPair aie;
   private final float axx;
   private final float Or;
   private final float hT;
   private final float ts;
   private final float qw;
   private final int op;

   public BuiltTexture(Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1, float f2, float f3, float f4, int i) {
      this.aqx = size;
      this.nj = radiusconfig;
      this.aie = colorpair;
      this.axx = f;
      this.Or = f1;
      this.hT = f2;
      this.ts = f3;
      this.qw = f4;
      this.op = i;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, this.op);
      float f3 = this.aqx.a();
      float f4 = this.aqx.b();
      ShaderProgram shaderprogram = RenderSystem.setShader(apr);
      shaderprogram.getUniform("Size").set(f3, f4);
      shaderprogram.getUniform("Radius").set(this.nj.a(), this.nj.b(), this.nj.c(), this.nj.d());
      shaderprogram.getUniform("Smoothness").set(this.axx);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, f2).texture(this.Or, this.hT).color(this.aie.a());
      bufferbuilder.vertex(matrix4f, f, f1 + f4, f2).texture(this.Or, this.hT + this.qw).color(this.aie.b());
      bufferbuilder.vertex(matrix4f, f + f3, f1 + f4, f2).texture(this.Or + this.ts, this.hT + this.qw).color(this.aie.c());
      bufferbuilder.vertex(matrix4f, f + f3, f1, f2).texture(this.Or + this.ts, this.hT).color(this.aie.d());
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public Size a() {
      return this.aqx;
   }

   public RadiusConfig b() {
      return this.nj;
   }

   public ColorPair c() {
      return this.aie;
   }

   public float d() {
      return this.axx;
   }

   public float e() {
      return this.Or;
   }

   public float f() {
      return this.hT;
   }

   public float g() {
      return this.ts;
   }

   public float h() {
      return this.qw;
   }

   public int i() {
      return this.op;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltTexture builttexture1 = (BuiltTexture)object;
         return Objects.equals(this.aqx, builttexture1.aqx)
            && Objects.equals(this.nj, builttexture1.nj)
            && Objects.equals(this.aie, builttexture1.aie)
            && Float.floatToIntBits(this.axx) == Float.floatToIntBits(builttexture1.axx)
            && Float.floatToIntBits(this.Or) == Float.floatToIntBits(builttexture1.Or)
            && Float.floatToIntBits(this.hT) == Float.floatToIntBits(builttexture1.hT)
            && Float.floatToIntBits(this.ts) == Float.floatToIntBits(builttexture1.ts)
            && Float.floatToIntBits(this.qw) == Float.floatToIntBits(builttexture1.qw)
            && this.op == builttexture1.op;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.aqx, this.nj, this.aie, this.axx, this.Or, this.hT, this.ts, this.qw, this.op);
   }

   @Override
   public String toString() {
      String s3 = String.valueOf(this.aqx);
      String s4 = String.valueOf(this.nj);
      String s5 = String.valueOf(this.aie);
      int i = this.op;
      float f = this.qw;
      float f1 = this.ts;
      float f2 = this.hT;
      float f3 = this.Or;
      float f4 = this.axx;
      String s = s5;
      String s1 = s4;
      String s2 = s3;
      return "BuiltTexture[size="
         + s2
         + ", radius="
         + s1
         + ", color="
         + s
         + ", smoothness="
         + f4
         + ", u="
         + f3
         + ", v="
         + f2
         + ", texWidth="
         + f1
         + ", texHeight="
         + f
         + ", textureId="
         + i
         + "]";
   }
}
