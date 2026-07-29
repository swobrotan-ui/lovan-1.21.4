import net.minecraft.client.texture.AbstractTexture;
import render.BuiltTexture;
import render.ColorPair;
import render.RadiusConfig;
import render.Size;

public final class fgj extends se<BuiltTexture> {
   private Size wR;
   private RadiusConfig awe;
   private ColorPair Hx;
   private float als;
   private float anl;
   private float ayh;
   private float fI;
   private float aun;
   private int bx;

   public fgj a(Size size) {
      this.wR = size;
      return this;
   }

   public fgj b(RadiusConfig radiusconfig) {
      this.awe = radiusconfig;
      return this;
   }

   public fgj c(ColorPair colorpair) {
      this.Hx = colorpair;
      return this;
   }

   public fgj d(float f) {
      this.als = f;
      return this;
   }

   public fgj e(float f, float f1, float f2, float f3, AbstractTexture abstracttexture) {
      return this.f(f, f1, f2, f3, abstracttexture.getGlId());
   }

   public fgj f(float f, float f1, float f2, float f3, int i) {
      this.anl = f;
      this.ayh = f1;
      this.fI = f2;
      this.aun = f3;
      this.bx = i;
      return this;
   }

   protected BuiltTexture g() {
      return new BuiltTexture(this.wR, this.awe, this.Hx, this.als, this.anl, this.ayh, this.fI, this.aun, this.bx);
   }

   @Override
   protected void b() {
      this.wR = Size.kb;
      this.awe = RadiusConfig.azt;
      this.Hx = ColorPair.gH;
      this.als = 1.0F;
      this.anl = 0.0F;
      this.ayh = 0.0F;
      this.fI = 0.0F;
      this.aun = 0.0F;
      this.bx = 0;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected Object c() {
      return this.g();
   }
}
