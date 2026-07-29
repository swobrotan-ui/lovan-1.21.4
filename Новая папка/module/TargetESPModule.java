package module;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class TargetESPModule extends Module {
   private LivingEntity Ym = null;
   private LivingEntity bb = null;
   private float a = 0.0F;
   private float ado = 0.0F;
   private float avX = 0.0F;
   private float Xr = 0.0F;
   private float TZ = 0.0F;
   private float sh = 0.0F;
   private float OD = 0.0F;
   private float amN = 0.0F;
   private float iQ = 0.0F;
   private float Ae = 0.0F;
   private long xB = 0L;
   private boolean amv = false;
   private static long aff = 3000L;
   private ListSetting modeSetting = new ListSetting(
      "Текстура",
      "Текстура призраков",
      List.<String>of("Обычный", "Треугольник", "Круг", "Квадрат", "Звезда"),
      Collections.<String>singletonList("Обычный"),
      false
   );
   private BooleanSetting colorAnimationSetting = new BooleanSetting("Анимация Цветов", "1", true);
   private ColorSetting colorSetting = new ColorSetting("Цвет", "1", new Color(66, 165, 245));
   private SliderSetting ghostSpeedSetting = new SliderSetting("Скорость Призраков", "1", 0.5, 0.1F, 2.0, 0.1F);
   private BooleanSetting hitAnimationSetting = new BooleanSetting("Анимация при ударе", "1", true);
   private ColorSetting hitColorSetting = new ColorSetting("Цвет удара", "1", Color.RED, true);
   private SliderSetting animationTimeSetting = new SliderSetting("Время анимации", "1", 800.0, 200.0, 2000.0, 50.0);
   private SliderSetting fadeStartSetting = new SliderSetting("Начало затухания", "1", 50.0, 20.0, 80.0, 5.0);
   private Map<Integer, dfi> eq = new HashMap<Integer, dfi>();
   private long ab = 0L;
   private final MatrixStack Sl = new MatrixStack();

   public TargetESPModule() {
      super("ТаргетЕЗП", "Подсвечивает цель при ударе", Category.VISUAL);
      this.addSettings(
         this.modeSetting,
         this.colorAnimationSetting,
         this.colorSetting,
         this.ghostSpeedSetting,
         this.hitAnimationSetting,
         this.hitColorSetting,
         this.animationTimeSetting,
         this.fadeStartSetting
      );
   }

   @Override
   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (this.isEnabled() && entity != null) {
         if (entity instanceof LivingEntity livingentity && livingentity.isAlive()) {
            if (this.Ym != entity) {
               this.bb = this.Ym;
               this.ado = this.a;
               this.amN = this.OD;
               this.Xr = this.ado;
               this.Ym = livingentity;
               this.a = 0.0F;
               this.OD = 0.0F;
               this.avX = 0.0F;
            }

            this.xB = System.currentTimeMillis();
            this.amv = true;
            if (this.hitAnimationSetting.getValue()) {
               dfi dfi = this.eq.computeIfAbsent(entity.getId(), integer -> {
                  return new dfi();
               });
               dfi.a();
            }
         }
      }
   }

   @Override
   public void onEnable() {
      this.ab = System.currentTimeMillis();
      this.xB = 0L;
      this.amv = false;
      this.a = 0.0F;
      this.ado = 0.0F;
      this.avX = 0.0F;
      this.Xr = 0.0F;
      this.TZ = 0.0F;
      this.sh = 0.0F;
   }

   @Override
   public void onDisable() {
      this.Ym = null;
      this.bb = null;
      this.a = 0.0F;
      this.ado = 0.0F;
      this.avX = 0.0F;
      this.Xr = 0.0F;
      this.TZ = 0.0F;
      this.sh = 0.0F;
      this.OD = 0.0F;
      this.amN = 0.0F;
      this.iQ = 0.0F;
      this.Ae = 0.0F;
      this.xB = 0L;
      this.amv = false;
      this.eq.clear();
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld()) {
         this.ab = System.currentTimeMillis();
         this.iQ = this.OD;
         this.Ae = this.amN;
         this.TZ = this.a;
         this.sh = this.ado;
         long i = System.currentTimeMillis() - this.xB;
         if (this.Ym != null && (this.Ym.isRemoved() || !this.Ym.isAlive())) {
            this.amv = false;
            this.avX = 0.0F;
         }

         if (this.amv && this.Ym != null && i < aff) {
            this.avX = 1.0F;
            this.a = this.f(this.a, this.avX, 0.1F);
         } else if (this.amv && i >= aff || this.Ym != null && (this.Ym.isRemoved() || !this.Ym.isAlive())) {
            this.avX = 0.0F;
            this.a = this.f(this.a, this.avX, 0.1F);
            if (this.a < 0.01F) {
               this.a = 0.0F;
               this.amv = false;
               this.Ym = null;
            }
         }

         if (this.bb != null) {
            this.Xr = 0.0F;
            this.ado = this.f(this.ado, this.Xr, 0.2F);
            if (this.ado < 0.01F) {
               this.ado = 0.0F;
               this.bb = null;
            }
         }

         if (this.a > 0.01F) {
            this.OD = this.OD + this.ghostSpeedSetting.getFloatValue();
         }

         if (this.ado > 0.01F) {
            this.amN = this.amN + this.ghostSpeedSetting.getFloatValue();
         }

         this.a();
      }
   }

   private void a() {
      Iterator iterator = this.eq.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         ((dfi)entry.getValue()).b();
         if (!((dfi)entry.getValue()).i()) {
            iterator.remove();
         }
      }
   }

   @Override
   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && this.getClient().currentScreen == null) {
         long i = System.currentTimeMillis();
         long j = i - this.ab;
         float f = Math.min((float)j / 50.0F, 1.0F);
         float f1 = this.f(this.TZ, this.a, f);
         float f2 = this.f(this.sh, this.ado, f);
         if (this.bb != null && f2 > 0.0F) {
            float f3 = this.Ae + (this.amN - this.Ae) * f;
            this.c(f, f2, this.bb, f3);
         }

         if (this.Ym != null && f1 > 0.0F) {
            float f4 = this.iQ + (this.OD - this.iQ) * f;
            this.c(f, f1, this.Ym, f4);
         }
      }
   }

   private Identifier b() {
      String s = this.modeSetting.getFirst();
      switch (s) {
         case "Треугольник":
            return this.glowPolygonTexture;
         case "Круг":
            return this.glowEllipseTexture;
         case "Квадрат":
            return this.glowSquareTexture;
         case "Звезда":
            return this.glowStarTexture;
         default:
            return this.glowTexture;
      }
   }

   private void c(float f, float f1, LivingEntity livingentity, float f2) {
      if (!(f1 <= 0.0F) && livingentity != null) {
         int i = this.k();
         int j = this.d(livingentity.getId(), i);
         float f3 = this.g(f1);
         Camera camera = this.getClient().getEntityRenderDispatcher().camera;
         Vec3d vec3d = camera.getPos();
         Vec3d vec3d1 = livingentity.getLerpedPos(f);
         boolean flag = this.getPlayer().canSee(livingentity);
         if (flag) {
            float f4 = camera.getPitch();
            float f5 = camera.getYaw();
            float f6 = (float)Math.cos(Math.toRadians(f4));
            float f7 = (float)Math.sin(Math.toRadians(f4));
            float f8 = (float)Math.cos(Math.toRadians(f5 + 90.0F));
            float f9 = (float)Math.sin(Math.toRadians(f5 + 90.0F));
            double d0 = f6 * f8;
            double d1 = -f7;
            double d2 = f6 * f9;
            float f10 = f2 * 3.2F;
            float f11 = f2 * -4.4F;
            float f12 = (float)Math.toRadians(f11);
            float f13 = livingentity.getWidth();
            float f14 = livingentity.getHeight() / 2.0F;
            float f15 = livingentity.getHeight() / 2.5F;
            byte b0 = 33;
            byte b1 = 1;
            float f16 = 0.2F;
            float f17 = 0.00515F;
            Identifier identifier = this.b();
            AbstractTexture abstracttexture = this.getClient().getTextureManager().getTexture(identifier);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, abstracttexture.getGlId());
            RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
            Tessellator tessellator = Tessellator.getInstance();

            for (int k = 0; k < 4; k++) {
               BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

               for (byte b2 = 0; b2 < b0; b2 += b1) {
                  float f18 = f10 + k * 90.0F - b2;
                  float f19 = (float)Math.sin(Math.toRadians(f18)) * f13;
                  float f20 = (float)Math.cos(Math.toRadians(f18)) * f13;
                  float f21 = f14 + (float)(Math.cos(Math.toRadians(f18 * 2.0F)) * f15);
                  float f22 = f19 * (float)Math.cos(f12) - f20 * (float)Math.sin(f12);
                  float f23 = f19 * (float)Math.sin(f12) + f20 * (float)Math.cos(f12);
                  double d3 = vec3d1.x + f22;
                  double d4 = vec3d1.y + f21;
                  double d5 = vec3d1.z + f23;
                  double d6 = d3 - vec3d.x;
                  double d7 = d4 - vec3d.y;
                  double d8 = d5 - vec3d.z;
                  double d9 = Math.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
                  if (d9 > 0.0) {
                     d6 /= d9;
                     d7 /= d9;
                     d8 /= d9;
                  }

                  double d10 = d6 * d0 + d7 * d1 + d8 * d2;
                  if (!(d10 < 0.0)) {
                     float f24 = (float)(b0 - b2) / b0;
                     float f25 = f16 + f17 * (b0 - b2);
                     int l = this.j((int)(f24 * 255.0F));
                     int i1 = this.i(j, l);
                     int j1 = this.h(i1, f24 * f3);
                     double d11 = d3 - vec3d.x;
                     double d12 = d4 - vec3d.y;
                     double d13 = d5 - vec3d.z;
                     this.Sl.push();
                     this.Sl.translate(d11, d12, d13);
                     this.Sl.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
                     this.Sl.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
                     Matrix4f matrix4f = this.Sl.peek().getPositionMatrix();
                     float f26 = f25 / 2.0F;
                     bufferbuilder.vertex(matrix4f, -f26, f25 - f26, 0.0F).texture(0.0F, 1.0F).color(j1);
                     bufferbuilder.vertex(matrix4f, f25 - f26, f25 - f26, 0.0F).texture(1.0F, 1.0F).color(j1);
                     bufferbuilder.vertex(matrix4f, f25 - f26, -f26, 0.0F).texture(1.0F, 0.0F).color(j1);
                     bufferbuilder.vertex(matrix4f, -f26, -f26, 0.0F).texture(0.0F, 0.0F).color(j1);
                     this.Sl.pop();
                  }
               }

               BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            }

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableBlend();
         }
      }
   }

   private int d(int i, int j) {
      if (!this.hitAnimationSetting.getValue()) {
         return j;
      } else {
         dfi dfi = this.eq.get(i);
         if (dfi != null && dfi.i()) {
            float f = dfi.c();
            Color color = this.hitColorSetting.getColor();
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int i1 = j & 0xFF;
            int j1 = j >> 24 & 0xFF;
            int k1 = (int)this.e(k / 255.0F, color.getRed() / 255.0F, f);
            int l1 = (int)this.e(l / 255.0F, color.getGreen() / 255.0F, f);
            int i2 = (int)this.e(i1 / 255.0F, color.getBlue() / 255.0F, f);
            int j2 = (int)this.e(j1 / 255.0F, color.getAlpha() / 255.0F, f);
            return j2 << 24 | k1 << 16 | l1 << 8 | i2;
         } else {
            return j;
         }
      }
   }

   private float e(float f, float f1, float f2) {
      return (f + (f1 - f) * f2) * 255.0F;
   }

   private float f(float f, float f1, float f2) {
      float f3 = f1 - f;
      return Math.abs(f3) < 0.001F ? f1 : f + f3 * f2;
   }

   private float g(float f) {
      return f < 0.5F ? 4.0F * f * f * f : (float)(1.0 - Math.pow(-2.0 * f + 2.0, 3.0) / 2.0);
   }

   private int h(int i, float f) {
      int j = (int)((i >> 24 & 0xFF) * f);
      return j << 24 | i & 16777215;
   }

   private int i(int i, int j) {
      int k = i >> 16 & 0xFF;
      int l = i >> 8 & 0xFF;
      int i1 = i & 0xFF;
      int j1 = i >> 24 & 0xFF;
      int k1 = j >> 16 & 0xFF;
      int l1 = j >> 8 & 0xFF;
      int i2 = j & 0xFF;
      int j2 = j >> 24 & 0xFF;
      int k2 = (int)(k + (k1 - k) * 0.7F);
      int l2 = (int)(l + (l1 - l) * 0.7F);
      int i3 = (int)(i1 + (i2 - i1) * 0.7F);
      int j3 = (int)(j1 + (j2 - j1) * 0.7F);
      return j3 << 24 | k2 << 16 | l2 << 8 | i3;
   }

   private int j(int i) {
      return 0xFF000000 | i << 16 | i << 8 | i;
   }

   private int k() {
      if (this.colorAnimationSetting.getValue()) {
         float f = (float)(System.currentTimeMillis() % 3000L) / 3000.0F;
         return Color.HSBtoRGB(f, 0.8F, 1.0F) | 0xFF000000;
      } else {
         Color color = this.colorSetting.getColor();
         return 0xFF000000 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
      }
   }

   public SliderSetting l() {
      return this.animationTimeSetting;
   }

   public SliderSetting m() {
      return this.fadeStartSetting;
   }
}
