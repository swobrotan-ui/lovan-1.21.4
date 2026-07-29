import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.RectangleCache;

public abstract class ct extends Component {
   protected static final float ayE = 286.0F;
   protected static final float AP = 40.0F;
   protected static final float abB = 8.0F;
   protected final BuiltRectangle apY;
   protected final vp azS;
   protected float anC = 1.0F;
   protected float ku = 1.0F;

   protected ct(float f, float f1, boolean flag) {
      super(f, f1, 286.0F, 40.0F);
      this.apY = RectangleCache.b(286.0F, 40.0F, 8.0F);
      this.azS = new vp();
      if (flag) {
         this.azS.d();
      } else {
         this.azS.b();
      }
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.azS.f();
      this.a(f2);
      float f4 = this.azS.j() * this.anC;
      float f5 = f3 * this.azS.k();
      Matrix4f matrix4f1 = upq.b(matrix4f, f, f1, f4);
      if (this.azS.g()) {
         this.azS.g(matrix4f, f, f1);
      }

      this.apY.a(matrix4f1, f, f1, f5);
      this.b(matrix4f1, f, f1, i, j, f2, f5);
      this.c(matrix4f1, f, f1, i, j, f2, f5);
   }

   protected void a(float f) {
      this.ku = this.cH ? 1.02F : 1.0F;
      this.anC = mdd.a(this.anC, this.ku, f * 0.15F);
   }

   protected abstract void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3);

   protected abstract void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3);

   public void d(Runnable runnable) {
      this.azS.c(runnable);
   }

   public void e() {
      this.azS.b();
   }

   public boolean g() {
      return this.azS.g();
   }
}
