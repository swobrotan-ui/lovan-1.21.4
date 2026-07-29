package render;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltBlur implements Renderable {
   private static final ShaderProgramKey NY = ShaderUtil.a("blur", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final Supplier<SimpleFramebuffer> abD = Suppliers.memoize(() -> {
      return new SimpleFramebuffer(1920, 1080, false);
   });
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private final Size amp;
   private final RadiusConfig vj;
   private final ColorPair Jg;
   private final float blurRadius;
   private final float smoothness;

   public BuiltBlur(Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1) {
      this.amp = size;
      this.vj = radiusconfig;
      this.Jg = colorpair;
      this.blurRadius = f;
      this.smoothness = f1;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      this.a(matrix4f, f, f1, f2, this.smoothness);
   }

   public void a(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      Framebuffer framebuffer = mc.getFramebuffer();
      SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)abD.get();
      if (simpleframebuffer.textureWidth != framebuffer.textureWidth || simpleframebuffer.textureHeight != framebuffer.textureHeight) {
         simpleframebuffer.resize(framebuffer.textureWidth, framebuffer.textureHeight);
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      simpleframebuffer.beginWrite(false);
      framebuffer.draw(simpleframebuffer.textureWidth, simpleframebuffer.textureHeight);
      framebuffer.beginWrite(false);
      RenderSystem.setShaderTexture(0, simpleframebuffer.getColorAttachment());
      float f4 = this.amp.a();
      float f5 = this.amp.b();
      ShaderProgram shaderprogram = RenderSystem.setShader(NY);
      shaderprogram.getUniform("Size").set(f4, f5);
      shaderprogram.getUniform("Radius").set(this.vj.a(), this.vj.b(), this.vj.c(), this.vj.d());
      shaderprogram.getUniform("Smoothness").set(this.blurRadius);
      shaderprogram.getUniform("BlurRadius").set(f3);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, f2).color(this.Jg.a());
      bufferbuilder.vertex(matrix4f, f, f1 + f5, f2).color(this.Jg.b());
      bufferbuilder.vertex(matrix4f, f + f4, f1 + f5, f2).color(this.Jg.c());
      bufferbuilder.vertex(matrix4f, f + f4, f1, f2).color(this.Jg.d());
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void b() {
      SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)abD.get();
      if (simpleframebuffer != null) {
         simpleframebuffer.delete();
      }
   }

   public Size c() {
      return this.amp;
   }

   public RadiusConfig d() {
      return this.vj;
   }

   public ColorPair e() {
      return this.Jg;
   }

   public float f() {
      return this.blurRadius;
   }

   public float g() {
      return this.smoothness;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltBlur builtblur1 = (BuiltBlur)object;
         return Objects.equals(this.amp, builtblur1.amp)
            && Objects.equals(this.vj, builtblur1.vj)
            && Objects.equals(this.Jg, builtblur1.Jg)
            && Float.floatToIntBits(this.blurRadius) == Float.floatToIntBits(builtblur1.blurRadius)
            && Float.floatToIntBits(this.smoothness) == Float.floatToIntBits(builtblur1.smoothness);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.amp, this.vj, this.Jg, this.blurRadius, this.smoothness);
   }

   @Override
   public String toString() {
      String s3 = String.valueOf(this.amp);
      String s4 = String.valueOf(this.vj);
      String s5 = String.valueOf(this.Jg);
      float f = this.smoothness;
      float f1 = this.blurRadius;
      String s = s5;
      String s1 = s4;
      String s2 = s3;
      return "BuiltBlur[size=" + s2 + ", radius=" + s1 + ", color=" + s + ", smoothness=" + f1 + ", blurRadius=" + f + "]";
   }
}
