import gui.Component;
import java.awt.Color;
import org.joml.Matrix4f;
import render.BuiltColorPicker;
import render.BuiltLine2d;
import render.BuiltRectangle;
import render.BuiltText;

public class oip extends Component {
   private static final float ayl = 286.0F;
   private static final float afl = 201.0F;
   private static final float qW = 12.0F;
   private static final float xe = 40.0F;
   private static final float Et = 0.01F;
   private static final float ajh = 10.0F;
   private static final float Ee = 266.0F;
   private static final float asi = 122.0F;
   private static final float akS = 130.0F;
   private static final float arJ = 8.0F;
   private static final float Gu = 10.0F;
   private static final float zw = 11.0F;
   private static final float alZ = 11.0F;
   public final vp aak;
   private final Runnable alN;
   private final lh ayi;
   private final BuiltRectangle aux;
   private final BuiltLine2d adh;
   private final BuiltRectangle auL;
   private final BuiltRectangle FL;
   private final String ail;
   private final boolean Z;
   private Runnable amT;
   private float My = 0.0F;
   private float Bo = 1.0F;
   private float xm = 1.0F;
   private float Al = 1.0F;
   private boolean KT = false;
   private boolean XQ = false;
   private boolean vQ = false;
   private boolean ajb = false;
   private final BuiltRectangle asj;
   private BuiltColorPicker agt;
   private BuiltColorPicker Wk;
   private BuiltColorPicker CD;
   private float alq = -1.0F;
   private float pq = -1.0F;
   private float sQ = -1.0F;
   private final Matrix4f aAo = new Matrix4f();

   public oip(float f, float f1, String s, boolean flag, Runnable runnable) {
      super(f, f1, 286.0F, 201.0F);
      this.ail = s;
      this.Z = flag;
      this.alN = runnable;
      this.aak = new vp();
      this.aak.a();
      this.aak.b();
      this.ayi = new lh(246.0F, 5.0F, this::a);
      this.aux = new br().a(286.0F, 201.0F).b(8.0F).a();
      this.adh = new otj().a(286.0F).b(1.0F).d().a();
      this.auL = new br().a(11.0F, 11.0F).b(5.5F).a();
      this.FL = new br().a(9.0F, 9.0F).b(4.5F).a();
      this.asj = new br().a(11.0F, 11.0F).b(4.5F).a();
   }

   private void a() {
      if (!this.ajb && this.alN != null) {
         this.ajb = true;
         this.alN.run();
      }
   }

   public void b(Runnable runnable) {
      this.aak.c(runnable);
   }

   public void c(Color color) {
      float[] afloat = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      this.My = afloat[0];
      this.Bo = afloat[1];
      this.xm = afloat[2];
      this.Al = color.getAlpha() / 255.0F;
   }

