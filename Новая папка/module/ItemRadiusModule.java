package module;

import com.mojang.blaze3d.systems.RenderSystem;
import data.PlanePose;
import data.RenderData;
import enum.Category;
import java.awt.Color;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import render.BuiltLine3d;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.GroupSetting;
import setting.SliderSetting;

public class ItemRadiusModule extends Module {
   private GroupSetting itemsGroup = new GroupSetting("Предметы", "Какие предметы отслеживать", true);
   private BooleanSetting eyeOfEnderSetting = new BooleanSetting("Дезка", "Око Эндера — радиус 10", true);
   private BooleanSetting sugarSetting = new BooleanSetting("Явка", "Сахар — радиус 10", true);
   private BooleanSetting fireChargeSetting = new BooleanSetting("Огненный Заряд", "Огненный заряд — радиус 10", true);
   private BooleanSetting phantomMembraneSetting = new BooleanSetting("Божья Аура", "Мембрана фантома — радиус 2", true);
   private BooleanSetting netheriteScrapSetting = new BooleanSetting("Трапка", "Незеритовый скрап — куб 4x4", true);
   private BooleanSetting driedKelpSetting = new BooleanSetting("Пласт", "Сушёная ламинария — плоскость", true);
   private ColorSetting colorSetting = new ColorSetting("Цвет", "можно юзать", new Color(0, 255, 0, 200), true);
   private SliderSetting lineWidthSetting = new SliderSetting("Толщина линии", "Толщина линий обводки", 2.0, 0.5, 6.0, 0.5);
   private SliderSetting smoothSpeedSetting = new SliderSetting("Скорость сглаживания", "Скорость интерполяции пласта", 12.0, 4.0, 24.0, 1.0);
   private final rpy LV = new rpy();
   private int Bb = -1;
   private int ayL = -1;
   private float abg = 0.0F;
   private boolean adA = false;
   private static final float axT = 0.5F;
   private Vec3d jG = Vec3d.ZERO;
   private float IS = 0.0F;
   private float dQ = 0.0F;
   private boolean FB = false;
   private RenderData acf = null;

   public ItemRadiusModule() {
      super("ИтемРадиуз", "Отображает радиус действия предметов", Category.RENDER);
      this.itemsGroup
         .addSettings(
            this.eyeOfEnderSetting, this.sugarSetting, this.fireChargeSetting, this.phantomMembraneSetting, this.netheriteScrapSetting, this.driedKelpSetting
         );
      this.addSettings(this.itemsGroup, this.colorSetting, this.lineWidthSetting, this.smoothSpeedSetting);
   }

   @Override
   public void onEnable() {
      this.Bb = -1;
      this.ayL = -1;
      this.abg = 0.0F;
      this.adA = false;
      this.FB = false;
      this.acf = null;
   }

   @Override
   public void onDisable() {
      this.acf = null;
      this.FB = false;
   }

