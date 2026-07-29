import font.MSDFFont;
import java.awt.Color;
import org.joml.Matrix4f;
import render.BuiltText;
import render.TextCache;

public class v {
   private static final float ayM = 5.0F;
   private static final float GM = 5.0F;
   private static final float mI = 5.0F;
   private static final float anG = 16.0F;
   private static final long anw = 200000000L;
   private static final Color pt = new Color(220, 220, 220);
   private final MSDFFont afa;
   private final keb acl;
   private String Ng = "";
   private String Sb = "";
   private float azv = 0.0F;
   private float sR = 0.0F;
   private long atq = 0L;
   private boolean jS = false;
   private boolean Om = false;

   public v(MSDFFont msdffont) {
      this.afa = msdffont;
      this.acl = new keb();
      this.acl.a();
   }

   public void a(String s) {
      if (s != null && !s.trim().isEmpty()) {
         if (!s.equals(this.Sb)) {
            this.Sb = s;
            if (this.jS && this.Om) {
               this.d();
               return;
            }

            this.atq = System.nanoTime();
            this.jS = true;
            this.Om = false;
         }
      } else {
         this.b();
      }
   }

   public void b() {
      this.Sb = "";
      this.jS = false;
      this.Om = false;
      if (this.acl.d() > 0.001F) {
         this.acl.c();
      }
   }

   public void c() {
      if (this.jS && !this.Om && !this.Sb.isEmpty()) {
         long i = System.nanoTime();
         if (i - this.atq >= 200000000L) {
            this.Om = true;
            this.d();
            this.acl.b();
         }
      }

      if (this.Sb.isEmpty() && this.acl.d() > 0.001F) {
         this.acl.c();
      }

      this.acl.f();
   }

   private void d() {
      this.Ng = this.Sb;
      String[] astring = this.e(this.Ng);
      float f = 0.0F;

      for (String s : astring) {
         float f1 = this.afa.c(s, 16.0F);
         if (f1 > f) {
            f = f1;
         }
      }

      this.azv = f + 10.0F;
      this.sR = astring.length * 16.0F * 1.2F + 10.0F;
   }

   private String[] e(String s) {
      return new String[]{s};
   }

   public void f(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (!this.Ng.isEmpty() && !(this.acl.d() < 0.001F)) {
         float f4 = this.acl.d();
         float f5 = this.g(f4) * f2;
         float f6 = 0.9F + 0.1F * this.h(f4);
         float f7 = this.azv * f3;
         float f8 = this.sR * f3;
         float f9 = f - f7 / 2.0F;
         float f10 = f1 - f8 - 5.0F * f3;
         float f11 = f9 + f7 / 2.0F;
         float f12 = f10 + f8 / 2.0F;
         matrix4f.translate(f11, f12, 0.0F);
         matrix4f.scale(f6, f6, 1.0F);
         matrix4f.translate(-f11, -f12, 0.0F);
         String[] astring = this.e(this.Ng);
         float f13 = f10 + 5.0F * f3;
         float f14 = 16.0F * f3;

         for (String s : astring) {
            BuiltText builttext = TextCache.a(this.afa, s, f14, pt);
            float f15 = f9 + 5.0F * f3;
            builttext.a(matrix4f, f15, f13, f5);
            f13 += f14 * 1.2F;
         }

         matrix4f.translate(f11, f12, 0.0F);
         matrix4f.scale(1.0F / f6, 1.0F / f6, 1.0F);
         matrix4f.translate(-f11, -f12, 0.0F);
      }
   }

   private float g(float f) {
      if (f < 0.5F) {
         return 4.0F * f * f * f;
      } else {
         float f1 = -2.0F * f + 2.0F;
         return 1.0F - f1 * f1 * f1 / 2.0F;
      }
   }

   private float h(float f) {
      float f1 = 1.70158F;
      float f2 = f1 + 1.0F;
      return 1.0F + f2 * (float)Math.pow(f - 1.0F, 3.0) + f1 * (float)Math.pow(f - 1.0F, 2.0);
   }
}
