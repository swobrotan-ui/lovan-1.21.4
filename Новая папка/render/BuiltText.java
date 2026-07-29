package render;

import com.mojang.blaze3d.systems.RenderSystem;
import font.MSDFFont;
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

public final class BuiltText implements Renderable {
   private static final ShaderProgramKey aoq = ShaderUtil.a("msdf_font", VertexFormats.POSITION_TEXTURE_COLOR, Defines.EMPTY);
   private static final int kF = 16;
   private static final float[] bQ = new float[16];
   private static final float[] aAn = new float[16];
   private static final float[] GT;
   private static final float[] i;
   private static final float[] aaL;
   private static final float[] Ld;
   private final MSDFFont SH;
   private final String kH;
   private final float lN;
   private final float akR;
   private final int FI;
   private final float UJ;
   private final float Nu;
   private final int DY;
   private final float QN;
   private final boolean ee;
   private final int jd;
   private final int fG;
   private final float qp;
   private final float ZJ;
   private final float amR;
   private final float WC;
   private final float PT;
   private final float Pm;
   private final float ek;
   private final float dg;

   public BuiltText(MSDFFont msdffont, String s, float f, float f1, int i, float f2, float f3, int j, float f4) {
      this(msdffont, s, f, f1, ix, f2, f3, j, f4, true);
   }

   public BuiltText(MSDFFont msdffont, String s, float f, float f1, int i, float f2, float f3, int j, float f4, boolean flag) {
      this(msdffont, s, f, f1, ix, f2, f3, j, f4, flag, -14737633, -1, 0.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.1F, 0.15F, 0.25F);
   }

