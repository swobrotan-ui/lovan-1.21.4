import core.Localization;
import font.MSDFFont;
import gui.Component;
import gui.GuiConstants;
import java.awt.Color;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class q extends Component {
   private static final float arN = 175.0F;
   private static final float Ul = 142.0F;
   private static final float Dg = 30.0F;
   private static final float Aw = 20.0F;
   private static final float XM = 10.0F;
   private static final float oK = 10.0F;
   private final BuiltRectangle GW;
   private final String is;
   private final String arI;
   private final Runnable zC;
   private final float Mm;
   private final MSDFFont DI;
   private final dk Nr;
   private final Matrix4f afg = new Matrix4f();
   private final boolean Kb;
   private final s Gm;
   private float ajO = -1.0F;
   private Color Ov = GuiConstants.Bz;

   public q(float f, float f1, Supplier<MSDFFont> supplier, String s, String s1, Runnable runnable) {
      this(f, f1, supplier, s, s1, runnable, false);
   }

   public q(float f, float f1, Supplier<MSDFFont> supplier, String s, String s1, Runnable runnable, boolean flag) {
      super(f, f1, flag ? 142.0F : 175.0F, 30.0F);
      this.zC = runnable;
      this.arI = s;
      this.DI = (MSDFFont)supplier.get();
      this.is = s1;
      this.Kb = flag;
      this.GW = RectangleCache.b(flag ? 142.0F : 175.0F, 30.0F, 8.0F);
      this.Nr = new dk();
      this.Nr.a();
      this.Gm = flag ? new s(18.0F, 17.0F, 5.0F, 1.5F) : null;
      float f2 = this.DI.e().d() * 15.0F;
      this.Mm = 15.0F - f2 / 2.0F - 2.0F;
   }

   public String a() {
      return this.arI;
   }

   public void b(boolean flag) {
      this.Nr.c(flag);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f6, float f2) {
      this.Nr.a(this.cH);
      this.Nr.f();
      Matrix4f matrix4f1 = this.c(matrix4f, f, f1, this.Nr.j());
      float f3 = f2 * this.Nr.k();
      if (this.Kb) {
         float f4 = f - 22.0F;
         float f5 = f1 + 15.0F - 16.5F;
         this.Gm.a(matrix4f, f4, f5, f3);
      }

      this.GW.a(matrix4f1, f, f1, f3);
      String s = Localization.a().c(this.arI);
      if (this.Kb) {
         Color color2 = this.d(this.Nr.i());
         BuiltText builttext2 = TextCache.a(this.DI, s, 15.0F, color2);
         builttext2.a(matrix4f1, f + 10.0F, f1 + this.Mm, f3);
      } else {
         Color color1 = this.d(this.Nr.i());
         BuiltText builttext = TextCache.a(this.aiL, this.is, 20.0F, color1);
         builttext.a(matrix4f1, f + 9.0F, f1 + 3.0F, f3);
         Color color = this.d(this.Nr.i());
         BuiltText builttext1 = TextCache.a(this.DI, s, 15.0F, color);
         builttext1.a(matrix4f1, f + 10.0F + 20.0F + 10.0F, f1 + this.Mm, f3);
      }
   }

   private Matrix4f c(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + this.qd / 2.0F;
         float f4 = f1 + this.aem / 2.0F;
         this.afg.set(matrix4f);
         this.afg.translate(f3, f4, 0.0F);
         this.afg.scale(f2, f2, 1.0F);
         this.afg.translate(-f3, -f4, 0.0F);
         return this.afg;
      }
   }

   private Color d(float f) {
      f = Math.max(0.0F, Math.min(1.0F, f));
      if (Math.abs(f - this.ajO) < 0.01F) {
         return this.Ov;
      } else {
         this.ajO = f;
         int i = (int)(GuiConstants.Bz.getRed() + (Color.WHITE.getRed() - GuiConstants.Bz.getRed()) * f);
         int j = (int)(GuiConstants.Bz.getGreen() + (Color.WHITE.getGreen() - GuiConstants.Bz.getGreen()) * f);
         int k = (int)(GuiConstants.Bz.getBlue() + (Color.WHITE.getBlue() - GuiConstants.Bz.getBlue()) * f);
         this.Ov = new Color(i, j, k);
         return this.Ov;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0 && this.zC != null) {
         this.Nr.b(true);
         this.zC.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.Nr.b(false);
         return true;
      } else {
         return false;
      }
   }
}
