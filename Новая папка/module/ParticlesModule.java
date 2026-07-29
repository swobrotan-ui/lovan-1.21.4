package module;

import enum.Category;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import setting.ColorSetting;
import setting.ListSetting;
import setting.SliderSetting;

public class ParticlesModule extends Module {
   private ColorSetting colorSetting = new ColorSetting("Цвет", "1", new Color(255, 255, 255));
   private ListSetting particleTypeSetting = new ListSetting(
      "Тип частиц", "1", List.<String>of("Кубы", "Треугольники", "Шарики", "Кометы"), List.<String>of("Кубы"), false
   );
   private SliderSetting maxParticlesSetting = new SliderSetting("Макс. частиц", "1", 100.0, 10.0, 200.0, 10.0);
   private SliderSetting spawnRadiusSetting = new SliderSetting("Радиус спавна", "1", 20.0, 5.0, 50.0, 5.0);
   private SliderSetting lifetimeSetting = new SliderSetting("Время жизни (мс)", "1", 3000.0, 500.0, 10000.0, 100.0);
   private SliderSetting sizeSetting = new SliderSetting("Размер", "1", 0.2F, 0.05F, 1.0, 0.05F);
   private SliderSetting glowAlphaSetting = new SliderSetting("Прозрачность свечения", "1", 0.4F, 0.0, 1.0, 0.1F);
   private SliderSetting glowSizeSetting = new SliderSetting("Размер свечения", "1", 4.0, 1.0, 10.0, 0.5);
   private final List<sq> pW = new ArrayList<sq>();
   private long aoF = 0L;
   private final MatrixStack zi = new MatrixStack();
   private static final Box app = new Box(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5);

   public ParticlesModule() {
      super("Партислез", "Создает летающие анимированные кубы вокруг игрока", Category.PARTICLES);
      this.addSettings(
         this.colorSetting,
         this.particleTypeSetting,
         this.maxParticlesSetting,
         this.spawnRadiusSetting,
         this.lifetimeSetting,
         this.sizeSetting,
         this.glowAlphaSetting,
         this.glowSizeSetting
      );
   }

   @Override
   public void onEnable() {
      this.pW.clear();
      this.aoF = System.nanoTime();
   }

   @Override
   public void onDisable() {
      this.pW.clear();
   }

   @Override
   public void onEndTick() {
      if (this.isNotInWorld()) {
         this.pW.clear();
      } else {
         int i = (int)this.maxParticlesSetting.getValue();
         float f = this.spawnRadiusSetting.getFloatValue();
         float f1 = this.sizeSetting.getFloatValue();
         String s = this.particleTypeSetting.getFirst();
         if (this.pW.size() < i) {
            if (s.equals("Кометы")) {
               Vec3d vec3d = this.getPlayer().getPos().add(xw.e(-f, f), xw.f(8.0, 14.0), xw.e(-f, f));
               Vec3d vec3d1 = new Vec3d(xw.f(-0.08, 0.08), xw.f(-0.25, -0.15), xw.f(-0.08, 0.08));
               sq sq = new sq(vec3d, Vec3d.ZERO, vec3d1, Vec3d.ZERO, (long)this.lifetimeSetting.getValue(), xw.e(f1 * 0.9F, f1 * 1.1F), "comet");
               Vec3d vec3d2 = vec3d1.normalize().multiply(-1.0);

               for (int j = sq.Qs - 1; j >= 0; j--) {
                  sq.asV.add(vec3d.add(vec3d2.multiply(j * 0.15)));
               }

               this.pW.add(sq);
               return;
            }

            this.pW
               .add(
                  new sq(
                     this.getPlayer().getPos().add(xw.e(-f, f), xw.f(0.0, 5.0), xw.e(-f, f)),
                     Vec3d.ZERO,
                     new Vec3d(xw.f(-1.0, 1.0), xw.f(0.0, 2.0), xw.f(-1.0, 1.0)),
                     new Vec3d(xw.f(-1.0, 1.0), xw.f(-1.0, 1.0), xw.f(-1.0, 1.0)),
                     (long)this.lifetimeSetting.getValue(),
                     xw.e(f1 * 0.9F, f1 * 1.1F)
                  )
               );
         }
      }
   }

