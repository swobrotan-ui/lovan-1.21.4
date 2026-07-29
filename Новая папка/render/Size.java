package render;

public record Size(float width, float height, float width, float height, float width, float height, float width, float height) {
   private final float jI;
   private final float YH;
   public static final Size kb = new Size(0.0F, 0.0F);

   public Size(double d0, double d1) {
      this((float)d0, (float)d1);
   }

   public Size(float f, float f1) {
      this.jI = f;
      this.YH = f1;
   }

   public float a() {
      return this.jI;
   }

   public float b() {
      return this.YH;
   }
}
