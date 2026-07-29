package event;

public class SpeedEvent {
   private float speedMultiplier = 1.0F;
   private boolean cancelled;

   public float getSpeedMultiplier() {
      return this.speedMultiplier;
   }

   public void setSpeedMultiplier(float f) {
      this.speedMultiplier = f;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
