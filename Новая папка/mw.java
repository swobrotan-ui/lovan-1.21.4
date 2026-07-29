public class mw extends vjv {
   private static final float Hq = 0.3F;
   private static final float ZS = 0.3F;
   private static final float hW = 0.7F;
   private static final float sc = 2.0F;
   private static final float sF = 40.0F;
   private static final float nh = 16.0F;
   private static final float arF = 2.0F;
   private static final float IE = 22.0F;
   private float adV = 2.0F;
   private float vf = 0.0F;
   private boolean vy = false;
   private float axo = 0.0F;
   private boolean rT = false;
   private boolean bc = true;

   public void a(boolean flag) {
      this.vy = flag;
      this.adV = flag ? 22.0F : 2.0F;
      this.vf = flag ? 1.0F : 0.0F;
      this.axo = flag ? 1.0F : 0.0F;
      this.rT = false;
      this.bc = false;
      this.wI = System.nanoTime();
   }

   public void b(boolean flag) {
      if (this.vy != flag || this.rT) {
         if (this.bc) {
            this.vy = flag;
            this.adV = flag ? 22.0F : 2.0F;
            this.vf = flag ? 1.0F : 0.0F;
            this.axo = flag ? 1.0F : 0.0F;
            this.bc = false;
         } else {
            if (this.vy != flag) {
               this.vy = flag;
               this.axo = 0.0F;
               this.rT = true;
               this.wI = System.nanoTime();
            }
         }
      }
   }

   @Override
   public void f() {
      if (this.bc) {
         this.bc = false;
      } else if (this.rT) {
         float f = this.b();
         this.axo += f / 0.3F;
         if (this.axo >= 1.0F) {
            this.axo = 1.0F;
            this.rT = false;
         }

         float f1 = this.d(this.axo);
         float f2 = 20.0F;
         this.adV = this.vy ? 2.0F + f2 * f1 : 22.0F - f2 * f1;
         this.c(f1);
      }
   }

   private void c(float f) {
      if (f < 0.3F) {
         this.vf = this.vy ? 0.0F : 1.0F;
      } else if (f > 0.7F) {
         this.vf = this.vy ? 1.0F : 0.0F;
      } else {
         float f1 = (f - 0.3F) / 0.39999998F;
         float f2 = this.e(f1);
         this.vf = this.vy ? f2 : 1.0F - f2;
      }
   }

   @Override
   public boolean g() {
      return this.rT;
   }

   @Override
   public void h() {
      this.adV = 2.0F;
      this.vf = 0.0F;
      this.vy = false;
      this.axo = 0.0F;
      this.rT = false;
      this.bc = true;
      this.wI = System.nanoTime();
   }

   public float d() {
      return this.adV;
   }

   public float e() {
      return this.vf;
   }
}
