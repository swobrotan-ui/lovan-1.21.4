package module;

import core.FriendManager;
import enum.Category;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.SliderSetting;

public class HitboxTweaksModule extends Module {
   private final ColorSetting friendColorSetting = new ColorSetting("Цвет друга", "Цвет хитбокса для друзей", Color.GREEN, true);
   private final ColorSetting playerColorSetting = new ColorSetting("Цвет игрока", "Цвет хитбокса для остальных игроков", Color.WHITE, true);
   private final BooleanSetting hoverHighlightSetting = new BooleanSetting("Подсветка при наводке", "Менять цвет хитбокса при наведении прицела", false);
   private final ColorSetting hoverColorSetting = new ColorSetting("Цвет наводки", "Цвет хитбокса при наведении прицела", Color.CYAN, true);
   private final BooleanSetting hitAnimationSetting = new BooleanSetting("Анимация при ударе", "Анимация хитбокса при попадании", false);
   private final ColorSetting hitColorSetting = new ColorSetting("Цвет удара", "Цвет подсветки хитбокса при ударе", Color.RED, true);
   public final SliderSetting timeSetting = new SliderSetting("Время", "Длительность анимации удара (мс)", 800.0, 200.0, 2000.0, 50.0);
   public final SliderSetting fadeStartSetting = new SliderSetting("Начало затухания", "Момент начала затухания анимации (%)", 50.0, 20.0, 80.0, 5.0);
   private final Map<Integer, obp> gr = new HashMap<Integer, obp>();
   private final Map<Integer, Float> wk = new HashMap<Integer, Float>();
   private int anR = -1;
   private static final RenderLayer arc = RenderLayer.of(
      "hitbox_fill",
      VertexFormats.POSITION_COLOR,
      DrawMode.QUADS,
      1536,
      false,
      true,
      MultiPhaseParameters.builder()
         .program(RenderPhase.POSITION_COLOR_PROGRAM)
         .transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
         .depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
         .writeMaskState(RenderPhase.COLOR_MASK)
         .build(false)
   );

   public HitboxTweaksModule() {
      super("Хитбо$Тшеакз", "манипуляции с хитбоксом", Category.VISUAL);
      this.addSettings(
         this.friendColorSetting,
         this.playerColorSetting,
         this.hoverHighlightSetting,
         this.hoverColorSetting,
         this.hitAnimationSetting,
         this.hitColorSetting,
         this.timeSetting,
         this.fadeStartSetting
      );
      this.hitColorSetting.setVisibilitySupplier(this.hitAnimationSetting::getValue);
      this.timeSetting.setVisibilitySupplier(this.hitAnimationSetting::getValue);
      this.fadeStartSetting.setVisibilitySupplier(this.hitAnimationSetting::getValue);
      this.hoverColorSetting.setVisibilitySupplier(this.hoverHighlightSetting::getValue);
   }

   @Override
   public void onAttackEntity(PlayerEntity playerentity, World world, Hand hand, Entity entity, EntityHitResult entityhitresult) {
      if (this.hitAnimationSetting.getValue() && entity != null) {
         obp obp = this.gr.computeIfAbsent(entity.getId(), integer -> {
            return new obp(this);
         });
         obp.a();
      }
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
      this.gr.clear();
      this.wk.clear();
      this.anR = -1;
   }

