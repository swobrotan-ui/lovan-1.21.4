package render;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public final class BuiltLine3d implements Renderable {
   private static final ShaderProgramKey cE = ShaderUtil.a("line3d", VertexFormats.LINES, Defines.EMPTY);
   private static final Identifier ahU = Identifier.of("minecraft", "textures/glow_line.png");
   private static final double[][] ahu = new double[9][];
   private static final double[][] Da = new double[9][];
   private final float gO;
   private final float pK;
   private final float Rk;
   private final float kE;
   private final float Hl;
   private final float Yd;
   private final boolean aaO;
   private final boolean ho;
   private final float auD;
   private final float EE;
   private final float wf;
   private final float amq;
   private final float Ck;
   private final float hA;
   private final int sT;
   private final boolean PE;
   private final boolean sk;
   private final float Kk;
   private final float ahO;
   private final int alE;

   public BuiltLine3d(
      float f,
      float f1,
      float f2,
      float f3,
      float f4,
      float f5,
      boolean flag,
      boolean flag1,
      float f6,
      float f7,
      float f8,
      float f9,
      float f10,
      float f11,
      int i,
      boolean flag2,
      boolean flag3,
      float f12,
      float f13,
      int j
   ) {
      this.gO = f;
      this.pK = f1;
      this.Rk = f2;
      this.kE = f3;
      this.Hl = f4;
      this.Yd = f5;
      this.aaO = flag;
      this.ho = flag1;
      this.auD = f6;
      this.EE = f7;
      this.wf = f8;
      this.amq = f9;
      this.Ck = f10;
      this.hA = f11;
      this.sT = i;
      this.PE = flag2;
      this.sk = flag3;
      this.Kk = f12;
      this.ahO = f13;
      this.alE = j;
   }

   @Override
   public void render(Matrix4f matrix4f, float f, float f1, float f2) {
   }

   private void a(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d2) {
      this.b(bufferbuilder, matrix4f, vec3d, vec3d1, this.gO, this.pK, this.Rk, this.kE, this.Hl);
   }

   private void b(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d1, float f, float f1, float f2, float f3, float f4) {
      Vec3d vec3d2 = vec3d1.subtract(vec3d);
      double d0 = vec3d2.length();
      if (!(d0 < 0.001)) {
         vec3d2 = vec3d2.normalize();
         Vec3d vec3d3;
         if (Math.abs(vec3d2.y) < 0.9) {
            vec3d3 = new Vec3d(0.0, 1.0, 0.0).crossProduct(vec3d2).normalize();
         } else {
            vec3d3 = new Vec3d(1.0, 0.0, 0.0).crossProduct(vec3d2).normalize();
         }

         Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).normalize();
         double d1 = f * 0.01;
         Vec3d vec3d5 = vec3d3.multiply(d1);
         Vec3d vec3d6 = vec3d4.multiply(d1);
         Vec3d[] avec3d = new Vec3d[this.sT];
         Vec3d[] avec3d1 = new Vec3d[this.sT];
         Vec3d[] avec3d2 = new Vec3d[this.sT];
         double[] adouble = ahu[this.sT];
         double[] adouble1 = Da[this.sT];

         for (int i = 0; i < this.sT; i++) {
            double d2 = adouble[i];
            double d3 = adouble1[i];
            Vec3d vec3d7 = vec3d5.multiply(d2).add(vec3d6.multiply(d3));
            avec3d[i] = vec3d.add(vec3d7);
            avec3d1[i] = vec3d1.add(vec3d7);
            avec3d2[i] = vec3d3.multiply(d2).add(vec3d4.multiply(d3)).normalize();
         }

         int k = (int)(f4 * 255.0F) << 24 | (int)(f1 * 255.0F) << 16 | (int)(f2 * 255.0F) << 8 | (int)(f3 * 255.0F);

         for (int l = 0; l < this.sT; l++) {
            int j = (l + 1) % this.sT;
            bufferbuilder.vertex(matrix4f, (float)avec3d[l].x, (float)avec3d[l].y, (float)avec3d[l].z)
               .color(k)
               .normal((float)avec3d2[l].x, (float)avec3d2[l].y, (float)avec3d2[l].z);
            bufferbuilder.vertex(matrix4f, (float)avec3d1[l].x, (float)avec3d1[l].y, (float)avec3d1[l].z)
               .color(k)
               .normal((float)avec3d2[l].x, (float)avec3d2[l].y, (float)avec3d2[l].z);
            bufferbuilder.vertex(matrix4f, (float)avec3d1[j].x, (float)avec3d1[j].y, (float)avec3d1[j].z)
               .color(k)
               .normal((float)avec3d2[j].x, (float)avec3d2[j].y, (float)avec3d2[j].z);
            bufferbuilder.vertex(matrix4f, (float)avec3d[l].x, (float)avec3d[l].y, (float)avec3d[l].z)
               .color(k)
               .normal((float)avec3d2[l].x, (float)avec3d2[l].y, (float)avec3d2[l].z);
            bufferbuilder.vertex(matrix4f, (float)avec3d1[j].x, (float)avec3d1[j].y, (float)avec3d1[j].z)
               .color(k)
               .normal((float)avec3d2[j].x, (float)avec3d2[j].y, (float)avec3d2[j].z);
            bufferbuilder.vertex(matrix4f, (float)avec3d[j].x, (float)avec3d[j].y, (float)avec3d[j].z)
               .color(k)
               .normal((float)avec3d2[j].x, (float)avec3d2[j].y, (float)avec3d2[j].z);
         }

         if (this.aaO) {
            Vec3d vec3d8 = vec3d2.multiply(-1.0);

            for (int i1 = 1; i1 < this.sT - 1; i1++) {
               bufferbuilder.vertex(matrix4f, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z)
                  .color(k)
                  .normal((float)vec3d8.x, (float)vec3d8.y, (float)vec3d8.z);
               bufferbuilder.vertex(matrix4f, (float)avec3d[i1].x, (float)avec3d[i1].y, (float)avec3d[i1].z)
                  .color(k)
                  .normal((float)vec3d8.x, (float)vec3d8.y, (float)vec3d8.z);
               bufferbuilder.vertex(matrix4f, (float)avec3d[i1 + 1].x, (float)avec3d[i1 + 1].y, (float)avec3d[i1 + 1].z)
                  .color(k)
                  .normal((float)vec3d8.x, (float)vec3d8.y, (float)vec3d8.z);
            }

            Vec3d vec3d9 = vec3d2;

            for (int j1 = 1; j1 < this.sT - 1; j1++) {
               bufferbuilder.vertex(matrix4f, (float)vec3d1.x, (float)vec3d1.y, (float)vec3d1.z)
                  .color(k)
                  .normal((float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z);
               bufferbuilder.vertex(matrix4f, (float)avec3d1[j1 + 1].x, (float)avec3d1[j1 + 1].y, (float)avec3d1[j1 + 1].z)
                  .color(k)
                  .normal((float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z);
               bufferbuilder.vertex(matrix4f, (float)avec3d1[j1].x, (float)avec3d1[j1].y, (float)avec3d1[j1].z)
                  .color(k)
                  .normal((float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z);
            }
         }
      }
   }

   private void c(MatrixStack matrixstack, Vec3d vec3d, rh rh) {
      if (rh.c() != 0) {
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         if (this.sk) {
            this.d(matrix4f, vec3d, rh);
         } else {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            if (this.PE) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthMask(false);
            } else {
               RenderSystem.disableDepthTest();
            }

            ShaderProgram shaderprogram = RenderSystem.setShader(cE);
            if (shaderprogram.getUniform("LineWidth") != null) {
               shaderprogram.getUniform("LineWidth").set(this.gO);
            }

            if (shaderprogram.getUniform("LineColor") != null) {
               shaderprogram.getUniform("LineColor").set(this.pK, this.Rk, this.kE, this.Hl);
            }

            if (shaderprogram.getUniform("Softness") != null) {
               shaderprogram.getUniform("Softness").set(this.Yd);
            }

            if (shaderprogram.getUniform("RoundCaps") != null) {
               shaderprogram.getUniform("RoundCaps").set(this.aaO ? 1.0F : 0.0F);
            }

            if (shaderprogram.getUniform("ShadowEnabled") != null) {
               shaderprogram.getUniform("ShadowEnabled").set(this.ho ? 1.0F : 0.0F);
            }

            if (shaderprogram.getUniform("ShadowOffset") != null) {
               shaderprogram.getUniform("ShadowOffset").set(this.auD, this.EE);
            }

            if (shaderprogram.getUniform("ShadowColor") != null) {
               shaderprogram.getUniform("ShadowColor").set(this.wf, this.amq, this.Ck, this.hA);
            }

            if (shaderprogram.getUniform("LightDirection") != null) {
               shaderprogram.getUniform("LightDirection").set(0.2F, 1.0F, 0.3F);
            }

            if (shaderprogram.getUniform("AmbientStrength") != null) {
               shaderprogram.getUniform("AmbientStrength").set(0.4F);
            }

            BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.TRIANGLES, VertexFormats.LINES);

            for (int i = 0; i < rh.c(); i++) {
               this.a(bufferbuilder, matrix4f, rh.cj.get(i), rh.Bs.get(i), vec3d);
            }

            BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         }
      }
   }

   private void d(Matrix4f matrix4f, Vec3d vec3d, rh rh) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      AbstractTexture abstracttexture = minecraftclient.getTextureManager().getTexture(ahU);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
      RenderSystem.depthMask(false);
      if (this.PE) {
         RenderSystem.enableDepthTest();
      } else {
         RenderSystem.disableDepthTest();
      }

      RenderSystem.disableCull();
      RenderSystem.setShaderTexture(0, abstracttexture.getGlId());
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_COLOR);
      float f = this.gO * 0.01F * this.Kk;
      int i = (int)(this.Hl * this.ahO * 255.0F) << 24 | (int)(this.pK * 255.0F) << 16 | (int)(this.Rk * 255.0F) << 8 | (int)(this.kE * 255.0F);

      for (int j = 0; j < rh.c(); j++) {
         Vec3d vec3d1 = rh.cj.get(j);
         Vec3d vec3d2 = rh.Bs.get(j);
         this.e(bufferbuilder, matrix4f, vec3d1, vec3d2, vec3d, f, i);
      }

      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.enableCull();
   }

   private void e(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d11, float f, int i) {
      Vec3d vec3d2 = vec3d1.subtract(vec3d);
      double d0 = vec3d2.length();
      if (!(d0 < 0.001)) {
         vec3d2 = vec3d2.normalize();
         Vec3d vec3d3;
         if (Math.abs(vec3d2.y) < 0.9) {
            vec3d3 = new Vec3d(0.0, 1.0, 0.0).crossProduct(vec3d2).normalize();
         } else {
            vec3d3 = new Vec3d(1.0, 0.0, 0.0).crossProduct(vec3d2).normalize();
         }

         Vec3d vec3d4 = vec3d2.crossProduct(vec3d3).normalize();
         float f1 = f;
         int j = Math.max(4, this.sT);

         for (int k = 0; k < j; k++) {
            double d1 = Math.PI * k / j;
            double d2 = Math.cos(d1);
            double d3 = Math.sin(d1);
            Vec3d vec3d5 = vec3d3.multiply(d2).add(vec3d4.multiply(d3));
            Vec3d vec3d6 = vec3d5.multiply(f1);
            Vec3d vec3d7 = vec3d.subtract(vec3d6);
            Vec3d vec3d8 = vec3d1.subtract(vec3d6);
            Vec3d vec3d9 = vec3d1.add(vec3d6);
            Vec3d vec3d10 = vec3d.add(vec3d6);
            bufferbuilder.vertex(matrix4f, (float)vec3d7.x, (float)vec3d7.y, (float)vec3d7.z).texture(0.0F, 0.0F).color(i);
            bufferbuilder.vertex(matrix4f, (float)vec3d8.x, (float)vec3d8.y, (float)vec3d8.z).texture(1.0F, 0.0F).color(i);
            bufferbuilder.vertex(matrix4f, (float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z).texture(1.0F, 1.0F).color(i);
            bufferbuilder.vertex(matrix4f, (float)vec3d7.x, (float)vec3d7.y, (float)vec3d7.z).texture(0.0F, 0.0F).color(i);
            bufferbuilder.vertex(matrix4f, (float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z).texture(1.0F, 1.0F).color(i);
            bufferbuilder.vertex(matrix4f, (float)vec3d10.x, (float)vec3d10.y, (float)vec3d10.z).texture(0.0F, 1.0F).color(i);
         }

         this.f(bufferbuilder, matrix4f, vec3d, vec3d2.multiply(-1.0), vec3d3, vec3d4, f1, i);
         this.f(bufferbuilder, matrix4f, vec3d1, vec3d2, vec3d3, vec3d4, f1, i);
      }
   }

   private void f(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d2, Vec3d vec3d3, float f, int i) {
      int j = Math.max(6, this.sT);
      int k = i & 16777215;
      float f1 = 0.5F;
      float f2 = 0.5F;

      for (int l = 0; l < 3; l++) {
         float f3 = f * l / 3.0F;
         float f4 = f * (l + 1) / 3.0F;
         float f5 = f * 0.3F * l / 3.0F;
         float f6 = f * 0.3F * (l + 1) / 3.0F;
         int i1 = (int)((i >> 24 & 0xFF) * (1.0F - l / 3.0F));
         int j1 = (int)((i >> 24 & 0xFF) * (1.0F - (l + 1) / 3.0F));
         int k1 = i1 << 24 | i & 16777215;
         int l1 = j1 << 24 | i & 16777215;

         for (int i2 = 0; i2 < j; i2++) {
            double d0 = (Math.PI * 2) * i2 / j;
            double d1 = (Math.PI * 2) * (i2 + 1) / j;
            double d2 = Math.cos(d0);
            double d3 = Math.sin(d0);
            double d4 = Math.cos(d1);
            double d5 = Math.sin(d1);
            Vec3d vec3d4 = vec3d2.multiply(d2).add(vec3d3.multiply(d3));
            Vec3d vec3d5 = vec3d2.multiply(d4).add(vec3d3.multiply(d5));
            Vec3d vec3d6 = vec3d.add(vec3d1.multiply(f5)).add(vec3d4.multiply(f3));
            Vec3d vec3d7 = vec3d.add(vec3d1.multiply(f5)).add(vec3d5.multiply(f3));
            Vec3d vec3d8 = vec3d.add(vec3d1.multiply(f6)).add(vec3d4.multiply(f4));
            Vec3d vec3d9 = vec3d.add(vec3d1.multiply(f6)).add(vec3d5.multiply(f4));
            float f7 = f1 + (float)(d2 * f3 / f) * 0.5F;
            float f8 = f2 + (float)(d3 * f3 / f) * 0.5F;
            float f9 = f1 + (float)(d4 * f3 / f) * 0.5F;
            float f10 = f2 + (float)(d5 * f3 / f) * 0.5F;
            float f11 = f1 + (float)(d2 * f4 / f) * 0.5F;
            float f12 = f2 + (float)(d3 * f4 / f) * 0.5F;
            float f13 = f1 + (float)(d4 * f4 / f) * 0.5F;
            float f14 = f2 + (float)(d5 * f4 / f) * 0.5F;
            bufferbuilder.vertex(matrix4f, (float)vec3d6.x, (float)vec3d6.y, (float)vec3d6.z).texture(f7, f8).color(k1);
            bufferbuilder.vertex(matrix4f, (float)vec3d8.x, (float)vec3d8.y, (float)vec3d8.z).texture(f11, f12).color(l1);
            bufferbuilder.vertex(matrix4f, (float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z).texture(f13, f14).color(l1);
            bufferbuilder.vertex(matrix4f, (float)vec3d6.x, (float)vec3d6.y, (float)vec3d6.z).texture(f7, f8).color(k1);
            bufferbuilder.vertex(matrix4f, (float)vec3d9.x, (float)vec3d9.y, (float)vec3d9.z).texture(f13, f14).color(l1);
            bufferbuilder.vertex(matrix4f, (float)vec3d7.x, (float)vec3d7.y, (float)vec3d7.z).texture(f9, f10).color(k1);
         }
      }
   }

   public void g(MatrixStack matrixstack, Vec3d vec3d, float f, float f1) {
      float f2 = f / 2.0F;
      rh rh = new rh();
      rh.a(new Vec3d(-f2, 0.0, -f2), new Vec3d(-f2, 0.0, f2));
      rh.a(new Vec3d(-f2, 0.0, f2), new Vec3d(f2, 0.0, f2));
      rh.a(new Vec3d(f2, 0.0, f2), new Vec3d(f2, 0.0, -f2));
      rh.a(new Vec3d(f2, 0.0, -f2), new Vec3d(-f2, 0.0, -f2));
      rh.a(new Vec3d(-f2, f1, -f2), new Vec3d(-f2, f1, f2));
      rh.a(new Vec3d(-f2, f1, f2), new Vec3d(f2, f1, f2));
      rh.a(new Vec3d(f2, f1, f2), new Vec3d(f2, f1, -f2));
      rh.a(new Vec3d(f2, f1, -f2), new Vec3d(-f2, f1, -f2));
      rh.a(new Vec3d(-f2, 0.0, -f2), new Vec3d(-f2, f1, -f2));
      rh.a(new Vec3d(-f2, 0.0, f2), new Vec3d(-f2, f1, f2));
      rh.a(new Vec3d(f2, 0.0, f2), new Vec3d(f2, f1, f2));
      rh.a(new Vec3d(f2, 0.0, -f2), new Vec3d(f2, f1, -f2));
      this.c(matrixstack, vec3d, rh);
   }

   public xzs h() {
      return new xzs(this);
   }

   public float i() {
      return this.gO;
   }

   public float j() {
      return this.pK;
   }

   public float k() {
      return this.Rk;
   }

   public float l() {
      return this.kE;
   }

   public float m() {
      return this.Hl;
   }

   public float n() {
      return this.Yd;
   }

   public boolean o() {
      return this.aaO;
   }

   public boolean p() {
      return this.ho;
   }

   public float q() {
      return this.auD;
   }

   public float r() {
      return this.EE;
   }

   public float s() {
      return this.wf;
   }

   public float t() {
      return this.amq;
   }

   public float u() {
      return this.Ck;
   }

   public float v() {
      return this.hA;
   }

   public int w() {
      return this.sT;
   }

   public boolean x() {
      return this.PE;
   }

   public boolean y() {
      return this.sk;
   }

   public float z() {
      return this.Kk;
   }

   public float A() {
      return this.ahO;
   }

   public int B() {
      return this.alE;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         BuiltLine3d builtline3d1 = (BuiltLine3d)object;
         return Float.floatToIntBits(this.gO) == Float.floatToIntBits(builtline3d1.gO)
            && Float.floatToIntBits(this.pK) == Float.floatToIntBits(builtline3d1.pK)
            && Float.floatToIntBits(this.Rk) == Float.floatToIntBits(builtline3d1.Rk)
            && Float.floatToIntBits(this.kE) == Float.floatToIntBits(builtline3d1.kE)
            && Float.floatToIntBits(this.Hl) == Float.floatToIntBits(builtline3d1.Hl)
            && Float.floatToIntBits(this.Yd) == Float.floatToIntBits(builtline3d1.Yd)
            && this.aaO == builtline3d1.aaO
            && this.ho == builtline3d1.ho
            && Float.floatToIntBits(this.auD) == Float.floatToIntBits(builtline3d1.auD)
            && Float.floatToIntBits(this.EE) == Float.floatToIntBits(builtline3d1.EE)
            && Float.floatToIntBits(this.wf) == Float.floatToIntBits(builtline3d1.wf)
            && Float.floatToIntBits(this.amq) == Float.floatToIntBits(builtline3d1.amq)
            && Float.floatToIntBits(this.Ck) == Float.floatToIntBits(builtline3d1.Ck)
            && Float.floatToIntBits(this.hA) == Float.floatToIntBits(builtline3d1.hA)
            && this.sT == builtline3d1.sT
            && this.PE == builtline3d1.PE
            && this.sk == builtline3d1.sk
            && Float.floatToIntBits(this.Kk) == Float.floatToIntBits(builtline3d1.Kk)
            && Float.floatToIntBits(this.ahO) == Float.floatToIntBits(builtline3d1.ahO)
            && this.alE == builtline3d1.alE;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.gO,
         this.pK,
         this.Rk,
         this.kE,
         this.Hl,
         this.Yd,
         this.aaO,
         this.ho,
         this.auD,
         this.EE,
         this.wf,
         this.amq,
         this.Ck,
         this.hA,
         this.sT,
         this.PE,
         this.sk,
         this.Kk,
         this.ahO,
         this.alE
      );
   }

   @Override
   public String toString() {
      int i = this.alE;
      float f = this.ahO;
      float f1 = this.Kk;
      boolean flag = this.sk;
      boolean flag1 = this.PE;
      int j = this.sT;
      float f2 = this.hA;
      float f3 = this.Ck;
      float f4 = this.amq;
      float f5 = this.wf;
      float f6 = this.EE;
      float f7 = this.auD;
      boolean flag2 = this.ho;
      boolean flag3 = this.aaO;
      float f8 = this.Yd;
      float f9 = this.Hl;
      float f10 = this.kE;
      float f11 = this.Rk;
      float f12 = this.pK;
      float f13 = this.gO;
      return "BuiltLine3d[lineWidth="
         + f13
         + ", red="
         + f12
         + ", green="
         + f11
         + ", blue="
         + f10
         + ", alpha="
         + f9
         + ", softness="
         + f8
         + ", roundCaps="
         + flag3
         + ", shadowEnabled="
         + flag2
         + ", shadowOffsetX="
         + f7
         + ", shadowOffsetY="
         + f6
         + ", shadowRed="
         + f5
         + ", shadowGreen="
         + f4
         + ", shadowBlue="
         + f3
         + ", shadowAlpha="
         + f2
         + ", prismSides="
         + j
         + ", depthTest="
         + flag1
         + ", glowEnabled="
         + flag
         + ", glowRadius="
         + f1
         + ", glowIntensity="
         + f
         + ", glowLayers="
         + i
         + "]";
   }

   static {
      for (int i = 3; i <= 8; i++) {
         ahu[i] = new double[i];
         Da[i] = new double[i];

         for (int j = 0; j < i; j++) {
            double d0 = (Math.PI * 2) * j / i;
            ahu[i][j] = Math.cos(d0);
            Da[i][j] = Math.sin(d0);
         }
      }
   }
}
