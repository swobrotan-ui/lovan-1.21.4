package render;

import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import java.awt.Color;
import java.util.Objects;
import module.CustomThemeModule;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import org.joml.Matrix4f;

public final class BuiltWhiteRectangle implements Renderable {
   private static final ShaderProgramKey zJ = ShaderUtil.a("white_rectangle", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final float An = 0.3F;
   private static final float avc = 2.5F;
   private static final float dF = 1.5F;
   private static final float[] oI = new float[]{1.0F, 1.0F, 1.0F};
   private static final float[] NF = new float[]{0.670588F, 0.678431F, 0.690196F, 1.0F};
   private static final float[] apW = new float[]{0.462745F, 0.462745F, 0.45098F, 1.0F};
   private static final float[] CY = new float[]{0.262745F, 0.266667F, 0.270588F, 1.0F};
   private static final float[] LR = new float[]{0.129412F, 0.129412F, 0.129412F, 1.0F};
   private static final long asl = 4000L;
   private static long tt = 0L;
   private static boolean ah = false;
   private static String jb = "Стандарт";
   private static long MX = 0L;
   private static final float[] zY = new float[4];
   private static final float[] cu = new float[4];
   private static final float[] rM = new float[4];
   private static final float[] RN = new float[4];
   private static boolean ex = false;
   private static final float[] cy = new float[4];
   private static final float[] ajz = new float[4];
   private static final float[] akE = new float[4];
   private static final float[] FF = new float[4];
   private static final float[] akM = new float[4];
   private static final float[] Fm = new float[4];
   private static final float[] wH = new float[4];
   private static final float[] PF = new float[4];
   private static boolean amW = false;
   private final float EI;
   private final float acD;
   private final float tn;
   private final float Oy;
   private final float PO;
   private final float tS;
   private final float axN;
   private final float QI;
   private final float avf;
   private final float[] Yo;
   private final float[] wg;
   private final float[] jm;
   private final float[] agI;

   public BuiltWhiteRectangle(
      float f,
      float f1,
      float f2,
      float f3,
      float f4,
      float f5,
      float f6,
      float f7,
      float f8,
      float[] afloat,
      float[] afloat1,
      float[] afloat2,
      float[] afloat3
   ) {
      this.EI = f;
      this.acD = f1;
      this.tn = f2;
      this.Oy = f3;
      this.PO = f4;
      this.tS = f5;
      this.axN = f6;
      this.QI = f7;
      this.avf = f8;
      this.Yo = afloat;
      this.wg = afloat1;
      this.jm = afloat2;
      this.agI = afloat3;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
      this.h(matrix4f, f, f1, f2, 1.0F);
   }

   public void a(Matrix4f matrix4f, float f, float f1, float f2) {
      this.h(matrix4f, f, f1, 0.0F, f2);
   }

   private static float b(float f) {
      return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
   }

   private static void c(float[] afloat, float[] afloat1, float f) {
      for (int i = 0; i < 4; i++) {
         afloat[i] += (afloat1[i] - afloat[i]) * f;
      }
   }

   private static void d(Color color, float[] afloat) {
      afloat[0] = color.getRed() / 255.0F;
      afloat[1] = color.getGreen() / 255.0F;
      afloat[2] = color.getBlue() / 255.0F;
      afloat[3] = color.getAlpha() / 255.0F;
   }

   private static void e(float[] afloat, float[] afloat1) {
      afloat1[0] = afloat[0];
      afloat1[1] = afloat[1];
      afloat1[2] = afloat[2];
      afloat1[3] = afloat[3];
   }

   private static boolean f(float[] afloat, float[] afloat1) {
      for (int i = 0; i < 4; i++) {
         if (Math.abs(afloat[i] - afloat1[i]) > 0.001F) {
            return false;
         }
      }

      return true;
   }

   private void g(String s, CustomThemeModule customthememodule) {
      long i = System.currentTimeMillis();
      if (i != MX) {
         MX = i;
         if (ah || !s.equals(jb) || s.equals("Кастом")) {
            if (!ex) {
               e(this.Yo, zY);
               e(this.wg, cu);
               e(this.jm, rM);
               e(this.agI, RN);
               ex = true;
            }

            if (s.equals("Кастом") && customthememodule != null) {
               d(customthememodule.h().getColor(), cy);
               d(customthememodule.i().getColor(), ajz);
               d(customthememodule.j().getColor(), akE);
               d(customthememodule.k().getColor(), FF);
               boolean flag = false;
               if (amW && jb.equals("Кастом")) {
                  flag = !f(cy, akM) || !f(ajz, Fm) || !f(akE, wH) || !f(FF, PF);
               }

               e(cy, akM);
               e(ajz, Fm);
               e(akE, wH);
               e(FF, PF);
               amW = true;
               if (flag) {
                  tt = i;
                  ah = true;
               }
            } else if (s.equals("Сильвер")) {
               e(NF, cy);
               e(apW, ajz);
               e(CY, akE);
               e(LR, FF);
            } else {
               e(this.Yo, cy);
               e(this.wg, ajz);
               e(this.jm, akE);
               e(this.agI, FF);
            }

            if (!s.equals(jb)) {
               tt = i;
               ah = true;
               jb = s;
            }

            float f = 1.0F;
            if (ah) {
               long j = i - tt;
               f = Math.min(1.0F, (float)j / 4000.0F);
               f = b(f);
               if (j >= 4000L) {
                  ah = false;
                  f = 1.0F;
               }
            }

            c(zY, cy, f);
            c(cu, ajz, f);
            c(rM, akE, f);
            c(RN, FF, f);
         }
      }
   }

   private void h(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      CustomThemeModule customthememodule = ClientMain.getInstance().getModuleManager().<CustomThemeModule>getModule(CustomThemeModule.class);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      ShaderProgram shaderprogram = RenderSystem.setShader(zJ);
      float f4 = Math.abs(this.PO) + this.Oy + this.axN + 2.0F;
      shaderprogram.getUniform("Size").set(this.EI, this.acD);
      shaderprogram.getUniform("Offset").set(f4, f4);
      shaderprogram.getUniform("CornerRadius").set(this.tn);
      shaderprogram.getUniform("BlurRadius").set(this.Oy);
      shaderprogram.getUniform("ShadowOffset").set(this.PO);
      shaderprogram.getUniform("ShadowStrength").set(this.tS);
      shaderprogram.getUniform("BorderThickness").set(0.3F);
      shaderprogram.getUniform("BorderSoftness").set(2.5F);
      shaderprogram.getUniform("EdgeSoftness").set(this.axN);
      shaderprogram.getUniform("DitherStrength").set(this.QI);
      shaderprogram.getUniform("Margin").set(this.avf);
      shaderprogram.getUniform("NoiseStrength").set(1.5F);
      shaderprogram.getUniform("GlobalAlpha").set(f3);
      String s = "Стандарт";
      if (customthememodule != null) {
         String s1 = customthememodule.f().getFirst();
         if (s1.contains("Сильвер")) {
            s = "Сильвер";
         } else if (s1.contains("Кастом")) {
            s = "Кастом";
         }
      }

      this.g(s, customthememodule);
      shaderprogram.getUniform("GradientColor1").set(zY[0], zY[1], zY[2], zY[3] * f3);
      shaderprogram.getUniform("GradientColor2").set(cu[0], cu[1], cu[2], cu[3] * f3);
      shaderprogram.getUniform("GradientColor3").set(rM[0], rM[1], rM[2], rM[3] * f3);
      shaderprogram.getUniform("GradientColor4").set(RN[0], RN[1], RN[2], RN[3] * f3);
      shaderprogram.getUniform("BorderColor").set(oI[0], oI[1], oI[2]);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float f5 = f - f4;
      float f6 = f1 - f4;
      float f7 = this.EI + f4 * 2.0F;
      float f8 = this.acD + f4 * 2.0F;
      int i = (int)(f3 * 255.0F) << 24 | 16777215;
      bufferbuilder.vertex(matrix4f, f5, f6, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5, f6 + f8, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5 + f7, f6 + f8, f2).color(i);
      bufferbuilder.vertex(matrix4f, f5 + f7, f6, f2).color(i);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public float i() {
      return this.EI;
   }

   public float j() {
      return this.acD;
   }

   public float k() {
      return this.tn;
   }

   public float l() {
      return this.Oy;
   }

   public float m() {
      return this.PO;
   }

   public float n() {
      return this.tS;
   }

   public float o() {
      return this.axN;
   }

   public float p() {
      return this.QI;
   }

   public float q() {
      return this.avf;
   }

   public float[] r() {
      return this.Yo;
   }

   public float[] s() {
      return this.wg;
   }

   public float[] t() {
      return this.jm;
   }

   public float[] u() {
      return this.agI;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltWhiteRectangle builtwhiterectangle1 = (BuiltWhiteRectangle)object;
         return Float.floatToIntBits(this.EI) == Float.floatToIntBits(builtwhiterectangle1.EI)
            && Float.floatToIntBits(this.acD) == Float.floatToIntBits(builtwhiterectangle1.acD)
            && Float.floatToIntBits(this.tn) == Float.floatToIntBits(builtwhiterectangle1.tn)
            && Float.floatToIntBits(this.Oy) == Float.floatToIntBits(builtwhiterectangle1.Oy)
            && Float.floatToIntBits(this.PO) == Float.floatToIntBits(builtwhiterectangle1.PO)
            && Float.floatToIntBits(this.tS) == Float.floatToIntBits(builtwhiterectangle1.tS)
            && Float.floatToIntBits(this.axN) == Float.floatToIntBits(builtwhiterectangle1.axN)
            && Float.floatToIntBits(this.QI) == Float.floatToIntBits(builtwhiterectangle1.QI)
            && Float.floatToIntBits(this.avf) == Float.floatToIntBits(builtwhiterectangle1.avf)
            && Objects.equals(this.Yo, builtwhiterectangle1.Yo)
            && Objects.equals(this.wg, builtwhiterectangle1.wg)
            && Objects.equals(this.jm, builtwhiterectangle1.jm)
            && Objects.equals(this.agI, builtwhiterectangle1.agI);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.EI, this.acD, this.tn, this.Oy, this.PO, this.tS, this.axN, this.QI, this.avf, this.Yo, this.wg, this.jm, this.agI);
   }

   @Override
   public String toString() {
      float f9 = this.EI;
      float f10 = this.acD;
      float f11 = this.tn;
      float f12 = this.Oy;
      float f13 = this.PO;
      float f14 = this.tS;
      float f15 = this.axN;
      float f16 = this.QI;
      float f17 = this.avf;
      String s4 = String.valueOf(this.Yo);
      String s5 = String.valueOf(this.wg);
      String s6 = String.valueOf(this.jm);
      String s = String.valueOf(this.agI);
      String s1 = s6;
      String s2 = s5;
      String s3 = s4;
      float f = f17;
      float f1 = f16;
      float f2 = f15;
      float f3 = f14;
      float f4 = f13;
      float f5 = f12;
      float f6 = f11;
      float f7 = f10;
      float f8 = f9;
      return "BuiltWhiteRectangle[width="
         + f8
         + ", height="
         + f7
         + ", cornerRadius="
         + f6
         + ", blurRadius="
         + f5
         + ", shadowOffset="
         + f4
         + ", shadowStrength="
         + f3
         + ", edgeSoftness="
         + f2
         + ", ditherStrength="
         + f1
         + ", margin="
         + f
         + ", gradientColor1="
         + s3
         + ", gradientColor2="
         + s2
         + ", gradientColor3="
         + s1
         + ", gradientColor4="
         + s
         + "]";
   }
}