   @Override
   public void onRenderStart() {
      if (this.isEnabled() && !this.isNotInWorld()) {
         this.acf = null;
         PlayerEntity playerentity = this.getPlayer();
         if (playerentity != null) {
            ItemStack itemstack = playerentity.getMainHandStack();
            ItemStack itemstack1 = playerentity.getOffHandStack();
            if (this.eyeOfEnderSetting.getValue() && (j(itemstack, Items.ENDER_EYE) || j(itemstack1, Items.ENDER_EYE))) {
               ItemStack itemstack2 = j(itemstack, Items.ENDER_EYE) ? itemstack : itemstack1;
               if (k(itemstack2, 'Я')) {
                  this.b(playerentity, 10.0F);
                  return;
               }
            }

            if (this.sugarSetting.getValue() && (j(itemstack, Items.SUGAR) || j(itemstack1, Items.SUGAR))) {
               ItemStack itemstack3 = j(itemstack, Items.SUGAR) ? itemstack : itemstack1;
               if (k(itemstack3, 'Я')) {
                  this.b(playerentity, 10.0F);
                  return;
               }
            }

            if (this.fireChargeSetting.getValue() && (j(itemstack, Items.FIRE_CHARGE) || j(itemstack1, Items.FIRE_CHARGE))) {
               ItemStack itemstack4 = j(itemstack, Items.FIRE_CHARGE) ? itemstack : itemstack1;
               if (k(itemstack4, 'С')) {
                  this.b(playerentity, 10.0F);
                  return;
               }
            }

            if (this.phantomMembraneSetting.getValue() && (j(itemstack, Items.PHANTOM_MEMBRANE) || j(itemstack1, Items.PHANTOM_MEMBRANE))) {
               ItemStack itemstack5 = j(itemstack, Items.PHANTOM_MEMBRANE) ? itemstack : itemstack1;
               if (k(itemstack5, 'У')) {
                  this.b(playerentity, 2.0F);
                  return;
               }
            }

            if (this.netheriteScrapSetting.getValue() && (j(itemstack, Items.NETHERITE_SCRAP) || j(itemstack1, Items.NETHERITE_SCRAP))) {
               ItemStack itemstack6 = j(itemstack, Items.NETHERITE_SCRAP) ? itemstack : itemstack1;
               if (k(itemstack6, 'А')) {
                  this.c(playerentity);
                  return;
               }
            }

            if (this.driedKelpSetting.getValue() && (j(itemstack, Items.DRIED_KELP) || j(itemstack1, Items.DRIED_KELP))) {
               ItemStack itemstack7 = j(itemstack, Items.DRIED_KELP) ? itemstack : itemstack1;
               if (k(itemstack7, 'П')) {
                  this.d(playerentity);
                  return;
               }
            }

            this.FB = false;
         }
      }
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && this.acf != null) {
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         Vec3d vec3d = worldrendercontext.camera().getPos();
         float f = worldrendercontext.tickCounter().getTickDelta(true);
         PlayerEntity playerentity = this.getPlayer();
         if (playerentity != null) {
            Vec3d vec3d1 = this.a(playerentity, f);
            RenderData renderdata = this.acf;
            Vec3d vec3d2 = renderdata.center;
            if (renderdata.type == jm.qC) {
               vec3d2 = vec3d1.add(0.0, 0.1, 0.0);
            } else if (renderdata.type == jm.sr) {
               vec3d2 = new Vec3d(vec3d1.x, vec3d1.y + 0.5 + 1.625, vec3d1.z);
            }

            Color color = this.n();
            float f1 = this.lineWidthSetting.getFloatValue();
            BuiltLine3d builtline3d = this.LV.a(f1).e(color).l(6).g(true).a();
            xzs xzs = builtline3d.h();
            switch (renderdata.type) {
               case qC:
                  this.e(xzs, vec3d2, renderdata.radius, vec3d);
                  xzs.d(matrixstack, vec3d);
                  if (renderdata.playersInRadius) {
                     Color color2 = this.colorSetting.getColor();
                     this.f(
                        matrixstack, vec3d2, renderdata.radius, vec3d, color2.getRed() / 255.0F, color2.getGreen() / 255.0F, color2.getBlue() / 255.0F, 0.12F
                     );
                     return;
                  }
                  break;
               case sr:
                  this.g(xzs, vec3d2, renderdata.size, vec3d);
                  xzs.d(matrixstack, vec3d);
                  return;
               case ako:
                  matrixstack.push();
                  matrixstack.translate(vec3d2.x - vec3d.x, vec3d2.y - vec3d.y, vec3d2.z - vec3d.z);
                  if (Math.abs(renderdata.pitchDeg) > 0.001F) {
                     matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(renderdata.pitchDeg));
                  }

                  if (Math.abs(renderdata.yawDeg) > 0.001F) {
                     matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderdata.yawDeg));
                  }

                  float f2 = 2.0F;
                  float f3 = 2.0F;
                  float f4 = 0.75F;
                  Vec3d vec3d3 = new Vec3d(-f2, -f3, -f4);
                  Vec3d vec3d4 = new Vec3d(f2, f3, f4);
                  xzs.c(vec3d3, vec3d4);
                  xzs.d(matrixstack, Vec3d.ZERO);
                  if (renderdata.playersInRadius) {
                     Color color1 = this.colorSetting.getColor();
                     dr.q(matrixstack, vec3d3, vec3d4, color1.getRed() / 255.0F, color1.getGreen() / 255.0F, color1.getBlue() / 255.0F, 0.15F, false);
                  }

