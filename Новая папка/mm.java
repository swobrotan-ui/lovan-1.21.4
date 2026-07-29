public class mm extends vjv {
   private static final float DA = 10.0F;
   private static final float WL = 1.0F;
   private static final float YT = 0.3F;
   private float Qx = 0.3F;
   private float FQ = 0.3F;
   private boolean Ts = false;
   private boolean aiP = true;

   public void a(boolean flag) {
      this.Ts = flag;
      this.FQ = flag ? 1.0F : 0.3F;
      this.Qx = this.FQ;
      this.aiP = false;
      this.wI = System.nanoTime();
   }

   public void b(boolean flag) {
      if (this.Ts != flag) {
         if (this.aiP) {
            this.Ts = flag;
            this.FQ = flag ? 1.0F : 0.3F;
            this.Qx = this.FQ;
            this.aiP = false;
         } else {
            this.Ts = flag;
            this.FQ = flag ? 1.0F : 0.3F;
         }
      }
   }

   @Override
   public void f() {
      if (this.aiP) {
         this.aiP = false;
      } else {
         float f = this.b();
         float f1 = this.FQ - this.Qx;
         if (Math.abs(f1) > 0.001F) {
            this.Qx += f1 * 10.0F * f;
            if (Math.abs(this.FQ - this.Qx) < 0.01F) {
               this.Qx = this.FQ;
            }

            this.Qx = Math.max(0.3F, Math.min(1.0F, this.Qx));
         } else {
            this.Qx = this.FQ;
         }
      }
   }

   @Override
   public boolean g() {
      return Math.abs(this.Qx - this.FQ) > 0.001F;
   }

   @Override
   public void h() {
      this.Qx = 0.3F;
      this.FQ = 0.3F;
      this.Ts = false;
      this.aiP = true;
      this.wI = System.nanoTime();
   }

   public float c() {
      return this.Qx;
   }
}
