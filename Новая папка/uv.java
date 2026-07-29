import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class uv extends Component {
   private static final String pX = "J";
   private static final String asn = "K";
   private final BuiltRectangle JI;
   private final qm aat;
   private final Matrix4f Mf = new Matrix4f();
   private Runnable LB;
   private boolean awa;

   public uv(float f, float f1, boolean flag) {
      super(f, f1, 30.0F, 30.0F);
      this.JI = RectangleCache.b(30.0F, 30.0F, 8.0F);
      this.aat = new qm();
      this.aat.a();
      this.awa = flag;
   }

   public uv(float f, float f1) {
      this(f, f1, false);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f3, float f2) {
      this.aat.a(this.cH);
      this.aat.f();
      Matrix4f matrix4f1 = this.a(matrix4f, f, f1, this.aat.d());
      this.JI.a(matrix4f1, f, f1, f2);
      String s = this.awa ? "J" : "K";
      BuiltText builttext = TextCache.a(this.aiL, s, 16.0F, Bz);
      builttext.a(matrix4f1, f + 8.0F - 2.0F, f1 + 8.0F - 2.0F, f2);
   }

   private Matrix4f a(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + this.qd / 2.0F;
         float f4 = f1 + this.aem / 2.0F;
         this.Mf.set(matrix4f);
         this.Mf.translate(f3, f4, 0.0F);
         this.Mf.scale(f2, f2, 1.0F);
         this.Mf.translate(-f3, -f4, 0.0F);
         return this.Mf;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0) {
         this.aat.b(true);
         this.awa = !this.awa;
         if (this.LB != null) {
            this.LB.run();
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.aat.b(false);
         return true;
      } else {
         return false;
      }
   }

   public void b(Runnable runnable) {
      this.LB = runnable;
   }

   @Override
   public boolean isEnabled() {
      return this.awa;
   }

   @Override
   public void setEnabled(boolean flag) {
      this.awa = flag;
   }
}
