import org.joml.Matrix4f;
import render.BuiltBlur;
import render.RadiusConfig;
import render.Size;

public class it extends vjv {
   private static final float SO = 0.15F;
   private static final float nW = 15.0F;
   private static final float ahh = 12.0F;
   private static final float jU = 0.92F;
   private static final float axU = 286.0F;
   private static final float ahl = 201.0F;
   private static final float avG = 4.0F;
   private static final float si = 0.001F;
   private static final float Fc = 1.0E-4F;
   private zq RF = zq.atV;
   private float sm = 0.0F;
   private float azT = 0.0F;
   private float lU = 1.0F;
   private float FP = 1.0F;
   private float lw = -1.0F;
   private float ye = -1.0F;
   private BuiltBlur pQ;
   private Runnable oa;
   private boolean ajR = false;
   private BuiltBlur Dn;
   private float amX = -1.0F;
   private float ue = -1.0F;
   private float iS = -1.0F;
   private float aaE = -1.0F;

   @Override
   public void a() {
      super.a();
   }

   public void a(Runnable runnable) {
      if (this.RF == zq.atV) {
         this.oa = runnable;
         this.ajR = false;
         this.RF = zq.TI;
         this.sm = 0.0F;
         this.wI = System.nanoTime();
         this.i();
      }
   }

   @Override
   public void f() {
      if (this.RF != zq.atV) {
         this.sm = this.sm + this.b();
         switch (this.RF) {
            case TI:
               this.b();
               return;
            case se:
               this.c();
               return;
            case Eu:
               this.d();
         }
      }
   }

   private void b() {
      float f = Math.min(this.sm / 0.15F, 1.0F);
      float f1 = this.c(f);
      this.azT = f1 * 15.0F;
      this.lU = 1.0F - f1 * 0.07999998F;
      this.FP = 1.0F - f1;
      if (f >= 1.0F) {
         this.RF = zq.se;
         this.sm = 0.0F;
         this.FP = 0.0F;
         this.lU = 0.92F;
         this.azT = 15.0F;
      }
   }

   private void c() {
      if (!this.ajR && this.oa != null) {
         this.oa.run();
         this.ajR = true;
      }

      if (this.ajR) {
         this.RF = zq.Eu;
         this.sm = 0.0F;
      }
   }

   private void d() {
      float f = Math.min(this.sm / 0.15F, 1.0F);
      float f1 = this.c(f);
      this.azT = (1.0F - f1) * 15.0F;
      this.lU = 0.92F + f1 * 0.07999998F;
      this.FP = f1;
      if (f >= 1.0F) {
         this.RF = zq.atV;
         this.sm = 0.0F;
         this.azT = 0.0F;
         this.lU = 1.0F;
         this.FP = 1.0F;
         this.i();
      }
   }

   private void e() {
      if (this.pQ != null && this.f()) {
         this.pQ = this.g(this.lU, this.azT);
         this.lw = this.azT;
         this.ye = this.lU;
      }
   }

   private boolean f() {
      return Math.abs(this.azT - this.lw) > 0.001F || Math.abs(this.lU - this.ye) > 1.0E-4F;
   }

   private BuiltBlur g(float f, float f1) {
      float f2 = 294.0F * f;
      float f3 = 209.0F * f;
      float f4 = 12.0F * f;
      return new hvg().a(new Size(f2, f3)).b(new RadiusConfig(f4)).d(1.0F).e(f1).a();
   }

   private void i() {
      this.lw = -1.0F;
      this.ye = -1.0F;
      this.Dn = null;
      this.amX = -1.0F;
      this.ue = -1.0F;
      this.iS = -1.0F;
      this.aaE = -1.0F;
   }

   public void j(Matrix4f matrix4f, float f, float f1) {
      if (this.RF != zq.atV && this.pQ != null) {
         float f2 = f + 143.0F;
         float f3 = f1 + 100.5F;
         float f4 = 294.0F * this.lU;
         float f5 = 209.0F * this.lU;
      }
   }

   public void k(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (this.RF != zq.atV) {
         float f4 = f + f2 / 2.0F;
         float f5 = f1 + f3 / 2.0F;
         float f6 = (f2 + 8.0F) * this.lU;
         float f7 = (f3 + 8.0F) * this.lU;
         if (this.Dn == null
            || Math.abs(f2 - this.amX) > 0.1F
            || Math.abs(f3 - this.ue) > 0.1F
            || Math.abs(this.lU - this.aaE) > 1.0E-4F
            || Math.abs(this.azT - this.iS) > 0.001F) {
            this.Dn = new hvg().a(new Size(f6, f7)).b(new RadiusConfig(12.0F * this.lU)).d(1.0F).e(this.azT).a();
            this.amX = f2;
            this.ue = f3;
            this.aaE = this.lU;
            this.iS = this.azT;
         }

         this.Dn.a(matrix4f, f4 - f6 / 2.0F, f5 - f7 / 2.0F, 0.0F, this.azT);
      }
   }

   public void l() {
      this.RF = zq.atV;
      this.sm = 0.0F;
      this.azT = 0.0F;
      this.lU = 1.0F;
      this.FP = 1.0F;
      this.ajR = false;
      this.oa = null;
      this.i();
      this.wI = System.nanoTime();
   }

   @Override
   public boolean g() {
      return this.RF != zq.atV;
   }

   @Override
   public void h() {
      this.RF = zq.atV;
      this.sm = 0.0F;
      this.azT = 0.0F;
      this.lU = 1.0F;
      this.FP = 1.0F;
      this.i();
      this.wI = System.nanoTime();
   }

   public float m() {
      return this.lU;
   }

   public float n() {
      return this.FP;
   }
}