                  matrixstack.pop();
            }
         }
      }
   }

   private Vec3d a(PlayerEntity playerentity, float f) {
      double d0 = MathHelper.lerp(f, playerentity.prevX, playerentity.getX());
      double d1 = MathHelper.lerp(f, playerentity.prevY, playerentity.getY());
      double d2 = MathHelper.lerp(f, playerentity.prevZ, playerentity.getZ());
      return new Vec3d(d0, d1, d2);
   }

   private void b(PlayerEntity playerentity, float f) {
      Vec3d vec3d = playerentity.getPos().add(0.0, 0.1, 0.0);
      boolean flag = this.l(playerentity, vec3d, f);
      this.m(flag);
      this.acf = new RenderData(jm.qC, vec3d, f, 0.0F, 0.0F, 0.0F, flag);
   }

   private void c(PlayerEntity playerentity) {
      Vec3d vec3d = playerentity.getPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x, vec3d.y + 0.5 + 1.625, vec3d.z);
      boolean flag = this.l(playerentity, vec3d1, 2.5);
      this.m(flag);
      this.acf = new RenderData(jm.sr, vec3d1, 0.0F, 4.0F, 0.0F, 0.0F, flag);
   }

   private void d(PlayerEntity playerentity) {
      PlanePose planepose = this.h(playerentity);
      PlanePose planepose1 = this.i(planepose);
      boolean flag = this.l(playerentity, planepose1.auc, 2.5);
      this.m(flag);
      this.acf = new RenderData(jm.ako, planepose1.auc, 0.0F, 0.0F, planepose1.yawDeg, planepose1.pitchDeg, flag);
   }

   private void e(xzs xzs, Vec3d vec3d, float f, Vec3d vec3d1) {
      byte b0 = 64;
      double d0 = vec3d.x - vec3d1.x;
      double d1 = vec3d.y - vec3d1.y;
      double d2 = vec3d.z - vec3d1.z;

      for (int i = 0; i < b0; i++) {
         double d3 = (Math.PI * 2) * i / b0;
         double d4 = (Math.PI * 2) * (i + 1) / b0;
         double d5 = d0 + Math.sin(d3) * f;
         double d6 = d2 - Math.cos(d3) * f;
         double d7 = d0 + Math.sin(d4) * f;
         double d8 = d2 - Math.cos(d4) * f;
         xzs.b(new Vec3d(d5, d1, d6), new Vec3d(d7, d1, d8));
      }
   }

   private void f(MatrixStack matrixstack, Vec3d vec3d, float f, Vec3d vec3d1, float f1, float f2, float f3, float f4) {
      float f5 = (float)(vec3d.x - vec3d1.x);
      float f6 = (float)(vec3d.y - vec3d1.y);
      float f7 = (float)(vec3d.z - vec3d1.z);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
      RenderSystem.disableDepthTest();
      RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      bufferbuilder.vertex(matrix4f, f5, f6, f7).color(f1, f2, f3, f4);
      byte b0 = 72;

      for (int i = 0; i <= b0; i++) {
         float f8 = (float)(i * 2.0 * Math.PI / b0);
         float f9 = f5 + MathHelper.sin(f8) * f;
         float f10 = f7 - MathHelper.cos(f8) * f;
         bufferbuilder.vertex(matrix4f, f9, f6, f10).color(f1, f2, f3, f4);
      }

      BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
      RenderSystem.enableDepthTest();
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
   }

   private void g(xzs xzs, Vec3d vec3d, float f, Vec3d vec3d1) {
      float f1 = f / 2.0F;
      Vec3d vec3d2 = vec3d.subtract(vec3d1);
      Vec3d vec3d3 = new Vec3d(vec3d2.x - f1, vec3d2.y - f1, vec3d2.z - f1);
      Vec3d vec3d4 = new Vec3d(vec3d2.x + f1, vec3d2.y + f1, vec3d2.z + f1);
      xzs.c(vec3d3, vec3d4);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private PlanePose h(PlayerEntity playerentity) {
      Vec3d vec3d = playerentity.getEyePos();
      Vec3d vec3d1 = playerentity.getRotationVector();
      Vec3d vec3d2 = vec3d.add(vec3d1.multiply(4.0));
      BlockHitResult blockhitresult = this.getWorld().raycast(new RaycastContext(vec3d, vec3d2, ShapeType.COLLIDER, FluidHandling.NONE, playerentity));
      float f = playerentity.getPitch();
      boolean flag = f > 45.0F;
      boolean flag1 = f < -45.0F;
      float f1 = 1.5F;
      float f2 = f1 / 2.0F;
      Vec3d vec3d3;
      float f3;
      float f4;
      if (blockhitresult.getType() == Type.BLOCK && blockhitresult.getPos().distanceTo(vec3d) <= 4.0) {
         Vec3d vec3d5 = blockhitresult.getPos();
         Direction direction = blockhitresult.getSide();
         if (flag) {
            vec3d3 = new Vec3d(Math.floor(vec3d5.x) + 0.5, Math.floor(vec3d5.y + 1.0) - 1.8 + f2, Math.floor(vec3d5.z) + 0.5);
            f3 = 0.0F;
            f4 = 90.0F;
         } else if (flag1) {
            vec3d3 = new Vec3d(Math.floor(vec3d5.x) + 0.5, Math.floor(vec3d5.y) - f2 + 1.6, Math.floor(vec3d5.z) + 0.5);
            f3 = 0.0F;
            f4 = -90.0F;
         } else {
            double d1 = direction.getOffsetX() != 0 ? direction.getOffsetX() * f2 : 0.0;
            double d2 = direction.getOffsetZ() != 0 ? direction.getOffsetZ() * f2 : 0.0;
            vec3d3 = new Vec3d(Math.floor(vec3d5.x) + 0.5 + d1, Math.floor(vec3d5.y) + 0.5 + 1.6, Math.floor(vec3d5.z) + 0.5 + d2);

            f3 = switch (direction) {
               case NORTH -> 180.0F;
               case SOUTH -> 0.0F;
               case WEST -> 90.0F;
               case EAST -> -90.0F;
               default -> -playerentity.getYaw();
            };
            f4 = 0.0F;
         }
      } else {
         Vec3d vec3d4 = vec3d.add(vec3d1.multiply(4.0));
         double d0 = Math.floor(vec3d4.y) + (flag ? -1.8 + f2 : (flag1 ? -f2 + 1.6 : 2.1));
         vec3d3 = new Vec3d(Math.floor(vec3d4.x) + 0.5, d0, Math.floor(vec3d4.z) + 0.5);
         f3 = -playerentity.getYaw();
         f4 = 0.0F;
      }

      if (flag || flag1) {
         f3 = -playerentity.getYaw();
      }

      return new PlanePose(vec3d3, f3, f4);
   }

   private PlanePose i(PlanePose planepose) {
      float f = this.smoothSpeedSetting.getFloatValue();
      if (!this.FB) {
         this.jG = planepose.auc;
         this.IS = planepose.yawDeg;
         this.dQ = planepose.pitchDeg;
         this.FB = true;
         return planepose;
      } else {
         float f1 = 1.0F - (float)Math.exp(-f * 0.05F);
         this.jG = this.jG.lerp(planepose.auc, f1);
         this.IS = p(this.IS, planepose.yawDeg, f1);
         this.dQ = p(this.dQ, planepose.pitchDeg, f1);
         return new PlanePose(this.jG, this.IS, this.dQ);
      }
   }

   private static boolean j(ItemStack itemstack, Item item) {
      return itemstack != null && !itemstack.isEmpty() && itemstack.isOf(item);
   }

   private static boolean k(ItemStack itemstack, char c0) {
      if (itemstack != null && !itemstack.isEmpty()) {
         String s = itemstack.getName().getString();
         return s != null && !s.isEmpty() ? s.toUpperCase().indexOf(Character.toUpperCase(c0)) >= 0 : false;
      } else {
         return false;
      }
   }

   private boolean l(PlayerEntity playerentity, Vec3d vec3d, double d0) {
      if (this.getWorld() == null) {
         return false;
      } else {
         double d1 = d0 * d0;

         for (PlayerEntity playerentity1 : this.getClientWorld().getPlayers()) {
            if (playerentity1 != null && playerentity1 != playerentity && playerentity1.squaredDistanceTo(vec3d) <= d1) {
               return true;
            }
         }

         return false;
      }
   }

   private void m(boolean flag) {
      if (flag != this.adA) {
         this.abg = 0.0F;
         this.adA = flag;
      }

      Color color = this.colorSetting.getColor();
      int i = color.getAlpha() << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
      this.ayL = flag ? i : -1;
      float f = 0.1F;
      this.abg = Math.min(this.abg + f, 1.0F);
      this.Bb = o(this.Bb, this.ayL, this.abg);
   }

   private Color n() {
      int i = this.Bb >> 24 & 0xFF;
      int j = this.Bb >> 16 & 0xFF;
      int k = this.Bb >> 8 & 0xFF;
      int l = this.Bb & 0xFF;
      return new Color(j, k, l, i);
   }

   private static int o(int i, int j, float f) {
      int k = i >> 24 & 0xFF;
      int l = i >> 16 & 0xFF;
      int i1 = i >> 8 & 0xFF;
      int j1 = i & 0xFF;
      int k1 = j >> 24 & 0xFF;
      int l1 = j >> 16 & 0xFF;
      int i2 = j >> 8 & 0xFF;
      int j2 = j & 0xFF;
      int k2 = (int)(k + (k1 - k) * f);
      int l2 = (int)(l + (l1 - l) * f);
      int i3 = (int)(i1 + (i2 - i1) * f);
      int j3 = (int)(j1 + (j2 - j1) * f);
      return k2 << 24 | l2 << 16 | i3 << 8 | j3;
   }

   private static float p(float f, float f1, float f2) {
      float f3 = MathHelper.wrapDegrees(f1 - f);
      return f + f3 * f2;
   }
}
