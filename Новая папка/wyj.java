import gui.Component;
import java.util.Objects;
import setting.Setting;

public class wyj {
   public final String atb;
   public final Component aoL;
   public float agp;
   private float yh;
   public final Setting MA;
   private final vb EY;
   private boolean Ix = true;

   public wyj(String s, Component component, float f) {
      this(s, component, f, null);
   }

   public wyj(String s, Component component, float f, Setting setting) {
      this.atb = s;
      this.aoL = component;
      this.agp = f;
      this.yh = component.getHeight() + 10.0F;
      this.MA = setting;
      this.EY = new vb();
      boolean flag = setting == null || setting.isVisible();
      this.Ix = flag;
      this.EY.a(flag, this.yh);
   }

   public float a() {
      if (!this.Ix && !this.EY.g()) {
         return 0.0F;
      } else {
         return this.Ix && !this.EY.g() ? this.yh : this.EY.l();
      }
   }

   public float b() {
      return this.EY.j();
   }

   public float c() {
      return this.EY.k();
   }

   public boolean d() {
      return this.EY.m();
   }

   public void e() {
      this.EY.f();
   }

   public void f() {
      this.Ix = true;
      this.EY.b(this.yh);
   }

   public void g(Runnable runnable) {
      this.EY.c(() -> {
         this.Ix = false;
         if (runnable != null) {
            runnable.run();
         }
      });
   }

   public boolean h() {
      return this.EY.g();
   }

   public String i() {
      return this.atb;
   }

   public Component j() {
      return this.aoL;
   }

   public float k() {
      return this.agp;
   }

   public Setting l() {
      return this.MA;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         wyj wyj = (wyj)object;
         return Objects.equals(this.atb, wyj.atb)
            && Objects.equals(this.aoL, wyj.aoL)
            && Float.floatToIntBits(this.agp) == Float.floatToIntBits(wyj.agp)
            && Objects.equals(this.MA, wyj.MA);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.atb, this.aoL, this.agp, this.MA);
   }

   @Override
   public String toString() {
      String s3 = this.atb;
      String s4 = String.valueOf(this.aoL);
      float f1 = this.agp;
      String s = String.valueOf(this.MA);
      float f = f1;
      String s1 = s4;
      String s2 = s3;
      return "SettingRow[label=" + s2 + ", component=" + s1 + ", yOffset=" + f + ", setting=" + s + "]";
   }

   public float m() {
      return this.agp;
   }

   public void n(float f) {
      this.agp = f;
   }

   public float o() {
      return this.yh;
   }

   public void p(float f) {
      this.yh = f;
   }

   public vb q() {
      return this.EY;
   }

   public boolean r() {
      return this.Ix;
   }

   public void s(boolean flag) {
      this.Ix = flag;
   }
}
