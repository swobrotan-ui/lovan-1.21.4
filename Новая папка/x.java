import core.Localization;
import gui.Component;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.TextCache;

public class x extends Component {
   private static final float be = 4.0F;
   private static final float awz = 2.0F;
   private static final float oA = 4.0F;
   private static final float Qr = 8.0F;
   private static final float asN = 4.0F;
   private static final float DP = 10.0F;
   private static final float vB = 5.0F;
   private static final float ari = 6.0F;
   private static final float OF = 6.0F;
   private static final float JU = 200.0F;
   private static final int dT = 3;
   private static final float nI = 3.0F;
   private static final float Yc = 2.0F;
   private static final String sA = "Y";
   private final List<String> asr;
   private String aoz;
   private boolean awh = false;
   private Consumer<String> awo;
   private Consumer<Boolean> Rz;
   private boolean azZ = false;
   private BuiltRectangle zh;
   private BuiltRectangle R;
   private final List<Float> FR = new ArrayList<Float>();
   private final float kN;
   private float lR;
   private float nN;
   private float aga;
   private float atA;
   private float qJ = 0.0F;
   private boolean agC = false;
   private BuiltRectangle Iq;
   private BuiltRectangle BH;
   private float auk = 0.0F;
   private float VT = 0.0F;
   private long pJ = 0L;
   private final cd Oq = new cd();
   private boolean NK = false;
   private double So = 0.0;
   private float SJ = 0.0F;
   public float cM = 1.0F;
   public float Lp = 0.0F;
   public float Kp = 0.0F;
   private final Matrix4f aiD = new Matrix4f();

   public x(float f, float f1, List<String> list, String s) {
      super(f, f1, 0.0F, 0.0F);
      this.asr = new ArrayList<String>(list);
      this.aoz = s != null && list.contains(s) ? s : (String)list.getFirst();
      this.kN = this.aeN.e().d() * 13.0F;
      this.g();
      this.i();
   }

   public void a(boolean flag) {
      if (this.awh != flag) {
         this.awh = flag;
         this.VT = flag ? 1.0F : 0.0F;
         this.pJ = System.currentTimeMillis();
         if (!flag) {
            this.Oq.f(0.0F);
         }

         if (this.Rz != null) {
            this.Rz.accept(flag);
         }
      }
   }

   public boolean b() {
      return Math.abs(this.auk - this.VT) > 0.001F;
   }

   public boolean c() {
      return this.awh && this.auk >= 0.99F;
   }

   public float d() {
      float f = this.j(this.auk);
      return !this.awh && !(this.auk > 0.0F) ? 0.0F : (5.0F + this.aga) * f;
   }

   public float e() {
      return 5.0F + this.aga;
   }

   private String f(String s) {
      return Localization.a().c(s);
   }

   private void g() {
      float f = 0.0F;
      int i = 0;

      for (int j = this.asr.size(); i < j; i++) {
         float f1 = this.aeN.c(this.f(this.asr.get(i)), 13.0F);
         if (f1 > f) {
            f = f1;
         }
      }

      this.agC = this.asr.size() > 3;
      float f2 = this.agC ? 7.0F : 0.0F;
      this.nN = 8.0F + f + 10.0F + 8.0F + 15.0F + f2;
      this.atA = 12.0F + this.kN * this.asr.size() + 6.0F * (this.asr.size() - 1);
      int k = Math.min(this.asr.size(), 3);
      this.aga = 12.0F + this.kN * k + 6.0F * (k - 1);
      this.qJ = Math.max(0.0F, this.atA - this.aga);
      this.aem = 2.0F + this.kN + 4.0F;
      this.h();
   }

   private void h() {
      float f = this.aeN.c(this.f(this.aoz), 13.0F);
      this.lR = 8.0F + f + 10.0F + 8.0F + 15.0F;
      this.qd = this.lR;
      this.zh = new br().a(this.lR, this.aem).b(4.0F).a();
   }

