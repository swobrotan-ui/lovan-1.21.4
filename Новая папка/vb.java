public class vb extends vjv {
   private static final float iI = 0.15F;
   private static final float RA = 0.95F;
   private static final float aiR = 0.050000012F;
   private acf rt = acf.xu;
   private float jv = 0.0F;
   private float vF = 1.0F;
   private float Ir = 1.0F;
   private float alc = 1.0F;
   private boolean aed = true;
   private float xE = 1.0F;
   private Runnable qR;

   @Override
   public void a() {
      super.a();
      this.h();
   }

   public void a(boolean flag, float f) {
      super.a();
      this.xE = f;
      if (flag) {
         this.vF = 1.0F;
         this.Ir = 1.0F;
         this.alc = f;
         this.aed = true;
      } else {
         this.vF = 0.0F;
         this.Ir = 0.95F;
         this.alc = 0.0F;
         this.aed = false;
      }

      this.rt = acf.xu;
      this.jv = 0.0F;
   }

   public void b(float f) {
      if (this.rt != acf.Gb) {
         this.rt = acf.Gb;
         this.jv = 0.0F;
         this.xE = f;
         this.aed = true;
         this.wI = System.nanoTime();
         this.qR = null;
      }
   }

   public void c(Runnable runnable) {
      if (this.rt != acf.adG) {
         this.rt = acf.adG;
         this.jv = 0.0F;
         this.wI = System.nanoTime();
         this.qR = runnable;
      }
   }

   public void d() {
      this.rt = acf.xu;
      this.jv = 0.0F;
      this.vF = 1.0F;
      this.Ir = 1.0F;
      this.alc = this.xE;
      this.aed = true;
   }

   public void e() {
      this.rt = acf.xu;
      this.jv = 0.0F;
      this.vF = 0.0F;
      this.Ir = 0.95F;
      this.alc = 0.0F;
      this.aed = false;
      if (this.qR != null) {
         Runnable runnable = this.qR;
         this.qR = null;
         runnable.run();
      }
   }

   @Override
   public void f() {
      if (this.rt != acf.xu) {
         this.jv = this.jv + this.b();
         float f = Math.min(this.jv / 0.15F, 1.0F);
         float f1 = this.c(f);
         if (this.rt == acf.Gb) {
            this.vF = f1;
            this.Ir = 0.95F + f1 * 0.050000012F;
            this.alc = f1 * this.xE;
         } else if (this.rt == acf.adG) {
            this.vF = 1.0F - f1;
            this.Ir = 1.0F - f1 * 0.050000012F;
            this.alc = (1.0F - f1) * this.xE;
         }

         if (f >= 1.0F) {
            this.g();
         }
      }
   }

   private void g() {
      boolean flag = this.rt == acf.adG;
      this.rt = acf.xu;
      this.jv = 0.0F;
      if (flag) {
         this.vF = 0.0F;
         this.Ir = 0.95F;
         this.alc = 0.0F;
         this.aed = false;
      } else {
         this.vF = 1.0F;
         this.Ir = 1.0F;
         this.alc = this.xE;
         this.aed = true;
      }

      if (this.qR != null) {
         Runnable runnable = this.qR;
         this.qR = null;
         runnable.run();
      }
   }

   @Override
   public boolean g() {
      return this.rt != acf.xu;
   }

   public boolean h() {
      return this.rt == acf.Gb;
   }

   public boolean i() {
      return this.rt == acf.adG;
   }

   @Override
   public void h() {
      this.rt = acf.xu;
      this.jv = 0.0F;
      this.vF = 1.0F;
      this.Ir = 1.0F;
      this.alc = 1.0F;
      this.xE = 1.0F;
      this.aed = true;
      this.qR = null;
      this.wI = System.nanoTime();
   }

   public float j() {
      return this.vF;
   }

   public float k() {
      return this.Ir;
   }

   public float l() {
      return this.alc;
   }

   public boolean m() {
      return this.aed;
   }
}
