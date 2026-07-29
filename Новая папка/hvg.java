import render.BuiltBlur;
import render.ColorPair;
import render.RadiusConfig;
import render.Size;

public final class hvg extends se<BuiltBlur> {
   private Size cr;
   private RadiusConfig amJ;
   private ColorPair HX;
   private float Ha;
   private float OO;

   public hvg a(Size size) {
      this.cr = size;
      return this;
   }

   public hvg b(RadiusConfig radiusconfig) {
      this.amJ = radiusconfig;
      return this;
   }

   public hvg c(ColorPair colorpair) {
      this.HX = colorpair;
      return this;
   }

   public hvg d(float f) {
      this.Ha = f;
      return this;
   }

   public hvg e(float f) {
      this.OO = f;
      return this;
   }

   protected BuiltBlur f() {
      return new BuiltBlur(this.cr, this.amJ, this.HX, this.Ha, this.OO);
   }

   @Override
   protected void b() {
      this.cr = Size.kb;
      this.amJ = RadiusConfig.azt;
      this.HX = ColorPair.gH;
      this.Ha = 1.0F;
      this.OO = 0.0F;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.f();
   }
}
