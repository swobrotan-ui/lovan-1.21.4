package event;

public class RotationEvent {
   private float pitch;
   private float yaw;
   private final float staticPitch;
   private final float staticYaw;
   private boolean strictMoveCorrection;

   public RotationEvent(float f, float f1, boolean flag) {
      this.pitch = f;
      this.yaw = f1;
      this.staticPitch = f;
      this.staticYaw = f1;
      this.strictMoveCorrection = flag;
   }

   public boolean isModified() {
      return this.pitch != this.staticPitch || this.yaw != this.staticYaw;
   }

   public void setPitch(float f) {
      this.pitch = f;
   }

   public float getPitch() {
      return this.pitch;
   }

   public void setYaw(float f) {
      this.yaw = f;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getStaticPitch() {
      return this.staticPitch;
   }

   public float getStaticYaw() {
      return this.staticYaw;
   }

   public void setStrictMoveCorrection(boolean flag) {
      this.strictMoveCorrection = flag;
   }

   public boolean isStrictMoveCorrection() {
      return this.strictMoveCorrection;
   }
}
