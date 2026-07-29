package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import render.BuiltLine3d;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class PlayerESPModule extends Module {
   private final String UO = "Бокс";
   private final String agA = "Углы";
   private ListSetting modeSetting = new ListSetting("Режим", "Режим отображения", Arrays.<String>asList("Бокс", "Углы"), List.<String>of("Углы"), false);
   private SliderSetting cornerSizeSetting = new SliderSetting("Размер углов", "Размер углов", 0.2, 0.0, 0.64, 0.05);
   private SliderSetting lineWidthSetting = new SliderSetting("Толщина линии", "Толщина линий", 2.0, 1.0, 10.0, 0.5);
   private ColorSetting lineColorSetting = new ColorSetting("Цвет линий", "Цвет линий", new Color(255, 255, 255), true);
   private BooleanSetting showSkeletonSetting = new BooleanSetting("Показывать скелет", "скелет", false);
   private BooleanSetting showFriendsSetting = new BooleanSetting("Показывать друзей", "Отображать друзей", true);
   private BooleanSetting showSelfSetting = new BooleanSetting("Показывать себя", "Отображать себя", false);
   private final PlayerEntityRenderState afS = new PlayerEntityRenderState();
   private final rpy avr = new rpy();
   private float ZW = 1.0F;
   private float awC = 0.3F;
   private float zt = 1.0F;
   private float Zy = 0.0F;
   private boolean Sn = false;
   private long axb = System.currentTimeMillis();

   public PlayerESPModule() {
      super("ПлайерЕЗП", "Показывать игроков сквозь стены", Category.RENDER);
      this.a();
      this.addSettings(
         this.modeSetting,
         this.cornerSizeSetting,
         this.lineWidthSetting,
         this.lineColorSetting,
         this.showSkeletonSetting,
         this.showFriendsSetting,
         this.showSelfSetting
      );
   }

   private void a() {
      this.cornerSizeSetting.setVisibilitySupplier(() -> {
         return this.modeSetting.isSelected("Углы");
      });
   }

   @Override
   public void onEnable() {
      this.axb = System.currentTimeMillis();
      this.zt = 1.0F;
      this.Zy = 0.0F;
      this.Sn = false;
      this.ZW = this.modeSetting.isSelected(ZK[1]) ? 0.0F : 1.0F;
      this.awC = 0.0F;
   }

   @Override
   public void onDisable() {
      this.Sn = true;
      this.axb = System.currentTimeMillis();
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && (this.isEnabled() || this.Sn)) {
         this.f();
         if (this.Sn && this.Zy <= 0.0F) {
            this.Sn = false;
         } else {
            Color color = this.lineColorSetting.getColor();
            float f = color.getRed() / 255.0F;
            float f1 = color.getGreen() / 255.0F;
            float f2 = color.getBlue() / 255.0F;
            float f3 = color.getAlpha() / 255.0F * this.zt;
            this.c(worldrendercontext, f, f1, f2, f3);
            if (this.showSkeletonSetting.getValue()) {
               this.b(worldrendercontext, f, f1, f2, f3);
            }
         }
      }
   }

   private void b(WorldRenderContext worldrendercontext, float f, float f1, float f2, float f3) {
      MatrixStack matrixstack = worldrendercontext.matrixStack();
      float f4 = worldrendercontext.tickCounter().getTickDelta(true);
      Vec3d vec3d = worldrendercontext.camera().getPos();
      GL11.glDepthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      float f5 = (float)((Integer)this.getClient().options.getFov().getValue()).intValue();
      Camera camera = worldrendercontext.camera();

      for (PlayerEntity playerentity : this.getWorld().getPlayers()) {
         if (playerentity.isAlive()
            && (!this.getClient().options.getPerspective().isFirstPerson() || playerentity != this.getClient().player)
            && wu.b(playerentity, this.getPlayer(), this.getPlayer().getPos(), 128.0, this.showSelfSetting.getValue(), this.showFriendsSetting.getValue(), this)
            )
          {
            Vec3d vec3d1 = playerentity.getPos().add(0.0, playerentity.getHeight() * 0.5, 0.0);
            if (wu.a(vec3d1, camera, f5)
               && this.getClient().getEntityRenderDispatcher().getRenderer(playerentity) instanceof PlayerEntityRenderer playerentityrenderer) {
               double d0 = MathHelper.lerp(f4, playerentity.prevX, playerentity.getX()) - vec3d.x;
               double d1 = MathHelper.lerp(f4, playerentity.prevY, playerentity.getY()) - vec3d.y;
               double d2 = MathHelper.lerp(f4, playerentity.prevZ, playerentity.getZ()) - vec3d.z;
               PlayerEntityModel playerentitymodel = (PlayerEntityModel)playerentityrenderer.getModel();
               if (playerentity instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
                  playerentityrenderer.updateRenderState(abstractclientplayerentity, this.afS, f4);
                  playerentitymodel.setAngles(this.afS);
               }

               float f10 = MathHelper.lerpAngleDegrees(f4, playerentity.prevBodyYaw, playerentity.bodyYaw);

               assert matrixstack != null;

               matrixstack.push();
               matrixstack.translate(d0, d1, d2);
               if (playerentity.isInSwimmingPose()) {
                  matrixstack.translate(0.0F, 0.35F, 0.0F);
               }

               matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f10 - 180.0F));
               if (playerentity.isInSwimmingPose() || playerentity.isGliding()) {
                  float f6 = MathHelper.lerp(f4, playerentity.prevPitch, playerentity.getPitch());
                  matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-(90.0F + f6)));
                  if (playerentity.isInSwimmingPose()) {
                     matrixstack.translate(0.0F, -0.95F, 0.0F);
                  }
               }

               Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
               boolean flag = playerentity.isSneaking();
               float f7 = flag ? 0.6F : 0.7F;
               float f8 = flag ? 0.23F : 0.0F;
               float f9 = flag ? 1.05F : 1.4F;
               bufferbuilder.vertex(matrix4f, 0.0F, f7, f8).color(f, f1, f2, f3);
               bufferbuilder.vertex(matrix4f, 0.0F, f9, 0.0F).color(f, f1, f2, f3);
               bufferbuilder.vertex(matrix4f, -0.37F, f9 - 0.05F, 0.0F).color(f, f1, f2, f3);
               bufferbuilder.vertex(matrix4f, 0.37F, f9 - 0.05F, 0.0F).color(f, f1, f2, f3);
               bufferbuilder.vertex(matrix4f, -0.15F, f7, f8).color(f, f1, f2, f3);
               bufferbuilder.vertex(matrix4f, 0.15F, f7, f8).color(f, f1, f2, f3);
               this.h(matrixstack, bufferbuilder, playerentitymodel.head, 0.0F, f9, 0.0F, 0.25F, f, f1, f2, f3);
               this.h(matrixstack, bufferbuilder, playerentitymodel.rightLeg, 0.15F, f7, f8, -0.6F, f, f1, f2, f3);
               this.h(matrixstack, bufferbuilder, playerentitymodel.leftLeg, -0.15F, f7, f8, -0.6F, f, f1, f2, f3);
               this.h(matrixstack, bufferbuilder, playerentitymodel.rightArm, 0.37F, f9 - 0.05F, 0.0F, -0.55F, f, f1, f2, f3);
               this.h(matrixstack, bufferbuilder, playerentitymodel.leftArm, -0.37F, f9 - 0.05F, 0.0F, -0.55F, f, f1, f2, f3);
               matrixstack.pop();
            }
         }
      }

      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.depthMask(true);
      GL11.glDepthFunc(515);
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
   }

   private void c(WorldRenderContext worldrendercontext, float f, float f1, float f2, float f3) {
      MatrixStack matrixstack = worldrendercontext.matrixStack();
      Vec3d vec3d = worldrendercontext.camera().getPos();
      float f4 = worldrendercontext.tickCounter().getTickDelta(true);
      PlayerEntity playerentity = this.getPlayer();
      Vec3d vec3d1 = playerentity.getPos();
      float f5 = this.lineWidthSetting.getFloatValue();
      BuiltLine3d builtline3d = this.avr.a(f5).b(f, f1, f2, f3).l(8).a();
      float f6 = (float)((Integer)this.getClient().options.getFov().getValue()).intValue();
      Camera camera = worldrendercontext.camera();
      GL11.glDepthFunc(519);
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      xzs xzs = builtline3d.h();
      boolean flag = false;

      for (PlayerEntity playerentity1 : this.getWorld().getPlayers()) {
         if (wu.b(playerentity1, playerentity, vec3d1, 128.0, this.showSelfSetting.getValue(), this.showFriendsSetting.getValue(), this)) {
            Vec3d vec3d2 = playerentity1.getPos().add(0.0, playerentity1.getHeight() * 0.5, 0.0);
            if (wu.a(vec3d2, camera, f6)) {
               Vec3d vec3d3 = dr.c(playerentity1, f4, vec3d);
               this.d(xzs, vec3d3, playerentity1.getWidth(), playerentity1.getHeight());
               flag = true;
            }
         }
      }

      if (flag) {
         xzs.d(matrixstack, Vec3d.ZERO);
      }

      RenderSystem.depthMask(true);
      GL11.glDepthFunc(515);
      RenderSystem.disableBlend();
      RenderSystem.enableCull();
   }

   private void d(xzs xzs, Vec3d vec3d, float f, float f1) {
      float f2 = f / 2.0F;
      double d0 = vec3d.x - f2;
      double d1 = vec3d.x + f2;
      double d2 = vec3d.y;
      double d3 = vec3d.y + f1;
      double d4 = vec3d.z - f2;
      double d5 = vec3d.z + f2;
      this.e(xzs, d0, d2, d4, f, f1, f, 1, 1, 1);
      this.e(xzs, d1, d2, d4, f, f1, f, -1, 1, 1);
      this.e(xzs, d0, d2, d5, f, f1, f, 1, 1, -1);
      this.e(xzs, d1, d2, d5, f, f1, f, -1, 1, -1);
      this.e(xzs, d0, d3, d4, f, f1, f, 1, -1, 1);
      this.e(xzs, d1, d3, d4, f, f1, f, -1, -1, 1);
      this.e(xzs, d0, d3, d5, f, f1, f, 1, -1, -1);
      this.e(xzs, d1, d3, d5, f, f1, f, -1, -1, -1);
   }

   private void e(xzs xzs, double d0, double d1, double d2, float f, float f1, float f2, int i, int j, int k) {
      float f3 = this.awC + (f - this.awC) * this.ZW;
      float f4 = this.awC + (f1 - this.awC) * this.ZW;
      float f5 = this.awC + (f2 - this.awC) * this.ZW;
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      xzs.b(vec3d, new Vec3d(d0 + f3 * i, d1, d2));
      xzs.b(vec3d, new Vec3d(d0, d1 + f4 * j, d2));
      xzs.b(vec3d, new Vec3d(d0, d1, d2 + f5 * k));
   }

   private void f() {
      long i = System.currentTimeMillis();
      float f = (float)(i - this.axb) / 1000.0F;
      this.axb = i;
      if (this.Sn) {
         this.Zy = Math.max(0.0F, this.Zy - f * 5.0F);
      } else {
         this.Zy = Math.min(1.0F, this.Zy + f * 5.0F);
      }

      this.zt = this.Zy;
      String s = this.modeSetting.isSelected("Углы") ? "Углы" : "Бокс";
      float f1 = s.equals("Бокс") ? 1.0F : 0.0F;
      this.ZW = this.g(this.ZW, f1, f * 6.0F);
      float f2 = this.cornerSizeSetting.getFloatValue() * this.Zy;
      this.awC = this.g(this.awC, f2, f * 12.0F);
   }

   private float g(float f, float f1, float f2) {
      float f3 = f1 - f;
      return Math.abs(f3) < 0.001F ? f1 : f + f3 * Math.min(f2, 1.0F);
   }

   private void h(
      MatrixStack matrixstack, BufferBuilder bufferbuilder, ModelPart modelpart, float f, float f1, float f2, float f3, float f4, float f5, float f6, float f7
   ) {
      matrixstack.push();
      matrixstack.translate(f, f1, f2);
      if (modelpart.roll != 0.0F) {
         matrixstack.multiply(RotationAxis.NEGATIVE_Z.rotation(modelpart.roll));
      }

      if (modelpart.yaw != 0.0F) {
         matrixstack.multiply(RotationAxis.NEGATIVE_Y.rotation(modelpart.yaw));
      }

      if (modelpart.pitch != 0.0F) {
         matrixstack.multiply(RotationAxis.NEGATIVE_X.rotation(modelpart.pitch));
      }

      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      bufferbuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(f4, f5, f6, f7);
      bufferbuilder.vertex(matrix4f, 0.0F, f3, 0.0F).color(f4, f5, f6, f7);
      matrixstack.pop();
   }
}
