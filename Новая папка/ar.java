import core.Localization;
import font.MSDFFont;
import gui.Component;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import module.Module;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import setting.BlockListSetting;
import setting.ColorSetting;
import setting.GroupSetting;
import setting.KeyBindSetting;
import setting.Setting;

public class ar extends sc {
   private static final float aog = 45.0F;
   private static final int XS = 5;
   private static final float aeC = 12.0F;
   private static final float TT = 3.0F;
   private static final float acN = -2.0F;
   private static final float Gk = 95.0F;
   private static final float Yw = 0.2F;
   private static final float DM = 5.0F;
   private static final float agm = 20.0F;
   private static final float iG = 4.0F;
   private static final float qG = 10.0F;
   public final Module aqa;
   private final mm aup;
   private final vp HT;
   private final vp aeH;
   private final uv jP;
   private final zv avS;
   private boolean kO;
   private final Map<String, wyj> IW = new HashMap<String, wyj>();
   private boolean anz = false;
   private zj vz;
   private x jl;
   private mld arD;
   private boolean Us = false;
   private oip Yl;
   private boolean aoB = false;
   private hc apv;
   private boolean At = false;
   private gs ayx;
   private pkp auF;
   private boolean agE = false;

   public ar(float f, float f1, Module module) {
      super(f, f1, 286.0F, 201.0F, module.getName(), !module.getSettings().isEmpty() && module.getSettings().size() > 5);
      this.aqa = module;
      this.kO = module.getSettings().stream().anyMatch(setting -> {
         return !(setting instanceof KeyBindSetting);
      });
      this.aup = new mm();
      this.aup.a(module.isEnabled());
      this.HT = new vp();
      this.HT.a();
      this.aeH = new vp();
      this.aeH.a();
      this.jP = new uv(211.0F, 5.0F, module.isEnabled());
      this.jP.b(module::toggle);
      this.avS = new zv(246.0F, 5.0F, this::z);
      if (this.kO) {
         this.c();
         this.f();
         this.a();
      }

      this.ayx = new gs(0.0F, 0.0F, 286.0F, this.aem, null);
   }

   public void a(float f) {
      if (!(f <= 0.0F) && this.kO) {
         float f1 = Math.min(f, this.ayt);
         this.aoj = f1;
         this.oj.f(f1);
         this.oj.a(f1);
         if (this.Zt != null && this.ayt > 0.0F) {
            this.Zt.b(f1 / this.ayt);
         }
      }
   }

   private void c() {
      float f = this.Zt != null ? ek.c() + ek.e() : 0.0F;

      for (kjx kjx : kk.a(this.aqa.getSettings(), 51.0F, f, this::i)) {
         wyj wyj = new wyj(kjx.a(), kjx.b(), kjx.c(), kjx.e());
         this.lK.add(wyj);
         this.IW.put(kjx.e().getName(), wyj);
         if (kjx.e() != null) {
            kjx.e().setOnVisibilityChange(this::d);
         }

         if (kjx.d() != null) {
            x x = kjx.d();
            x.t(obool -> {
               if (obool) {
                  this.s(x);
               } else {
                  if (this.jl == x) {
                     this.jl = null;
                  }
               }
            });
         }

         if (kjx.b() instanceof zl zl) {
            zl.a(() -> {
               this.k(zl, (ColorSetting)kjx.e());
            });
         }

         if (kjx.b() instanceof nw nw) {
            nw.a(() -> {
               this.n((BlockListSetting)kjx.e());
            });
         }
      }
   }

   private void d() {
      this.anz = true;
   }

   private void e() {
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

      if (flag || this.anz) {
         this.anz = false;
      }
   }

