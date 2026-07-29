import gui.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import render.BuiltLine2d;
import render.BuiltRectangle;
import render.BuiltText;

public abstract class sc extends Component {
   private static final List<String> cC = new ArrayList<String>(4);
   protected final Map<String, String[]> aqJ = new HashMap<String, String[]>();
   protected static final int kQ = 25;
   protected static final float aAj = -2.0F;
   protected static final float Br = 12.0F;
   protected static final float wm = 286.0F;
   protected static final float asO = 201.0F;
   protected static final float asZ = 143.0F;
   protected static final float gc = 100.5F;
   protected static final float avM = 12.0F;
   protected static final float Nd = 40.0F;
   protected static final float axK = 2.0F;
   protected static final float Vu = 51.0F;
   protected static final float Nb = 0.01F;
   protected static final float ahb = 2.0F;
   public final String acH;
   protected final BuiltRectangle hH;
   protected final BuiltLine2d asw;
   protected final List<wyj> lK;
   protected final ek Zt;
   protected final float iP;
   protected final cd oj;
   public float aoj = 0.0F;
   protected float ayt = 0.0F;
   protected boolean Nx = true;
   public float uQ = 1.0F;
   public float uv = 0.0F;
   public float Cc = 0.0F;
   private final Matrix4f UM = new Matrix4f();

   protected sc(float f, float f1, float f2, float f3, String s, boolean flag) {
      super(f, f1, f2, f3);
      this.acH = s;
      this.iP = f3 - 40.0F;
      this.hH = new br().a(f2, f3).b(8.0F).a();
      this.asw = new otj().a(f2).b(1.0F).d().a();
      this.Zt = flag ? new ek(f2 - ek.e() - ek.c(), 40.0F + ek.d()) : null;
      this.lK = new ArrayList<wyj>();
      this.oj = new cd();
      this.oj.f(0.0F);
   }

   protected void a() {
      float f = 0.0F;

      for (wyj wyj : this.lK) {
         float f1 = wyj.m() + wyj.aoL.getHeight();
         if (wyj.aoL instanceof x x && x.r()) {
            f1 += x.d();
         }

         if (f1 > f) {
            f = f1;
         }
      }

      this.ayt = Math.max(0.0F, f - this.iP);
      this.Nx = false;
   }

   protected void b() {
      if (this.Zt != null) {
         if (this.Nx) {
            this.a();
         }

         float f = this.Zt.f() * this.ayt;
         this.oj.a(f);
         this.oj.c();
         this.aoj = Math.max(0.0F, Math.min(this.oj.d(), this.ayt));
      }
   }

   protected void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      float f4 = f1 + 40.0F;
      float f5 = f1 + this.aem - 2.0F;
      efj.a(0.0F, 42.0F, this.qd, this.aem - 40.0F - 2.0F - 2.0F, this.uQ, this.uv + f * this.uQ, this.Cc + f1 * this.uQ);

      for (wyj wyj : this.lK) {
         this.e(matrix4f, f, f1, wyj, i, j, f2, f3);
         if (wyj.aoL instanceof x xx) {
            float f11 = f1 + wyj.m() - this.aoj;
            xx.atW = f11;
            xx.u(true);
            if (xx.r() && this.Zt != null) {
               float f6 = f11 + xx.getHeight() + 5.0F;
               float f7 = f6 + xx.e();
               if (f7 > f5) {
                  float f8 = f7 - f5;
                  float f9 = this.aoj + f8;
                  f9 = Math.min(f9, this.ayt);
                  float f10 = this.ayt > 0.0F ? f9 / this.ayt : 0.0F;
                  this.Zt.a(f10);
                  this.oj.a(f9);
               }

               if (f6 < f4) {
                  float f15 = f4 - f6;
                  float f16 = this.aoj - f15;
                  f16 = Math.max(0.0F, f16);
                  float f17 = this.ayt > 0.0F ? f16 / this.ayt : 0.0F;
                  this.Zt.a(f17);
                  this.oj.a(f16);
               }
            }
         }
      }

      efj.b();

