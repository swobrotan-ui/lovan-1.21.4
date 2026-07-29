import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

class wy extends Component {
   private String Qj;
   private final Runnable afD;
   private final qm abd;
   private final BuiltRectangle adQ;
   private final Matrix4f rY = new Matrix4f();
   private boolean amt = false;
   private boolean ML = true;
   private float PJ = 0.6F;
   private long apx = System.nanoTime();

   public wy(float f, float f1, String s, Runnable runnable) {
      super(f, f1, 30.0F, 30.0F);
      this.Qj = s;
      this.afD = runnable;
      this.abd = new qm();
      this.abd.a();
      this.adQ = RectangleCache.b(30.0F, 30.0F, 8.0F);
   }

   public void a(String s) {
      this.Qj = s;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f7, float f2) {
      this.abd.a(this.cH);
      this.abd.f();
      this.b();
      float f3 = this.abd.d();
      Matrix4f matrix4f1 = f3 != 1.0F ? this.c(matrix4f, f, f1, f3) : matrix4f;
      float f4 = this.amt && this.cH ? 1.0F : this.PJ;
      float f5 = f2 * f4;
      if (this.ML) {
         this.adQ.a(matrix4f1, f, f1, f5);
      }

      float f6 = 6.0F;
      BuiltText builttext = TextCache.a(this.aiL, this.Qj, 18.0F, Bz);
      builttext.a(matrix4f1, f + f6 - 1.0F, f1 + f6, f5);
   }

   private void b() {
      long i = System.nanoTime();
      float f = (float)(i - this.apx) / 1.0E9F;
      this.apx = i;
      f = Math.min(f, 0.05F);
      float f1 = this.cH ? 1.0F : 0.6F;
      float f2 = f1 - this.PJ;
      if (Math.abs(f2) > 0.001F) {
         this.PJ += f2 * f * 10.0F;
         this.PJ = Math.max(0.6F, Math.min(1.0F, this.PJ));
      } else {
         this.PJ = f1;
      }
   }

   private Matrix4f c(Matrix4f matrix4f, float f, float f1, float f2) {
      float f3 = f + this.qd / 2.0F;
      float f4 = f1 + this.aem / 2.0F;
      this.rY.set(matrix4f);
      this.rY.translate(f3, f4, 0.0F);
      this.rY.scale(f2, f2, 1.0F);
      this.rY.translate(-f3, -f4, 0.0F);
      return this.rY;
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0 && this.afD != null) {
         this.abd.b(true);
         this.afD.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.abd.b(false);
         return true;
      } else {
         return false;
      }
   }

   public void d(boolean flag) {
      this.amt = flag;
   }

   public void e(boolean flag) {
      this.ML = flag;
   }
}