   private void i() {
      this.R = new br().a(this.nN, this.aga).b(4.0F).a();
      this.FR.clear();
      float f = 6.0F;

      for (int i = 0; i < this.asr.size(); i++) {
         this.FR.add(f);
         f += this.kN + 6.0F;
      }

      if (this.agC) {
         float f2 = this.aga - 4.0F;
         this.Iq = new br().a(3.0F, f2).b(1.5F).a();
         float f1 = Math.max(20.0F, f2 * (this.aga / this.atA));
         this.BH = new br().a(3.0F, f1).b(1.5F).a();
      }
   }

   private float j(float f) {
      return 1.0F - (float)Math.pow(1.0F - f, 3.0);
   }

   private void k(float f2) {
      if (this.auk != this.VT) {
         long i = System.currentTimeMillis();
         float f = (float)(i - this.pJ);
         float f1 = Math.min(1.0F, f / 200.0F);
         if (this.VT > 0.0F) {
            this.auk = f1;
         } else {
            this.auk = 1.0F - f1;
         }

         if (f1 >= 1.0F) {
            this.auk = this.VT;
         }
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.k(f2);
      float f4 = f - this.lR;
      this.zh.a(matrix4f, f4, f1, f3);
      String s = this.f(this.aoz);
      BuiltText builttext = TextCache.a(this.ayW, s, 13.0F, aeq);
      builttext.a(matrix4f, f4 + 4.0F, f1 + 2.0F, f3);
      BuiltText builttext1 = TextCache.a(this.aiL, "Y", 8.0F, Bz);
      float f5 = f4 + this.lR - 10.0F - 8.0F;
      float f6 = f1 + (this.aem - 4.0F) / 2.0F;
      float f7 = f5 + 4.0F;
      float f8 = f6 + 2.0F;
      this.aiD.set(matrix4f);
      this.aiD.translate(f7, f8, 0.0F);
      this.aiD.rotateZ((float)Math.toRadians(this.auk * 180.0F));
      this.aiD.translate(-f7, -f8, 0.0F);
      builttext1.a(this.aiD, f5, f6, f3);
      if ((this.awh || this.auk > 0.01F) && !this.azZ) {
         this.l(matrix4f, f, f1, f3);
      }
   }

   public void l(Matrix4f matrix4f, float f, float f1, float f2) {
      if (!(this.auk < 0.01F)) {
         this.Oq.c();
         float f3 = this.Oq.d();
         float f4 = this.j(this.auk);
         float f5 = f - this.nN;
         float f6 = f1 + this.aem + 5.0F;
         float f7 = (1.0F - f4) * -8.0F;
         float f8 = f6 + f7;
         float f9 = f2 * f4;
         this.R.a(matrix4f, f5, f8, f9);
         float f10 = 6.0F;
         float f11 = this.aga - f10 * 2.0F;
         efj.a(0.0F, 0.0F, this.nN, f11, this.cM, this.Lp + f5 * this.cM, this.Kp + (f8 + f10) * this.cM);

         for (int i = 0; i < this.asr.size(); i++) {
            String s = this.asr.get(i);
            boolean flag = s.equals(this.aoz);
            Color color = flag ? aeq : KY;
            BuiltText builttext = TextCache.a(this.ayW, this.f(s), 13.0F, color);
            float f12 = f5 + 4.0F;
            float f13 = this.FR.get(i) - f3;
            float f14 = (float)i / this.asr.size() * 0.3F;
            float f15 = this.j(Math.max(0.0F, Math.min(1.0F, (this.auk - f14) / (1.0F - f14))));
            float f16 = (1.0F - f15) * -6.0F;
            float f17 = flag ? 1.0F : 0.2F;
            float f18 = f9 * f15 * f17;
            if (f18 > 0.01F) {
               builttext.a(matrix4f, f12, f8 + f13 + f16, f18);
            }
         }

         efj.b();
         if (this.agC && this.qJ > 0.0F) {
            float f19 = f5 + this.nN - 3.0F - 2.0F;
            float f20 = f8 + 2.0F;
            float f21 = this.aga - 4.0F;
            this.Iq.a(matrix4f, f19, f20, f9 * 0.3F);
            float f22 = Math.max(20.0F, f21 * (this.aga / this.atA));
            float f23 = f21 - f22;
            float f24 = f20 + f23 * (f3 / this.qJ);
            this.BH.a(matrix4f, f19, f24, f9 * 0.6F);
         }
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i != 0 && i != 1) {
         return false;
      } else {
         float f = this.it - this.lR;
         if (this.p(d0, d1, f, this.atW, this.lR, this.aem)) {
            this.a(!this.awh);
            return true;
         } else {
            if (this.awh) {
               float f1 = this.it - this.nN;
               float f2 = this.atW + this.aem + 5.0F;
               if (this.p(d0, d1, f1, f2, this.nN, this.aga)) {
                  if (this.agC && this.m(d0, d1, f1, f2)) {
                     return this.n(d0, d1, f2);
                  }

                  return this.o(d0, d1, f2);
               }

               this.a(false);
            }

            return false;
         }
      }
   }

   private boolean m(double d0, double d1, float f, float f2) {
      float f1 = f + this.nN - 3.0F - 2.0F;
      return d0 >= f1 - 2.0F && d0 <= f + this.nN;
   }

   private boolean n(double d1, double d0, float f) {
      float f1 = this.Oq.d();
      float f2 = this.aga - 4.0F;
      float f3 = Math.max(20.0F, f2 * (this.aga / this.atA));
      float f4 = f2 - f3;
      float f5 = f + 2.0F + f4 * (f1 / this.qJ);
      if (d0 >= f5 && d0 <= f5 + f3) {
         this.NK = true;
         this.So = d0;
         this.SJ = f1;
      } else {
         float f6 = (float)(d0 - f - 2.0);
         float f7 = f6 / f2;
         float f8 = Math.max(0.0F, Math.min(this.qJ, f7 * this.qJ));
         this.Oq.a(f8);
      }

      return true;
   }

   private boolean o(double d0, double d1, float f) {
      float f1 = this.it - this.nN;
      if (this.agC && this.m(d0, d1, f1, f)) {
         return true;
      } else {
         float f2 = this.Oq.d();

         for (int i = 0; i < this.asr.size(); i++) {
            float f3 = f + this.FR.get(i) - f2;
            if (d1 >= f3 && d1 <= f3 + this.kN) {
               String s = this.asr.get(i);
               if (!s.equals(this.aoz)) {
                  this.aoz = s;
                  this.h();
                  if (this.awo != null) {
                     this.awo.accept(this.aoz);
                  }
               }

               this.a(false);
               return true;
            }
         }

         return true;
      }
   }

   @Override
   protected boolean e(double d0, double d1, double d3, double d2) {
      if (this.awh && this.agC) {
         float f = this.it - this.nN;
         float f1 = this.atW + this.aem + 5.0F;
         if (this.p(d0, d1, f, f1, this.nN, this.aga)) {
            float f2 = (float)(-d2 * (this.kN + 6.0F));
            float f3 = Math.max(0.0F, Math.min(this.qJ, this.Oq.e() + f2));
            this.Oq.a(f3);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean d(double d1, double d0, int i, double d2, double d3) {
      if (this.NK && this.agC) {
         float f = this.aga - 4.0F;
         float f1 = Math.max(20.0F, f * (this.aga / this.atA));
         float f2 = f - f1;
         float f3 = (float)(d0 - this.So);
         float f4 = f3 / f2;
         float f5 = Math.max(0.0F, Math.min(this.qJ, this.SJ + f4 * this.qJ));
         this.Oq.f(f5);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (this.NK) {
         this.NK = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isHovered(double d0, double d1) {
      float f = this.it - this.lR;
      if (this.p(d0, d1, f, this.atW, this.lR, this.aem)) {
         return true;
      } else if (this.awh) {
         float f1 = this.it - this.nN;
         float f2 = this.atW + this.aem + 5.0F;
         return this.p(d0, d1, f1, f2, this.nN, this.aga);
      } else {
         return false;
      }
   }

   private boolean p(double d0, double d1, float f, float f1, float f2, float f3) {
      return d0 >= f && d0 <= f + f2 && d1 >= f1 && d1 <= f1 + f3;
   }

   public String q() {
      return this.aoz;
   }

   public boolean r() {
      return this.awh;
   }

   public void s(Consumer<String> consumer) {
      this.awo = consumer;
   }

   public void t(Consumer<Boolean> consumer) {
      this.Rz = consumer;
   }

   public void u(boolean flag) {
      this.azZ = flag;
   }
}
