package event;

public class ChatMessageEvent {
   private final String message;
   private boolean cancelled;

   public ChatMessageEvent(String s) {
      this.message = s;
      this.cancelled = false;
   }

   public void cancel() {
      this.cancelled = true;
   }

   public String getMessage() {
      return this.message;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean flag) {
      this.cancelled = flag;
   }
}