   private void f() {
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

   private void g() {
      boolean flag = false;

      for (wyj wyj : this.lK) {
         if (wyj.h()) {
            wyj.e();
            flag = true;
         }
      }

      if (flag) {
         this.f();
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

   public void h(ColorSetting colorsetting) {
      this.HT.d();
      zl zlx = null;

      for (wyj wyj : this.lK) {
         if (wyj.MA == colorsetting && wyj.aoL instanceof zl) {
            zlx = (zl)wyj.aoL;
            break;
         }
      }

      this.Yl = new oip(this.aP, this.hn, colorsetting.getDisplayName(), colorsetting.hasAlpha(), this::m);
      this.Yl.c(colorsetting.getColor());
      zl zlx = zlx;
      this.Yl.p(() -> {
         Color color = this.Yl.d();
         colorsetting.setColor(color);
         if (zl != null) {
            zl.a(color);
         }
      });
      if (this.Yl.aak != null) {
         this.Yl.aak.d();
      }

      this.aoB = true;
   }

   private void i() {
      for (wyj wyj : this.lK) {
         Component component = wyj.aoL;
         if (component instanceof gl && component.cH && wyj.MA instanceof GroupSetting groupsetting && !this.Us && !this.HT.g() && !this.aeH.g()) {
            this.j(groupsetting);
            return;
         }
      }
   }

   private void j(GroupSetting groupsetting) {
      this.HT.c(() -> {
         this.arD = new mld(this.aP, this.hn, groupsetting.getDisplayName(), groupsetting.getSettings(), this::r);
         this.Us = true;
      });
   }

   private void k(zl zl, ColorSetting colorsetting) {
      if (!this.aoB && !this.HT.g() && !this.aeH.g()) {
         this.l(zl, colorsetting);
      }
   }

   private void l(zl zl, ColorSetting colorsetting) {
      this.HT.c(() -> {
         this.Yl = new oip(this.aP, this.hn, colorsetting.getDisplayName(), colorsetting.hasAlpha(), this::m);
         this.Yl.c(colorsetting.getColor());
         this.Yl.p(() -> {
            Color color = this.Yl.d();
            colorsetting.setColor(color);
            zl.a(color);
         });
         this.aoB = true;
      });
   }

   private void m() {
      if (this.Yl != null) {
         this.Yl.b(() -> {
            this.aoB = false;
            this.Yl = null;
            this.aeH.b();
         });
      }
   }

   private void n(BlockListSetting blocklistsetting) {
      if (!this.At && !this.HT.g() && !this.aeH.g()) {
         this.o(blocklistsetting);
      }
   }

   private void o(BlockListSetting blocklistsetting) {
      this.HT.c(() -> {
         this.apv = new hc(this.aP, this.hn, blocklistsetting.getDisplayName(), blocklistsetting, this::p);
         this.At = true;
      });
   }

   private void p() {
      if (this.apv != null) {
         this.apv.h(() -> {
            this.At = false;
            this.apv = null;
            this.aeH.b();
         });
      }
   }

   public void q(GroupSetting groupsetting, float f) {
      this.HT.d();
      this.arD = new mld(this.aP, this.hn, groupsetting.getDisplayName(), groupsetting.getSettings(), this::r);
      this.arD.aoj = f;
      if (this.arD.QZ != null) {
         this.arD.QZ.d();
      }

      this.Us = true;
   }

   private void r() {
      if (this.arD != null) {
         this.arD.d(() -> {
            this.Us = false;
            this.arD = null;
            this.aeH.b();
         });
      }
   }

   public void s(x x) {
      if (this.jl != null && this.jl != x) {
         this.jl.a(false);
      }

      this.jl = x;
      if (x != null && x.r()) {
         for (wyj wyj : this.lK) {
            if (wyj.aoL == x) {
               this.u(x, wyj.m());
               break;
            }
         }
      }

      this.Nx = true;
      this.a();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.aup.b(this.aqa.isEnabled());
      this.aup.f();
      this.HT.f();
      this.aeH.f();
      this.e();
      this.g();
      this.b();
      this.jP.setEnabled(this.aqa.isEnabled());
      this.avS.b(this.agE);
      if (!this.Us) {
         if (!this.aoB) {
            if (!this.At) {
               float f4 = f3 * this.aup.c();
               vp vp = this.HT.g() ? this.HT : (this.aeH.g() ? this.aeH : null);
               Matrix4f matrix4f1 = matrix4f;
               float f5 = f4;
               if (vp != null) {
                  f5 = f4 * vp.k();
                  if (f5 < 0.01F) {
                     return;
                  }

                  matrix4f1 = this.d(matrix4f, f, f1, vp.j(), 201.0F);
                  vp.g(matrix4f, f, f1);
               }

               this.hH.a(matrix4f1, f, f1, f5);
               String s = this.aqa.getDisplayName();
               BuiltText builttext = this.k(this.aeN, s, 16.0F, Bz);
               builttext.a(matrix4f1, f + 10.0F, f1 + 12.0F, f5);
               this.t(matrix4f1, f, f1, f5);
               float f6 = f3 * (this.aqa.isEnabled() ? 0.2F : 0.1F);
               if (vp != null) {
                  f6 *= vp.k();
               }

               if (this.kO) {
                  this.asw.a(matrix4f1, f, f1 + 40.0F, f6);
                  this.c(matrix4f1, f, f1, i, j, f2, f5);
                  if (this.Zt != null) {
                     this.f(this.Zt, f, f1, i, j, f2, f5, matrix4f1);
                  }

                  if (this.jl != null && this.jl.r()) {
                     this.C(matrix4f1, f, f1, f5);
                  }
               } else {
                  this.asw.a(matrix4f1, f, f1 + 40.0F, f6);
                  String s1 = Localization.a().c("Здесь пока нет");
                  String s2 = Localization.a().c("настроек");
                  BuiltText builttext1 = this.k(this.nO, s1, 13.0F, hp);
                  BuiltText builttext2 = this.k(this.nO, s2, 13.0F, hp);
                  float f7 = builttext1.c().c(s1, 13.0F);
                  float f8 = builttext1.c().c(s2, 13.0F);
                  builttext1.a(matrix4f1, f + (286.0F - f7) * 0.5F, f1 + 95.0F, f5);
                  builttext2.a(matrix4f1, f + (286.0F - f8) * 0.5F, f1 + 95.0F + 12.0F, f5);
               }

               this.f(this.jP, f, f1, i, j, f2, f5, matrix4f1);
               this.f(this.avS, f, f1, i, j, f2, f5, matrix4f1);
               if (this.ayx != null && this.ayx.d()) {
                  this.ayx.render(matrix4f1, f, f1, i, j, f2, f3);
               }

               if (this.auF != null && this.auF.c()) {
                  this.auF.render(matrix4f, f, f1, i, j, f2, f3);
               }
            }
         }
      }
   }

   private void t(Matrix4f matrix4f, float f, float f1, float f2) {
      KeyBindSetting keybindsetting = this.w();
      if (keybindsetting != null) {
         String s = keybindsetting.getKeyName();
         if (s != null && !s.isEmpty()) {
            String s1 = this.aqa.getDisplayName();
            BuiltText builttext = this.k(this.aeN, s1, 16.0F, Bz);
            float f3 = builttext.c().c(s1, 16.0F);
            boolean flag = s.equals("None");
            String s2 = flag ? "j" : s;
            MSDFFont msdffont = flag ? this.aiL : this.aeN;
            float f4 = flag ? 18.0F : 13.0F;
            BuiltText builttext1 = this.k(msdffont, s2, f4, Bz);
            float f5 = builttext1.c().c(s2, f4);
            float f6 = Math.max(20.0F, f5 + 8.0F);
            float f7 = 20.0F;
            float f8 = f + 10.0F + f3 + 5.0F - 3.0F;
            float f9 = f1 + 12.0F - (f7 - 15.0F) * 0.5F + 3.0F;
            BuiltRectangle builtrectangle = RectangleCache.b(f6, f7, 4.0F);
            builtrectangle.a(matrix4f, f8, f9, f2);
            float f10;
            float f11;
            if (flag) {
               f10 = f8 + 3.0F;
               f11 = f9 + 2.0F;
            } else {
               f10 = f8 + (f6 - f5) * 0.5F;
               f11 = f9 + (f7 - 13.0F) * 0.5F - 1.0F;
            }

            builttext1.a(matrix4f, f10, f11, f2);
         }
      }
   }

   public void u(x x, float f) {
      if (this.kO && this.Zt != null) {
         float f1 = f - this.aoj + x.getHeight() + 5.0F;
         float f2 = f1 + x.d();
         float f3 = this.aem - 2.0F;
         if (f2 > f3) {
            float f5 = Math.min(this.aoj + (f2 - f3) + 10.0F, this.ayt);
            this.oj.a(f5);
            this.Zt.a(f5 / this.ayt);
         } else {
            if (f1 < 40.0F) {
               float f4 = Math.max(this.aoj - (40.0F - f1) - 10.0F, 0.0F);
               this.oj.a(f4);
               this.Zt.a(f4 / this.ayt);
            }
         }
      }
   }

   @Override
   protected void e(Matrix4f matrix4f, float f, float f1, wyj wyj, int i, int j, float f2, float f3) {
      if (wyj.d()) {
         float f4 = f3 * wyj.b();
         if (!(f4 < 0.01F)) {
            float f5 = f1 + wyj.m() - this.aoj;
            Component component = wyj.aoL;
            if (component instanceof x x) {
               x.u(x.r());
            }

            component.it = f + component.getX();
            component.atW = f5;
            String s = wyj.MA != null ? wyj.MA.getDisplayName() : wyj.atb;
            if (component instanceof cm && s.length() > 25) {
               this.v(matrix4f, f, f5, s, f4);
            } else {
               BuiltText builttext = this.k(this.nO, s, 13.0F, hp);
               builttext.a(matrix4f, f + 10.0F, f5 + 3.0F - 1.0F, f4);
            }

            component.render(matrix4f, f + component.getX(), f5, i, j, f2, f4);
         }
      }
   }

   private void v(Matrix4f matrix4f, float f, float f1, String s, float f2) {
      this.g(matrix4f, f, f1, s, f2, 25);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (this.auF != null && this.auF.c()) {
         return this.auF.mouseClicked(d0, d1, i);
      } else if (this.ayx != null && this.ayx.d()) {
         return this.ayx.mouseClicked(d0, d1, i);
      } else if (!this.HT.g() && !this.aeH.g() && !this.Us) {
         if (this.kO) {
            for (wyj wyjx : this.lK) {
               if (wyjx.aoL instanceof pl pl && pl.e()) {
                  wyjx.aoL.atW = this.atW + wyjx.m() - this.aoj;
                  return wyjx.aoL.mouseClicked(d0, d1, i);
               }
            }
         }

         if (i == 2 && d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem) {
            KeyBindSetting keybindsetting = this.w();
            if (keybindsetting != null) {
               this.ayx = new gs(0.0F, 0.0F, 286.0F, this.aem, keybindsetting);
               this.ayx.b();
               return true;
            }
         }

         if (!this.i(this.avS, d0, d1, i) && !this.i(this.jP, d0, d1, i) && !this.i(this.Zt, d0, d1, i)) {
            if (!this.kO) {
               return false;
            } else {
               for (wyj wyjx : this.lK) {
                  if (wyjx.d() && !(wyjx.b() < 0.01F)) {
                     wyjx.aoL.it = this.it + wyjx.aoL.getX();
                     wyjx.aoL.atW = this.atW + wyjx.m() - this.aoj;
                     if (wyjx.aoL.mouseClicked(d0, d1, i)) {
                        this.Nx = true;
                        return true;
                     }
                  }
               }

               return false;
            }
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (this.jP != null) {
         this.jP.mouseReleased(d0, d1, i);
      }

      if (this.avS != null) {
         this.avS.mouseReleased(d0, d1, i);
      }

      if (this.kO && i == 0) {
         for (wyj wyj : this.lK) {
            if (wyj.d()) {
               wyj.aoL.it = this.it + wyj.aoL.getX();
               wyj.aoL.atW = this.atW + wyj.m() - this.aoj;
               wyj.aoL.mouseReleased(d0, d1, i);
            }
         }
      }

      return super.c(d0, d1, i);
   }

   @Override
   protected boolean e(double d0, double d1, double d2, double d3) {
      if (this.jl != null && this.jl.r()) {
         for (wyj wyj : this.lK) {
            if (wyj.aoL == this.jl) {
               wyj.aoL.it = this.it + wyj.aoL.getX();
               wyj.aoL.atW = this.atW + wyj.m() - this.aoj;
               if (wyj.aoL.mouseScrolled(d0, d1, d2, d3)) {
                  return true;
               }
               break;
            }
         }
      }

      if (this.Zt != null && this.kO) {
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
      } else {
         return false;
      }
   }

   @Override
   protected boolean f(int i, int j, int k) {
      if (this.ayx != null && this.ayx.d()) {
         return this.ayx.keyPressed(i, j, k);
      } else {
         if (this.kO) {
            for (wyj wyj : this.lK) {
               if (wyj.aoL instanceof pl pl && pl.e()) {
                  return pl.keyPressed(i, j, k);
               }
            }
         }

         return false;
      }
   }

   @Override
   protected boolean g(int i, int j, int k) {
      if (this.ayx != null && this.ayx.d()) {
         return this.ayx.keyReleased(i, j, k);
      } else {
         if (this.kO) {
            for (wyj wyj : this.lK) {
               if (wyj.aoL instanceof pl pl && pl.e()) {
                  return pl.keyReleased(i, j, k);
               }
            }
         }

         return false;
      }
   }

   @Override
   protected void i(float f) {
      if (this.ayx != null) {
         this.ayx.tick(f);
      }

      if (this.auF != null) {
         this.auF.tick(f);
      }
   }

   private KeyBindSetting w() {
      for (Setting setting : this.aqa.getVisibleSettings()) {
         if (setting instanceof KeyBindSetting) {
            return (KeyBindSetting)setting;
         }

         if (setting instanceof GroupSetting groupsetting) {
            KeyBindSetting keybindsetting = this.x(groupsetting.getSettings());
            if (keybindsetting != null) {
               return keybindsetting;
            }
         }
      }

      return null;
   }

   private KeyBindSetting x(List<Setting> list) {
      for (Setting setting : list) {
         if (setting instanceof KeyBindSetting) {
            return (KeyBindSetting)setting;
         }

         if (setting instanceof GroupSetting groupsetting) {
            KeyBindSetting keybindsetting = this.x(groupsetting.getSettings());
            if (keybindsetting != null) {
               return keybindsetting;
            }
         }
      }

      return null;
   }

   public void y(zj zj) {
      this.vz = zj;
      if (this.vz != null) {
         this.agE = this.vz.isModuleFavourite(this.aqa.getName());
      }
   }

   private void z() {
      if (this.vz != null) {
         this.vz.toggleModuleFavourite(this.aqa.getName());
         this.agE = this.vz.isModuleFavourite(this.aqa.getName());
         this.avS.b(this.agE);
      }
   }

   public String A(double d0, double d1) {
      if (this.jP != null && this.jP.isHovered(d0, d1)) {
         return this.aqa.isEnabled() ? "Выключить модуль" : "Включить модуль";
      } else if (this.avS != null && this.avS.isHovered(d0, d1)) {
         return this.agE ? "Убрать из избранного" : "Добавить в избранное";
      } else {
         if (this.kO) {
            for (wyj wyj : this.lK) {
               if (wyj.d() && !(wyj.b() < 0.01F)) {
                  float f = this.atW + wyj.m() - this.aoj;
                  Component component = wyj.aoL;
                  component.it = this.it + component.getX();
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
         }

         return d0 >= this.it && d0 <= this.it + this.qd && d1 >= this.atW && d1 <= this.atW + this.aem ? this.aqa.getDisplayDescription() : null;
      }
   }

   public void B() {
      if (this.jl != null) {
         this.jl.a(false);
         this.jl = null;
      }

      if (this.arD != null) {
         this.Us = false;
         this.arD = null;
      }

      if (this.Yl != null) {
         this.aoB = false;
         this.Yl = null;
      }

      this.aup.h();
      this.HT.h();
      this.aeH.h();
      if (this.ayx != null && this.ayx.d()) {
         this.ayx = null;
      }

      if (this.auF != null && this.auF.c()) {
         this.auF = null;
      }

      for (wyj wyj : this.lK) {
         if (wyj.MA != null) {
            wyj.MA.setOnVisibilityChange(null);
         }
      }

      this.lK.clear();
      this.IW.clear();
   }

   private void C(Matrix4f matrix4f, float f, float f1, float f2) {
      for (wyj wyj : this.lK) {
         if (wyj.aoL == this.jl) {
            float f3 = f1 + wyj.m() - this.aoj;
            this.jl.cM = this.uQ;
            this.jl.Lp = this.uv;
            this.jl.Kp = this.Cc;
            this.jl.l(matrix4f, f + this.jl.getX(), f3, f2);
            return;
         }
      }
   }

   public void D(mld mld) {
      this.arD = mld;
   }

   public mld E() {
      return this.arD;
   }

   public void F(boolean flag) {
      this.Us = flag;
   }

   public boolean G() {
      return this.Us;
   }

   public void H(oip oip) {
      this.Yl = oip;
   }

   public oip I() {
      return this.Yl;
   }

   public void J(boolean flag) {
      this.aoB = flag;
   }

   public boolean K() {
      return this.aoB;
   }

   public void L(hc hc) {
      this.apv = hc;
   }

   public hc M() {
      return this.apv;
   }

   public void N(boolean flag) {
      this.At = flag;
   }

   public boolean O() {
      return this.At;
   }
}
