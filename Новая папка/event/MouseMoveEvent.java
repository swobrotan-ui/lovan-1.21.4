package event;

public class MouseMoveEvent {
   private final double xOffset;
   private final double yOffset;
   private boolean cancelled;

   public MouseMoveEvent(double d0, double d1) {
      this.xOffset = d0;
      this.yOffset = d1;
      this.cancelled = false;
   }

   public int getDirection() {
      if (this.yOffset > 0.0) {
         return 1;
      } else {
         return this.yOffset < 0.0 ? -1 : 0;
      }
   }

   public double getXOffset() {
      return this.xOffset;
   }

   public double getYOffset() {
      return this.yOffset;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
