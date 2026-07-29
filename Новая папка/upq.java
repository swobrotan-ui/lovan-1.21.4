import org.joml.Matrix4f;

public class upq {
   private static final Matrix4f db = new Matrix4f();
   private static final Matrix4f acX = new Matrix4f();
   private static final Matrix4f alO = new Matrix4f();

   public static Matrix4f a(Matrix4f matrix4f, float f, float f1, float f2, float f3, float f4) {
      if (f4 == 1.0F) {
         return matrix4f;
      } else {
         float f5 = f + f2 / 2.0F;
         float f6 = f1 + f3 / 2.0F;
         db.set(matrix4f);
         db.translate(f5, f6, 0.0F);
         db.scale(f4, f4, 1.0F);
         db.translate(-f5, -f6, 0.0F);
         return db;
      }
   }

   public static Matrix4f b(Matrix4f matrix4f, float f, float f1, float f2) {
      if (f2 == 1.0F) {
         return matrix4f;
      } else {
         float f3 = f + 143.0F;
         float f4 = f1 + 20.0F;
         acX.set(matrix4f);
         acX.translate(f3, f4, 0.0F);
         acX.scale(f2, f2, 1.0F);
         acX.translate(-f3, -f4, 0.0F);
         return acX;
      }
   }

   public static Matrix4f c(Matrix4f matrix4f, float f, float f1, float f2, float f3) {
      if (f3 == 1.0F) {
         return matrix4f;
      } else {
         float f4 = f + 143.0F;
         float f5 = f1 + f2 / 2.0F;
         alO.set(matrix4f);
         alO.translate(f4, f5, 0.0F);
         alO.scale(f3, f3, 1.0F);
         alO.translate(-f4, -f5, 0.0F);
         return alO;
      }
   }
}
