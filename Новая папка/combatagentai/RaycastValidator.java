package combatagentai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class RaycastValidator {

   public boolean isVisible(Vec3d eyePos, Vec3d lookVec, Vec3d targetPos, World world) {
      RaycastContext context = new RaycastContext(
         eyePos,
         targetPos,
         RaycastContext.ShapeType.COLLIDER,
         RaycastContext.FluidHandling.NONE,
         Entity.NONE
      );

      return world.raycast(context).getType() == HitResult.Type.MISS;
   }

   public boolean isTargetValid(LivingEntity entity) {
      if (entity == null) return false;
      if (!entity.isAlive()) return false;
      if (entity.isDead()) return false;
      return true;
   }
}