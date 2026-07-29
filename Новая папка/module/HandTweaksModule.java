package module;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import event.UseItemEvent;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.gl.Defines;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import render.ShaderUtil;
import setting.ActionKeySetting;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class HandTweaksModule extends Module {
   private static final ShaderProgramKey XE = ShaderUtil.a("hand_solid", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final ShaderProgramKey hf = ShaderUtil.a("hand_liquidglass", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final ShaderProgramKey ala = ShaderUtil.a("hand_blend", VertexFormats.POSITION_COLOR, Defines.EMPTY);
   private static final List<String> QA = List.<String>of("Mix", "Add", "Multiply", "Difference");
   private ActionKeySetting swapHandKeySetting = new ActionKeySetting("Клавиша свапа руки", "Кнопка для смены руки (из левой в правую например)", -1);
   private BooleanSetting swapAfterHitSetting = new BooleanSetting("Свапать после удара", "Свап руки после удара", false);
   private BooleanSetting enableShaderSetting = new BooleanSetting("Включить шейдер", "Включает шейдерные эффекты на руке", false);
   private SliderSetting distortionStrengthSetting = new SliderSetting("Сила дисторшена", "Сила искажения LiquidGlass", 0.08, 0.0, 0.3, 0.001);
   private SliderSetting glassBrightnessSetting = new SliderSetting("Яркость стекла", "Яркость фона через стекло", 1.1, 0.5, 2.0, 0.05);
   private SliderSetting backgroundBlurSetting = new SliderSetting("Блюр фона", "Размытие фона за стеклом", 70.0, 0.0, 150.0, 5.0);
   private SliderSetting edgeRadiusSetting = new SliderSetting("Радиус краёв", "Дальность поиска краёв руки (пикселей)", 4.0, 1.0, 10.0, 1.0);
   private BooleanSetting dualShaderModeSetting = new BooleanSetting("Режим двойного шейдера", "Добавляет Solid шейдер поверх Glass", false);
   private ListSetting blendModeSetting = new ListSetting("Режим наложения", "Как смешивать два шейдера", QA, List.<String>of("Add"), false);
   private SliderSetting blendStrengthSetting = new SliderSetting("Сила наложения", "Интенсивность смешивания", 1.0, 0.0, 1.0, 0.05);
   private SliderSetting blurStrengthSetting = new SliderSetting("Сила Blur", "Яркость Glass шейдера", 1.0, 0.0, 1.0, 0.05);
   private SliderSetting solidStrengthSetting = new SliderSetting("Сила Solid", "Яркость Solid шейдера", 1.0, 0.0, 1.0, 0.05);
   private ColorSetting solidColor1Setting = new ColorSetting("Цвет 1 (Solid)", "Первый цвет градиента", new Color(230, 100, 80));
   private ColorSetting solidColor2Setting = new ColorSetting("Цвет 2 (Solid)", "Второй цвет градиента", new Color(80, 100, 230));
   private BooleanSetting gradientAnimationSetting = new BooleanSetting("Анимация градиента", "Включает анимацию перехода градиента", true);
   private SliderSetting gradientSpeedSetting = new SliderSetting("Скорость градиента", "Скорость анимации градиента", 0.5, 0.1, 2.0, 0.1);
   private SliderSetting gradientPositionSetting = new SliderSetting("Позиция градиента", "Ручная позиция градиента", 0.0, 0.0, 1.0, 0.01);
   private SimpleFramebuffer xA;
   private SimpleFramebuffer Qd;
   private SimpleFramebuffer hG;
   private SimpleFramebuffer HD;
   public boolean Pe;

   public HandTweaksModule() {
      super("ХандТшеакз", "Манипуляции с рукой", Category.VISUAL);
      this.distortionStrengthSetting.setVisibilitySupplier(this.enableShaderSetting::getValue);
      this.glassBrightnessSetting.setVisibilitySupplier(this.enableShaderSetting::getValue);
      this.backgroundBlurSetting.setVisibilitySupplier(this.enableShaderSetting::getValue);
      this.edgeRadiusSetting.setVisibilitySupplier(this.enableShaderSetting::getValue);
      this.dualShaderModeSetting.setVisibilitySupplier(this.enableShaderSetting::getValue);
      this.blendModeSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.blendStrengthSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.blurStrengthSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.solidStrengthSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.solidColor1Setting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.solidColor2Setting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.gradientAnimationSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue();
      });
      this.gradientSpeedSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue() && this.gradientAnimationSetting.getValue();
      });
      this.gradientPositionSetting.setVisibilitySupplier(() -> {
         return this.enableShaderSetting.getValue() && this.dualShaderModeSetting.getValue() && !this.gradientAnimationSetting.getValue();
      });
      this.addSettings(
         this.swapAfterHitSetting,
         this.swapHandKeySetting,
         this.enableShaderSetting,
         this.distortionStrengthSetting,
         this.glassBrightnessSetting,
         this.backgroundBlurSetting,
         this.edgeRadiusSetting,
         this.dualShaderModeSetting,
         this.blendModeSetting,
         this.blendStrengthSetting,
         this.blurStrengthSetting,
         this.solidStrengthSetting,
         this.solidColor1Setting,
         this.solidColor2Setting,
         this.gradientAnimationSetting,
         this.gradientSpeedSetting,
         this.gradientPositionSetting
      );
   }

   private boolean a() {
      return this.getClient().options.getPerspective() == Perspective.FIRST_PERSON;
   }

   @Override
   public void onUseItem(UseItemEvent useitemevent) {
      if (this.a()) {
         if (this.swapAfterHitSetting.getValue()) {
            this.b();
         }
      }
   }

   @Override
   public void onEndTick() {
      if (this.a()) {
         if (this.getScreen() == null) {
            if (!this.swapHandKeySetting.isPressed()) {
               this.Pe = false;
            }

            if (this.swapHandKeySetting.isPressed()) {
               this.b();
               this.Pe = true;
            }
         }
      }
   }

   private void b() {
      if (this.getPlayer().getMainArm() == Arm.RIGHT && !this.Pe) {
         this.getPlayer().setMainArm(Arm.LEFT);
      } else {
         if (this.getPlayer().getMainArm() == Arm.LEFT && !this.Pe) {
            this.getPlayer().setMainArm(Arm.RIGHT);
         }
      }
   }

   public boolean c() {
      return this.enableShaderSetting.getValue() && this.a() && !this.d();
   }

   private boolean d() {
      if (this.getPlayer() == null) {
         return false;
      } else {
         ItemStack itemstack = this.getPlayer().getMainHandStack();
         if (!itemstack.isEmpty() && itemstack.getItem() instanceof FilledMapItem) {
            return true;
         } else {
            ItemStack itemstack1 = this.getPlayer().getOffHandStack();
            return !itemstack1.isEmpty() && itemstack1.getItem() instanceof FilledMapItem;
         }
      }
   }

   public void e() {
      if (this.a()) {
         try {
            Framebuffer framebuffer = this.getClient().getFramebuffer();
            if (framebuffer != null) {
               this.n(framebuffer.textureWidth, framebuffer.textureHeight);
               this.Qd.beginWrite(true);
               framebuffer.draw(this.Qd.textureWidth, this.Qd.textureHeight);
               framebuffer.beginWrite(true);
            }
         } catch (Exception exception) {
         }
      }
   }

   public void f() {
      if (this.a()) {
         try {
            Framebuffer framebuffer = this.getClient().getFramebuffer();
            if (framebuffer != null) {
               int i = framebuffer.textureWidth;
               int j = framebuffer.textureHeight;
               this.n(i, j);
               this.xA.beginWrite(true);
               framebuffer.draw(i, j);
               this.xA.copyDepthFrom(framebuffer);
               framebuffer.beginWrite(true);
               if (this.dualShaderModeSetting.getValue()) {
                  this.h(framebuffer, i, j);
               } else {
                  this.g(i, j);
               }
            }
         } catch (Exception exception) {
         }
      }
   }

   private void g(int i, int j) {
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableDepthTest();
      this.i(true);
      ShaderProgram shaderprogram = RenderSystem.setShader(hf);
      if (shaderprogram != null) {
         this.k(shaderprogram);
         this.m(i, j);
      }

      this.j(3);
      RenderSystem.disableBlend();
      RenderSystem.enableDepthTest();
   }

   private void h(Framebuffer framebuffer, int i, int j) {
      RenderSystem.disableDepthTest();
      RenderSystem.disableBlend();
      this.hG.beginWrite(true);
      this.i(true);
      ShaderProgram shaderprogram = RenderSystem.setShader(hf);
      if (shaderprogram != null) {
         this.k(shaderprogram);
         this.m(i, j);
      }

      this.HD.beginWrite(true);
      this.i(false);
      ShaderProgram shaderprogram1 = RenderSystem.setShader(XE);
      if (shaderprogram1 != null) {
         this.l(shaderprogram1);
         this.m(i, j);
      }

      framebuffer.beginWrite(true);
      RenderSystem.setShaderTexture(0, this.hG.getColorAttachment());
      RenderSystem.setShaderTexture(1, this.HD.getColorAttachment());
      RenderSystem.setShaderTexture(2, this.xA.getDepthAttachment());
      ShaderProgram shaderprogram2 = RenderSystem.setShader(ala);
      if (shaderprogram2 != null) {
         shaderprogram2.getUniform("BlendMode").set(Math.max(0, QA.indexOf(this.blendModeSetting.getFirst())));
         shaderprogram2.getUniform("BlendIntensity").set(this.blendStrengthSetting.getFloatValue());
         shaderprogram2.getUniform("Shader1Strength").set(this.blurStrengthSetting.getFloatValue());
         shaderprogram2.getUniform("Shader2Strength").set(this.solidStrengthSetting.getFloatValue());
         this.m(i, j);
      }

      this.j(3);
      RenderSystem.enableDepthTest();
   }

   private void i(boolean flag) {
      RenderSystem.setShaderTexture(0, this.xA.getColorAttachment());
      RenderSystem.setShaderTexture(1, this.xA.getDepthAttachment());
      if (flag) {
         RenderSystem.setShaderTexture(2, this.Qd.getColorAttachment());
      }
   }

   private void j(int i) {
      for (int j = 0; j < i; j++) {
         RenderSystem.setShaderTexture(j, 0);
      }
   }

   private void k(ShaderProgram shaderprogram) {
      int i = this.getClient().getWindow().getFramebufferWidth();
      int j = this.getClient().getWindow().getFramebufferHeight();
      shaderprogram.getUniform("Resolution").set(i, j);
      shaderprogram.getUniform("DistortStrength").set(this.distortionStrengthSetting.getFloatValue());
      shaderprogram.getUniform("FresnelPower").set(0.5F);
      shaderprogram.getUniform("GlassBrightness").set(this.glassBrightnessSetting.getFloatValue());
      shaderprogram.getUniform("BackgroundBlur").set(this.backgroundBlurSetting.getFloatValue());
      shaderprogram.getUniform("Quality").set(3.0F);
      shaderprogram.getUniform("Direction").set(10.0F);
      shaderprogram.getUniform("EdgeRadius").set(this.edgeRadiusSetting.getFloatValue());
      shaderprogram.getUniform("EdgeDirections").set(4.0F);
      shaderprogram.getUniform("FresnelHighlight").set(0.01F);
   }

   private void l(ShaderProgram shaderprogram) {
      int i = this.getClient().getWindow().getFramebufferWidth();
      int j = this.getClient().getWindow().getFramebufferHeight();
      float f = this.gradientAnimationSetting.getValue()
         ? (float)System.nanoTime() / 1.0E9F * this.gradientSpeedSetting.getFloatValue()
         : this.gradientPositionSetting.getFloatValue() * (float) Math.PI * 2.0F;
      shaderprogram.getUniform("Time").set(f);
      shaderprogram.getUniform("Resolution").set(i, j);
      Color color = this.solidColor1Setting.getColor();
      Color color1 = this.solidColor2Setting.getColor();
      shaderprogram.getUniform("Color1").set(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F);
      shaderprogram.getUniform("Color2").set(color1.getRed() / 255.0F, color1.getGreen() / 255.0F, color1.getBlue() / 255.0F);
      shaderprogram.getUniform("EffectAlpha").set(1.0F);
   }

   private void m(int i, int j) {
      Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
      matrix4fstack.pushMatrix();
      matrix4fstack.identity();
      RenderSystem.setProjectionMatrix(new Matrix4f().setOrtho(0.0F, i, j, 0.0F, -1000.0F, 1000.0F), ProjectionType.ORTHOGRAPHIC);
      Matrix4f matrix4f = new Matrix4f();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(-1);
      bufferbuilder.vertex(matrix4f, 0.0F, j, 0.0F).color(-1);
      bufferbuilder.vertex(matrix4f, i, j, 0.0F).color(-1);
      bufferbuilder.vertex(matrix4f, i, 0.0F, 0.0F).color(-1);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      matrix4fstack.popMatrix();
   }

   private void n(int i, int j) {
      this.xA = this.o(this.xA, i, j, true);
      this.Qd = this.o(this.Qd, i, j, false);
      if (this.dualShaderModeSetting.getValue()) {
         this.hG = this.o(this.hG, i, j, false);
         this.HD = this.o(this.HD, i, j, false);
      }
   }

   private SimpleFramebuffer o(SimpleFramebuffer simpleframebuffer, int i, int j, boolean flag) {
      if (simpleframebuffer == null) {
         simpleframebuffer = new SimpleFramebuffer(i, j, flag);
      } else {
         if (simpleframebuffer.textureWidth == i && simpleframebuffer.textureHeight == j) {
            return simpleframebuffer;
         }

         simpleframebuffer.resize(i, j);
      }

      this.p(simpleframebuffer);
      return simpleframebuffer;
   }

   private void p(SimpleFramebuffer simpleframebuffer) {
      GlStateManager._bindTexture(simpleframebuffer.getColorAttachment());
      GlStateManager._texParameter(3553, 10241, 9729);
      GlStateManager._texParameter(3553, 10240, 9729);
      if (simpleframebuffer.getDepthAttachment() != -1) {
         GlStateManager._bindTexture(simpleframebuffer.getDepthAttachment());
         GlStateManager._texParameter(3553, 10241, 9729);
         GlStateManager._texParameter(3553, 10240, 9729);
      }

      GlStateManager._bindTexture(0);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   public ActionKeySetting q() {
      return this.swapHandKeySetting;
   }

   public BooleanSetting r() {
      return this.swapAfterHitSetting;
   }

   public BooleanSetting s() {
      return this.enableShaderSetting;
   }

   public SliderSetting t() {
      return this.distortionStrengthSetting;
   }

   public SliderSetting u() {
      return this.glassBrightnessSetting;
   }

   public SliderSetting v() {
      return this.backgroundBlurSetting;
   }

   public SliderSetting w() {
      return this.edgeRadiusSetting;
   }

   public BooleanSetting x() {
      return this.dualShaderModeSetting;
   }

   public ListSetting y() {
      return this.blendModeSetting;
   }

   public SliderSetting z() {
      return this.blendStrengthSetting;
   }

   public SliderSetting A() {
      return this.blurStrengthSetting;
   }

   public SliderSetting B() {
      return this.solidStrengthSetting;
   }

   public ColorSetting C() {
      return this.solidColor1Setting;
   }

   public ColorSetting D() {
      return this.solidColor2Setting;
   }

   public BooleanSetting E() {
      return this.gradientAnimationSetting;
   }

   public SliderSetting F() {
      return this.gradientSpeedSetting;
   }

   public SliderSetting G() {
      return this.gradientPositionSetting;
   }

   public SimpleFramebuffer H() {
      return this.xA;
   }

   public SimpleFramebuffer I() {
      return this.Qd;
   }

   public SimpleFramebuffer J() {
      return this.hG;
   }

   public SimpleFramebuffer K() {
      return this.HD;
   }

   public boolean L() {
      return this.Pe;
   }
}