   public BuiltText(
      MSDFFont msdffont,
      String s,
      float f,
      float f1,
      int i,
      float f2,
      float f3,
      int j,
      float f4,
      boolean flag,
      int k,
      int l,
      float f5,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      float f12
   ) {
      this.SH = msdffont;
      this.kH = s;
      this.lN = f;
      this.akR = f1;
      this.FI = ix;
      this.UJ = f2;
      this.Nu = f3;
      this.DY = j;
      this.QN = f4;
      this.ee = flag;
      this.jd = k;
      this.fG = l;
      this.qp = f5;
      this.ZJ = f6;
      this.amR = f7;
      this.WC = f8;
      this.PT = f9;
      this.Pm = f10;
      this.ek = f11;
      this.dg = f12;
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
      RenderSystem.setShaderTexture(0, this.SH.a());
      boolean flag = this.QN > 0.0F;
      ShaderProgram shaderprogram = RenderSystem.setShader(aoq);
      byte b0 = -1;
      if (this.dg > 0.0F && (this.PT != 0.0F || this.Pm != 0.0F || this.ek > 0.0F)) {
         shaderprogram.getUniform("Range").set(this.SH.d().a());
         shaderprogram.getUniform("Thickness").set(this.akR);
         shaderprogram.getUniform("Smoothness").set(this.UJ + this.ek * 2.0F);
         shaderprogram.getUniform("Outline").set(0);
         shaderprogram.getUniform("UseGradient").set(0);
         float f4 = this.dg * f3;
         int ix = this.ek > 0.0F ? 9 : 1;
         float f5 = f4 / ix;
         shaderprogram.getUniform("TextColor").set(0.0F, 0.0F, 0.0F, f5);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (int j = 0; j < ix; j++) {
            float f6 = 0.0F;
            float f7 = 0.0F;
            if (ix > 1) {
               int k = j / 3 - 1;
               int l = j % 3 - 1;
               f6 = l * this.ek * this.lN * 0.5F;
               f7 = k * this.ek * this.lN * 0.5F;
            }

            float f12 = f + this.PT * this.lN + f6;
            float f14 = f1 + this.Pm * this.lN + f7;
            this.SH
               .b(
                  matrix4f,
                  bufferbuilder,
                  this.kH,
                  this.lN,
                  (this.akR + this.QN * 0.5F) * 0.5F * this.lN,
                  this.Nu,
                  f12,
                  f14 + this.SH.e().d() * this.lN,
                  f2 - 0.01F,
                  b0
               );
         }

         BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      }

      if (this.WC > 0.0F && this.amR > 0.0F) {
         shaderprogram.getUniform("Range").set(this.SH.d().a());
         shaderprogram.getUniform("Thickness").set(this.akR - this.amR * 0.3F);
         shaderprogram.getUniform("Smoothness").set(this.UJ + this.amR * 3.0F);
         shaderprogram.getUniform("Outline").set(0);
         shaderprogram.getUniform("UseGradient").set(0);
         float f8 = this.WC * f3;
         byte b1 = 16;
         float f9 = f8 / b1;
         shaderprogram.getUniform("TextColor").set(1.0F, 1.0F, 1.0F, f9);
         BufferBuilder bufferbuilder1 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         float f10 = this.amR * this.lN;

         for (int i1 = 0; i1 < b1; i1++) {
            float f11 = bQ[i1] * f10;
            float f13 = aAn[i1] * f10;
            this.SH
               .b(
                  matrix4f,
                  bufferbuilder1,
                  this.kH,
                  this.lN,
                  (this.akR + this.QN * 0.5F) * 0.5F * this.lN,
                  this.Nu,
                  f + f11,
                  f1 + f13 + this.SH.e().d() * this.lN,
                  f2 - 0.005F,
                  b0
               );
         }

         BufferRenderer.drawWithGlobalProgram(bufferbuilder1.end());
      }

      shaderprogram.getUniform("Range").set(this.SH.d().a());
      shaderprogram.getUniform("Thickness").set(this.akR);
      shaderprogram.getUniform("Smoothness").set(this.UJ);
      shaderprogram.getUniform("Outline").set(flag ? 1 : 0);
      if (flag) {
         shaderprogram.getUniform("OutlineThickness").set(this.QN);
         db.e(this.DY, i);
         shaderprogram.getUniform("OutlineColor").set(i[0], i[1], i[2], i[3] * f3);
      }

      db.e(this.FI, GT);
      shaderprogram.getUniform("TextColor").set(GT[0], GT[1], GT[2], GT[3] * f3);
      String s = this.SH.f();
      boolean flag1 = "a".equals(s) || "aa".equals(s);
      boolean flag2 = (this.FI & 16777215) == 16777215;
      boolean flag3 = this.ee && !flag1 && flag2;
      shaderprogram.getUniform("UseGradient").set(flag3 ? 1 : 0);
      if (flag3) {
         db.e(this.jd, aaL);
         db.e(this.fG, Ld);
         shaderprogram.getUniform("GradientColor1").set(aaL[0], aaL[1], aaL[2], aaL[3]);
         shaderprogram.getUniform("GradientColor2").set(Ld[0], Ld[1], Ld[2], Ld[3]);
         shaderprogram.getUniform("GradientCenter").set(this.qp, this.ZJ);
      }

      BufferBuilder bufferbuilder2 = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      this.SH.b(matrix4f, bufferbuilder2, this.kH, this.lN, (this.akR + this.QN * 0.5F) * 0.5F * this.lN, this.Nu, f, f1 + this.SH.e().d() * this.lN, f2, b0);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder2.end());
      RenderSystem.setShaderTexture(0, 0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public MSDFFont c() {
      return this.SH;
   }

   public String d() {
      return this.kH;
   }

   public float e() {
      return this.lN;
   }

   public float f() {
      return this.akR;
   }

   public int g() {
      return this.FI;
   }

   public float h() {
      return this.UJ;
   }

   public float i() {
      return this.Nu;
   }

   public int j() {
      return this.DY;
   }

   public float k() {
      return this.QN;
   }

   public boolean l() {
      return this.ee;
   }

   public int m() {
      return this.jd;
   }

   public int n() {
      return this.fG;
   }

   public float o() {
      return this.qp;
   }

   public float p() {
      return this.ZJ;
   }

   public float q() {
      return this.amR;
   }

   public float r() {
      return this.WC;
   }

   public float s() {
      return this.PT;
   }

   public float t() {
      return this.Pm;
   }

   public float u() {
      return this.ek;
   }

   public float v() {
      return this.dg;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltText builttext1 = (BuiltText)object;
         return Objects.equals(this.SH, builttext1.SH)
            && Objects.equals(this.kH, builttext1.kH)
            && Float.floatToIntBits(this.lN) == Float.floatToIntBits(builttext1.lN)
            && Float.floatToIntBits(this.akR) == Float.floatToIntBits(builttext1.akR)
            && this.FI == builttext1.FI
            && Float.floatToIntBits(this.UJ) == Float.floatToIntBits(builttext1.UJ)
            && Float.floatToIntBits(this.Nu) == Float.floatToIntBits(builttext1.Nu)
            && this.DY == builttext1.DY
            && Float.floatToIntBits(this.QN) == Float.floatToIntBits(builttext1.QN)
            && this.ee == builttext1.ee
            && this.jd == builttext1.jd
            && this.fG == builttext1.fG
            && Float.floatToIntBits(this.qp) == Float.floatToIntBits(builttext1.qp)
            && Float.floatToIntBits(this.ZJ) == Float.floatToIntBits(builttext1.ZJ)
            && Float.floatToIntBits(this.amR) == Float.floatToIntBits(builttext1.amR)
            && Float.floatToIntBits(this.WC) == Float.floatToIntBits(builttext1.WC)
            && Float.floatToIntBits(this.PT) == Float.floatToIntBits(builttext1.PT)
            && Float.floatToIntBits(this.Pm) == Float.floatToIntBits(builttext1.Pm)
            && Float.floatToIntBits(this.ek) == Float.floatToIntBits(builttext1.ek)
            && Float.floatToIntBits(this.dg) == Float.floatToIntBits(builttext1.dg);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.SH,
         this.kH,
         this.lN,
         this.akR,
         this.FI,
         this.UJ,
         this.Nu,
         this.DY,
         this.QN,
         this.ee,
         this.jd,
         this.fG,
         this.qp,
         this.ZJ,
         this.amR,
         this.WC,
         this.PT,
         this.Pm,
         this.ek,
         this.dg
      );
   }

