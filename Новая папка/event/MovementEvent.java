package event;

import data.MutableVec3d;
import net.minecraft.util.math.Vec3d;

public class MovementEvent {
   private final MutableVec3d vector;

   public MovementEvent(Vec3d vec3d) {
      this.vector = new MutableVec3d(vec3d.x, vec3d.y, vec3d.z);
   }

   public Vec3d getVelocity() {
      return new Vec3d(this.vector.x, this.vector.y, this.vector.z);
   }

   public MutableVec3d getMutableVector() {
      return this.vector;
   }
}
