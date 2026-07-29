import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import render.BuiltLine3d;

public class xzs {
   private final BuiltLine3d aqc;
   private final rh JA = new rh();

   private xzs(BuiltLine3d builtline3d) {
      this.aqc = builtline3d;
   }

   public void a(Vec3d vec3d, float f, float f1) {
      float f2 = f / 2.0F;
      this.JA.a(vec3d.add(-f2, 0.0, -f2), vec3d.add(-f2, 0.0, f2));
      this.JA.a(vec3d.add(-f2, 0.0, f2), vec3d.add(f2, 0.0, f2));
      this.JA.a(vec3d.add(f2, 0.0, f2), vec3d.add(f2, 0.0, -f2));
      this.JA.a(vec3d.add(f2, 0.0, -f2), vec3d.add(-f2, 0.0, -f2));
      this.JA.a(vec3d.add(-f2, f1, -f2), vec3d.add(-f2, f1, f2));
      this.JA.a(vec3d.add(-f2, f1, f2), vec3d.add(f2, f1, f2));
      this.JA.a(vec3d.add(f2, f1, f2), vec3d.add(f2, f1, -f2));
      this.JA.a(vec3d.add(f2, f1, -f2), vec3d.add(-f2, f1, -f2));
      this.JA.a(vec3d.add(-f2, 0.0, -f2), vec3d.add(-f2, f1, -f2));
      this.JA.a(vec3d.add(-f2, 0.0, f2), vec3d.add(-f2, f1, f2));
      this.JA.a(vec3d.add(f2, 0.0, f2), vec3d.add(f2, f1, f2));
      this.JA.a(vec3d.add(f2, 0.0, -f2), vec3d.add(f2, f1, -f2));
   }

   public void b(Vec3d vec3d, Vec3d vec3d1) {
      this.JA.a(vec3d, vec3d1);
   }

   public void c(Vec3d vec3d, Vec3d vec3d1) {
      this.JA.a(new Vec3d(vec3d.x, vec3d.y, vec3d.z), new Vec3d(vec3d1.x, vec3d.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d.y, vec3d.z), new Vec3d(vec3d1.x, vec3d.y, vec3d1.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d.y, vec3d1.z), new Vec3d(vec3d.x, vec3d.y, vec3d1.z));
      this.JA.a(new Vec3d(vec3d.x, vec3d.y, vec3d1.z), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d.x, vec3d1.y, vec3d.z), new Vec3d(vec3d1.x, vec3d1.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d1.y, vec3d.z), new Vec3d(vec3d1.x, vec3d1.y, vec3d1.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d1.y, vec3d1.z), new Vec3d(vec3d.x, vec3d1.y, vec3d1.z));
      this.JA.a(new Vec3d(vec3d.x, vec3d1.y, vec3d1.z), new Vec3d(vec3d.x, vec3d1.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d.x, vec3d.y, vec3d.z), new Vec3d(vec3d.x, vec3d1.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d.y, vec3d.z), new Vec3d(vec3d1.x, vec3d1.y, vec3d.z));
      this.JA.a(new Vec3d(vec3d1.x, vec3d.y, vec3d1.z), new Vec3d(vec3d1.x, vec3d1.y, vec3d1.z));
      this.JA.a(new Vec3d(vec3d.x, vec3d.y, vec3d1.z), new Vec3d(vec3d.x, vec3d1.y, vec3d1.z));
   }

   public void d(MatrixStack matrixstack, Vec3d vec3d) {
      this.aqc.c(matrixstack, vec3d, this.JA);
   }
}
