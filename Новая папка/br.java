import render.BuiltRectangle;

public final class br extends se<BuiltRectangle> {
   private float acS;
   private float BT;
   private float ix = 8.0F;
   private float[] lA;
   private float[] OB;
   private float[] atD;
   private float[] L;

   public br a(float f, float f1) {
      this.acS = f;
      this.BT = f1;
      return this;
   }

   public br b(float f) {
      this.ix = f;
      return this;
   }

   public br c(float[] afloat, float[] afloat1, float[] afloat2, float[] afloat3) {
      this.lA = afloat;
      this.OB = afloat1;
      this.atD = afloat2;
      this.L = afloat3;
      return this;
   }

   protected BuiltRectangle d() {
      return new BuiltRectangle(this.acS, this.BT, this.ix, this.lA, this.OB, this.atD, this.L);
   }

   @Override
   protected void b() {
      this.acS = 0.0F;
      this.BT = 0.0F;
      this.ix = 8.0F;
      this.lA = null;
      this.OB = null;
      this.atD = null;
      this.L = null;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.d();
   }
}
