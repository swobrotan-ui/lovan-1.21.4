import core.ClientMain;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class hbk {
   private static hbk uo;
   private final List<g> bv = new CopyOnWriteArrayList<g>();

   private hbk() {
   }

   public static hbk a() {
      if (uo == null) {
         uo = new hbk();
      }

      return uo;
   }

   public void b(g g) {
      if (g != null) {
         this.bv.add(g);
         this.i();
      }
   }

   public void c(String s) {
      this.bv.removeIf(g -> {
         return g.e().equalsIgnoreCase(s);
      });
      this.i();
   }

   public void d(g g) {
      this.bv.remove(g);
      this.i();
   }

   public g e(String s) {
      return this.bv.stream().filter(g -> {
         return g.e().equalsIgnoreCase(s);
      }).findFirst().orElse(null);
   }

   public List<g> f(String s) {
      return this.bv.stream().filter(g -> {
         return g.g().equals(s);
      }).toList();
   }

   public void g() {
      this.bv.clear();
      this.i();
   }

   public boolean h(String s) {
      return this.bv.stream().anyMatch(g -> {
         return g.e().equalsIgnoreCase(s);
      });
   }

   private void i() {
      try {
         ClientMain clientmain = ClientMain.getInstance();
         if (clientmain != null && clientmain.getConfigSyncManager() != null && clientmain.getConfigManager() != null) {
            if (ClientMain.getInstance().getConfigSyncManager().k()) {
               return;
            }

            clientmain.getConfigSyncManager().d(clientmain.getConfigManager().x());
         }
      } catch (Exception exception) {
      }
   }

   public List<g> j() {
      return this.bv;
   }
}
