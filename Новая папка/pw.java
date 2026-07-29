import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.BuiltWhiteRectangle;
import render.RectangleCache;
import render.TextCache;

public class pw extends Component {
   private static float Yp = 20.0F;
   private static float EM = 40.0F;
   private static float ms = 0.5F;
   private static float aiN = 3.0F;
   private static float QS = 5.0F;
   private static float DL = 3.0F;
   private static float Tl = 2.0F;
   private static float lQ = 4.0F;
   private static float aam = 0.15F;
   private static String hi = "[";
   private static String adv = "]";
   private static String VB = "|";
   private BuiltRectangle q;
   private BuiltRectangle apy;
   private BuiltWhiteRectangle sI;
   private final float JR;
   private final float kP;
   private float LC;
   private float awX;
   private float asx;
   private float ml;
   private float NE = -1.0F;
   private float afC = -1.0F;
   private float aba = -1.0F;
   private final float FN;
   private final float fT;
   private final float CT;
   private final int gZ;
   private float pO;
   private float jr;
   private tz atf = tz.Xm;
   private String mY;
   private String aZ;
   private final float cZ;
   private Runnable GQ;

   public pw(float f, float f1, float f2, float f3, float f4, float f5, float f6, int i) {
      super(f, f1, 0.0F, Yp + DL + Tl);
      this.FN = f2;
      this.fT = f3;
      this.CT = f6;
      this.gZ = Math.max(0, i);
      this.pO = this.h(Math.max(f2, Math.min(f3, f4)));
      this.jr = this.h(Math.max(f2, Math.min(f3, f5)));
      if (this.pO > this.jr) {
         float f7 = this.pO;
         this.pO = this.jr;
         this.jr = f7;
      }

      this.JR = this.aiL.c(hi, EM);
      this.kP = this.aiL.c(adv, EM);
      this.cZ = this.aeN.c(VB, 13.0F);
      this.mY = this.i(this.pO);
      this.aZ = this.i(this.jr);
      this.b();
      this.awX = this.LC;
      this.asx = this.a(this.pO);
      this.ml = this.a(this.jr);
      this.c();
   }

   private float a(float f) {
      return this.fT == this.FN ? 0.0F : (f - this.FN) / (this.fT - this.FN);
   }

   private void b() {
      float f = this.aeN.c(this.mY, 13.0F);
      float f1 = this.aeN.c(this.aZ, 13.0F);
      this.LC = f + QS + this.cZ + QS + f1 + 45.0F;
      this.qd = this.LC;
   }

