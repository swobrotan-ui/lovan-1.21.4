public class cd {
   private float HW = 0.0F;
   private float Jd = 0.0F;
   private static final float aqO = 0.05F;

   public void a(float f) {
      this.HW = f;
   }

   public void b(float f) {
      this.HW += f;
   }

   public void c() {
      float f = this.HW - this.Jd;
      this.Jd += f * 0.05F;
   }

   public float d() {
      return this.Jd;
   }

   public float e() {
      return this.HW;
   }

   public void f(float f) {
      this.Jd = f;
      this.HW = f;
   }

   public boolean g() {
      return Math.abs(this.HW - this.Jd) > 0.01F;
   }
}