      for (wyj wyjx : this.lK) {
         if (wyjx.aoL instanceof x xx && (xx.r() || xx.b())) {
            xx.u(false);
            float f12 = f1 + wyjx.m() - this.aoj;
            xx.it = f + xx.getX();
            xx.atW = f12;
            xx.cM = this.uQ;
            xx.Lp = this.uv;
            xx.Kp = this.Cc;
            float f13 = f12 + xx.getHeight() + 5.0F;
            float f14 = f13 + xx.e();
            if (f13 < f5 && f14 > f4) {
               xx.l(matrix4f, f + xx.getX(), f12, f3);
            }
         }
      }
   }

   protected Matrix4f d(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f4 = f + this.qd * 0.5F;
         float f5 = f1 + f3 * 0.5F;
         this.UM.set(matrix4f);
         this.UM.translate(f4, f5, 0.0F);
         this.UM.scale(f2, f2, 1.0F);
         this.UM.translate(-f4, -f5, 0.0F);
         return this.UM;
      }
   }

   protected void e(Matrix4f matrix4f, float f, float f1, wyj wyj, int i, int j, float f2, float f3) {
      float f4 = f1 + wyj.m() - this.aoj;
      Component component = wyj.aoL;
      component.it = f + component.getX();
      component.atW = f4;
      BuiltText builttext = this.k(this.nO, wyj.atb, 13.0F, hp);
      builttext.a(matrix4f, f + 10.0F, f4, f3);
      component.render(matrix4f, f + component.getX(), f4, i, j, f2, f3);
   }

   protected void f(Component component, float f, float f1, int i, int j, float f2, float f3, Matrix4f matrix4f) {
      if (component != null) {
         float f4 = f + component.getX();
         float f5 = f1 + component.getY();
         component.it = f4;
         component.atW = f5;
         component.render(matrix4f, f4, f5, i, j, f2, f3);
      }
   }

   protected void g(Matrix4f matrix4f, float f, float f1, String s, float f2, int i) {
      String[] astring = this.aqJ.get(s);
      if (astring == null) {
         astring = h(s, i);
         this.aqJ.put(s, astring);
      }

      float f3 = f1 + -2.0F;

      for (int j = 0; j < astring.length; j++) {
         BuiltText builttext = this.k(this.nO, astring[j], 13.0F, hp);
         builttext.a(matrix4f, f + 10.0F, f3, f2);
         f3 += 12.0F;
      }
   }

   private static String[] h(String s, int i) {
      String[] astring = s.split(" ");
      cC.clear();
      StringBuilder stringbuilder = new StringBuilder();

      for (String s1 : astring) {
         if (stringbuilder.length() + s1.length() + 1 > i) {
            if (!stringbuilder.isEmpty()) {
               cC.add(stringbuilder.toString());
               stringbuilder.setLength(0);
               stringbuilder.append(s1);
            } else {
               cC.add(s1);
            }
         } else {
            if (!stringbuilder.isEmpty()) {
               stringbuilder.append(" ");
            }

            stringbuilder.append(s1);
         }
      }

      if (!stringbuilder.isEmpty()) {
         cC.add(stringbuilder.toString());
      }

      return cC.<String>toArray(new String[0]);
   }

   protected boolean i(Component component, double d0, double d1, int i) {
      if (component == null) {
         return false;
      } else {
         float f = this.it + component.getX();
         float f1 = this.atW + component.getY();
         component.it = f;
         component.atW = f1;
         return d0 >= f && d0 <= f + component.getWidth() && d1 >= f1 && d1 <= f1 + component.getHeight() && component.mouseClicked(d0, d1, i);
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (this.Zt != null) {
         this.Zt.mouseReleased(d0, d1, i);
      }

      for (wyj wyj : this.lK) {
         if (wyj.aoL.mouseReleased(d0, d1, i)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean d(double d0, double d1, int i, double d2, double d3) {
      if (this.Zt != null && this.Zt.mouseDragged(d0, d1, i, d2, d3)) {
         return true;
      } else {
         for (wyj wyj : this.lK) {
            if (wyj.aoL.mouseDragged(d0, d1, i, d2, d3)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected boolean e(double d0, double d1, double d3, double d2) {
      if (this.Zt == null) {
         return false;
      } else {
         float f = this.atW + 40.0F;
         float f1 = this.atW + this.aem;
         if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= f && d1 <= f1) {
            float f2 = (float)(-d2 * 0.05F);
            float f3 = this.Zt.f() + f2;
            f3 = Math.max(0.0F, Math.min(f3, 1.0F));
            this.Zt.a(f3);
            return true;
         } else {
            return false;
         }
      }
   }
}
