package data;

public class Angle {
   public static Angle ZERO = new Angle(0.0F, 0.0F);
   private float yaw;
   private float pitch;

   public Angle(float f, float f1) {
      this.yaw = f;
      this.pitch = f1;
   }

   @Override
   public String toString() {
      float f = this.pitch;
      float f1 = this.yaw;
      return "Angle(yaw=" + f1 + ", pitch=" + f + ")";
   }

   public void setYaw(float f) {
      this.yaw = f;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }
}
