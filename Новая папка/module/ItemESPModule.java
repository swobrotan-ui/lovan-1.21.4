package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import font.MSDFFont;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class ItemESPModule extends Module {
   private ListSetting espModeSetting = new ListSetting(
      "Режим ESP", "Режим отображения ESP", Arrays.<String>asList("Ящик", "Углы"), List.<String>of("Углы"), false
   );
   private SliderSetting cornerSizeSetting = new SliderSetting("Размер углов", "Размер углов ESP", 0.3, 0.1, 1.0, 0.1);
   private ListSetting labelModeSetting = new ListSetting(
      "Режим плашки", "Стиль отображения меток", Arrays.<String>asList("Стандартный", "Плашки"), List.<String>of("Стандартный"), false
   );
   private SliderSetting textScaleSetting = new SliderSetting("Масштаб текста", "Масштаб текста", 1.0, 0.5, 2.0, 0.1);
   private ColorSetting itemColorSetting = new ColorSetting("Цвет предметов", "Цвет ESP для предметов", new Color(255, 255, 0), true);
   private BooleanSetting onlyValuableSetting = new BooleanSetting("Только ценные предметы", "Показывать только ценные предметы", false);
   private BooleanSetting clusterSetting = new BooleanSetting("Кучковать", "Группировать близкие предметы в одну плашку", true);
   private SliderSetting minItemsSetting = new SliderSetting("Мин. предметов", "Минимум предметов для отображения как кучи", 4.0, 2.0, 10.0, 1.0);
   private MSDFFont fd;
   private final Quaternionf yS = new Quaternionf();
   private final Quaternionf kl = new Quaternionf();
   private final float[] apu = new float[3];
   private static final float xK = 16.0F;
   private static final float auX = 5.0F;
   private static final float eL = 10.0F;
   private static final float nc = 8.0F;
   private static final float Ml = 4.0F;
   private static final double akJ = 2.0;

   public ItemESPModule() {
      super("ИтемЕЗП", "Показывать предметы сквозь стены", Category.RENDER);
      this.fd = MSDFFont.g().b("bb").c("bb").e();
      this.cornerSizeSetting.setVisibilitySupplier(() -> {
         return this.espModeSetting.isSelected("Углы");
      });
      this.minItemsSetting.setVisibilitySupplier(() -> {
         return this.clusterSetting.getValue();
      });
      this.addSettings(
         this.espModeSetting,
         this.cornerSizeSetting,
         this.labelModeSetting,
         this.textScaleSetting,
         this.itemColorSetting,
         this.onlyValuableSetting,
         this.clusterSetting,
         this.minItemsSetting
      );
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld()) {
         try {
            this.a(worldrendercontext);
         } catch (Exception exception) {
         }
      }
   }

   private void a(WorldRenderContext worldrendercontext) {
      MatrixStack matrixstack = worldrendercontext.matrixStack();
      if (matrixstack != null) {
         Vec3d vec3d = worldrendercontext.camera().getPos();
         float f = worldrendercontext.tickCounter().getTickDelta(true);
         Vec3d vec3d1 = this.getPlayer().getPos();
         double d0 = 128.0;
         Camera camera = worldrendercontext.camera();
         float f1 = (float)((Integer)this.getClient().options.getFov().getValue()).intValue();
         List list = this.getWorld().getEntitiesByClass(ItemEntity.class, this.getPlayer().getBoundingBox().expand(d0), itementity1 -> {
            return true;
         });
         ArrayList arraylist = new ArrayList();

         for (ItemEntity itementity : list) {
            if (wu.d(itementity, vec3d1, d0, this.onlyValuableSetting.getValue(), 1.0)) {
               Vec3d vec3d2 = itementity.getPos().add(0.0, itementity.getHeight() + 0.5, 0.0);
               if (wu.a(vec3d2, camera, f1)) {
                  arraylist.add(itementity);
               }
            }
         }

         if (!arraylist.isEmpty()) {
            mq mq = this.clusterSetting.getValue()
               ? this.b(arraylist, 2.0, (int)this.minItemsSetting.getValue())
               : new mq(Collections.<sm>emptyList(), arraylist);

            try {
               this.c(matrixstack, mq, vec3d, f);
            } catch (Exception exception1) {
            }

            try {
               if (this.labelModeSetting.isSelected("Плашки")) {
                  this.g(worldrendercontext, mq, vec3d, f, vec3d1);
               } else {
                  this.e(worldrendercontext, mq, vec3d, f, vec3d1);
               }
            } catch (Exception exception) {
            }
         }
      }
   }

   private mq b(List<ItemEntity> list, double d0, int i) {
      ArrayList arraylist = new ArrayList();
      ArrayList arraylist1 = new ArrayList();
      boolean[] aboolean = new boolean[list.size()];

      for (int j = 0; j < list.size(); j++) {
         if (!aboolean[j]) {
            ArrayList arraylist2 = new ArrayList();
            arraylist2.add(j);
            Vec3d vec3d = ((ItemEntity)list.get(j)).getPos();

            for (int k = j + 1; k < list.size(); k++) {
               if (!aboolean[k] && vec3d.distanceTo(((ItemEntity)list.get(k)).getPos()) <= d0) {
                  arraylist2.add(k);
               }
            }

            for (int l : arraylist2) {
               aboolean[l] = true;
            }

            if (arraylist2.size() < i) {
               for (int j1 : arraylist2) {
                  arraylist1.add((ItemEntity)list.get(j1));
               }
            } else {
               double d3 = 0.0;
               double d1 = 0.0;
               double d2 = 0.0;
               ArrayList arraylist3 = new ArrayList();
               LinkedHashMap linkedhashmap = new LinkedHashMap();

               for (int i1 : arraylist2) {
                  ItemEntity itementity = (ItemEntity)list.get(i1);
                  arraylist3.add(itementity);
                  Vec3d vec3d1 = itementity.getPos();
                  d3 += vec3d1.x;
                  d1 += vec3d1.y;
                  d2 += vec3d1.z;
                  linkedhashmap.merge(this.j(itementity.getStack()), itementity.getStack().getCount(), Integer::sum);
               }

               int k1 = arraylist3.size();
               arraylist.add(new sm(new Vec3d(d3 / k1, d1 / k1, d2 / k1), arraylist3, linkedhashmap));
            }
         }
      }

      return new mq(arraylist, arraylist1);
   }

   private void c(MatrixStack matrixstack, mq mq, Vec3d vec3d, float f) {
      dr.a(false);

      try {
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
         boolean flag = false;

         for (ItemEntity itementity : mq.yT) {
            Vec3d vec3d1 = dr.c(itementity, f, vec3d);
            matrixstack.push();

            try {
               matrixstack.translate(vec3d1.x, vec3d1.y, vec3d1.z);
               this.d(matrixstack, itementity, bufferbuilder);
               flag = true;
            } finally {
               matrixstack.pop();
            }
         }

         for (sm sm : mq.auf) {
            Vec3d vec3d2 = sm.ajP.subtract(vec3d);
            matrixstack.push();

            try {
               matrixstack.translate(vec3d2.x, vec3d2.y, vec3d2.z);
               this.d(matrixstack, sm.OJ.getFirst(), bufferbuilder);
               flag = true;
            } finally {
               matrixstack.pop();
            }
         }

         if (flag) {
            BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
         }
      } finally {
         dr.b();
      }
   }

   private void d(MatrixStack matrixstack, ItemEntity itementity, BufferBuilder bufferbuilder) {
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      float f = itementity.getWidth();
      float f1 = itementity.getHeight();
      float[] afloat = this.l();
      if (this.espModeSetting.isSelected("Ящик")) {
         dr.f(bufferbuilder, matrix4f, f, f1, afloat, 1.0F);
      } else {
         if (this.espModeSetting.isSelected("Углы")) {
            dr.g(bufferbuilder, matrix4f, f, f1, this.cornerSizeSetting.getFloatValue(), afloat, 1.0F);
         }
      }
   }

   private void e(WorldRenderContext worldrendercontext, mq mq, Vec3d vec3d, float f, Vec3d vec3d1) {
      MatrixStack matrixstack = worldrendercontext.matrixStack();
      TextRenderer textrenderer = this.getClient().textRenderer;
      Camera camera = worldrendercontext.camera();
      if (matrixstack != null && textrenderer != null) {
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableDepthTest();
         RenderSystem.disableCull();
         Immediate immediate = this.getClient().getBufferBuilders().getEntityVertexConsumers();

         try {
            for (ItemEntity itementity : mq.yT) {
               Vec3d vec3d2 = dr.c(itementity, f, vec3d);
               this.f(
                  matrixstack,
                  camera,
                  immediate,
                  textrenderer,
                  vec3d2,
                  itementity.getHeight(),
                  vec3d1.distanceTo(itementity.getPos()),
                  Collections.<String>singletonList(this.k(itementity.getStack()))
               );
            }

            for (sm sm : mq.auf) {
               Vec3d vec3d3 = sm.ajP.subtract(vec3d);
               this.f(matrixstack, camera, immediate, textrenderer, vec3d3, 0.5F, vec3d1.distanceTo(sm.ajP), this.i(sm));
            }

            immediate.draw();
         } finally {
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
         }
      }
   }

   private void f(MatrixStack matrixstack, Camera camera, Immediate immediate, TextRenderer textrenderer, Vec3d vec3d, float f, double d0, List<String> list) {
      matrixstack.push();

      try {
         matrixstack.translate(vec3d.x, vec3d.y + f + 0.5, vec3d.z);
         matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
         float f1 = this.textScaleSetting.getFloatValue() * 0.025F * (0.8F + Math.min(1.0F, (float)(d0 / 10.0)) * 0.4F);
         matrixstack.scale(-f1, -f1, f1);
         byte b0 = 2;
         int i = 9 + 2;
         int j = i * list.size() - 2 + b0 * 2;
         int k = 0;

         for (String s : list) {
            int l = textrenderer.getWidth(Text.literal(s));
            if (l > k) {
               k = l;
            }
         }

         int l1 = k + b0 * 2;
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         int i2 = -l1 / 2;
         int i1 = -j / 2;
         RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         bufferbuilder.vertex(matrix4f, i2, i1, 0.0F).color(0, 0, 0, 128);
         bufferbuilder.vertex(matrix4f, i2, i1 + j, 0.0F).color(0, 0, 0, 128);
         bufferbuilder.vertex(matrix4f, i2 + l1, i1 + j, 0.0F).color(0, 0, 0, 128);
         bufferbuilder.vertex(matrix4f, i2 + l1, i1, 0.0F).color(0, 0, 0, 128);
         BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
         int j1 = i1 + b0;

         for (String s1 : list) {
            MutableText mutabletext = Text.literal(s1);
            int k1 = -textrenderer.getWidth(mutabletext) / 2;
            textrenderer.draw(mutabletext.asOrderedText(), k1, j1, 16777215, true, matrix4f, immediate, TextLayerType.SEE_THROUGH, 0, 15728880);
            j1 += i;
         }
      } finally {
         matrixstack.pop();
      }
   }

   private void g(WorldRenderContext worldrendercontext, mq mq, Vec3d vec3d, float f, Vec3d vec3d1) {
      MatrixStack matrixstack = worldrendercontext.matrixStack();
      Camera camera = worldrendercontext.camera();
      if (matrixstack != null && this.fd != null) {
         RenderSystem.disableDepthTest();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.disableCull();

         try {
            for (ItemEntity itementity : mq.yT) {
               Vec3d vec3d2 = dr.c(itementity, f, vec3d);
               this.h(
                  matrixstack,
                  camera,
                  vec3d2,
                  itementity.getHeight(),
                  vec3d1.distanceTo(itementity.getPos()),
                  Collections.<String>singletonList(this.k(itementity.getStack()))
               );
            }

            for (sm sm : mq.auf) {
               Vec3d vec3d3 = sm.ajP.subtract(vec3d);
               this.h(matrixstack, camera, vec3d3, 0.5F, vec3d1.distanceTo(sm.ajP), this.i(sm));
            }
         } finally {
            dr.b();
         }
      }
   }

   private void h(MatrixStack matrixstack, Camera camera, Vec3d vec3d, float f, double d0, List<String> list) {
      matrixstack.push();

      try {
         matrixstack.translate(vec3d.x, vec3d.y + f + 0.5, vec3d.z);
         matrixstack.multiply(this.yS.rotationY(-camera.getYaw() * (float) (Math.PI / 180.0)));
         matrixstack.multiply(this.kl.rotationX(camera.getPitch() * (float) (Math.PI / 180.0)));
         float f1 = this.textScaleSetting.getFloatValue() * 0.025F * (0.8F + Math.min(1.0F, (float)(d0 / 10.0)) * 0.4F);
         matrixstack.scale(-f1, -f1, f1);
         float f2 = 0.0F;

         for (String s : list) {
            float f3 = this.fd.c(s, 16.0F);
            if (f3 > f2) {
               f2 = f3;
            }
         }

         float f10 = 20.0F;
         float f11 = f10 * list.size() - 4.0F;
         float f12 = f2 + 20.0F;
         float f4 = f11 + 10.0F;
         float f5 = -f12 / 2.0F;
         float f6 = -f4 / 2.0F;
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         BuiltRectangle builtrectangle = RectangleCache.b(f12, f4, 8.0F);
         builtrectangle.a(matrix4f, f5, f6, 1.0F);
         float f7 = f6 + 5.0F;

         for (String s1 : list) {
            float f8 = this.fd.c(s1, 16.0F);
            float f9 = -f8 / 2.0F;
            BuiltText builttext = TextCache.a(this.fd, s1, 16.0F, Color.WHITE);
            builttext.render(matrix4f, f9, f7 - 1.5F);
            f7 += f10;
         }
      } finally {
         matrixstack.pop();
      }
   }

   private List<String> i(sm sm) {
      ArrayList arraylist = new ArrayList();

      for (Entry entry : sm.auT.entrySet()) {
         String s2;
         if ((Integer)entry.getValue() > 1) {
            s2 = (String)entry.getKey();
            String s = String.valueOf(entry.getValue());
            String s1 = s2;
            s2 = s1 + " x" + s;
         } else {
            s2 = (String)entry.getKey();
         }

         arraylist.add(s2);
      }

      return arraylist;
   }

   private String j(ItemStack itemstack) {
      String s = itemstack.getItem() == Items.PLAYER_HEAD
         ? itemstack.getName().getString()
         : Text.translatable(itemstack.getItem().getTranslationKey()).getString();
      return s.replaceAll("§.", "");
   }

   private String k(ItemStack itemstack) {
      String s = this.j(itemstack);
      if (itemstack.getCount() > 1) {
         int i = itemstack.getCount();
         return s + " x" + i;
      } else {
         return s;
      }
   }

   private float[] l() {
      Color color = this.itemColorSetting.getColor();
      this.apu[0] = color.getRed() / 255.0F;
      this.apu[1] = color.getGreen() / 255.0F;
      this.apu[2] = color.getBlue() / 255.0F;
      return this.apu;
   }

   @Override
   public void onPlayerDeath(PlayerEntity playerentity) {
      super.onPlayerDeath(playerentity);
      if (this.enabledSetting.getValue() && playerentity == this.getPlayer()) {
         this.setEnabled(false);
      }
   }
}
