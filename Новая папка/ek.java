import gui.Component;
import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.RectangleCache;

public class ek extends Component {
   private static final float add = 5.0F;
   private static final float WO = 140.0F;
   private static final float Qk = 71.0F;
   private static final float akb = 10.0F;
   private static final float nM = 10.0F;
   private final BuiltRectangle we;
   private final BuiltRectangle tY;
   private float ayC = 0.0F;
   private boolean fc = false;
   private double MZ = 0.0;
   private float awx = 0.0F;
   private final cd jA = new cd();

   public ek(float f, float f1) {
      super(f, f1, 5.0F, 140.0F);
      this.we = RectangleCache.b(5.0F, 140.0F, 2.5F);
      this.tY = new br().a(5.0F, 71.0F).b(2.5F).a();
      this.jA.f(0.0F);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f6, float f2) {
      this.we.a(matrix4f, f, f1, f2);
      this.jA.c();
      float f3 = this.jA.d();
      float f4 = 69.0F;
      float f5 = f1 + f4 * f3;
      this.tY.a(matrix4f, f, f5, f2);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i != 0) {
         return false;
      } else {
         float f = 69.0F;
         float f1 = this.jA.d();
         float f2 = this.atW + f * f1;
         if (d0 >= this.it && d0 <= this.it + 5.0F && d1 >= f2 && d1 <= f2 + 71.0F) {
            this.fc = true;
            this.MZ = d1;
            this.awx = this.ayC;
            return true;
         } else if (d0 >= this.it && d0 <= this.it + 5.0F && d1 >= this.atW && d1 <= this.atW + 140.0F) {
            float f3 = (float)((d1 - this.atW) / f);
            this.a(f3);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0 && this.fc) {
         this.fc = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean d(double d1, double d0, int i, double d2, double d3) {
      if (this.fc) {
         float f = 69.0F;
         float f1 = (float)(d0 - this.MZ);
         float f2 = this.awx + f1 / f;
         this.ayC = Math.max(0.0F, Math.min(1.0F, f2));
         this.jA.a(this.ayC);
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean e(double d1, double d2, double d3, double d0) {
      float f = (float)(-d0 * 0.1F);
      this.ayC = Math.max(0.0F, Math.min(1.0F, this.ayC + f));
      this.jA.a(this.ayC);
      return true;
   }

   public void a(float f) {
      this.ayC = Math.max(0.0F, Math.min(1.0F, f));
      this.jA.a(this.ayC);
   }

   public void b(float f) {
      this.ayC = Math.max(0.0F, Math.min(1.0F, f));
      this.jA.f(this.ayC);
      this.jA.a(this.ayC);
   }

   public static float c() {
      return 5.0F;
   }

   public static float d() {
      return 10.0F;
   }

   public static float e() {
      return 10.0F;
   }

   public float f() {
      return this.ayC;
   }
}
