package render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltColorPicker implements Renderable {
   private static final ShaderProgramKey Dt = ShaderUtil.a("colorpicker", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private final float yL;
   private final float eJ;
   private final vt BV;
   private final float asJ;
   private final float mx;
   private final float up;
   private final float EW;

   public BuiltColorPicker(float f, float f1, vt vt, float f2, float f3, float f4, float f5) {
      this.yL = f;
      this.eJ = f1;
      this.BV = vt;
      this.asJ = f2;
      this.mx = f3;
      this.up = f4;
      this.EW = f5;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      this.a(matrix4f, f, f1, 1.0F);
   }

   public void a(Matrix4f matrix4f, float f, float f1, float f2) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      ShaderProgram shaderprogram = RenderSystem.setShader(Dt);
      shaderprogram.getUniform("Size").set(this.yL, this.eJ);
      shaderprogram.getUniform("PickerType").set(this.BV.b());
      shaderprogram.getUniform("Hue").set(this.asJ);
      shaderprogram.getUniform("Saturation").set(this.mx);
      shaderprogram.getUniform("Brightness").set(this.up);
      shaderprogram.getUniform("Alpha").set(f2);
      shaderprogram.getUniform("CornerRadius").set(this.EW);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      int i = (int)(f2 * 255.0F) << 24 | 16777215;
      bufferbuilder.vertex(matrix4f, f, f1, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f, f1 + this.eJ, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f + this.yL, f1 + this.eJ, 0.0F).color(i);
      bufferbuilder.vertex(matrix4f, f + this.yL, f1, 0.0F).color(i);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public float b() {
      return this.yL;
   }

   public float c() {
      return this.eJ;
   }
}
