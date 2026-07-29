package data;

import java.util.Objects;

class ProjectileParams {
   private final double speed;
   private final double gravity;
   private final double drag;

   private ProjectileParams(double d0, double d1, double d2) {
      this.speed = d0;
      this.gravity = d1;
      this.drag = d2;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         ProjectileParams projectileparams1 = (ProjectileParams)object;
         return Double.doubleToLongBits(this.speed) == Double.doubleToLongBits(projectileparams1.speed)
            && Double.doubleToLongBits(this.gravity) == Double.doubleToLongBits(projectileparams1.gravity)
            && Double.doubleToLongBits(this.drag) == Double.doubleToLongBits(projectileparams1.drag);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.speed, this.gravity, this.drag);
   }

   @Override
   public String toString() {
      double d0 = this.drag;
      double d1 = this.gravity;
      double d2 = this.speed;
      return "ProjectileParams[speed=" + d2 + ", gravity=" + d1 + ", drag=" + d0 + "]";
   }
}
