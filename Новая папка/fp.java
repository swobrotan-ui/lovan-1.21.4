public class fp extends vjv {
   private static final float PL = 30.0F;
   private static final float agL = 0.2F;
   private wgk th = wgk.MT;
   private float aom = 0.0F;
   private float CG = 0.0F;
   private float HH = 0.0F;
   private Runnable aiF;

   @Override
   public void a() {
      super.a();
      this.h();
   }

   public void b() {
      this.th = wgk.IH;
      this.aom = 0.0F;
      this.CG = 0.0F;
      this.HH = 0.0F;
      this.wI = System.nanoTime();
      this.aiF = null;
   }

   public void c(Runnable runnable) {
      this.th = wgk.su;
      this.aom = 0.0F;
      this.wI = System.nanoTime();
      this.aiF = runnable;
   }

   @Override
   public void f() {
      if (this.th != wgk.MT) {
         this.aom = this.aom + this.b();
         float f = Math.min(this.aom / 0.2F, 1.0F);
         float f1 = this.c(f);
         if (this.th == wgk.IH) {
            this.CG = f1;
            this.HH = f1 * 30.0F;
         } else if (this.th == wgk.su) {
            this.CG = 1.0F - f1;
            this.HH = (1.0F - f1) * 30.0F;
         }

         if (f >= 1.0F) {
            this.d();
         }
      }
   }

   private void d() {
      boolean flag = this.th == wgk.su;
      this.th = wgk.MT;
      this.aom = 0.0F;
      this.CG = flag ? 0.0F : 1.0F;
      this.HH = flag ? 0.0F : 30.0F;
      if (this.aiF != null) {
         Runnable runnable = this.aiF;
         this.aiF = null;
         runnable.run();
      }
   }

   @Override
   public boolean g() {
      return this.th != wgk.MT;
   }

   @Override
   public void h() {
      this.th = wgk.MT;
      this.aom = 0.0F;
      this.CG = 0.0F;
      this.HH = 0.0F;
      this.aiF = null;
      this.wI = System.nanoTime();
   }

   public float e() {
      return this.CG;
   }

   public float f() {
      return this.HH;
   }
}
