import render.BuiltLine2d;

public final class otj extends se<BuiltLine2d> {
   private float Pk;
   private float xx = 1.0F;
   private boolean AQ = false;
   private int gk = -1;
   private boolean XL = false;

   public otj a(float f) {
      this.Pk = f;
      return this;
   }

   public otj b(float f) {
      this.xx = f;
      return this;
   }

   public otj c() {
      this.AQ = true;
      return this;
   }

   public otj d() {
      this.AQ = false;
      return this;
   }

   public otj e(int i) {
      this.gk = i;
      return this;
   }

   public otj f(int i, int j, int k, int l) {
      this.gk = (i & 0xFF) << 24 | (j & 0xFF) << 16 | (k & 0xFF) << 8 | l & 0xFF;
      return this;
   }

   public otj g(float f, float f1, float f2, float f3) {
      return this.f((int)(f * 255.0F), (int)(f1 * 255.0F), (int)(f2 * 255.0F), (int)(f3 * 255.0F));
   }

   public otj h(int i, int j, int k) {
      int l = this.gk >> 24 & 0xFF;
      return this.f(l, i, j, k);
   }

   public otj i(int i) {
      this.gk = this.gk & 16777215 | (i & 0xFF) << 24;
      return this;
   }

   public otj j(float f) {
      return this.i((int)(f * 255.0F));
   }

   public otj k() {
      this.XL = true;
      return this;
   }

   public otj l() {
      this.XL = false;
      return this;
   }

   public otj m(boolean flag) {
      this.XL = flag;
      return this;
   }

   protected BuiltLine2d n() {
      return new BuiltLine2d(this.Pk, this.xx, this.AQ, this.gk, this.XL);
   }

   @Override
   protected void b() {
      this.Pk = 0.0F;
      this.xx = 1.0F;
      this.AQ = false;
      this.gk = -1;
      this.XL = false;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.n();
   }
}
