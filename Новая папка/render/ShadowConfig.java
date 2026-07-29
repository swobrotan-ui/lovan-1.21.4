package render;

import java.awt.Color;

public record ShadowConfig(
   float offsetX,
   float offsetY,
   float blur,
   int color,
   boolean enabled,
   float offsetX,
   float offsetY,
   float blur,
   int color,
   boolean enabled,
   float offsetX,
   float offsetY,
   float blur,
   int color,
   boolean enabled,
   float offsetX,
   float offsetY,
   float blur,
   int color,
   boolean enabled
) {
   private final float aqk;
   private final float aaH;
   private final float Gg;
   private final int el;
   private final boolean HL;
   public static final ShadowConfig vA = new ShadowConfig(0.0F, 0.0F, 0.0F, 0, false);

   public ShadowConfig(float f, float f1, float f2, Color color) {
      this(f, f1, f2, color.getRGB(), true);
   }

   public ShadowConfig(float f, float f1, float f2, int i) {
      this(f, f1, f2, i, true);
   }

   public ShadowConfig(float f, float f1, float f2, int i, boolean flag) {
      this.aqk = f;
      this.aaH = f1;
      this.Gg = f2;
      this.el = i;
      this.HL = flag;
   }

   public static ShadowConfig a(Color color) {
      return new ShadowConfig(0.0F, 0.0F, 4.0F, color);
   }

   public static ShadowConfig b(int i) {
      return new ShadowConfig(0.0F, 0.0F, 4.0F, i);
   }

   public float c() {
      return this.aqk;
   }

   public float d() {
      return this.aaH;
   }

   public float e() {
      return this.Gg;
   }

   public int f() {
      return this.el;
   }

   public boolean g() {
      return this.HL;
   }
}
