package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FogShape;
import setting.ColorSetting;
import setting.SliderSetting;

public class CustomFogModule extends Module {
   private ColorSetting fogColorSetting = new ColorSetting("Цвет тумана", "1", Color.WHITE, true);
   private SliderSetting fogStartSetting = new SliderSetting("Начало тумана", "2", 0.0, 0.0, 200.0, 1.0);
   private SliderSetting fogEndSetting = new SliderSetting("Конец тумана", "3", 29.0, 5.0, 500.0, 1.0);
   private SliderSetting fogDensitySetting = new SliderSetting("Плотность тумана", "4", 2.4F, -1.0, 10.0, 0.01F);
   private boolean hZ = false;
   private long xo = 0L;
   private boolean nd = false;
   private boolean Ud = false;
   private static final long Ap = 800L;
   private static float Fv = 0.0F;
   private static float if = 800.0F;
   private static float VG = 0.753F;
   private static float adF = 0.847F;
   private static float aui = 1.0F;
   private float Gd;
   private float rm;
   private float anE;
   private float Ek;
   private float YZ;

   public CustomFogModule() {
      super("СузтомФог", "Кастомный туман с настройками цвета и дистанции", Category.VISUAL);
      this.addSettings(this.fogColorSetting, this.fogStartSetting, this.fogEndSetting, this.fogDensitySetting);
   }

   @Override
   public void onEnable() {
      this.Gd = Fv;
      this.rm = if;
      Color color = this.fogColorSetting.getColor();
      this.anE = color.getRed() / 255.0F;
      this.Ek = color.getGreen() / 255.0F;
      this.YZ = color.getBlue() / 255.0F;
      this.xo = System.currentTimeMillis();
      this.nd = true;
      this.Ud = true;
      this.hZ = true;
   }

   @Override
   public void onDisable() {
      Color color = this.fogColorSetting.getColor();
      float f = this.fogDensitySetting.getFloatValue();
      this.Gd = this.fogStartSetting.getFloatValue();
      this.rm = this.fogEndSetting.getFloatValue();
      if (f > 0.01F) {
         float f1 = 1.0F / f;
         this.Gd /= f1;
         this.rm /= f1;
      }

      this.anE = color.getRed() / 255.0F;
      this.Ek = color.getGreen() / 255.0F;
      this.YZ = color.getBlue() / 255.0F;
      this.xo = System.currentTimeMillis();
      this.nd = true;
      this.Ud = false;
      this.hZ = false;
   }

   private float a(float f) {
      return f < 0.5F ? 4.0F * f * f * f : 1.0F - (float)Math.pow(-2.0F * f + 2.0F, 3.0) / 2.0F;
   }

   private float b(float f, float f1, float f2) {
      return f + (f1 - f) * f2;
   }

   @Override
   public void onRenderAfterSetup(WorldRenderContext worldrendercontext) {
      if (this.hZ || this.nd) {
         this.c();
      }
   }

   public void c() {
      if (this.getWorld() != null) {
         Fog fog = this.d();
         if (fog != null) {
            RenderSystem.setShaderFog(fog);
         }
      }
   }

   public Fog d() {
      if (this.getWorld() == null) {
         return null;
      } else {
         float f;
         float f1;
         float f2;
         float f3;
         float f4;
         if (this.nd) {
            long i = System.currentTimeMillis() - this.xo;
            float f6 = Math.min(1.0F, (float)i / 800.0F);
            float f7 = this.a(f6);
            if (f6 >= 1.0F) {
               this.nd = false;
            }

            if (this.Ud) {
               Color color = this.fogColorSetting.getColor();
               float f8 = this.fogDensitySetting.getFloatValue();
               float f9 = this.fogStartSetting.getFloatValue();
               float f10 = this.fogEndSetting.getFloatValue();
               if (f8 > 0.01F) {
                  float f11 = 1.0F / f8;
                  f9 /= f11;
                  f10 /= f11;
               }

               f = this.b(this.Gd, f9, f7);
               f1 = this.b(this.rm, f10, f7);
               f2 = this.b(this.anE, color.getRed() / 255.0F, f7);
               f3 = this.b(this.Ek, color.getGreen() / 255.0F, f7);
               f4 = this.b(this.YZ, color.getBlue() / 255.0F, f7);
            } else {
               f = this.b(this.Gd, Fv, f7);
               f1 = this.b(this.rm, if, f7);
               f2 = this.b(this.anE, VG, f7);
               f3 = this.b(this.Ek, adF, f7);
               f4 = this.b(this.YZ, aui, f7);
            }
         } else {
            if (!this.hZ) {
               return null;
            }

            Color color1 = this.fogColorSetting.getColor();
            f2 = color1.getRed() / 255.0F;
            f3 = color1.getGreen() / 255.0F;
            f4 = color1.getBlue() / 255.0F;
            float f5 = this.fogDensitySetting.getFloatValue();
            f = this.fogStartSetting.getFloatValue();
            f1 = this.fogEndSetting.getFloatValue();
            if (f5 > 0.01F) {
               float f12 = 1.0F / f5;
               f /= f12;
               f1 /= f12;
            }
         }

         return new Fog(f, f1, FogShape.SPHERE, f2, f3, f4, 1.0F);
      }
   }

   public boolean e() {
      return this.hZ || this.nd;
   }

   public ColorSetting f() {
      return this.fogColorSetting;
   }

   public SliderSetting g() {
      return this.fogStartSetting;
   }

   public SliderSetting h() {
      return this.fogEndSetting;
   }

   public SliderSetting i() {
      return this.fogDensitySetting;
   }
}
