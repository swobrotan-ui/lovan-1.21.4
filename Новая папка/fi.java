import render.BuiltColorPicker;

public final class fi extends se<BuiltColorPicker> {
   private float ni;
   private float zb;
   private vt qn = vt.azP;
   private float EO = 0.0F;
   private float yy = 1.0F;
   private float BY = 1.0F;
   private float vW = 8.0F;

   public fi a(float f, float f1) {
      this.ni = f;
      this.zb = f1;
      return this;
   }

   public fi b(vt vt) {
      this.qn = vt;
      return this;
   }

   public fi c(float f) {
      this.EO = f;
      return this;
   }

   public fi d(float f) {
      this.yy = f;
      return this;
   }

   public fi e(float f) {
      this.BY = f;
      return this;
   }

   public fi f(float f) {
      this.vW = f;
      return this;
   }

   protected BuiltColorPicker g() {
      return new BuiltColorPicker(this.ni, this.zb, this.qn, this.EO, this.yy, this.BY, this.vW);
   }

   @Override
   protected void b() {
      this.ni = 0.0F;
      this.zb = 0.0F;
      this.qn = vt.azP;
      this.EO = 0.0F;
      this.yy = 1.0F;
      this.BY = 1.0F;
      this.vW = 8.0F;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.g();
   }
}
