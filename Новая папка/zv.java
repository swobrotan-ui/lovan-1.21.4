import core.SoundManager;
import enum.SoundType;
import org.joml.Matrix4f;
import render.BuiltText;
import render.TextCache;

public class zv extends ii {
   private boolean aiZ = false;

   public zv(float f, float f1, Runnable runnable) {
      super(f, f1, 30.0F, "W", 14.0F, 8.0F, 8.0F, runnable);
   }

   @Override
   protected void a(Matrix4f matrix4f, float f, float f1, int i, int j, float f3, float f2) {
      this.eV.a(this.cH);
      this.eV.f();
      Matrix4f matrix4f1 = this.b(matrix4f, f, f1, this.eV.d());
      this.LA.a(matrix4f1, f, f1, f2);
      String s = this.aiZ ? "X" : "W";
      BuiltText builttext = TextCache.a(this.aiL, s, 20.0F, Bz);
      builttext.a(matrix4f1, f + 3.6F, f1 + 3.0F, f2);
   }

   @Override
   protected boolean b(double d0, double d1, int i) {
      if (i == 0 && this.sY != null) {
         this.eV.b(true);
         if (!this.aiZ) {
            SoundManager.getInstance().c(SoundType.FAVOURITE_ADD);
         }

         this.sY.run();
         return true;
      } else {
         return false;
      }
   }

   public boolean a() {
      return this.aiZ;
   }

   public void b(boolean flag) {
      this.aiZ = flag;
   }
}
