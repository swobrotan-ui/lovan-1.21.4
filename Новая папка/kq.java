public class kq {
   private static final long axA = 1000000000L;
   private long pu = 0L;
   private boolean Sy = false;
   private float gs = 0.0F;
   private float amc = 6.0F;

   public void a() {
      this.pu = System.nanoTime();
      this.gs = 0.0F;
   }

   public void b() {
      long i = System.nanoTime();
      float f = (float)(i - this.pu) / 1.0E9F;
      this.pu = i;
      float f1 = this.amc;
      if (this.Sy) {
         this.gs += f * f1;
         if (this.gs > 1.0F) {
            this.gs = 1.0F;
            return;
         }
      } else {
         this.gs -= f * f1;
         if (this.gs < 0.0F) {
            this.gs = 0.0F;
         }
      }
   }

   public boolean c() {
      return this.gs > 0.001F && this.gs < 0.999F;
   }

   public void d(float f) {
      this.gs = f;
      this.pu = System.nanoTime();
   }

   public void e() {
      this.pu = System.nanoTime();
   }

   public void f(boolean flag) {
      this.Sy = flag;
   }

   public boolean g() {
      return this.Sy;
   }

   public float h() {
      return this.gs;
   }

   public void i(float f) {
      this.amc = f;
   }
}
