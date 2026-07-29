package data;

import java.util.Objects;
import net.minecraft.util.math.Vec3d;

final class RenderData {
   private final jm type;
   private final Vec3d center;
   private final float radius;
   private final float size;
   private final float yawDeg;
   private final float pitchDeg;
   private final boolean playersInRadius;

   private RenderData(jm jm, Vec3d vec3d, float f, float f1, float f2, float f3, boolean flag) {
      this.type = jm;
      this.center = vec3d;
      this.radius = f;
      this.size = f1;
      this.yawDeg = f2;
      this.pitchDeg = f3;
      this.playersInRadius = flag;
   }

   public jm getType() {
      return this.type;
   }

   public Vec3d getCenter() {
      return this.center;
   }

   public float getRadius() {
      return this.radius;
   }

   public float getSize() {
      return this.size;
   }

   public float getYawDeg() {
      return this.yawDeg;
   }

   public float getPitchDeg() {
      return this.pitchDeg;
   }

   public boolean hasPlayersInRadius() {
      return this.playersInRadius;
   }

   @Override
   public boolean equals(Object object) {
      if (object == this) {
         return true;
      } else if (object != null && object.getClass() == this.getClass()) {
         RenderData renderdata1 = (RenderData)object;
         return Objects.equals(this.type, renderdata1.type)
            && Objects.equals(this.center, renderdata1.center)
            && Float.floatToIntBits(this.radius) == Float.floatToIntBits(renderdata1.radius)
            && Float.floatToIntBits(this.size) == Float.floatToIntBits(renderdata1.size)
            && Float.floatToIntBits(this.yawDeg) == Float.floatToIntBits(renderdata1.yawDeg)
            && Float.floatToIntBits(this.pitchDeg) == Float.floatToIntBits(renderdata1.pitchDeg)
            && this.playersInRadius == renderdata1.playersInRadius;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.center, this.radius, this.size, this.yawDeg, this.pitchDeg, this.playersInRadius);
   }

   @Override
   public String toString() {
      String s2 = String.valueOf(this.type);
      String s3 = String.valueOf(this.center);
      boolean flag = this.playersInRadius;
      float f = this.pitchDeg;
      float f1 = this.yawDeg;
      float f2 = this.size;
      float f3 = this.radius;
      String s = s3;
      String s1 = s2;
      return "RenderData[type="
         + s1
         + ", center="
         + s
         + ", radius="
         + f3
         + ", size="
         + f2
         + ", yawDeg="
         + f1
         + ", pitchDeg="
         + f
         + ", playersInRadius="
         + flag
         + "]";
   }
}
