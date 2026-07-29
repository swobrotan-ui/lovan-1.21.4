import core.ClientMain;
import core.ConfigManager;

public class mu {
   public static final String ID = "Configs";
   private final ConfigManager atc = ClientMain.getInstance().getConfigManager();
   private Runnable Wu;

   public boolean a() {
      return this.atc.y().isEmpty();
   }

   public void b() {
      this.atc.p();
      if (this.Wu != null) {
         this.Wu.run();
      }
   }

   public void c() {
      if (this.Wu != null) {
         this.Wu.run();
      }
   }

   public void d(Runnable runnable) {
      this.Wu = runnable;
   }
}
