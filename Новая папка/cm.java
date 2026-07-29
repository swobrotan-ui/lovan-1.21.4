import gui.Component;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.BuiltWhiteRectangle;
import render.RectangleCache;
import render.TextCache;

public class cm extends Component {
   private static final float adS = 20.0F;
   private static float rB = 50.0F;
   private static float Cb = 20.0F;
   private static float cw = 20.0F;
   private static float arP = 5.0F;
   private static float aiI = 4.0F;
   private static float Gn = 3.0F;
   private static float axn = 2.0F;
   private static float amM = 4.0F;
   private static float aap = 0.15F;
   private static String wt = "d";
   private BuiltRectangle V;
   private BuiltRectangle yp;
   private BuiltWhiteRectangle fH;
   private final float HS;
   private final float aoU;
   private final float Zp;
   private float kk;
   private float lz;
   private float iZ;
   private float xj = -1.0F;
   private float nu = -1.0F;
   private final float Ke;
   private final float arq;
   private final float B;
   private final int ir;
   private float asW;
   private boolean MG = false;
   private String aaX;
   private Runnable oy;
   private static final List<cm> Se = new ArrayList<cm>();

   public cm(float f, float f1, float f2, float f3, float f4, float f5, int i) {
      super(f, f1, 0.0F, 20.0F + Gn + axn);
      this.Ke = f2;
      this.arq = f3;
      this.B = f5;
      this.ir = Math.max(0, i);
      this.asW = this.e(Math.max(f2, Math.min(f3, f4)));
      this.HS = this.aiL.c(wt, rB);
      this.aoU = Math.max(1.0F, this.HS - 2.0F * cw);
      float f6 = Math.max(this.aeN.c(this.f(f2), 13.0F), this.aeN.c(this.f(f3), 13.0F));
      this.Zp = Cb + this.aoU + arP + f6 + aiI;
      this.aaX = this.f(this.asW);
      this.a();
      this.lz = this.kk;
      this.iZ = (this.asW - f2) / (f3 - f2);
      this.b();
      Se.add(this);
   }

   private void a() {
      if (this.MG) {
         this.kk = this.Zp;
      } else {
         float f = this.aeN.c(this.aaX, 13.0F);
         this.kk = Cb + this.aoU + arP + f + aiI;
      }

      this.qd = this.kk;
   }

   private void b() {
      float f = this.lz;
      float f1 = this.iZ;
      if (!(Math.abs(f - this.xj) < 0.1F) || !(Math.abs(f1 - this.nu) < 0.001F)) {
         this.xj = f;
         this.nu = f1;
         this.V = RectangleCache.b(f, 20.0F, 4.0F);
         float f2 = Math.max(1.0F, f - amM * 2.0F);
         this.yp = new br().a(f2, axn).b(1.0F).a();
         float f3 = Math.max(1.0F, f2 * f1);
         this.fH = new ewt().a(f3, axn).b(1.0F).a();
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f15, float f2) {
      float f3 = (this.asW - this.Ke) / (this.arq - this.Ke);
      float f4 = this.kk - this.lz;
      if (Math.abs(f4) > 0.5F) {
         this.lz = this.lz + f4 * aap;
      } else if (Math.abs(f4) > 0.01F) {
         this.lz = this.kk;
      }

      float f5 = f3 - this.iZ;
      if (Math.abs(f5) > 0.001F) {
         this.iZ = this.iZ + f5 * aap;
      } else if (Math.abs(f5) > 1.0E-4F) {
         this.iZ = f3;
      }

      this.b();
      float f6 = f - this.lz;
      this.V.a(matrix4f, f6, f1, f2);
      float f7 = this.aiL.e().d() * rB;
      BuiltText builttext = TextCache.a(this.aiL, wt, rB, Bz);
      float f8 = f6 + Cb - cw;
      float f9 = f1 + (20.0F - f7) / 2.0F;
      builttext.a(matrix4f, f8 + 2.0F, f9 + 0.7F, f2 - 0.2F);
      BuiltText builttext1 = TextCache.a(this.aeN, this.aaX, 13.0F, Bz);
      float f10 = this.aeN.e().d() * 13.0F;
      float f11 = f6 + Cb + this.aoU + arP;
      float f12 = f1 + (20.0F - f10) / 2.0F;
      builttext1.a(matrix4f, f11, f12 - 1.0F, f2);
      float f13 = f6 + amM;
      float f14 = f1 + 20.0F + Gn;
      this.yp.a(matrix4f, f13, f14, f2);
      if (this.iZ > 0.0F) {
         this.fH.a(matrix4f, f13, f14, f2);
      }
   }

   @Override
   public boolean isHovered(double d0, double d1) {
      float f = this.it - this.lz;
      return d0 >= f && d0 <= this.it && d1 >= this.atW && d1 <= this.atW + this.aem;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         float f = this.it - this.lz;
         float f1 = this.atW;
         float f2 = this.atW + 20.0F;
         if (d0 >= f && d0 <= this.it && d1 >= f1 && d1 <= f2) {
            this.MG = true;
            this.kk = this.Zp;
            this.c(d0);
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0 && this.MG) {
         this.MG = false;
         this.a();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      if (this.MG) {
         this.c(d0);
         return true;
      } else {
         return false;
      }
   }

   private void c(double d0) {
      float f = this.it - this.lz;
      float f1 = f + amM;
      float f2 = this.lz - amM * 2.0F;
      float f3 = (float)(d0 - f1);
      float f4 = Math.max(0.0F, Math.min(1.0F, f3 / f2));
      float f5 = this.Ke + (this.arq - this.Ke) * f4;
      this.d(this.e(f5));
   }

   private void d(float f) {
      if (Math.abs(f - this.asW) > 1.0E-4F) {
         this.asW = f;
         String s = this.f(this.asW);
         if (!s.equals(this.aaX)) {
            this.aaX = s;
            this.a();
         }

         if (this.oy != null) {
            this.oy.run();
         }
      }
   }

   private float e(float f) {
      if (this.B <= 0.0F) {
         return f;
      } else {
         float f1 = Math.round((f - this.Ke) / this.B) * this.B + this.Ke;
         return Math.max(this.Ke, Math.min(this.arq, f1));
      }
   }

   private String f(float f) {
      if (this.ir != 0 && !(this.B >= 1.0F)) {
         int i = this.ir;
         return String.format("%." + i + "f", f);
      } else {
         return String.valueOf(Math.round(f));
      }
   }

   public float g() {
      return this.asW;
   }

   public void h(Runnable runnable) {
      this.oy = runnable;
   }
}
