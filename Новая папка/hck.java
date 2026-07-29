import org.joml.Matrix4f;
import render.BuiltRectangle;
import render.BuiltText;
import render.RectangleCache;
import render.TextCache;

public class hck extends ct {
   private static final float oE = 286.0F;
   private static final float aey = 130.0F;
   private static final float cb = 40.0F;
   private static final float Ll = 0.5F;
   private static final float It = 1.0F;
   private static final float rs = 10.0F;
   private static final long cS = 1000000000L;
   private final String ahC = "R";
   private final Runnable atu;
   private final qm QM;
   private final BuiltRectangle kV;
   private final Matrix4f Zg = new Matrix4f();
   private float apF = 0.5F;
   private long DJ = System.nanoTime();

   public hck(float f, float f1, Runnable runnable, boolean flag) {
      super(f, f1, flag);
      this.atu = runnable;
      this.QM = new qm();
      this.QM.a();
      this.qd = 286.0F;
      this.aem = 130.0F;
      this.kV = RectangleCache.b(286.0F, 130.0F, 8.0F);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
      this.azS.f();
      this.a();
      float f4 = this.azS.j();
      float f5 = f3 * this.azS.k() * this.apF;
      Matrix4f matrix4f1 = upq.b(matrix4f, f, f1, f4);
      if (this.azS.g()) {
         this.azS.g(matrix4f, f, f1);
      }

      this.kV.a(matrix4f1, f, f1, f5);
      this.b(matrix4f1, f, f1, i, j, f2, f5);
      this.c(matrix4f1, f, f1, i, j, f2, f5);
   }

   private void a() {
      long i = System.nanoTime();
      float f = (float)(i - this.DJ) / 1.0E9F;
      this.DJ = i;
      f = Math.min(f, 0.05F);
      float f1 = this.cH ? 1.0F : 0.5F;
      float f2 = f1 - this.apF;
      if (Math.abs(f2) > 0.001F) {
         this.apF += f2 * f * 10.0F;
      } else {
         this.apF = f1;
      }
   }

   @Override
   protected void a(float f) {
      this.anC = 1.0F;
      this.ku = 1.0F;
   }

   @Override
   protected void b(Matrix4f matrix4f, float f, float f1, int i, int j, float f5, float f2) {
      this.QM.a(this.cH);
      this.QM.f();
      float f3 = f + 123.0F;
      float f4 = f1 + 45.0F;
      Matrix4f matrix4f1 = this.b(matrix4f, f, f1, this.QM.d());
      BuiltText builttext = TextCache.a(this.aiL, "R", 40.0F, Bz);
      builttext.a(matrix4f1, f3, f4, f2);
   }

   @Override
   protected void c(Matrix4f matrix4f, float f, float f1, int i, int j, float f2, float f3) {
   }

   private Matrix4f b(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + 143.0F;
         float f4 = f1 + 65.0F;
         this.Zg.set(matrix4f);
         this.Zg.translate(f3, f4, 0.0F);
         this.Zg.scale(f2, f2, 1.0F);
         this.Zg.translate(-f3, -f4, 0.0F);
         return this.Zg;
      }
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0 && this.atu != null) {
         this.QM.b(true);
         this.atu.run();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean c(double d0, double d1, int i) {
      if (i == 0) {
         this.QM.b(false);
         return true;
      } else {
         return false;
      }
   }
}
