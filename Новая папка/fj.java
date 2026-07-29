import java.util.ArrayList;
import java.util.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class fj {
   MinecraftClient yD = MinecraftClient.getInstance();

   public ArrayList<Vec3d> a(Entity entity, int i, int j) {
      double d0 = entity.getHeight();
      double d1 = entity.getWidth();
      double d2 = d0 / (j + 1);
      double d3 = d1 / (i + 1);
      ArrayList arraylist = new ArrayList();

      for (int k = 0; k <= j + 1; k++) {
         double d4 = d2 * k;

         for (int l = 0; l <= i; l++) {
            double d5 = d1 / 2.0 - d3 * l;
            arraylist.add(entity.getPos().add(d1 / 2.0, d4, d5));
            arraylist.add(entity.getPos().add(-d5, d4, d1 / 2.0));
            arraylist.add(entity.getPos().add(-d1 / 2.0, d4, -d5));
            arraylist.add(entity.getPos().add(d5, d4, -d1 / 2.0));
         }
      }

      for (int i1 = 1; i1 <= i; i1++) {
         double d6 = d1 / 2.0 - d3 * i1;

         for (int j1 = 1; j1 <= i; j1++) {
            double d7 = d1 / 2.0 - d3 * j1;
            arraylist.add(entity.getPos().add(d6, 0.0, d7));
            arraylist.add(entity.getPos().add(d6, d0, d7));
         }
      }

      return arraylist;
   }

   public Vec3d b(Entity entity, int i, int j, double d0) {
      assert this.yD.player != null;

      return this.a(entity, i, j).parallelStream().filter(vec3d -> {
         return this.yD.player.getEyePos().squaredDistanceTo(vec3d) <= MathHelper.square(d0);
      }).min(Comparator.comparingDouble(this.yD.player.getEyePos()::squaredDistanceTo)).orElse(Vec3d.ZERO);
   }
}
