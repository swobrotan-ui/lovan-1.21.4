public abstract class vjv {
   protected static final long anq = 1000000000L;
   protected long wI = 0L;

   public void a() {
      this.wI = System.nanoTime();
   }

   protected float b() {
      long i = System.nanoTime();
      float f = (float)(i - this.wI) / 1.0E9F;
      this.wI = i;
      return f;
   }

   protected float c(float f) {
      return 1.0F - (float)Math.pow(1.0F - f, 3.0);
   }

   protected float d(float f) {
      if (f < 0.5F) {
         return 4.0F * f * f * f;
      } else {
         float f1 = 2.0F * f - 2.0F;
         return 0.5F * f1 * f1 * f1 + 1.0F;
      }
   }

   protected float e(float f) {
      return f < 0.5F ? 2.0F * f * f : -1.0F + (4.0F - 2.0F * f) * f;
   }

   public abstract void f();

   public abstract boolean g();

   public abstract void h();
}
