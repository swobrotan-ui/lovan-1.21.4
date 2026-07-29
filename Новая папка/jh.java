import config.Config;
import core.ClientMain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import module.Module;

public class jh {
   public static final String KE = "Favourites";
   private final Set<String> tC = new HashSet<String>();
   private Runnable cQ;

   public jh() {
      this.a();
   }

   private void a() {
      Config config = ClientMain.getInstance().getConfigManager().x();
      if (config != null && config.j() != null) {
         this.tC.addAll(config.j());
      }
   }

   private void b() {
      Config config = ClientMain.getInstance().getConfigManager().x();
      if (config != null) {
         config.w(new ArrayList<String>(this.tC));
         ClientMain.getInstance().getConfigSyncManager().d(config);
      }
   }

   public void c(String s) {
      if (this.tC.contains(s)) {
         this.tC.remove(s);
      } else {
         this.tC.add(s);
      }

      this.b();
      if (this.cQ != null) {
         this.cQ.run();
      }
   }

   public boolean d(String s) {
      return this.tC.contains(s);
   }

   public boolean e() {
      return this.tC.isEmpty();
   }

   public List<Module> f(List<Module> list) {
      ArrayList arraylist = new ArrayList();

      for (Module module : list) {
         if (this.tC.contains(module.getName())) {
            arraylist.add(module);
         }
      }

      return arraylist;
   }

   public void g(Runnable runnable) {
      this.cQ = runnable;
   }
}
