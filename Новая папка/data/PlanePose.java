package data;

import java.util.Objects;
import net.minecraft.util.math.Vec3d;

final class PlanePose {
   private final Vec3d auc;
   private final float yawDeg;
   private final float pitchDeg;

   private PlanePose(Vec3d vec3d, float f, float f1) {
      this.auc = vec3d;
      this.yawDeg = f;
      this.pitchDeg = f1;
   }

   public Vec3d getCenter() {
      return this.auc;
   }

   public float getYawDeg() {
      return this.yawDeg;
   }

   public float getPitchDeg() {
      return this.pitchDeg;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         PlanePose planepose1 = (PlanePose)object;
         return Objects.equals(this.auc, planepose1.auc)
            && Float.floatToIntBits(this.yawDeg) == Float.floatToIntBits(planepose1.yawDeg)
            && Float.floatToIntBits(this.pitchDeg) == Float.floatToIntBits(planepose1.pitchDeg);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.auc, this.yawDeg, this.pitchDeg);
   }

   @Override
   public String toString() {
      String s1 = String.valueOf(this.auc);
      float f = this.pitchDeg;
      float f1 = this.yawDeg;
      String s = s1;
      return "PlanePose[center=" + s + ", yawDeg=" + f1 + ", pitchDeg=" + f + "]";
   }
}