   @Override
   public void onRenderEnd(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && !this.pW.isEmpty()) {
         long i = System.nanoTime();
         float f = Math.min((float)(i - this.aoF) / (float)NANOS_PER_SECOND, 0.1F);
         this.aoF = i;
         long j = System.currentTimeMillis();
         String s = this.particleTypeSetting.getFirst();

         for (int k = this.pW.size() - 1; k >= 0; k--) {
            sq sq = this.pW.get(k);
            if (j - sq.Yx <= sq.Vm && (!s.equals("Кометы") || !(sq.lW < this.getPlayer().getY() + 5.0))) {
               sq.a(f);
            } else {
               this.pW.remove(k);
            }
         }

         if (!this.pW.isEmpty()) {
            rg rg = rg.a(this.getClient());
            if (!s.equals("Кометы")) {
               this.a(rg, j);
            }

            switch (s) {
               case "Кубы":
                  this.b(rg, j);
                  return;
               case "Треугольники":
                  this.c(rg, j);
                  return;
               case "Кометы":
                  this.d(rg, j);
            }
         }
      }
   }

   private void a(rg rg, long i) {
      ml.d(this.glowTexture, this.getClient());
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float f = this.sizeSetting.getFloatValue() * this.glowSizeSetting.getFloatValue();
      boolean flag = false;

      for (sq sq : this.pW) {
         float f1 = e(sq, i);
         if (!(f1 > 1.0F)) {
            float f2 = f(f1);
            if (!(f2 <= 0.01F) && ml.a(rg, this.getWorld(), this.getPlayer(), sq.Hg, sq.lW, sq.ath)) {
               float[] afloat = ml.j(this.colorSetting.getColor(), f2 * this.glowAlphaSetting.getFloatValue());
               float f3 = f * sq.afi;
               ml.b(this.zi, rg, sq.Hg, sq.lW, sq.ath);
               ml.g(bufferbuilder, this.zi, f3, afloat[0], afloat[1], afloat[2], afloat[3]);
               this.zi.pop();
               flag = true;
            }
         }
      }

      h(bufferbuilder, flag);
      ml.f();
   }

   private void b(rg rg, long i) {
      ml.e();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      boolean flag = false;

      for (sq sq : this.pW) {
         float f = e(sq, i);
         if (!(f > 1.0F)) {
            float f1 = f(f);
            if (!(f1 <= 0.01F) && ml.a(rg, this.getWorld(), this.getPlayer(), sq.Hg, sq.lW, sq.ath)) {
               float[] afloat = ml.j(this.colorSetting.getColor(), f1);
               ml.c(this.zi, rg, sq.Hg, sq.lW, sq.ath, sq.atT, sq.QV, sq.hS, sq.afi);
               Matrix4f matrix4f = this.zi.peek().getPositionMatrix();
               dr.s(
                  bufferbuilder,
                  matrix4f,
                  (float)app.minX,
                  (float)app.minY,
                  (float)app.minZ,
                  (float)app.maxX,
                  (float)app.maxY,
                  (float)app.maxZ,
                  afloat[0],
                  afloat[1],
                  afloat[2],
                  afloat[3]
               );
               dr.t(
                  bufferbuilder,
                  matrix4f,
                  (float)app.minX,
                  (float)app.minY,
                  (float)app.minZ,
                  (float)app.maxX,
                  (float)app.maxY,
                  (float)app.maxZ,
                  afloat[0],
                  afloat[1],
                  afloat[2],
                  afloat[3]
               );
               this.zi.pop();
               flag = true;
            }
         }
      }

      h(bufferbuilder, flag);
      ml.f();
   }

   private void c(rg rg, long i) {
      ml.e();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
      boolean flag = false;

      for (sq sq : this.pW) {
         float f = e(sq, i);
         if (!(f > 1.0F)) {
            float f1 = f(f);
            if (!(f1 <= 0.01F) && ml.a(rg, this.getWorld(), this.getPlayer(), sq.Hg, sq.lW, sq.ath)) {
               float[] afloat = ml.j(this.colorSetting.getColor(), f1);
               ml.c(this.zi, rg, sq.Hg, sq.lW, sq.ath, sq.atT, sq.QV, sq.hS, sq.afi);
               dr.w(bufferbuilder, this.zi.peek().getPositionMatrix(), afloat[0], afloat[1], afloat[2], afloat[3]);
               this.zi.pop();
               flag = true;
            }
         }
      }

      h(bufferbuilder, flag);
      ml.f();
   }

   private void d(rg rg, long i) {
      ml.d(this.glowTexture, this.getClient());
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      float f = this.sizeSetting.getFloatValue() * this.glowSizeSetting.getFloatValue();
      boolean flag = false;

      for (sq sq : this.pW) {
         float f1 = e(sq, i);
         if (!(f1 > 1.0F)) {
            float f2 = g(f1);
            if (!(f2 <= 0.01F)) {
               int j = sq.asV.size();

               for (int k = 0; k < j; k++) {
                  Vec3d vec3d = sq.asV.get(k);
                  float f3 = (float)k / Math.max(1, j - 1);
                  float f4 = f * (0.15F + f3 * 0.85F);
                  float[] afloat = ml.j(this.colorSetting.getColor(), f2 * f3 * this.glowAlphaSetting.getFloatValue());
                  ml.b(this.zi, rg, vec3d.x, vec3d.y, vec3d.z);
                  ml.g(bufferbuilder, this.zi, f4, afloat[0], afloat[1], afloat[2], afloat[3]);
                  this.zi.pop();
               }

               float[] afloat1 = ml.j(this.colorSetting.getColor(), f2);
               ml.b(this.zi, rg, sq.Hg, sq.lW, sq.ath);
               ml.g(bufferbuilder, this.zi, f, afloat1[0], afloat1[1], afloat1[2], afloat1[3]);
               this.zi.pop();
               flag = true;
            }
         }
      }

      h(bufferbuilder, flag);
      ml.f();
   }

   private static float e(sq sq, long i) {
      return (float)(i - sq.Yx) / (float)sq.Vm;
   }

   private static float f(float f) {
      return ml.m(f, 0.1F, 0.8F);
   }

   private static float g(float f) {
      return ml.m(f, 0.25F, 0.6F);
   }

   private static void h(BufferBuilder bufferbuilder, boolean flag) {
      if (flag) {
         BuiltBuffer builtbuffer = bufferbuilder.endNullable();
         if (builtbuffer != null) {
            BufferRenderer.drawWithGlobalProgram(builtbuffer);
         }
      }
   }
}
