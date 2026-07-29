import module.ParticleModule;
import net.minecraft.util.math.Vec3d;

public final class mvy {
   public static void a(ParticleModule particlemodule, Vec3d vec3d, int i, float f, float f1) {
      for (int j = 0; j < i; j++) {
         double d0 = Math.random() * Math.PI * 2.0;
         double d1 = (Math.random() - 0.3) * Math.PI * 0.5;
         double d2 = f * (0.5 + Math.random() * 0.5);
         double d3 = f * (0.3 + Math.random() * 0.7);
         double d4 = Math.cos(d0) * Math.cos(d1) * d2;
         double d5 = Math.sin(d0) * Math.cos(d1) * d2;
         double d6 = Math.sin(d1) * d3 + f * 0.3;
         float f2 = (float)j / i;
         float f3 = 0.8F + (float)Math.random() * 0.4F;
         double d7 = (Math.random() - 0.5) * 0.3;
         double d8 = (Math.random() - 0.5) * 0.3;
         double d9 = (Math.random() - 0.5) * 0.3;
         particlemodule.l(vec3d.x + d7, vec3d.y + d8, vec3d.z + d9, d4, d6, d5, f2, f3, f1);
      }
   }

   public static void b(ParticleModule particlemodule, Vec3d vec3d, int i, float f, float f1, float f2) {
      for (int j = 0; j < i; j++) {
         double d0 = Math.random() * Math.PI * 2.0;
         double d1 = Math.random() * f;
         double d2 = Math.cos(d0) * d1 * 0.3;
         double d3 = Math.sin(d0) * d1 * 0.3;
         double d4 = (Math.random() - 0.3) * f * 0.2;
         float f3 = (float)j / i;
         float f4 = 0.7F + (float)Math.random() * 0.6F;
         double d5 = (Math.random() - 0.5) * 0.2;
         double d6 = f2 > 0.0F ? Math.random() * 0.3 + f2 : (Math.random() - 0.5) * 0.1;
         double d7 = (Math.random() - 0.5) * 0.2;
         particlemodule.l(vec3d.x + d5, vec3d.y + d6, vec3d.z + d7, d2, d4, d3, f3, f4, f1);
      }
   }

   public static void c(ParticleModule particlemodule, Vec3d vec3d, int i, float f, float f1) {
      float f2 = f * 1.5F;

      for (int j = 0; j < i; j++) {
         double d0 = (double)j / i * Math.PI * 2.0;
         double d1 = f + (Math.random() - 0.5) * f2;
         double d2 = Math.cos(d0) * d1;
         double d3 = Math.sin(d0) * d1;
         float f3 = (float)j / i;
         float f4 = 0.9F + (float)Math.random() * 0.2F;
         particlemodule.m(vec3d.x, vec3d.y + 0.1, vec3d.z, d2, 0.0, d3, f3, f4, 0.0F, f1);
      }
   }
}
