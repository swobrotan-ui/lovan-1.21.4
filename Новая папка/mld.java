import gui.Component;
import java.util.List;
import org.joml.Matrix4f;
import render.BuiltText;
import setting.Setting;

public class mld extends sc {
   private static final int Va = 3;
   private static final float arn = 3.0F;
   private static final float Ea = 0.2F;
   public final vp QZ;
   private final Runnable aae;
   private final lh Kh;
   private boolean Aq = false;
   private boolean Cr = false;
   private x nL;

   public mld(float f, float f1, String s, List<Setting> list, Runnable runnable) {
      super(f, f1, 286.0F, 201.0F, s, list.size() > 3);
      this.aae = runnable;
      this.QZ = new vp();
      this.QZ.a();
      this.QZ.b();
      this.Kh = new lh(246.0F, 5.0F, this::c);
      this.e(list);
      this.g();
      this.a();
   }

   private void c() {
      if (!this.Aq && this.aae != null) {
         this.Aq = true;
         this.aae.run();
      }
   }

   public void d(Runnable runnable) {
      this.QZ.c(runnable);
   }

   private void e(List<Setting> list) {
      float f = this.Zt != null ? ek.c() + ek.e() : 0.0F;

      for (kjx kjx : kk.a(list, 51.0F, f, null)) {
         this.lK.add(new wyj(kjx.a(), kjx.b(), kjx.c(), kjx.e()));
         if (kjx.d() != null) {
            x x = kjx.d();
            x.t(obool -> {
               if (obool) {
                  if (this.nL != null && this.nL != x) {
                     this.nL.a(false);
                  }

                  this.nL = x;
               } else {
                  if (this.nL == x) {
                     this.nL = null;
                  }
               }
            });
         }
      }
   }

   private void f() {
      boolean flag = false;

      for (wyj wyj : this.lK) {
         if (wyj.MA != null) {
            boolean flag1 = wyj.MA.isVisible();
            boolean flag2 = wyj.r();
            if (flag1 != flag2) {
               if (flag1) {
                  wyj.f();
               } else {
                  wyj.g(null);
               }

               flag = true;
            }
         }
      }

      if (flag || this.Cr) {
         this.Cr = false;
      }
   }

   private void g() {
      float f = 51.0F;

      for (wyj wyj : this.lK) {
         wyj.e();
         float f1 = wyj.a();
         wyj.n(f);
         if (f1 > 0.0F) {
            f += f1;
         }
      }

      this.Nx = true;
   }

   private void h() {
      boolean flag = false;

      for (wyj wyj : this.lK) {
         if (wyj.h()) {
            wyj.e();
            flag = true;
         }
      }

      if (flag) {
         this.g();
      }
   }

   @Override
   protected void a() {
      float f = 0.0F;

      for (wyj wyj : this.lK) {
         if (wyj.d() || wyj.h()) {
            float f1 = wyj.m() + wyj.a();
            if (wyj.aoL instanceof x x && x.r()) {
               f1 += x.d();
            }

            if (f1 > f) {
               f = f1;
            }
         }
      }

      this.ayt = Math.max(0.0F, f - this.iP);
      this.Nx = false;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.QZ.f();
      this.f();
      this.h();
      this.b();
      float f4 = f3 * this.QZ.k();
      if (!(f4 < 0.01F)) {
         Matrix4f matrix4f1 = this.QZ.g() ? this.d(matrix4f, f, f1, this.QZ.j(), 201.0F) : matrix4f;
         if (this.QZ.g()) {
            this.QZ.g(matrix4f, f, f1);
         }

         this.hH.a(matrix4f1, f, f1, f4);
         BuiltText builttext = this.k(this.ayW, this.acH, 15.0F, Bz);
         builttext.a(matrix4f1, f + 10.0F, f1 + 12.0F, f4);
         this.asw.a(matrix4f1, f, f1 + 40.0F, f4);
         this.c(matrix4f1, f, f1, i, j, f2, f4);
         this.f(this.Kh, f, f1, i, j, f2, f4, matrix4f1);
         if (this.Zt != null) {
            this.f(this.Zt, f, f1, i, j, f2, f4, matrix4f1);
         }
      }
   }

