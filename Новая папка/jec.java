import core.Localization;
import enum.Language;
import gui.Component;
import java.io.PrintStream;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class jec extends Component {
   private final BuiltRectangle gD;
   private final dk ajw;
   private final Matrix4f avD = new Matrix4f();
   private final float aoJ = 5.0F;
   private final float arR = 14.0F;

   public jec(float f, float f1, float f2, float f3) {
      super(f, f1, f2, f3);
      this.gD = RectangleCache.b(f2, f3, 5.0F);
      this.ajw = new dk();
      this.ajw.a();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f10, float f2) {
      this.ajw.a(this.cH);
      this.ajw.f();
      Matrix4f matrix4f1 = this.a(matrix4f, f, f1, this.ajw.j());
      float f3 = f2 * this.ajw.k();
      this.gD.a(matrix4f1, f, f1, f3);

      try {
         Language language = Localization.a().g();
         String s = language.getCode();
         BuiltText builttext1 = TextCache.a(this.aeN, s, 14.0F, Bz);
         float f8 = this.aeN.c(s, 14.0F);
         float f9 = f + (this.qd - f8) / 2.0F;
         float f7 = f1 + (this.aem - 14.0F) / 2.0F;
         builttext1.a(matrix4f1, f9, f7, f3);
      } catch (Exception exception) {
         BuiltText builttext = TextCache.a(this.aeN, "RU", 14.0F, Bz);
         float f4 = this.aeN.c("RU", 14.0F);
         float f5 = f + (this.qd - f4) / 2.0F;
         float f6 = f1 + (this.aem - 14.0F) / 2.0F;
         builttext.a(matrix4f1, f5, f6, f3);
      }
   }

   private Matrix4f a(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + this.qd / 2.0F;
         float f4 = f1 + this.aem / 2.0F;
         this.avD.set(matrix4f);
         this.avD.translate(f3, f4, 0.0F);
         this.avD.scale(f2, f2, 1.0F);
         this.avD.translate(-f3, -f4, 0.0F);
         return this.avD;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         this.ajw.b(true);

         try {
            Localization localization = Localization.a();
            localization.d();
         } catch (Exception exception) {
            PrintStream printstream = System.err;
            String s = exception.getMessage();
            printstream.println("Failed to change language: " + s);
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.ajw.b(false);
         return true;
      } else {
         return false;
      }
   }
}
