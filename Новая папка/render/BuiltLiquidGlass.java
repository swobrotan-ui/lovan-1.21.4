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

public final class BuiltLiquidGlass implements Renderable {
   private static final ShaderProgramKey QE = ShaderUtil.a("liquidglass", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final Supplier<SimpleFramebuffer> alA = Suppliers.memoize(() -> {
      return new SimpleFramebuffer(1920, 1080, false);
   });
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static boolean oF = false;
   private final Size HB;
   private final RadiusConfig ahA;
   private final ColorPair abJ;
   private final float axu;
   private final float aec;
   private final float awn;
   private final float NN;
   private final float aop;
   private final float afq;
   private final float ahe;
   private final float nZ;
   private final float vw;

   public BuiltLiquidGlass(Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1, float f2, float f3, float f4) {
      this(size, radiusconfig, colorpair, f, f1, f2, f3, f4, 1.0F, 0.0F, 0.0F, 8.0F);
   }

   public BuiltLiquidGlass(
      Size size, RadiusConfig radiusconfig, ColorPair colorpair, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7, float f8
   ) {
      this.HB = size;
      this.ahA = radiusconfig;
      this.abJ = colorpair;
      this.axu = f;
      this.aec = f1;
      this.awn = f2;
      this.NN = f3;
      this.aop = f4;
      this.afq = f5;
      this.ahe = f6;
      this.nZ = f7;
      this.vw = f8;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      this.b(matrix4f, f, f1, f2, 1.0F);
   }

   public void a(Matrix4f matrix4f, float f, float f1, float f2) {
      this.b(matrix4f, f, f1, 0.0F, f2);
   }

   private void b(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      Framebuffer framebuffer = mc.getFramebuffer();
      SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)alA.get();
      if (simpleframebuffer.textureWidth != framebuffer.textureWidth || simpleframebuffer.textureHeight != framebuffer.textureHeight) {
         simpleframebuffer.resize(framebuffer.textureWidth, framebuffer.textureHeight);
      }

      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      if (!oF) {
         simpleframebuffer.beginWrite(false);
         framebuffer.draw(simpleframebuffer.textureWidth, simpleframebuffer.textureHeight);
         framebuffer.beginWrite(false);
         oF = true;
      }

      RenderSystem.setShaderTexture(0, simpleframebuffer.getColorAttachment());
      float f4 = this.HB.a();
      float f5 = this.HB.b();
      float f6 = this.nZ > 0.001F ? Math.abs(this.ahe) + this.vw + 2.0F : 0.0F;
      ShaderProgram shaderprogram = RenderSystem.setShader(QE);
      shaderprogram.getUniform("Size").set(f4, f5);
      shaderprogram.getUniform("Offset").set(f6, f6);
      shaderprogram.getUniform("Radius").set(this.ahA.a(), this.ahA.b(), this.ahA.c(), this.ahA.d());
      shaderprogram.getUniform("Smoothness").set(this.axu);
      shaderprogram.getUniform("CornerSmoothness").set(this.aec);
      float f7 = (this.abJ.a() >> 24 & 0xFF) / 255.0F * f3;
      shaderprogram.getUniform("GlobalAlpha").set(f7);
      shaderprogram.getUniform("FresnelPower").set(this.awn);
      shaderprogram.getUniform("DistortStrength").set(this.NN);
      shaderprogram.getUniform("BlurRadius").set(this.aop);
      shaderprogram.getUniform("Brightness").set(this.afq);
      shaderprogram.getUniform("ShadowOffset").set(this.ahe);
      shaderprogram.getUniform("ShadowStrength").set(this.nZ);
      shaderprogram.getUniform("ShadowBlurRadius").set(this.vw);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int i = this.abJ.a();
      int j = this.abJ.b();
      int k = this.abJ.c();
      int l = this.abJ.d();
      float f8 = f - f6;
      float f9 = f1 - f6;
      float f10 = f4 + f6 * 2.0F;
      float f11 = f5 + f6 * 2.0F;
      bufferbuilder.vertex(matrix4f, f8, f9, f2).color(i);
      bufferbuilder.vertex(matrix4f, f8, f9 + f11, f2).color(j);
      bufferbuilder.vertex(matrix4f, f8 + f10, f9 + f11, f2).color(k);
      bufferbuilder.vertex(matrix4f, f8 + f10, f9, f2).color(l);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public static void c() {
      oF = false;
   }

   public static void d() {
      SimpleFramebuffer simpleframebuffer = (SimpleFramebuffer)alA.get();
      if (simpleframebuffer != null) {
         simpleframebuffer.delete();
      }
   }

   public Size e() {
      return this.HB;
   }

   public RadiusConfig f() {
      return this.ahA;
   }

   public ColorPair g() {
      return this.abJ;
   }

   public float h() {
      return this.axu;
   }

   public float i() {
      return this.aec;
   }

   public float j() {
      return this.awn;
   }

   public float k() {
      return this.NN;
   }

   public float l() {
      return this.aop;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltLiquidGlass builtliquidglass1 = (BuiltLiquidGlass)object;
         return Objects.equals(this.HB, builtliquidglass1.HB)
            && Objects.equals(this.ahA, builtliquidglass1.ahA)
            && Objects.equals(this.abJ, builtliquidglass1.abJ)
            && Float.floatToIntBits(this.axu) == Float.floatToIntBits(builtliquidglass1.axu)
            && Float.floatToIntBits(this.aec) == Float.floatToIntBits(builtliquidglass1.aec)
            && Float.floatToIntBits(this.awn) == Float.floatToIntBits(builtliquidglass1.awn)
            && Float.floatToIntBits(this.NN) == Float.floatToIntBits(builtliquidglass1.NN)
            && Float.floatToIntBits(this.aop) == Float.floatToIntBits(builtliquidglass1.aop);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.HB, this.ahA, this.abJ, this.axu, this.aec, this.awn, this.NN, this.aop);
   }

   @Override
   public String toString() {
      String s3 = String.valueOf(this.HB);
      String s4 = String.valueOf(this.ahA);
      String s5 = String.valueOf(this.abJ);
      float f = this.aop;
      float f1 = this.NN;
      float f2 = this.awn;
      float f3 = this.aec;
      float f4 = this.axu;
      String s = s5;
      String s1 = s4;
      String s2 = s3;
      return "BuiltLiquidGlass[size="
         + s2
         + ", radius="
         + s1
         + ", color="
         + s
         + ", smoothness="
         + f4
         + ", cornerSmoothness="
         + f3
         + ", fresnelPower="
         + f2
         + ", distortStrength="
         + f1
         + ", blurRadius="
         + f
         + "]";
   }
}
