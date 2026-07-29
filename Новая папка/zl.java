import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;
import render.ShaderUtil;

public class zl extends ii {
   private static final ShaderProgramKey avz = ShaderUtil.a("white_rectangle", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final float Wv = 0.3F;
   private static final float apO = 2.3F;
   private Color abk;

   public zl(float f, float f1, Color color) {
      super(f, f1, 20.0F, "L", 15.0F, 0.0F, 4.0F, null);
      this.abk = color;
   }

   public void a(Color color) {
      this.abk = color;
   }

   public Color b() {
      return this.abk;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f3, float f2) {
      this.eV.a(this.cH);
      this.eV.f();
      Matrix4f matrix4f1 = this.b(matrix4f, f, f1, this.eV.d());
      if (this.abk != null) {
         this.c(matrix4f1, f, f1, f2);
      }
   }

   private void c(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = 20.0F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      ShaderProgram shaderprogram = RenderSystem.setShader(avz);
      shaderprogram.getUniform("Size").set(f3, f3);
      shaderprogram.getUniform("Offset").set(2.3F, 2.3F);
      shaderprogram.getUniform("CornerRadius").set(4.0F);
      shaderprogram.getUniform("BlurRadius").set(0.0F);
      shaderprogram.getUniform("ShadowOffset").set(0.0F);
      shaderprogram.getUniform("ShadowStrength").set(0.0F);
      shaderprogram.getUniform("BorderThickness").set(0.5F);
      shaderprogram.getUniform("BorderSoftness").set(1.5F);
      shaderprogram.getUniform("EdgeSoftness").set(0.3F);
      shaderprogram.getUniform("DitherStrength").set(0.003F);
      shaderprogram.getUniform("Margin").set(0.0F);
      shaderprogram.getUniform("NoiseStrength").set(0.0F);
      shaderprogram.getUniform("GlobalAlpha").set(f2);
      float f4 = this.abk.getRed() / 255.0F;
      float f5 = this.abk.getGreen() / 255.0F;
      float f6 = this.abk.getBlue() / 255.0F;
      float f7 = this.abk.getAlpha() / 255.0F;
      shaderprogram.getUniform("GradientColor1").set(f4, f5, f6, f7);
      shaderprogram.getUniform("GradientColor2").set(f4, f5, f6, f7);
      shaderprogram.getUniform("GradientColor3").set(f4, f5, f6, f7);
      shaderprogram.getUniform("GradientColor4").set(f4, f5, f6, f7);
      shaderprogram.getUniform("BorderColor").set(Math.min(1.0F, f4 + 0.15F), Math.min(1.0F, f5 + 0.15F), Math.min(1.0F, f6 + 0.15F));
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float f8 = f - 2.3F;
      float f9 = f1 - 2.3F;
      float f10 = f3 + 4.6F;
      float f11 = f3 + 4.6F;
      int i = (int)(f2 * 255.0F) << 24 | 16777215;
      bufferbuilder.vertex(matrix4f, f8, f9, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f8, f9 + f11, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f8 + f10, f9 + f11, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f8 + f10, f9, 0.0F).color(i);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }
}
