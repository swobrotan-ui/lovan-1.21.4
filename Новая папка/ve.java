public class ve {
   private static final long Iu = 1000000000L;
   private long ard = 0L;
   private boolean wv = false;
   private float TE = 0.0F;
   private float cV = 0.0F;
   private float alD = 0.0F;

   public void a() {
      this.ard = System.nanoTime();
      this.TE = 0.0F;
      this.alD = 0.0F;
   }

   public void b() {
      long i = System.nanoTime();
      float f = (float)(i - this.ard) / 1.0E9F;
      this.ard = i;
      float f1 = 8.0F;
      if (this.wv) {
         this.TE += f * f1;
         if (this.TE > 1.0F) {
            this.TE = 1.0F;
         }
      } else {
         this.TE -= f * f1;
         if (this.TE < 0.0F) {
            this.TE = 0.0F;
         }
      }

      float f2 = 10.0F;
      float f3 = this.cV - this.alD;
      if (Math.abs(f3) > 0.1F) {
         this.alD += f3 * f * f2;
      } else {
         this.alD = this.cV;
      }
   }

   public boolean c() {
      return this.TE > 0.001F;
   }

   public boolean d() {
      return this.TE >= 0.999F;
   }

   public void e(boolean flag) {
      this.wv = flag;
   }

   public boolean f() {
      return this.wv;
   }

   public float g() {
      return this.TE;
   }

   public void h(float f) {
      this.TE = f;
   }

   public void i(float f) {
      this.cV = f;
   }

   public float j() {
      return this.alD;
   }
}
