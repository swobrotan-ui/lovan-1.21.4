public class qh {
   private static final long auu = 1000000000L;
   private long gX = 0L;
   private int Fp = 0;
   private int ZD = 0;
   private float lu = 1.0F;
   private float yP = 15.0F;

   public void a(int i) {
      this.gX = System.nanoTime();
      this.Fp = i;
      this.ZD = i;
      this.lu = 1.0F;
   }

   public void b(int i) {
      if (this.ZD != i) {
         this.Fp = this.ZD;
         this.ZD = i;
         this.lu = 0.0F;
         this.gX = System.nanoTime();
      }
   }

   public void c() {
      if (!(this.lu >= 1.0F) || this.Fp != this.ZD) {
         long i = System.nanoTime();
         float f = (float)(i - this.gX) / 1.0E9F;
         this.gX = i;
         float f1 = 4.0F;
         this.lu += f * f1;
         if (this.lu > 1.0F) {
            this.lu = 1.0F;
         }
      }
   }

   public float d() {
      float f = xsx.c(this.lu);
      return this.Fp + (this.ZD - this.Fp) * f;
   }

   public float e() {
      return this.lu;
   }

   public boolean f() {
      return this.lu < 1.0F;
   }

   public int g() {
      return this.Fp;
   }

   public int h() {
      return this.ZD;
   }

   public float i() {
      return this.lu;
   }

   public void j(float f) {
      this.yP = f;
   }
}
