public class go {
   public double oX;
   public double FO;
   public double dO;
   public double vc;
   public double gM;
   public double abH;
   public double aye;
   public double amI;
   public double arA;
   public final long OK;
   public final float pS;
   public final float atz;
   public final float awN;
   public final float Ye;

   public go(double d0, double d1, double d2, double d3, double d4, double d5, float f, float f1, float f2) {
      this(d0, d1, d2, d3, d4, d5, f, f1, f2, 0.97F);
   }

   public go(double d0, double d1, double d2, double d3, double d4, double d5, float f, float f1, float f2, float f3) {
      this.oX = d0;
      this.FO = d1;
      this.dO = d2;
      this.aye = d0;
      this.amI = d1;
      this.arA = d2;
      this.vc = d3;
      this.gM = d4;
      this.abH = d5;
      this.OK = System.currentTimeMillis();
      this.pS = f;
      this.atz = f1;
      this.awN = f2;
      this.Ye = f3;
   }

   public void a(float f) {
      this.aye = this.oX;
      this.amI = this.FO;
      this.arA = this.dO;
      float f1 = f * 20.0F;
      this.oX = this.oX + this.vc * f1;
      this.FO = this.FO + this.gM * f1;
      this.dO = this.dO + this.abH * f1;
      if (this.awN != 0.0F) {
         this.gM = this.gM - this.awN * f1;
      }

      float f2 = (float)Math.pow(this.Ye, f1);
      this.vc *= f2;
      this.gM *= f2;
      this.abH *= f2;
   }

   public boolean b() {
      return this.awN != 0.0F && this.gM < 0.0;
   }
}