   public String i(double d0, double d1) {
      if (this.Kh != null && this.Kh.isHovered(d0, d1)) {
         return "Закрыть группу настроек";
      } else {
         for (wyj wyj : this.lK) {
            if (wyj.d() && !(wyj.b() < 0.01F)) {
               float f = this.atW + wyj.agp - this.aoj;
               Component component = wyj.aoL;
               component.atW = f;
               if (component.isHovered(d0, d1)) {
                  if (wyj.MA != null && wyj.MA.getDisplayDescription() != null && !wyj.MA.getDisplayDescription().isEmpty()) {
                     return wyj.MA.getDisplayDescription();
                  }

                  if (wyj.MA != null) {
                     return wyj.MA.getDisplayName();
                  }

                  return wyj.atb;
               }
            }
         }

         if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem) {
            String s = this.acH;
            return "Группа настроек: " + s;
         } else {
            return null;
         }
      }
   }

   @Override
   protected void e(Matrix4f matrix4f, float f, float f1, wyj wyj, int i, int j, float f2, float f3) {
      if (wyj.d()) {
         float f4 = f3 * wyj.b();
         if (!(f4 < 0.01F)) {
            float f5 = f1 + wyj.agp - this.aoj;
            Component component = wyj.aoL;
            component.it = f + component.getX();
            component.atW = f5;
            String s = wyj.MA != null ? wyj.MA.getDisplayName() : wyj.atb;
            if ((component instanceof cm || component instanceof x) && s.length() > 25) {
               this.j(matrix4f, f, f5, s, f4);
            } else {
               BuiltText builttext = this.k(this.nO, s, 13.0F, hp);
               builttext.a(matrix4f, f + 10.0F, f5 + 3.0F - 1.0F, f4);
            }

            component.render(matrix4f, f + component.getX(), f5, i, j, f2, f4);
         }
      }
   }

   private void j(Matrix4f matrix4f, float f, float f1, String s, float f2) {
      this.g(matrix4f, f, f1, s, f2, 25);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if ((!this.QZ.g() || !(this.QZ.k() < 0.5F)) && !this.Aq) {
         if (!this.i(this.Kh, d0, d1, i) && !this.i(this.Zt, d0, d1, i)) {
            for (wyj wyj : this.lK) {
               if (wyj.d() && !(wyj.b() < 0.01F)) {
                  wyj.aoL.it = this.it + wyj.aoL.getX();
                  wyj.aoL.atW = this.atW + wyj.agp - this.aoj;
                  if (wyj.aoL.mouseClicked(d0, d1, i)) {
                     return true;
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      for (wyj wyj : this.lK) {
         if (wyj.aoL instanceof pl pl && pl.e()) {
            return pl.keyPressed(i, j, k);
         }
      }

      return false;
   }

   @Override
   protected boolean e(double d0, double d1, double d2, double d3) {
      if (this.nL != null && this.nL.r()) {
         for (wyj wyj : this.lK) {
            if (wyj.aoL == this.nL) {
               wyj.aoL.it = this.it + wyj.aoL.getX();
               wyj.aoL.atW = this.atW + wyj.agp - this.aoj;
               if (wyj.aoL.mouseScrolled(d0, d1, d2, d3)) {
                  return true;
               }
               break;
            }
         }
      }

      if (this.Zt == null) {
         return false;
      } else {
         float f3 = this.atW + 40.0F;
         float f4 = this.atW + this.aem;
         if (d0 >= this.it && d0 <= this.it + this.qd && d1 >= f3 && d1 <= f4) {
            float f = (float)(-d3 * 0.2F);
            float f1 = this.Zt.f() + f;
            f1 = Math.max(0.0F, Math.min(f1, 1.0F));
            this.Zt.a(f1);
            float f2 = f1 * this.ayt;
            this.oj.a(f2);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (this.Kh != null) {
         this.Kh.mouseReleased(d0, d1, i);
      }

      return super.c(d0, d1, i);
   }
}