   @Override
   public String toString() {
      String s2 = String.valueOf(this.SH);
      float f = this.dg;
      float f1 = this.ek;
      float f2 = this.Pm;
      float f3 = this.PT;
      float f4 = this.WC;
      float f5 = this.amR;
      float f6 = this.ZJ;
      float f7 = this.qp;
      int ix = this.fG;
      int j = this.jd;
      boolean flag = this.ee;
      float f8 = this.QN;
      int k = this.DY;
      float f9 = this.Nu;
      float f10 = this.UJ;
      int l = this.FI;
      float f11 = this.akR;
      float f12 = this.lN;
      String s = this.kH;
      String s1 = s2;
      return "BuiltText[font="
         + s1
         + ", text="
         + s
         + ", size="
         + f12
         + ", thickness="
         + f11
         + ", color="
         + l
         + ", smoothness="
         + f10
         + ", spacing="
         + f9
         + ", outlineColor="
         + k
         + ", outlineThickness="
         + f8
         + ", useGradient="
         + flag
         + ", gradientColor1="
         + j
         + ", gradientColor2="
         + ix
         + ", gradientCenterX="
         + f7
         + ", gradientCenterY="
         + f6
         + ", glowRadius="
         + f5
         + ", glowIntensity="
         + f4
         + ", shadowOffsetX="
         + f3
         + ", shadowOffsetY="
         + f2
         + ", shadowBlur="
         + f1
         + ", shadowIntensity="
         + f
         + "]";
   }

   static {
      for (int ix = 0; ix < 16; ix++) {
         double d0 = ix * 2.0 * Math.PI / 16.0;
         bQ[ix] = (float)Math.cos(d0);
         aAn[ix] = (float)Math.sin(d0);
      }

      GT = new float[4];
      i = new float[4];
      aaL = new float[4];
      Ld = new float[4];
   }
}
