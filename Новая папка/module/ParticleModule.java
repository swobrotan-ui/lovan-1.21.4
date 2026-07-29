package module;

import enum.Category;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public abstract class ParticleModule extends Module {
   protected final List<go> aql = new ArrayList<go>(200);
   protected long SC = 0L;
   private final MatrixStack Kz = new MatrixStack();
   private rg aaD;

   public ParticleModule(String s, String s1, Category category) {
      super(s, s1, category);
   }

   @Override
   public void onEnable() {
      this.SC = System.nanoTime();
   }

   @Override
   public void onDisable() {
      this.aql.clear();
   }

   @Override
   public void onEndTick() {
      if (this.isNotInWorld()) {
         this.aql.clear();
      } else {
         this.o();
         this.a();
      }
   }

   protected void a() {
   }

   protected int b() {
      return 500;
   }

   protected int c() {
      return 50;
   }

   protected Identifier d() {
      return this.glowStarTexture;
   }

   protected abstract long e();

   protected abstract float f();

   protected int g() {
      return 3;
   }

   protected float h() {
      return 0.15F;
   }

   protected float i() {
      return 1.0F;
   }

   protected boolean j() {
      return false;
   }

   protected Color k() {
      return Color.WHITE;
   }

   public void l(double d0, double d1, double d2, double d3, double d4, double d5, float f, float f1, float f2) {
      this.aql.add(new go(d0, d1, d2, d3, d4, d5, f, f1, f2));
   }

   public void m(double d0, double d1, double d2, double d3, double d4, double d5, float f, float f1, float f2, float f3) {
      this.aql.add(new go(d0, d1, d2, d3, d4, d5, f, f1, f2, f3));
   }

   protected void o() {
      if (this.aql.size() >= this.b()) {
         this.aql.subList(0, this.c()).clear();
      }
   }

   @Override
   public void onRenderEnd(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld() && !this.aql.isEmpty()) {
         long i = System.nanoTime();
         float f = Math.min((float)(i - this.SC) / (float)NANOS_PER_SECOND, 0.1F);
         this.SC = i;
         long j = System.currentTimeMillis();
         long k = this.e();

         for (int l = this.aql.size() - 1; l >= 0; l--) {
            go go = this.aql.get(l);
            if (j - go.OK > k) {
               this.aql.remove(l);
            } else {
               go.a(f);
            }
         }

         if (!this.aql.isEmpty()) {
            this.aaD = rg.a(this.getClient());
            ml.d(this.d(), this.getClient());
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
            boolean flag = this.p(bufferbuilder, j, k);
            if (flag) {
               BufferRenderer.drawWithGlobalProgram(bufferbuilder.end());
            }

            ml.f();
         }
      }
   }

   private boolean p(BufferBuilder bufferbuilder, long i, long j) {
      boolean flag = false;
      float f = this.f();
      int k = this.g();
      float f1 = this.h();

      for (go go : this.aql) {
         float f2 = (float)(i - go.OK) / (float)j;
         if (!(f2 > 1.0F)) {
            float f3 = this.q(f2);
            if (!(f3 <= 0.01F) && ml.a(this.aaD, this.getWorld(), this.getPlayer(), go.oX, go.FO, go.dO)) {
               float f4 = f * f3 * go.atz;
               int l = ml.k(this.r(go), f3);
               this.s(bufferbuilder, go.oX, go.FO, go.dO, f4, l);
               if (k > 0 && go.b() && f2 > f1) {
                  this.t(bufferbuilder, go, f4, l, k);
               }

               flag = true;
            }
         }
      }

      return flag;
   }

   protected float q(float f) {
      float f1 = this.i();
      float f2 = 0.3F / f1;
      float f3 = 1.0F - f2;
      if (f < 0.15F) {
         return tq.agP.b(f / 0.15F, 0.0F, 1.0F, 1.0F);
      } else {
         return f > f3 ? 1.0F - tq.auP.b((f - f3) / f2, 0.0F, 1.0F, 1.0F) : 1.0F;
      }
   }

   protected int r(go go) {
      return this.j() ? ml.l(go.pS, 3000L) : ml.h(this.k(), 1.0F);
   }

   protected void s(BufferBuilder bufferbuilder, double d0, double d1, double d2, float f, int i) {
      float[] afloat = ml.i(i);
      ml.b(this.Kz, this.aaD, d0, d1, d2);
      Matrix4f matrix4f = this.Kz.peek().getPositionMatrix();
      float f1 = f / 2.0F;
      bufferbuilder.vertex(matrix4f, -f1, f - f1, 0.0F).texture(0.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], afloat[3]);
      bufferbuilder.vertex(matrix4f, f - f1, f - f1, 0.0F).texture(1.0F, 1.0F).color(afloat[0], afloat[1], afloat[2], afloat[3]);
      bufferbuilder.vertex(matrix4f, f - f1, -f1, 0.0F).texture(1.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], afloat[3]);
      bufferbuilder.vertex(matrix4f, -f1, -f1, 0.0F).texture(0.0F, 0.0F).color(afloat[0], afloat[1], afloat[2], afloat[3]);
      this.Kz.pop();
   }

   protected void t(BufferBuilder bufferbuilder, go go, float f, int i, int j) {
      double d0 = go.aye - go.oX;
      double d1 = go.amI - go.FO;
      double d2 = go.arA - go.dO;
      double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
      if (!(d3 < 0.01)) {
         for (int k = 1; k <= j; k++) {
            float f1 = (float)k / j;
            double d4 = go.oX + d0 * f1;
            double d5 = go.FO + d1 * f1;
            double d6 = go.dO + d2 * f1;
            if (ml.a(this.aaD, this.getWorld(), this.getPlayer(), d4, d5, d6)) {
               float f2 = f * 0.5F * (1.0F - f1);
               int l = ml.k(i, 0.5F * (1.0F - f1));
               this.s(bufferbuilder, d4, d5, d6, f2, l);
            }
         }
      }
   }
}