   public Color d() {
      Color color = Color.getHSBColor(this.My, this.Bo, this.xm);
      return this.Z ? new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(this.Al * 255.0F)) : color;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.aak.f();
      float f4 = f3 * this.aak.k();
      if (!(f4 < 0.01F)) {
         Matrix4f matrix4f1 = this.aak.g() ? this.i(matrix4f, f, f1, this.aak.j(), this.aem) : matrix4f;
         if (this.aak.g()) {
            this.aak.g(matrix4f, f, f1);
         }

         this.aux.a(matrix4f1, f, f1, f4);
         BuiltText builttext = this.k(this.ayW, this.ail, 15.0F, Bz);
         builttext.a(matrix4f1, f + 10.0F, f1 + 12.0F, f4);
         this.adh.a(matrix4f1, f, f1 + 40.0F, f4);
         this.e(matrix4f1, f, f1, f4);
         this.j(this.ayi, f, f1, i, j, f2, f4, matrix4f1);
      }
   }

   private void e(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + 10.0F;
      float f4 = f1 + 40.0F + 10.0F;
      if (this.agt == null || Math.abs(this.alq - this.My) > 0.001F) {
         this.agt = new fi().a(266.0F, 122.0F).b(vt.azP).c(this.My).f(11.0F).a();
         this.alq = this.My;
      }

      this.agt.a(matrix4f, f3, f4, f2);
      this.f(matrix4f, f3, f4, f2);
      float f5 = f4 + 122.0F + 10.0F;
      float f6 = f + 10.0F;
      if (this.Wk == null) {
         this.Wk = new fi().a(130.0F, 8.0F).b(vt.TR).f(4.0F).a();
      }

      this.Wk.a(matrix4f, f6, f5, f2);
      this.g(matrix4f, f6, f5, f2);
      if (this.Z) {
         float f7 = f + 286.0F - 10.0F - 130.0F;
         if (this.CD == null || Math.abs(this.alq - this.My) > 0.001F || Math.abs(this.pq - this.Bo) > 0.001F || Math.abs(this.sQ - this.xm) > 0.001F) {
            this.CD = new fi().a(130.0F, 8.0F).b(vt.arm).c(this.My).d(this.Bo).e(this.xm).f(4.0F).a();
            this.pq = this.Bo;
            this.sQ = this.xm;
         }

         this.CD.a(matrix4f, f7, f5, f2);
         this.h(matrix4f, f7, f5, f2);
      }
   }

   private void f(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + this.Bo * 266.0F - 5.5F;
      float f4 = f1 + (1.0F - this.xm) * 122.0F - 5.5F;
      this.auL.a(matrix4f, f3, f4, f2);
      this.FL.a(matrix4f, f3 + 1.0F, f4 + 1.0F, f2 * 0.8F);
   }

   private void g(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + this.My * 130.0F - 1.0F;
      this.asj.a(matrix4f, f3, f1 - 1.5F, f2);
   }

   private void h(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + (1.0F - this.Al) * 130.0F - 1.0F;
      this.asj.a(matrix4f, f3, f1 - 1.5F, f2);
   }

   private Matrix4f i(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f4 = f + 143.0F;
         float f5 = f1 + f3 * 0.5F;
         this.aAo.set(matrix4f);
         this.aAo.translate(f4, f5, 0.0F);
         this.aAo.scale(f2, f2, 1.0F);
         this.aAo.translate(-f4, -f5, 0.0F);
         return this.aAo;
      }
   }

   private void j(lh lh, float f, float f1, int i, int j, float f2, float f3, Matrix4f matrix4f) {
      if (lh != null) {
         float f4 = f + lh.getX();
         float f5 = f1 + lh.getY();
         lh.it = f4;
         lh.atW = f5;
         lh.render(matrix4f, f4, f5, i, j, f2, f3);
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if ((!this.aak.g() || !(this.aak.k() < 0.5F)) && !this.ajb) {
         if (this.k(d0, d1, i)) {
            return true;
         } else {
            float f = this.it + 10.0F;
            float f1 = this.atW + 40.0F + 10.0F;
            if (d0 >= f && d0 <= f + 266.0F && d1 >= f1 && d1 <= f1 + 122.0F) {
               this.KT = true;
               this.l(d0, d1, f, f1);
               return true;
            } else {
               float f2 = f1 + 122.0F + 10.0F;
               float f3 = this.it + 10.0F;
               if (d0 >= f3 && d0 <= f3 + 130.0F && d1 >= f2 && d1 <= f2 + 8.0F) {
                  this.XQ = true;
                  this.m(d0, f3);
                  return true;
               } else {
                  if (this.Z) {
                     float f4 = this.it + 286.0F - 10.0F - 130.0F;
                     if (d0 >= f4 && d0 <= f4 + 130.0F && d1 >= f2 && d1 <= f2 + 8.0F) {
                        this.vQ = true;
                        this.n(d0, f4);
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   private boolean k(double d0, double d1, int i) {
      float f = this.it + this.ayi.getX();
      float f1 = this.atW + this.ayi.getY();
      this.ayi.it = f;
      this.ayi.atW = f1;
      return d0 >= f && d0 <= f + this.ayi.getWidth() && d1 >= f1 && d1 <= f1 + this.ayi.getHeight() && this.ayi.mouseClicked(d0, d1, i);
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.KT = false;
         this.XQ = false;
         this.vQ = false;
         if (this.ayi != null) {
            this.ayi.mouseReleased(d0, d1, i);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      float f = this.it + 10.0F;
      float f1 = this.atW + 40.0F + 10.0F;
      if (this.KT) {
         this.l(d0, d1, f, f1);
         return true;
      } else {
         float f2 = this.it + 10.0F;
         if (this.XQ) {
            this.m(d0, f2);
            return true;
         } else if (this.vQ) {
            float f3 = this.it + 286.0F - 10.0F - 130.0F;
            this.n(d0, f3);
            return true;
         } else {
            return false;
         }
      }
   }

   private void l(double d0, double d1, float f, float f1) {
      float f2 = (float)(d0 - f);
      float f3 = (float)(d1 - f1);
      this.Bo = Math.max(0.0F, Math.min(1.0F, f2 / 266.0F));
      this.xm = Math.max(0.0F, Math.min(1.0F, 1.0F - f3 / 122.0F));
      if (this.amT != null) {
         this.amT.run();
      }
   }

   private void m(double d0, float f) {
      float f1 = (float)(d0 - f);
      this.My = Math.max(0.0F, Math.min(1.0F, f1 / 130.0F));
      if (this.amT != null) {
         this.amT.run();
      }
   }

   private void n(double d0, float f) {
      float f1 = (float)(d0 - f);
      this.Al = Math.max(0.0F, Math.min(1.0F, 1.0F - f1 / 130.0F));
      if (this.amT != null) {
         this.amT.run();
      }
   }

   public String o() {
      return this.ail;
   }

   public void p(Runnable runnable) {
      this.amT = runnable;
   }
}
