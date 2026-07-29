import render.BuiltBorderOutline;

public final class pv extends se<BuiltBorderOutline> {
   private static final float[] amd = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private float nE;
   private float Ed;
   private float TC = 8.0F;
   private float pU = 1.0F;
   private float YV = 1.0F;
   private float XC = 0.0F;
   private float[] XW = (float[])amd.clone();

   public pv a(float f, float f1) {
      this.nE = f;
      this.Ed = f1;
      return this;
   }

   public pv b(float f) {
      this.TC = f;
      return this;
   }

   public pv c(float f) {
      this.pU = f;
      return this;
   }

   public pv d(float f) {
      this.YV = f;
      return this;
   }

   public pv e(float f) {
      this.XC = f;
      return this;
   }

   public pv f(float f, float f1, float f2, float f3) {
      this.XW = new float[]{f, f1, f2, f3};
      return this;
   }

   public pv g(float f, float f1, float f2) {
      this.XW = new float[]{f, f1, f2, 1.0F};
      return this;
   }

   protected BuiltBorderOutline h() {
      return new BuiltBorderOutline(this.nE, this.Ed, this.TC, this.pU, this.YV, this.XC, this.XW);
   }

   @Override
   protected void b() {
      this.nE = 0.0F;
      this.Ed = 0.0F;
      this.TC = 8.0F;
      this.pU = 1.0F;
      this.YV = 1.0F;
      this.XC = 0.0F;
      this.XW = (float[])amd.clone();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.h();
   }
}
