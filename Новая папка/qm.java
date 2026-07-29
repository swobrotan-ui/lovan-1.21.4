public class qm extends vjv {
   private static final float acu = 0.3F;
   private static final float agR = 0.15F;
   private static final float LU = 1.08F;
   private static final float JZ = 0.95F;
   private static final float in = 1.0F;
   private lzl VR = lzl.Lt;
   private float xX = 0.0F;
   private float amb = 1.0F;
   private float rq = 1.0F;
   private boolean pz = false;
   private boolean KS = false;

   public void a(boolean flag) {
      if (this.pz != flag && !this.KS) {
         this.pz = flag;
         this.c(flag ? lzl.BQ : lzl.Lt);
      }
   }

   public void b(boolean flag) {
      if (this.KS != flag) {
         this.KS = flag;
         this.c(flag ? lzl.tX : lzl.jB);
      }
   }

   private void c(lzl lzl) {
      this.VR = lzl;
      this.amb = this.rq;
      this.xX = 0.0F;
      this.wI = System.nanoTime();
   }

   @Override
   public void f() {
      this.xX = this.xX + this.b();
      float f;
      float f1;
      switch (this.VR) {
         case Lt:
            f = 1.0F;
            f1 = 0.3F;
            break;
         case BQ:
            f = 1.08F;
            f1 = 0.3F;
            break;
         case tX:
            f = 0.95F;
            f1 = 0.15F;
            break;
         case jB:
            f = this.pz ? 1.08F : 1.0F;
            f1 = 0.15F;
            break;
         default:
            return;
      }

      float f2 = Math.min(this.xX / f1, 1.0F);
      float f3 = this.c(f2);
      this.rq = this.amb + (f - this.amb) * f3;
      if (f2 >= 1.0F) {
         this.rq = f;
         if (this.VR == lzl.jB) {
            this.VR = this.pz ? lzl.BQ : lzl.Lt;
         }
      }
   }

   @Override
   public boolean g() {
      return this.VR != lzl.Lt || Math.abs(this.rq - 1.0F) > 0.001F;
   }

   @Override
   public void h() {
      this.VR = lzl.Lt;
      this.xX = 0.0F;
      this.rq = 1.0F;
      this.amb = 1.0F;
      this.pz = false;
      this.KS = false;
      this.wI = System.nanoTime();
   }

   public float d() {
      return this.rq;
   }
}