   private void c() {
      float f = this.awX;
      float f1 = this.asx;
      float f2 = this.ml;
      if (!(Math.abs(f - this.NE) < 0.1F) || !(Math.abs(f1 - this.afC) < 0.001F) || !(Math.abs(f2 - this.aba) < 0.001F)) {
         this.NE = f;
         this.afC = f1;
         this.aba = f2;
         this.q = RectangleCache.b(f, Yp, 4.0F);
         float f3 = Math.max(1.0F, f - lQ * 2.0F);
         this.apy = new br().a(f3, Tl).b(1.0F).a();
         float f4 = f3 * f1;
         float f5 = f3 * f2;
         float f6 = Math.max(1.0F, f5 - f4);
         this.sI = new ewt().a(f6, Tl).b(1.0F).a();
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f18, float f2) {
      float f3 = this.a(this.pO);
      float f4 = this.a(this.jr);
      float f5 = this.LC - this.awX;
      if (Math.abs(f5) > 0.5F) {
         this.awX = this.awX + f5 * aam;
      } else if (Math.abs(f5) > 0.01F) {
         this.awX = this.LC;
      }

      float f6 = f3 - this.asx;
      if (Math.abs(f6) > 0.001F) {
         this.asx = this.asx + f6 * aam;
      } else if (Math.abs(f6) > 1.0E-4F) {
         this.asx = f3;
      }

      float f7 = f4 - this.ml;
      if (Math.abs(f7) > 0.001F) {
         this.ml = this.ml + f7 * aam;
      } else if (Math.abs(f7) > 1.0E-4F) {
         this.ml = f4;
      }

      this.c();
      float f8 = f - this.awX;
      this.q.a(matrix4f, f8, f1, f2);
      float f9 = this.aiL.e().d() * EM;
      float f10 = this.aeN.e().d() * 13.0F;
      float f11 = f1 + (Yp - f10) / 2.0F - 1.0F;
      float f12 = f1 + (Yp - f9) / 2.0F;
      float f13 = f8 - 20.0F;
      BuiltText builttext = TextCache.a(this.aiL, hi, EM, Bz);
      builttext.a(matrix4f, f13 + 23.0F, f12 - 3.0F, f2 - 0.2F);
      f13 += this.JR + aiN;
      BuiltText builttext1 = TextCache.a(this.aeN, this.mY, 13.0F, Bz);
      builttext1.a(matrix4f, f13, f11 + ms, f2);
      f13 += this.aeN.c(this.mY, 13.0F) + QS;
      BuiltText builttext2 = TextCache.a(this.aeN, VB, 13.0F, hp);
      builttext2.a(matrix4f, f13, f11, f2);
      f13 += this.cZ + QS;
      BuiltText builttext3 = TextCache.a(this.aeN, this.aZ, 13.0F, Bz);
      builttext3.a(matrix4f, f13, f11 + ms, f2);
      f13 += this.aeN.c(this.aZ, 13.0F) + aiN;
      BuiltText builttext4 = TextCache.a(this.aiL, adv, EM, Bz);
      builttext4.a(matrix4f, f13 - 1.0F, f12 - 3.0F, f2 - 0.2F);
      float f14 = f8 + lQ;
      float f15 = f1 + Yp + DL;
      this.apy.a(matrix4f, f14, f15, f2);
      float f16 = Math.max(1.0F, this.awX - lQ * 2.0F);
      float f17 = f14 + f16 * this.asx;
      if (this.ml > this.asx) {
         this.sI.a(matrix4f, f17, f15, f2);
      }
   }

   @Override
   public boolean isHovered(double d0, double d1) {
      float f = this.it - this.awX;
      return d0 >= f && d0 <= this.it && d1 >= this.atW && d1 <= this.atW + this.aem;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         float f = this.it - this.awX;
         float f1 = this.atW;
         float f2 = this.atW + Yp;
         if (d0 >= f && d0 <= this.it && d1 >= f1 && d1 <= f2) {
            float f3 = this.d(d0);
            float f4 = this.a(this.pO);
            float f5 = this.a(this.jr);
            float f6 = Math.abs(f3 - f4);
            float f7 = Math.abs(f3 - f5);
            this.atf = f6 <= f7 ? tz.tJ : tz.cf;
            this.e(d0);
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0 && this.atf != tz.Xm) {
         this.atf = tz.Xm;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      if (this.atf != tz.Xm) {
         this.e(d0);
         return true;
      } else {
         return false;
      }
   }

   private float d(double d0) {
      float f = this.it - this.awX;
      float f1 = f + lQ;
      float f2 = this.awX - lQ * 2.0F;
      float f3 = (float)(d0 - f1);
      return Math.max(0.0F, Math.min(1.0F, f3 / f2));
   }

   private void e(double d0) {
      float f = this.d(d0);
      float f1 = this.FN + (this.fT - this.FN) * f;
      float f2 = this.h(f1);
      if (this.atf == tz.tJ) {
         this.f(Math.min(f2, this.jr));
      } else {
         if (this.atf == tz.cf) {
            this.g(Math.max(f2, this.pO));
         }
      }
   }

   private void f(float f) {
      if (Math.abs(f - this.pO) > 1.0E-4F) {
         this.pO = f;
         String s = this.i(this.pO);
         if (!s.equals(this.mY)) {
            this.mY = s;
            this.b();
         }

         if (this.GQ != null) {
            this.GQ.run();
         }
      }
   }

   private void g(float f) {
      if (Math.abs(f - this.jr) > 1.0E-4F) {
         this.jr = f;
         String s = this.i(this.jr);
         if (!s.equals(this.aZ)) {
            this.aZ = s;
            this.b();
         }

         if (this.GQ != null) {
            this.GQ.run();
         }
      }
   }

   private float h(float f) {
      if (this.CT <= 0.0F) {
         return f;
      } else {
         float f1 = Math.round((f - this.FN) / this.CT) * this.CT + this.FN;
         return Math.max(this.FN, Math.min(this.fT, f1));
      }
   }

   private String i(float f) {
      if (this.gZ != 0 && !(this.CT >= 1.0F)) {
         int i = this.gZ;
         return String.format("%." + i + "f", f);
      } else {
         return String.valueOf(Math.round(f));
      }
   }

   public float j() {
      return this.pO;
   }

   public float k() {
      return this.jr;
   }

   public void l(Runnable runnable) {
      this.GQ = runnable;
   }
}
