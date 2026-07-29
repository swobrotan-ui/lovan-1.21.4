package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import font.MSDFFont;
import hook.GameRendererInitHook;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import render.BuiltText;
import render.TextCache;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class TracersModule extends Module {
   private ListSetting displayModeSetting = new ListSetting(
      "Режим отображения", "Линии или стрелки", Arrays.<String>asList("Линии", "Стрелки"), List.<String>of("Линии"), false
   );
   private ListSetting colorModeSetting = new ListSetting(
      "Стиль стрелки", "Выбор между обычной и заполненной стрелкой", Arrays.<String>asList("Обычные", "Заполненные"), List.<String>of("Обычные"), false
   );
   private ColorSetting colorSetting = new ColorSetting("Цвет", "Цвет линий к игрокам", new Color(255, 255, 255), true);
   private ColorSetting friendColorSetting = new ColorSetting("Цвет для друзей", "Цвет линий к друзьям", new Color(0, 150, 255), true);
   private BooleanSetting showFriendsSetting = new BooleanSetting("Показывать друзей", "Отображать друзей", true);
   private SliderSetting maxDistanceSetting = new SliderSetting("Макс. дистанция", "Максимальная дистанция отображения", 64.0, 8.0, 256.0, 8.0);
   private SliderSetting arrowSizeSetting = new SliderSetting("Размер стрелки", "Размер стрелок", 10.0, 5.0, 30.0, 1.0);
   private SliderSetting arrowRadiusSetting = new SliderSetting("Радиус стрелок", "Радиус круга стрелок", 40.0, 20.0, 100.0, 5.0);
   private BooleanSetting showNamesSetting = new BooleanSetting("Показывать имена", "Отображать ники игроков", false);
   private BooleanSetting showDistanceSetting = new BooleanSetting("Показывать дистанцию", "Отображать расстояние до игроков", true);
   private SliderSetting textSizeSetting = new SliderSetting("Размер текста", "Размер шрифта", 6.4, 4.0, 12.0, 0.2);
   private MSDFFont rg;
   private final Map<String, tu> fF = new HashMap<String, tu>();
   private final Set<String> iu = new HashSet<String>();
   private long atg;
   private static final float aqT = 1.0F / (float)NANOS_PER_SECOND;
   private float FA;
   private float aaT;
   private double aqW;
   private double Ta;
   private float cm = 0.0F;

   public TracersModule() {
      super("Трасерз", "Рисует линии к игрокам", Category.RENDER);
      this.rg = MSDFFont.g().b("bb").c("bb").e();
      this.a();
      this.addSettings(
         this.displayModeSetting,
         this.showFriendsSetting,
         this.maxDistanceSetting,
         this.colorSetting,
         this.friendColorSetting,
         this.colorModeSetting,
         this.arrowSizeSetting,
         this.arrowRadiusSetting,
         this.showNamesSetting,
         this.showDistanceSetting,
         this.textSizeSetting
      );
      this.atg = System.nanoTime();
   }

   private void a() {
      this.colorModeSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
      this.arrowSizeSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
      this.arrowRadiusSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
      this.showNamesSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
      this.showDistanceSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
      this.textSizeSetting.setVisibilitySupplier(() -> {
         return "Стрелки".equals(this.displayModeSetting.getFirst());
      });
   }

   @Override
   public void onEnable() {
      this.fF.clear();
      this.atg = System.nanoTime();
      float f = this.arrowRadiusSetting.getFloatValue();
      this.FA = f - 15.0F;
      this.aaT = this.FA;
      this.cm = 0.0F;
      if (this.getPlayer() != null) {
         this.aqW = this.getPlayer().getX();
         this.Ta = this.getPlayer().getZ();
      }
   }

   @Override
   public void onDisable() {
      this.fF.clear();
      this.iu.clear();
      this.cm = 0.0F;
   }

   @Override
   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
      if (this.isEnabled() && !this.isNotInWorld()) {
         if ("Линии".equals(this.displayModeSetting.getFirst())) {
            try {
               this.e(worldrendercontext.matrixStack(), worldrendercontext.tickCounter());
               return;
            } catch (Exception exception) {
            }
         }
      }
   }

   public void b(DrawContext drawcontext) {
      if (this.isEnabled() && !this.isNotInWorld()) {
         if ("Стрелки".equals(this.displayModeSetting.getFirst())) {
            try {
               this.c();
               this.d(drawcontext);
               return;
            } catch (Exception exception) {
            }
         }
      }
   }

   private void c() {
      long i = System.nanoTime();
      float f = (float)(i - this.atg) * aqT;
      this.atg = i;
      if (f > 0.1F) {
         f = 0.1F;
      }

      PlayerEntity playerentity = this.getPlayer();
      Vec3d vec3d = playerentity.getPos();
      double d0 = this.maxDistanceSetting.getValue();
      float f1 = playerentity.getYaw();
      boolean flag = this.showFriendsSetting.getValue();
      double d1 = vec3d.x;
      double d2 = vec3d.z;
      if (this.aqW == 0.0 && this.Ta == 0.0) {
         this.aqW = d1;
         this.Ta = d2;
      }

      double d3 = d1 - this.aqW;
      double d4 = d2 - this.Ta;
      float f2 = (float)Math.sqrt(d3 * d3 + d4 * d4) / Math.max(f, 0.001F);
      this.aqW = d1;
      this.Ta = d2;
      this.cm = MathHelper.lerp(f * 5.0F, this.cm, f2);
      float f3 = this.arrowRadiusSetting.getFloatValue();
      float f4 = f3 - 15.0F;
      float f5 = f3 + 5.0F;
      float f6 = MathHelper.clamp(this.cm / 8.0F, 0.0F, 1.0F);
      this.aaT = MathHelper.lerp(f6, f4, f5);
      this.FA = MathHelper.lerp(f * 6.0F, this.FA, this.aaT);
      this.iu.clear();

      for (PlayerEntity playerentity1 : this.getWorld().getPlayers()) {
         if (wu.b(playerentity1, playerentity, vec3d, d0, false, flag, this)) {
            String s = playerentity1.getUuidAsString();
            this.iu.add(s);
            tu tux = this.fF.computeIfAbsent(s, s1 -> {
               return new tu();
            });
            Vec3d vec3d1 = playerentity1.getPos();
            double d5 = vec3d1.x - d1;
            double d6 = vec3d1.z - d2;
            double d7 = d5 * d5 + d6 * d6;
            if (!(d7 < 0.01)) {
               double d8 = Math.sqrt(d7);
               float f7 = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(d6, d5)) - f1 + 180.0);
               float f8 = f7 - tux.YW;
               if (f8 > 180.0F) {
                  f8 -= 360.0F;
               } else if (f8 < -180.0F) {
                  f8 += 360.0F;
               }

               tux.YW = MathHelper.wrapDegrees(tux.YW + f8 * f * 12.0F);
               tux.acd = playerentity1.getName().getString();
               tux.alp = d8;
               tux.aoH = this.isFriendPlayer(playerentity1);
               tux.sW = tux.sW + (1.0F - tux.sW) * f * 10.0F;
               if (tux.sW > 1.0F) {
                  tux.sW = 1.0F;
               }
            }
         }
      }

      Iterator iterator = this.fF.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         if (!this.iu.contains(entry.getKey())) {
            tu tu = (tu)entry.getValue();
            tu.sW = tu.sW - tu.sW * f * 10.0F;
            if (tu.sW < 0.01F) {
               iterator.remove();
            }
         }
      }
   }

   private void d(DrawContext drawcontext) {
      if (!this.fF.isEmpty()) {
         int i = this.getClient().getWindow().getScaledWidth() / 2;
         int j = this.getClient().getWindow().getScaledHeight() / 2;
         float f = this.FA;
         float f1 = this.arrowSizeSetting.getFloatValue();
         float f2 = f1 * 0.5F;
         float f3 = this.textSizeSetting.getFloatValue();
         float f4 = f3 - 2.0F;
         boolean flag = "Заполненные".equals(this.colorModeSetting.getFirst());
         boolean flag1 = this.showNamesSetting.getValue();
         boolean flag2 = this.showDistanceSetting.getValue();
         boolean flag3 = flag1 || flag2;
         Color color = this.friendColorSetting.getColor();
         Color color1 = this.colorSetting.getColor();
         MatrixStack matrixstack = drawcontext.getMatrices();

         for (tu tu : this.fF.values()) {
            float f5 = tu.sW;
            Color color2 = tu.aoH ? color : color1;
            int k = color2.getRGB() & 16777215 | (int)((color2.getRGB() >> 24 & 0xFF) * f5) << 24;
            float f6 = tu.YW;
            double d0 = Math.toRadians(f6);
            float f7 = (float)(i + Math.cos(d0) * f);
            float f8 = (float)(j + Math.sin(d0) * f);
            matrixstack.push();
            matrixstack.translate(f7, f8, 0.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f6 + 90.0F));
            if (flag) {
               this.k(matrixstack, -f2, -f2, f1, f1, k);
            } else {
               this.j(matrixstack, -f2, -f2, f1, f1, k);
            }

            matrixstack.pop();
            if (flag3) {
               Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
               float f9 = f8 + f2;
               if (flag1 && !tu.acd.isEmpty()) {
                  BuiltText builttext = TextCache.a(this.rg, tu.acd, f3, Color.WHITE);
                  float f10 = this.rg.c(tu.acd, f3);
                  builttext.a(matrix4f, f7 - f10 * 0.5F, f9, f5);
                  f9 += f3 + 2.0F;
               }

               if (flag2) {
                  int l = (int)tu.alp;
                  String s = l + "m";
                  BuiltText builttext1 = TextCache.a(this.rg, s, f4, Color.WHITE);
                  float f11 = this.rg.c(s, f4);
                  builttext1.a(matrix4f, f7 - f11 * 0.5F, f9, f5);
               }
            }
         }
      }
   }

   private void e(MatrixStack matrixstack, RenderTickCounter rendertickcounter) {
      if (!this.isNotInWorld()) {
         PlayerEntity playerentity = this.getPlayer();
         Vec3d vec3d = playerentity.getPos();
         double d0 = this.maxDistanceSetting.getValue();
         boolean flag = this.showFriendsSetting.getValue();
         float f = rendertickcounter.getTickDelta(false);
         List list = this.getWorld().getPlayers();
         int i = wu.e(list, playerentity, vec3d, d0, false, flag, this);
         if (i != 0) {
            dr.a(false);
            Vec3d vec3d1 = this.f(this.getClient().gameRenderer.getCamera(), f);
            Vec3d vec3d2 = this.g(f);
            Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
            Color color = this.friendColorSetting.getColor();
            Color color1 = this.colorSetting.getColor();
            BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            for (PlayerEntity playerentity1 : list) {
               if (wu.b(playerentity1, playerentity, vec3d, d0, false, flag, this)) {
                  Color color2 = this.isFriendPlayer(playerentity1) ? color : color1;
                  float f1 = color2.getRed() / 255.0F;
                  float f2 = color2.getGreen() / 255.0F;
                  float f3 = color2.getBlue() / 255.0F;
                  float f4 = color2.getAlpha() / 255.0F;
                  Vec3d vec3d3 = playerentity1.getLerpedPos(f).add(0.0, playerentity1.getEyeHeight(playerentity1.getPose()), 0.0);
                  this.h(bufferbuilder, matrix4f, vec3d2, vec3d3, vec3d1, f1, f2, f3, f4);
               }
            }

            BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            dr.b();
         }
      }
   }

   private Vec3d f(Camera camera, float f) {
      PlayerEntity playerentity = this.getPlayer();
      if (playerentity == null) {
         return camera.getPos();
      } else {
         Vec3d vec3d = playerentity.getLerpedPos(f);
         double d0 = playerentity.getEyeHeight(playerentity.getPose());
         return new Vec3d(vec3d.x, vec3d.y + d0, vec3d.z);
      }
   }

   private Vec3d g(float f) {
      PlayerEntity playerentity = this.getPlayer();
      if (playerentity == null) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec3d = this.f(this.getClient().gameRenderer.getCamera(), f);
         float f1 = playerentity.getYaw(f);
         float f2 = playerentity.getPitch(f);
         Vec3d vec3d1 = dr.o(f2, f1);
         return vec3d.add(vec3d1.multiply(3.0));
      }
   }

   private void h(BufferBuilder bufferbuilder, Matrix4f matrix4f, Vec3d vec3d, Vec3d vec3d1, Vec3d vec3d2, float f, float f1, float f2, float f3) {
      Vec3d vec3d3 = vec3d.subtract(vec3d2);
      Vec3d vec3d4 = vec3d1.subtract(vec3d2);
      dr.d(bufferbuilder, matrix4f, vec3d3, f, f1, f2, f3);
      dr.d(bufferbuilder, matrix4f, vec3d4, f, f1, f2, f3);
   }

   private Identifier i() {
      String s = this.colorModeSetting.getFirst();
      return "Заполненные".equals(s) ? GameRendererInitHook.arrow_3d : GameRendererInitHook.arrow;
   }

   private void j(MatrixStack matrixstack, float f, float f1, float f2, float f3, int i) {
      matrixstack.push();
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, this.i());
      int j = i >> 24 & 0xFF;
      int k = i >> 16 & 0xFF;
      int l = i >> 8 & 0xFF;
      int i1 = i & 0xFF;
      float f4 = f + f2;
      float f5 = f1 + f3;
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, 0.0F).texture(0.0F, 0.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f, f5, 0.0F).texture(0.0F, 1.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f4, f5, 0.0F).texture(1.0F, 1.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f4, f1, 0.0F).texture(1.0F, 0.0F).color(k, l, i1, j);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.disableBlend();
      matrixstack.pop();
   }

   private void k(MatrixStack matrixstack, float f, float f1, float f2, float f3, int i) {
      matrixstack.push();
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
      RenderSystem.setShaderTexture(0, this.i());
      int j = i >> 24 & 0xFF;
      int k = i >> 16 & 0xFF;
      int l = i >> 8 & 0xFF;
      int i1 = i & 0xFF;
      float f4 = f + f2;
      float f5 = f1 + f3;
      int j1 = k * 102 >> 8;
      int k1 = l * 102 >> 8;
      int l1 = i1 * 102 >> 8;
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, -1.0F).texture(0.0F, 0.0F).color(j1, k1, l1, j);
      bufferbuilder.vertex(matrix4f, f, f5, -1.0F).texture(0.0F, 1.0F).color(j1, k1, l1, j);
      bufferbuilder.vertex(matrix4f, f4, f5, -1.0F).texture(1.0F, 1.0F).color(j1, k1, l1, j);
      bufferbuilder.vertex(matrix4f, f4, f1, -1.0F).texture(1.0F, 0.0F).color(j1, k1, l1, j);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      int i2 = k * 179 >> 8;
      int j2 = l * 179 >> 8;
      int k2 = i1 * 179 >> 8;
      bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, -0.5F).texture(0.0F, 0.0F).color(i2, j2, k2, j);
      bufferbuilder.vertex(matrix4f, f, f5, -0.5F).texture(0.0F, 1.0F).color(i2, j2, k2, j);
      bufferbuilder.vertex(matrix4f, f4, f5, -0.5F).texture(1.0F, 1.0F).color(i2, j2, k2, j);
      bufferbuilder.vertex(matrix4f, f4, f1, -0.5F).texture(1.0F, 0.0F).color(i2, j2, k2, j);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(matrix4f, f, f1, 0.0F).texture(0.0F, 0.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f, f5, 0.0F).texture(0.0F, 1.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f4, f5, 0.0F).texture(1.0F, 1.0F).color(k, l, i1, j);
      bufferbuilder.vertex(matrix4f, f4, f1, 0.0F).texture(1.0F, 0.0F).color(k, l, i1, j);
      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.disableBlend();
      matrixstack.pop();
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue()) {
         this.setEnabled(false);
      }
   }
}
