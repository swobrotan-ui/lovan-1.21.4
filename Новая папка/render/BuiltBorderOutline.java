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

public final class BuiltBorderOutline implements Renderable {
   private static final ShaderProgramKey zA = ShaderUtil.a("outline", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private final float un;
   private final float aaB;
   private final float Rs;
   private final float bM;
   private final float KI;
   private final float hC;
   private final float[] td;

   public BuiltBorderOutline(float f, float f1, float f2, float f3, float f4, float f5, float[] afloat) {
      this.un = f;
      this.aaB = f1;
      this.Rs = f2;
      this.bM = f3;
      this.KI = f4;
      this.hC = f5;
      this.td = afloat;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      this.b(matrix4f, f, f1, f2, 1.0F);
   }

   public void a(Matrix4f matrix4f, float f, float f1, float f2) {
      this.b(matrix4f, f, f1, 0.0F, f2);
   }

   private void b(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      ShaderProgram shaderprogram = RenderSystem.setShader(zA);
      float f4 = this.bM + this.KI + 2.0F;
      shaderprogram.getUniform("Size").set(this.un, this.aaB);
      shaderprogram.getUniform("Offset").set(f4, f4);
      shaderprogram.getUniform("CornerRadius").set(this.Rs);
      shaderprogram.getUniform("BorderThickness").set(this.bM);
      shaderprogram.getUniform("BorderSoftness").set(this.KI);
      shaderprogram.getUniform("Margin").set(this.hC);
      shaderprogram.getUniform("GlobalAlpha").set(f3);
      shaderprogram.getUniform("BorderColor").set(this.td[0], this.td[1], this.td[2], this.td[3] * f3);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float f5 = f - f4;
      float f6 = f1 - f4;
      float f7 = this.un + f4 * 2.0F;
      float f8 = this.aaB + f4 * 2.0F;
      int i = (int)(f3 * 255.0F) << 24 | 16777215;
      bufferbuilder.vertex(matrix4f, f5, f6, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5, f6 + f8, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5 + f7, f6 + f8, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5 + f7, f6, f2).color(i);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public float c() {
      return this.un;
   }

   public float d() {
      return this.aaB;
   }

   public float e() {
      return this.Rs;
   }

   public float f() {
      return this.bM;
   }

   public float g() {
      return this.KI;
   }

   public float h() {
      return this.hC;
   }

   public float[] i() {
      return this.td;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltBorderOutline builtborderoutline1 = (BuiltBorderOutline)object;
         return Float.floatToIntBits(this.un) == Float.floatToIntBits(builtborderoutline1.un)
            && Float.floatToIntBits(this.aaB) == Float.floatToIntBits(builtborderoutline1.aaB)
            && Float.floatToIntBits(this.Rs) == Float.floatToIntBits(builtborderoutline1.Rs)
            && Float.floatToIntBits(this.bM) == Float.floatToIntBits(builtborderoutline1.bM)
            && Float.floatToIntBits(this.KI) == Float.floatToIntBits(builtborderoutline1.KI)
            && Float.floatToIntBits(this.hC) == Float.floatToIntBits(builtborderoutline1.hC)
            && Objects.equals(this.td, builtborderoutline1.td);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.un, this.aaB, this.Rs, this.bM, this.KI, this.hC, this.td);
   }

   @Override
   public String toString() {
      float f6 = this.un;
      float f7 = this.aaB;
      float f8 = this.Rs;
      float f9 = this.bM;
      float f10 = this.KI;
      float f11 = this.hC;
      String s = String.valueOf(this.td);
      float f = f11;
      float f1 = f10;
      float f2 = f9;
      float f3 = f8;
      float f4 = f7;
      float f5 = f6;
      return "BuiltBorderOutline[width="
         + f5
         + ", height="
         + f4
         + ", cornerRadius="
         + f3
         + ", borderThickness="
         + f2
         + ", borderSoftness="
         + f1
         + ", margin="
         + f
         + ", borderColor="
         + s
         + "]";
   }
}
