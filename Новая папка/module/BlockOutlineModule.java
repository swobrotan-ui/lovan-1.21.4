package module;

import enum.Category;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import render.BuiltLine3d;
import setting.BooleanSetting;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class BlockOutlineModule extends Module {
   private static final String vG = "Бокс";
   private static final String xa = "Углы";
   private ListSetting modeSetting = new ListSetting(
      "Режим", "Режим отображения контура", Arrays.<String>asList("Бокс", "Углы"), List.<String>of("Бокс"), false
   );
   private ColorSetting outlineColorSetting = new ColorSetting("Цвет контура", "Цвет линий контура", new Color(100, 200, 255, 200), true);
   private ColorSetting fillColorSetting = new ColorSetting("Цвет заливки", "Цвет заливки блока", new Color(100, 200, 255, 50), true);
   private SliderSetting lineWidthSetting = new SliderSetting("Толщина линии", "Толщина линий контура", 2.5, 0.5, 10.0, 0.5);
   private SliderSetting cornerSizeSetting = new SliderSetting("Размер углов", "Размер угловых линий", 0.3, 0.1, 1.0, 0.05);
   private SliderSetting transitionSpeedSetting = new SliderSetting("Скорость перехода", "Скорость перехода между блоками", 8.0, 1.0, 20.0, 1.0);
   private BooleanSetting fillSetting = new BooleanSetting("Заливка", "Включить заливку блока", false);
   private BooleanSetting throughWallsSetting = new BooleanSetting("Сквозь стены", "Видеть обводку через блоки", true);
   private final rpy mH = new rpy();
   private Vec3d afK = null;
   private Vec3d Mc = null;
   private Vec3d Mx = null;
   private Vec3d auR = null;
   private float aoT = 0.0F;
   private BlockPos dx = null;
   private long Ez = System.currentTimeMillis();
   private static final float aAd = 5.0F;

   public BlockOutlineModule() {
      super("БлоскОутлине", "Подсвечивает выбранный блок", Category.VISUAL);
      this.addSettings(
         this.modeSetting,
         this.outlineColorSetting,
         this.fillSetting,
         this.fillColorSetting,
         this.lineWidthSetting,
         this.cornerSizeSetting,
         this.transitionSpeedSetting,
         this.throughWallsSetting
      );
   }

   @Override
   public void onEnable() {
      this.Ez = System.currentTimeMillis();
      this.aoT = 0.0F;
      this.dx = null;
      this.afK = null;
      this.Mc = null;
      this.Mx = null;
      this.auR = null;
   }

   @Override
   public void onDisable() {
      this.dx = null;
      this.afK = null;
      this.Mc = null;
      this.Mx = null;
      this.auR = null;
   }

   @Override
   public void onRenderAfterEntities(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && this.isEnabled()) {
         float f = this.e();
         HitResult hitresult = this.getClient().crosshairTarget;
         if (hitresult != null && hitresult.getType() == Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)hitresult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            BlockState blockstate = this.getWorld().getBlockState(blockpos);
            if (blockstate.isAir()) {
               this.aoT = Math.max(0.0F, this.aoT - f * 5.0F);
               if (this.aoT <= 0.0F) {
                  this.a();
               }
            } else {
               this.aoT = Math.min(1.0F, this.aoT + f * 5.0F);
               VoxelShape voxelshape = blockstate.getOutlineShape(this.getWorld(), blockpos);
               if (!voxelshape.isEmpty()) {
                  Box box = voxelshape.getBoundingBox().offset(blockpos);
                  Vec3d vec3d = new Vec3d((box.minX + box.maxX) / 2.0, (box.minY + box.maxY) / 2.0, (box.minZ + box.maxZ) / 2.0);
                  Vec3d vec3d1 = new Vec3d(box.maxX - box.minX, box.maxY - box.minY, box.maxZ - box.minZ);
                  if (this.dx == null || !this.dx.equals(blockpos)) {
                     this.dx = blockpos;
                     this.Mc = vec3d;
                     this.auR = vec3d1;
                     if (this.afK == null) {
                        this.afK = this.Mc;
                        this.Mx = this.auR;
                     }
                  }
               }
            }
         } else {
            this.aoT = Math.max(0.0F, this.aoT - f * 5.0F);
            if (this.aoT <= 0.0F) {
               this.a();
            }
         }

         if (!(this.aoT <= 0.0F) && this.afK != null && this.Mx != null) {
            if (this.Mc != null && this.auR != null) {
               float f4 = this.transitionSpeedSetting.getFloatValue();
               float f5 = Math.min(1.0F, f * f4);
               this.afK = this.b(this.afK, this.Mc, f5);
               this.Mx = this.b(this.Mx, this.auR, f5);
            }

            Vec3d vec3d2 = worldrendercontext.camera().getPos();
            MatrixStack matrixstack = worldrendercontext.matrixStack();
            Vec3d vec3d3 = new Vec3d(this.afK.x - this.Mx.x / 2.0, this.afK.y - this.Mx.y / 2.0, this.afK.z - this.Mx.z / 2.0).subtract(vec3d2);
            Vec3d vec3d4 = new Vec3d(this.afK.x + this.Mx.x / 2.0, this.afK.y + this.Mx.y / 2.0, this.afK.z + this.Mx.z / 2.0).subtract(vec3d2);
            if (this.fillSetting.getValue()) {
               Color color = this.fillColorSetting.getColor();
               float f6 = color.getRed() / 255.0F;
               float f8 = color.getGreen() / 255.0F;
               float f1 = color.getBlue() / 255.0F;
               float f2 = color.getAlpha() / 255.0F * this.aoT;
               dr.q(matrixstack, vec3d3, vec3d4, f6, f8, f1, f2, this.throughWallsSetting.getValue());
            }

            Color color1 = this.outlineColorSetting.getColor();
            float f7 = color1.getRed() / 255.0F;
            float f9 = color1.getGreen() / 255.0F;
            float f10 = color1.getBlue() / 255.0F;
            float f11 = color1.getAlpha() / 255.0F * this.aoT;
            String s = this.modeSetting.getSelectedValues().getFirst();
            float f3 = this.lineWidthSetting.getFloatValue();
            BuiltLine3d builtline3d = this.mH.a(f3).b(f7, f9, f10, f11).l(8).g(true).m(!this.throughWallsSetting.getValue()).a();
            xzs xzs = builtline3d.h();
            if ("Бокс".equals(s)) {
               xzs.c(vec3d3, vec3d4);
            } else if ("Углы".equals(s)) {
               this.c(xzs, vec3d3, vec3d4);
            }

            xzs.d(matrixstack, Vec3d.ZERO);
         }
      }
   }

   private void a() {
      this.dx = null;
      this.afK = null;
      this.Mc = null;
      this.Mx = null;
      this.auR = null;
   }

   private Vec3d b(Vec3d vec3d, Vec3d vec3d1, float f) {
      return new Vec3d(vec3d.x + (vec3d1.x - vec3d.x) * f, vec3d.y + (vec3d1.y - vec3d.y) * f, vec3d.z + (vec3d1.z - vec3d.z) * f);
   }

   private void c(xzs xzs, Vec3d vec3d, Vec3d vec3d1) {
      float f = this.cornerSizeSetting.getFloatValue();
      double d0 = vec3d1.x - vec3d.x;
      double d1 = vec3d1.y - vec3d.y;
      double d2 = vec3d1.z - vec3d.z;
      double d3 = Math.min((double)f, d0 / 2.0);
      double d4 = Math.min((double)f, d1 / 2.0);
      double d5 = Math.min((double)f, d2 / 2.0);
      this.d(xzs, vec3d.x, vec3d.y, vec3d.z, d3, d4, d5, 1, 1, 1);
      this.d(xzs, vec3d1.x, vec3d.y, vec3d.z, d3, d4, d5, -1, 1, 1);
      this.d(xzs, vec3d.x, vec3d.y, vec3d1.z, d3, d4, d5, 1, 1, -1);
      this.d(xzs, vec3d1.x, vec3d.y, vec3d1.z, d3, d4, d5, -1, 1, -1);
      this.d(xzs, vec3d.x, vec3d1.y, vec3d.z, d3, d4, d5, 1, -1, 1);
      this.d(xzs, vec3d1.x, vec3d1.y, vec3d.z, d3, d4, d5, -1, -1, 1);
      this.d(xzs, vec3d.x, vec3d1.y, vec3d1.z, d3, d4, d5, 1, -1, -1);
      this.d(xzs, vec3d1.x, vec3d1.y, vec3d1.z, d3, d4, d5, -1, -1, -1);
   }

   private void d(xzs xzs, double d0, double d1, double d2, double d3, double d4, double d5, int i, int j, int k) {
      Vec3d vec3d = new Vec3d(d0, d1, d2);
      xzs.b(vec3d, new Vec3d(d0 + d3 * i, d1, d2));
      xzs.b(vec3d, new Vec3d(d0, d1 + d4 * j, d2));
      xzs.b(vec3d, new Vec3d(d0, d1, d2 + d5 * k));
   }

   private float e() {
      long i = System.currentTimeMillis();
      float f = (float)(i - this.Ez) / 1000.0F;
      this.Ez = i;
      return Math.min(f, 0.1F);
   }
}