   public void a() {
      this.anR = -1;
      if (this.hoverHighlightSetting.getValue()) {
         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient.crosshairTarget instanceof EntityHitResult entityhitresult) {
            this.anR = entityhitresult.getEntity().getId();
         }
      }
   }

   public void b(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, Entity entity, float f) {
      boolean flag = entity instanceof PlayerEntity;
      boolean flag1 = flag && FriendManager.getInstance().isFriend(entity.getName().getString());
      this.h();
      this.c(entity.getId(), f);
      if (flag && this.hitAnimationSetting.getValue()) {
         float f1 = this.e(entity.getId());
         if (f1 > 0.0F) {
            Color color = this.hitColorSetting.getColor();
            i(matrixstack, vertexconsumerprovider, entity, color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, f1);
         }
      }

      float[] afloat = flag ? this.d((PlayerEntity)entity, flag1) : new float[]{1.0F, 1.0F, 1.0F, 1.0F};
      j(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.getLines()), entity, f, afloat[0], afloat[1], afloat[2], afloat[3]);
   }

   private void c(int i, float f) {
      if (!this.hoverHighlightSetting.getValue()) {
         this.wk.remove(i);
      } else {
         float f1 = this.wk.getOrDefault(i, 0.0F);
         boolean flag = i == this.anR;
         float f2 = 1000.0F;
         float f3 = 50.0F / f2 * f;
         if (flag) {
            f1 = Math.min(1.0F, f1 + f3);
         } else {
            f1 = Math.max(0.0F, f1 - f3);
         }

         if (f1 <= 0.0F) {
            this.wk.remove(i);
         } else {
            this.wk.put(i, f1);
         }
      }
   }

   private float[] d(PlayerEntity playerentity, boolean flag) {
      int i = playerentity.getId();
      Color color = flag ? this.friendColorSetting.getColor() : this.playerColorSetting.getColor();
      float f = this.wk.getOrDefault(i, 0.0F);
      float f1 = color.getRed() / 255.0F;
      float f2 = color.getGreen() / 255.0F;
      float f3 = color.getBlue() / 255.0F;
      float f4 = color.getAlpha() / 255.0F;
      if (this.hoverHighlightSetting.getValue() && f > 0.0F) {
         Color color1 = this.hoverColorSetting.getColor();
         f1 = this.k(f1, color1.getRed() / 255.0F, f);
         f2 = this.k(f2, color1.getGreen() / 255.0F, f);
         f3 = this.k(f3, color1.getBlue() / 255.0F, f);
         f4 = this.k(f4, color1.getAlpha() / 255.0F, f);
      }

      obp obp = this.gr.get(i);
      if (obp != null && obp.h()) {
         float f5 = obp.c();
         Color color2 = this.hitColorSetting.getColor();
         f1 = this.k(f1, color2.getRed() / 255.0F, f5);
         f2 = this.k(f2, color2.getGreen() / 255.0F, f5);
         f3 = this.k(f3, color2.getBlue() / 255.0F, f5);
         f4 = this.k(f4, color2.getAlpha() / 255.0F, f5);
      }

      return new float[]{f1, f2, f3, f4};
   }

   private float e(int i) {
      obp obp = this.gr.get(i);
      return obp != null && obp.h() ? obp.d() : 0.0F;
   }

   public float f() {
      return this.hitColorSetting.getAlpha() / 255.0F;
   }

   private void h() {
      Iterator iterator = this.gr.entrySet().iterator();

      while (iterator.hasNext()) {
         Entry entry = (Entry)iterator.next();
         ((obp)entry.getValue()).b();
         if (!((obp)entry.getValue()).h()) {
            iterator.remove();
         }
      }
   }

   private static void i(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, Entity entity, float f, float f1, float f2, float f3) {
      Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(arc);
      net.minecraft.client.util.math.MatrixStack.Entry entry = matrixstack.peek();
      float f4 = (float)box.minX;
      float f5 = (float)box.minY;
      float f6 = (float)box.minZ;
      float f7 = (float)box.maxX;
      float f8 = (float)box.maxY;
      float f9 = (float)box.maxZ;
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f5, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f4, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f6).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f8, f9).color(f, f1, f2, f3);
      vertexconsumer.vertex(entry.getPositionMatrix(), f7, f5, f9).color(f, f1, f2, f3);
   }

   private static void j(MatrixStack matrixstack, VertexConsumer vertexconsumer, Entity entity, float f, float f1, float f2, float f3, float f4) {
      Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      VertexRendering.drawBox(matrixstack, vertexconsumer, box, f1, f2, f3, f4);
      if (entity instanceof EnderDragonEntity) {
         double d0 = -MathHelper.lerp(f, entity.lastRenderX, entity.getX());
         double d1 = -MathHelper.lerp(f, entity.lastRenderY, entity.getY());
         double d2 = -MathHelper.lerp(f, entity.lastRenderZ, entity.getZ());

         for (EnderDragonPart enderdragonpart : ((EnderDragonEntity)entity).getBodyParts()) {
            matrixstack.push();
            double d3 = d0 + MathHelper.lerp(f, enderdragonpart.lastRenderX, enderdragonpart.getX());
            double d4 = d1 + MathHelper.lerp(f, enderdragonpart.lastRenderY, enderdragonpart.getY());
            double d5 = d2 + MathHelper.lerp(f, enderdragonpart.lastRenderZ, enderdragonpart.getZ());
            matrixstack.translate(d3, d4, d5);
            VertexRendering.drawBox(
               matrixstack,
               vertexconsumer,
               enderdragonpart.getBoundingBox().offset(-enderdragonpart.getX(), -enderdragonpart.getY(), -enderdragonpart.getZ()),
               0.25F,
               1.0F,
               0.0F,
               1.0F
            );
            matrixstack.pop();
         }
      }

      Entity entity1 = entity.getVehicle();
      if (entity1 != null) {
         float f5 = Math.min(entity1.getWidth(), entity.getWidth()) / 2.0F;
         Vec3d vec3d = entity1.getPassengerRidingPos(entity).subtract(entity.getPos());
         VertexRendering.drawBox(
            matrixstack, vertexconsumer, vec3d.x - f5, vec3d.y, vec3d.z - f5, vec3d.x + f5, vec3d.y + 0.0625, vec3d.z + f5, 1.0F, 0.0F, 0.0F, 1.0F
         );
      }
   }

   private float k(float f, float f1, float f2) {
      return f + (f1 - f) * f2;
   }

   public ColorSetting l() {
      return this.friendColorSetting;
   }

   public ColorSetting m() {
      return this.playerColorSetting;
   }

   public BooleanSetting n() {
      return this.hoverHighlightSetting;
   }

   public ColorSetting o() {
      return this.hoverColorSetting;
   }

   public BooleanSetting p() {
      return this.hitAnimationSetting;
   }

   public ColorSetting q() {
      return this.hitColorSetting;
   }

   public SliderSetting r() {
      return this.timeSetting;
   }

   public SliderSetting s() {
      return this.fadeStartSetting;
   }
}
