import font.MSDFFont;
import java.awt.Color;
import render.BuiltText;

public final class qt extends se<BuiltText> {
   private MSDFFont Tq;
   private String aiS;
   private float aeL;
   private float RU;
   private int wB;
   private float apT;
   private float ach;
   private int adc;
   private float awt;
   private boolean lY;

   public qt a(MSDFFont msdffont) {
      this.Tq = msdffont;
      return this;
   }

   public qt b(String s) {
      this.aiS = s;
      return this;
   }

   public qt c(float f) {
      this.aeL = f;
      return this;
   }

   public qt d(float f) {
      this.RU = f;
      return this;
   }

   public qt e(Color color) {
      return this.f(color.getRGB());
   }

   public qt f(int i) {
      this.wB = i;
      return this;
   }

   public qt g(float f) {
      this.apT = f;
      return this;
   }

   public qt h(float f) {
      this.ach = f;
      return this;
   }

   public qt i(Color color, float f) {
      return this.j(color.getRGB(), f);
   }

   public qt j(int i, float f) {
      this.adc = i;
      this.awt = f;
      return this;
   }

   public qt k(boolean flag) {
      this.lY = flag;
      return this;
   }

   protected BuiltText l() {
      return new BuiltText(this.Tq, this.aiS, this.aeL, this.RU, this.wB, this.apT, this.ach, this.adc, this.awt, this.lY);
   }

   @Override
   protected void b() {
      this.Tq = null;
      this.aiS = "";
      this.aeL = 0.0F;
      this.RU = 0.05F;
      this.wB = -1;
      this.apT = 0.5F;
      this.ach = 0.0F;
      this.adc = 0;
      this.awt = 0.0F;
      this.lY = true;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.l();
   }
}
