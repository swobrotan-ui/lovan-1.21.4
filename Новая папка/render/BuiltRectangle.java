package render;

import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import java.awt.Color;
import java.util.Arrays;
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

public final class BuiltRectangle implements Renderable {
   private static final ShaderProgramKey VQ = ShaderUtil.a("rectangle", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final float ST = 1.2F;
   private static final float asY = -2.0F;
   private static final float TB = 0.5F;
   private static final float aqm = 1.0F;
   private static final float GL = 0.3F;
   private static final float ej = 0.3F;
   private static final float abi = 0.003F;
   private static final float nw = 0.0F;
   private static final float Mo = 1.5F;
   private static final float[] cn = new float[]{0.168627F, 0.180392F, 0.196078F, 1.0F};
   private static final float[] ls = new float[]{0.098039F, 0.101961F, 0.113725F, 1.0F};
   private static final float[] aoG = new float[]{0.058824F, 0.058824F, 0.066667F, 1.0F};
   private static final float[] bG = new float[]{0.015686F, 0.015686F, 0.015686F, 1.0F};
   private static final float[] Hv = new float[]{0.670588F, 0.678431F, 0.690196F, 1.0F};
   private static final float[] aiV = new float[]{0.462745F, 0.462745F, 0.45098F, 1.0F};
   private static final float[] arS = new float[]{0.262745F, 0.266667F, 0.270588F, 1.0F};
   private static final float[] axD = new float[]{0.129412F, 0.129412F, 0.129412F, 1.0F};
   private static final float[] pj = new float[]{0.282353F, 0.298039F, 0.32549F, 1.0F};
   private static final float[] lP = new float[]{0.14902F, 0.14902F, 0.14902F, 1.0F};
   private static final float[] no = new float[]{0.062745F, 0.062745F, 0.062745F, 0.0F};
   private static final float[] qU = new float[]{0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] OV = new float[]{0.67451F, 0.682353F, 0.690196F, 1.0F};
   private static final float[] arb = new float[]{0.737255F, 0.741176F, 0.733333F, 1.0F};
   private static final float[] azf = new float[]{1.0F, 1.0F, 1.0F, 0.0F};
   private static final float[] alK = new float[]{1.0F, 1.0F, 1.0F, 0.0F};
   private static final long ba = 4000L;
   private static long ahZ = 0L;
   private static boolean Gr = false;
   private static String oo = "Стандарт";
   private static long tu = 0L;
   private static final float[] Pc = new float[]{cn[0], cn[1], cn[2], cn[3]};
   private static final float[] asu = new float[]{ls[0], ls[1], ls[2], ls[3]};
   private static final float[] fi = new float[]{aoG[0], aoG[1], aoG[2], aoG[3]};
   private static final float[] tK = new float[]{bG[0], bG[1], bG[2], bG[3]};
   private static final float[] akh = new float[]{pj[0], pj[1], pj[2], pj[3]};
   private static final float[] tl = new float[]{lP[0], lP[1], lP[2], lP[3]};
   private static final float[] ap = new float[]{no[0], no[1], no[2], no[3]};
   private static final float[] Vl = new float[]{qU[0], qU[1], qU[2], qU[3]};
   private static final float[] Sc = new float[4];
   private static final float[] ajJ = new float[4];
   private static final float[] atE = new float[4];
   private static final float[] ep = new float[4];
   private static boolean arU = false;
   private static final float[] Ba = new float[4];
   private static final float[] kn = new float[4];
   private static final float[] arw = new float[4];
   private static final float[] ij = new float[4];
   private static boolean anO = false;
   private static final float[] pb = new float[4];
   private static final float[] OT = new float[4];
   private static final float[] alw = new float[4];
   private static final float[] rW = new float[4];
   private static final float[] azr = new float[4];
   private static final float[] ans = new float[4];
   private static final float[] qk = new float[4];
   private static final float[] ip = new float[4];
   private final float ati;
   private final float aoi;
   private final float qA;
   private final float[] UC;
   private final float[] tA;
   private final float[] anB;
   private final float[] mw;
   private BuiltLiquidGlass QD = null;
   private int Fw = -1;
   private float wu = Float.NaN;
   private float avv = Float.NaN;

   public BuiltRectangle(float f, float f1) {
      this(f, f1, 8.0F);
   }

   public BuiltRectangle(float f, float f1, float f2) {
      this(f, f1, f2, null, null, null, null);
   }

   public BuiltRectangle(float f, float f1, float f2, float[] afloat, float[] afloat1, float[] afloat2, float[] afloat3) {
      this.ati = f;
      this.aoi = f1;
      this.qA = f2;
      this.UC = afloat != null ? afloat : cn;
      this.tA = afloat1 != null ? afloat1 : ls;
      this.anB = afloat2 != null ? afloat2 : aoG;
      this.mw = afloat3 != null ? afloat3 : bG;
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

   private static void d(float[] afloat, float[] afloat1) {
      afloat1[0] = afloat[0];
      afloat1[1] = afloat[1];
      afloat1[2] = afloat[2];
      afloat1[3] = afloat[3];
   }

   private static void e(Color color, float[] afloat) {
      afloat[0] = color.getRed() / 255.0F;
      afloat[1] = color.getGreen() / 255.0F;
      afloat[2] = color.getBlue() / 255.0F;
      afloat[3] = color.getAlpha() / 255.0F;
   }

   private static boolean f(float[] afloat, float[] afloat1) {
      if (afloat != null && afloat1 != null) {
         if (afloat.length != afloat1.length) {
            return false;
         } else {
            for (int i = 0; i < afloat.length; i++) {
               if (Math.abs(afloat[i] - afloat1[i]) > 0.001F) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private static void g(String s, CustomThemeModule customthememodule) {
      long i = System.currentTimeMillis();
      if (i != tu) {
         long j = i - tu;
         tu = i;
         if (Gr || !s.equals(oo) || s.equals("Кастом")) {
            if (s.equals("Кастом") && customthememodule != null) {
               e(customthememodule.h().getColor(), pb);
               e(customthememodule.i().getColor(), OT);
               e(customthememodule.j().getColor(), alw);
               e(customthememodule.k().getColor(), rW);
               e(customthememodule.l().getColor(), azr);
               e(customthememodule.m().getColor(), ans);
               e(customthememodule.n().getColor(), qk);
               e(customthememodule.o().getColor(), ip);
               boolean flag = false;
               if (arU && oo.equals("Кастом")) {
                  flag = !f(pb, Sc) || !f(OT, ajJ) || !f(alw, atE) || !f(rW, ep) || !f(azr, Ba) || !f(ans, kn) || !f(qk, arw) || !f(ip, ij);
               }

               d(pb, Sc);
               d(OT, ajJ);
               d(alw, atE);
               d(rW, ep);
               d(azr, Ba);
               d(ans, kn);
               d(qk, arw);
               d(ip, ij);
               arU = true;
               anO = true;
               if (flag) {
                  ahZ = i;
                  Gr = true;
               }
            } else if (s.equals("Сильвер")) {
               d(Hv, pb);
               d(aiV, OT);
               d(arS, alw);
               d(axD, rW);
               d(OV, azr);
               d(arb, ans);
               d(azf, qk);
               d(alK, ip);
            } else {
               d(cn, pb);
               d(ls, OT);
               d(aoG, alw);
               d(bG, rW);
               d(pj, azr);
               d(lP, ans);
               d(no, qk);
               d(qU, ip);
            }

            if (!s.equals(oo)) {
               ahZ = i;
               Gr = true;
               oo = s;
            }

            float f = 1.0F;
            if (Gr) {
               long k = i - ahZ;
               f = Math.min(1.0F, (float)k / 4000.0F);
               f = b(f);
               if (k >= 4000L) {
                  Gr = false;
                  f = 1.0F;
               }
            }

            c(Pc, pb, f);
            c(asu, OT, f);
            c(fi, alw, f);
            c(tK, rW, f);
            c(akh, azr, f);
            c(tl, ans, f);
            c(ap, qk, f);
            c(Vl, ip, f);
         }
      }
   }

   private void h(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      CustomThemeModule customthememodule = ClientMain.getInstance().getModuleManager().<CustomThemeModule>getModule(CustomThemeModule.class);
      if (customthememodule != null && customthememodule.e()) {
         this.i(matrix4f, f, f1, f2, f3, customthememodule);
      } else {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();
         ShaderProgram shaderprogram = RenderSystem.setShader(VQ);
         float f4 = Math.abs(-2.0F) + 1.2F + 0.3F;
         shaderprogram.getUniform("Size").set(this.ati, this.aoi);
         shaderprogram.getUniform("Offset").set(f4, f4);
         shaderprogram.getUniform("CornerRadius").set(this.qA);
         shaderprogram.getUniform("BlurRadius").set(1.2F);
         shaderprogram.getUniform("ShadowOffset").set(-2.0F);
         shaderprogram.getUniform("ShadowStrength").set(0.5F);
         shaderprogram.getUniform("BorderThickness").set(1.0F);
         shaderprogram.getUniform("BorderSoftness").set(0.3F);
         shaderprogram.getUniform("EdgeSoftness").set(0.3F);
         shaderprogram.getUniform("DitherStrength").set(0.003F);
         shaderprogram.getUniform("Margin").set(0.0F);
         shaderprogram.getUniform("GlobalAlpha").set(f3);
         String s = "Стандарт";
         if (customthememodule != null) {
            shaderprogram.getUniform("NoiseStrength").set(customthememodule.u().getFloatValue());
            shaderprogram.getUniform("RadialScale").set(customthememodule.p().getFloatValue(), customthememodule.q().getFloatValue());
            shaderprogram.getUniform("RadialRadius").set(customthememodule.r().getFloatValue());
            shaderprogram.getUniform("RadialCenter").set(customthememodule.s().getFloatValue(), customthememodule.t().getFloatValue());
            String s1 = customthememodule.f().getFirst();
            if (s1.contains("Сильвер")) {
               s = "Сильвер";
            } else if (s1.contains("Кастом")) {
               s = "Кастом";
            }
         }

         g(s, customthememodule);
         shaderprogram.getUniform("GradientColor1").set(Pc[0], Pc[1], Pc[2], Pc[3]);
         shaderprogram.getUniform("GradientColor2").set(asu[0], asu[1], asu[2], asu[3]);
         shaderprogram.getUniform("GradientColor3").set(fi[0], fi[1], fi[2], fi[3]);
         shaderprogram.getUniform("GradientColor4").set(tK[0], tK[1], tK[2], tK[3]);
         shaderprogram.getUniform("BorderColor1").set(akh[0], akh[1], akh[2], akh[3]);
         shaderprogram.getUniform("BorderColor2").set(tl[0], tl[1], tl[2], tl[3]);
         shaderprogram.getUniform("BorderColor3").set(ap[0], ap[1], ap[2], ap[3]);
         shaderprogram.getUniform("BorderColor4").set(Vl[0], Vl[1], Vl[2], Vl[3]);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         float f5 = f - f4;
         float f6 = f1 - f4;
         float f7 = this.ati + f4 * 2.0F;
         float f8 = this.aoi + f4 * 2.0F;
         int i = (int)(f3 * 255.0F) << 24 | 16777215;
         bufferbuilder.vertex(matrix4f, f5, f6, f2).color(i);
         bufferbuilder.vertex(matrix4f, f5, f6 + f8, f2).color(i);
         bufferbuilder.vertex(matrix4f, f5 + f7, f6 + f8, f2).color(i);
         bufferbuilder.vertex(matrix4f, f5 + f7, f6, f2).color(i);
         BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
         RenderSystem.enableCull();
         RenderSystem.disableBlend();
      }
   }

   private void i(Matrix4f matrix4f, float f, float f1, float f5, float f2, CustomThemeModule customthememodule) {
      int i = (int)(f2 * 255.0F);
      float f3 = customthememodule.w().getFloatValue();
      float f4 = customthememodule.x().getFloatValue();
      if (this.QD == null || this.Fw != i || this.wu != f3 || this.avv != f4) {
         int j = i << 24 | 16777215;
         this.QD = new BuiltLiquidGlass(
            new Size(this.ati, this.aoi),
            new RadiusConfig(this.qA, this.qA, this.qA, this.qA),
            new ColorPair(j, j, j, j),
            2.0F,
            2.0F,
            f3,
            0.5F,
            5.5F,
            f4,
            2.0F,
            0.7F,
            2.0F
         );
         this.Fw = i;
         this.wu = f3;
         this.avv = f4;
      }

      this.QD.a(matrix4f, f, f1, f2);
   }

   public static void j(
      Matrix4f matrix4f, float f, float f1, float f2, float f3, int i, int j, int k, int l, float f4, boolean flag, Color color, Color color1, float f5
   ) {
      k(matrix4f, f, f1, f2, f3, i, j, k, l, f4, flag, color, color1, f5, 0.0F);
   }

   public static void k(
      Matrix4f matrix4f,
      float f,
      float f1,
      float f2,
      float f3,
      int i,
      int j,
      int k,
      int l,
      float f4,
      boolean flag,
      Color color,
      Color color1,
      float f5,
      float f6
   ) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      ShaderProgram shaderprogram = RenderSystem.setShader(VQ);
      float f7 = f3 * 2.0F;
      float f8 = 5.0F;
      shaderprogram.getUniform("Size").set(f7, f7);
      shaderprogram.getUniform("Offset").set(f8, f8);
      shaderprogram.getUniform("GlobalAlpha").set(f5);
      shaderprogram.getUniform("EdgeSoftness").set(1.5F);
      shaderprogram.getUniform("BorderThickness").set(1.0F);
      shaderprogram.getUniform("BorderSoftness").set(0.5F);
      shaderprogram.getUniform("NoiseStrength").set(1.5F);
      shaderprogram.getUniform("BorderColor1").set(0.35F, 0.37F, 0.4F, 0.6F);
      shaderprogram.getUniform("BorderColor2").set(0.2F, 0.2F, 0.2F, 0.3F);
      shaderprogram.getUniform("BorderColor3").set(0.1F, 0.1F, 0.1F, 0.0F);
      shaderprogram.getUniform("BorderColor4").set(0.0F, 0.0F, 0.0F, 0.0F);
      shaderprogram.getUniform("RingMode").set(1);
      shaderprogram.getUniform("InnerRadius").set(f2);
      shaderprogram.getUniform("OuterRadius").set(f3);
      shaderprogram.getUniform("SegmentCount").set(i);
      shaderprogram.getUniform("GapAngle").set(0.03F);
      shaderprogram.getUniform("RotationOffset").set(f6);
      shaderprogram.getUniform("HoverSegment").set(j);
      shaderprogram.getUniform("PendingSegment").set(k);
      shaderprogram.getUniform("FlashSegment").set(l);
      shaderprogram.getUniform("FlashProgress").set(f4);
      shaderprogram.getUniform("IsFlashDelete").set(flag ? 1 : 0);
      if (color != null) {
         float f9 = color.getRed() / 255.0F;
         float f10 = color.getGreen() / 255.0F;
         float f11 = color.getBlue() / 255.0F;
         float f12 = color.getAlpha() / 255.0F;
         shaderprogram.getUniform("RingBaseColor").set(f9, f10, f11, f12);
      } else {
         shaderprogram.getUniform("RingBaseColor").set(0.8F, 0.8F, 0.8F, 0.37F);
      }

      if (color1 != null) {
         float f13 = color1.getRed() / 255.0F;
         float f14 = color1.getGreen() / 255.0F;
         float f16 = color1.getBlue() / 255.0F;
         float f18 = color1.getAlpha() / 255.0F;
         shaderprogram.getUniform("RingHoverColor").set(f13, f14, f16, f18);
      } else {
         shaderprogram.getUniform("RingHoverColor").set(1.0F, 0.82F, 0.18F, 0.55F);
      }

      shaderprogram.getUniform("RingPendingColor").set(0.39F, 0.78F, 1.0F, 0.55F);
      shaderprogram.getUniform("RingFlashAddColor").set(0.39F, 1.0F, 0.39F, 0.55F);
      shaderprogram.getUniform("RingFlashDeleteColor").set(1.0F, 0.27F, 0.27F, 0.55F);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      float f15 = f - f3 - f8;
      float f17 = f1 - f3 - f8;
      float f19 = f7 + f8 * 2.0F;
      int i1 = (int)(f5 * 255.0F) << 24 | 16777215;
      bufferbuilder.vertex(matrix4f, f15, f17, 0.0F).color(i1);
      bufferbuilder.vertex(matrix4f, f15, f17 + f19, 0.0F).color(i1);
      bufferbuilder.vertex(matrix4f, f15 + f19, f17 + f19, 0.0F).color(i1);
      bufferbuilder.vertex(matrix4f, f15 + f19, f17, 0.0F).color(i1);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      shaderprogram.getUniform("RingMode").set(0);
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   public float l() {
      return this.ati;
   }

   public float m() {
      return this.aoi;
   }

   public float n() {
      return this.qA;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltRectangle builtrectangle1 = (BuiltRectangle)object;
         return Float.floatToIntBits(this.ati) == Float.floatToIntBits(builtrectangle1.ati)
            && Float.floatToIntBits(this.aoi) == Float.floatToIntBits(builtrectangle1.aoi)
            && Float.floatToIntBits(this.qA) == Float.floatToIntBits(builtrectangle1.qA)
            && Arrays.equals(this.UC, builtrectangle1.UC)
            && Arrays.equals(this.tA, builtrectangle1.tA)
            && Arrays.equals(this.anB, builtrectangle1.anB)
            && Arrays.equals(this.mw, builtrectangle1.mw);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.ati, this.aoi, this.qA, Arrays.hashCode(this.UC), Arrays.hashCode(this.tA), Arrays.hashCode(this.anB), Arrays.hashCode(this.mw));
   }

   @Override
   public String toString() {
      float f3 = this.ati;
      float f4 = this.aoi;
      float f5 = this.qA;
      String s4 = Arrays.toString(this.UC);
      String s5 = Arrays.toString(this.tA);
      String s6 = Arrays.toString(this.anB);
      String s = Arrays.toString(this.mw);
      String s1 = s6;
      String s2 = s5;
      String s3 = s4;
      float f = f5;
      float f1 = f4;
      float f2 = f3;
      return "BuiltRectangle[width="
         + f2
         + ", height="
         + f1
         + ", cornerRadius="
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
