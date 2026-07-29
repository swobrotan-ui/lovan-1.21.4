public class keb extends vjv {
   private float alz = 0.0F;
   private float Mt = 0.0F;
   private static final float alg = 8.0F;

   @Override
   public void a() {
      super.a();
      this.alz = 0.0F;
      this.Mt = 0.0F;
   }

   public void b() {
      this.Mt = 1.0F;
   }

   public void c() {
      this.Mt = 0.0F;
   }

   @Override
   public void f() {
      float f = this.b();
      if (Math.abs(this.alz - this.Mt) > 0.001F) {
         float f1 = this.Mt - this.alz;
         this.alz += f1 * f * 8.0F;
         if (Math.abs(this.alz - this.Mt) < 0.001F) {
            this.alz = this.Mt;
         }
      }
   }

   @Override
   public boolean g() {
      return Math.abs(this.alz - this.Mt) > 0.001F;
   }

   @Override
   public void h() {
      this.alz = 0.0F;
      this.Mt = 0.0F;
   }

   public float d() {
      return this.alz;
   }

   public float e() {
      return this.Mt;
   }
}
