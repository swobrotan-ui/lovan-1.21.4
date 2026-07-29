package render;

public record RadiusConfig(
   float radius1,
   float radius2,
   float radius3,
   float radius4,
   float radius1,
   float radius2,
   float radius3,
   float radius4,
   float radius1,
   float radius2,
   float radius3,
   float radius4,
   float radius1,
   float radius2,
   float radius3,
   float radius4
) {
   private final float Xq;
   private final float aou;
   private final float aoY;
   private final float adZ;
   public static final RadiusConfig azt = new RadiusConfig(0.0F, 0.0F, 0.0F, 0.0F);

   public RadiusConfig(double d0, double d1, double d2, double d3) {
      this((float)d0, (float)d1, (float)d2, (float)d3);
   }

   public RadiusConfig(double d0) {
      this(d0, d0, d0, d0);
   }

   public RadiusConfig(float f) {
      this(f, f, f, f);
   }

   public RadiusConfig(float f, float f1, float f2, float f3) {
      this.Xq = f;
      this.aou = f1;
      this.aoY = f2;
      this.adZ = f3;
   }

   public float a() {
      return this.Xq;
   }

   public float b() {
      return this.aou;
   }

   public float c() {
      return this.aoY;
   }

   public float d() {
      return this.adZ;
   }
}
