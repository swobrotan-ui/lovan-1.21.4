import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class ii extends Component {
   protected final BuiltRectangle LA;
   protected final String uf;
   protected final float azn;
   protected final float azx;
   protected Runnable sY;
   protected final qm eV;
   private final Matrix4f qF = new Matrix4f();

   public ii(float f, float f1, float f2, String s, float f3, float f4, float f5, Runnable runnable) {
      super(f, f1, f2, f2);
      this.uf = s;
      this.azn = f3;
      this.azx = f4;
      this.sY = runnable;
      this.LA = RectangleCache.b(f2, f2, f5);
      this.eV = new qm();
      this.eV.a();
   }

   public void a(Runnable runnable) {
      this.sY = runnable;
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f3, float f2) {
      this.eV.a(this.cH);
      this.eV.f();
      Matrix4f matrix4f1 = this.b(matrix4f, f, f1, this.eV.d());
      this.LA.a(matrix4f1, f, f1, f2);
      BuiltText builttext = TextCache.a(this.aiL, this.uf, this.azn, Bz);
      builttext.a(matrix4f1, f + this.azx + 2.0F, f1 + this.azx + 1.0F, f2);
   }

   protected Matrix4f b(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + this.qd / 2.0F;
         float f4 = f1 + this.aem / 2.0F;
         this.qF.set(matrix4f);
         this.qF.translate(f3, f4, 0.0F);
         this.qF.scale(f2, f2, 1.0F);
         this.qF.translate(-f3, -f4, 0.0F);
         return this.qF;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0 && this.sY != null) {
         this.eV.b(true);
         this.sY.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.eV.b(false);
         return true;
      } else {
         return false;
      }
   }
}
