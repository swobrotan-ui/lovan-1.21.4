package render;

import java.awt.Color;

public record ColorPair(
   int color1,
   int color2,
   int color3,
   int color4,
   int color1,
   int color2,
   int color3,
   int color4,
   int color1,
   int color2,
   int color3,
   int color4,
   int color1,
   int color2,
   int color3,
   int color4
) {
   private final int Iy;
   private final int ft;
   private final int ib;
   private final int afx;
   public static final ColorPair Jt = new ColorPair(0, 0, 0, 0);
   public static final ColorPair gH = new ColorPair(-1, -1, -1, -1);

   public ColorPair(Color color, Color color1, Color color2, Color color3) {
      this(color.getRGB(), color1.getRGB(), color2.getRGB(), color3.getRGB());
   }

   public ColorPair(Color color) {
      this(color, color, color, color);
   }

   public ColorPair(int i) {
      this(i, i, i, i);
   }

   public ColorPair(int i, int j, int k, int l) {
      this.Iy = i;
      this.ft = j;
      this.ib = k;
      this.afx = l;
   }

   public int a() {
      return this.Iy;
   }

   public int b() {
      return this.ft;
   }

   public int c() {
      return this.ib;
   }

   public int d() {
      return this.afx;
   }
}
