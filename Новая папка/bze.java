import core.FriendManager;

public class bze {
   public static final String ju = "Friends";
   private Runnable Ky;

   public boolean a() {
      return FriendManager.getInstance().getFriends().isEmpty();
   }

   public void b() {
      if (this.Ky != null) {
         this.Ky.run();
      }
   }

   public void c(Runnable runnable) {
      this.Ky = runnable;
   }
}
