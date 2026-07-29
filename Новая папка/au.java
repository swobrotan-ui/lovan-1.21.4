import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class au extends Component {
   private final BuiltRectangle lf;
   private final qm agc;
   private final Matrix4f agZ = new Matrix4f();
   private Runnable ok;

   public au(float f, float f1) {
      super(f, f1, 20.0F, 20.0F);
      this.lf = RectangleCache.b(20.0F, 20.0F, 4.0F);
      this.agc = new qm();
      this.agc.a();
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f4, float f2) {
      this.agc.a(this.cH);
      this.agc.f();
      float f3 = this.agc.d();
      Matrix4f matrix4f1 = f3 != 1.0F ? this.a(matrix4f, f, f1, f3) : matrix4f;
      this.lf.a(matrix4f1, f, f1, f2);
      BuiltText builttext = TextCache.a(this.aiL, "K", 15.0F, Bz);
      builttext.a(matrix4f1, f + 8.0F - 6.0F, f1 + 8.0F - 6.0F, f2);
   }

   private Matrix4f a(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + this.qd / 2.0F;
      float f4 = f1 + this.aem / 2.0F;
      this.agZ.set(matrix4f);
      this.agZ.translate(f3, f4, 0.0F);
      this.agZ.scale(f2, f2, 1.0F);
      this.agZ.translate(-f3, -f4, 0.0F);
      return this.agZ;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         this.agc.b(true);
         if (this.ok != null) {
            this.ok.run();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.agc.b(false);
         return true;
      } else {
         return false;
      }
   }

   public void b(Runnable runnable) {
      this.ok = runnable;
   }
}
