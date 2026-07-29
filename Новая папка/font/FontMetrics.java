package font;

public final class FontMetrics {
   private float lineHeight;
   private float ascender;
   private float descender;

   public float a() {
      return this.lineHeight;
   }

   public float b() {
      return this.ascender;
   }

   public float c() {
      return this.descender;
   }

   public float d() {
      return this.lineHeight + this.descender;
   }
}
