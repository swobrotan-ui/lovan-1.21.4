import data.ABItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import module.ABRaidmineModule;

public class ox {
   private final ABRaidmineModule axi;
   private final Map<String, pm> arL;
   private String abT;

   public ox(ABRaidmineModule abraidminemodule) {
      this.axi = abraidminemodule;
      this.arL = new HashMap<String, pm>();
      this.abT = null;
   }

   public void a(String s) {
      pm pmx = this.axi.q();
      if (pmx != null && pmx.c() != null) {
         pm pmx = new pm();
         ArrayList arraylist = new ArrayList();

         for (ABItem abitem : pmx.c()) {
            arraylist.add(new ABItem(abitem.getName(), abitem.getMaxPrice(), abitem.isPartial()));
         }

         pmx.b(arraylist);
         this.arL.put(s, pmx);
      }
   }

   public void b(String s) {
      pm pmx = this.arL.get(s);
      if (pmx != null) {
         pm pmx = new pm();
         ArrayList arraylist = new ArrayList();

         for (ABItem abitem : pmx.c()) {
            arraylist.add(new ABItem(abitem.getName(), abitem.getMaxPrice(), abitem.isPartial()));
         }

         pmx.b(arraylist);
         this.axi.r(pmx);
      }
   }

   public void c(String s) {
      this.arL.remove(s);
   }

   public boolean d(String s) {
      return this.arL.containsKey(s);
   }

   public pm e(String s) {
      return this.arL.get(s);
   }

   public List<String> f() {
      return new ArrayList<String>(this.arL.keySet());
   }

   public void g() {
      this.arL.clear();
      this.abT = null;
   }

   public Map<String, pm> h() {
      return new HashMap<String, pm>(this.arL);
   }

   public void i(Map<String, pm> map) {
      this.arL.clear();
      if (map != null) {
         this.arL.putAll(map);
      }
   }

   public String j() {
      return this.abT;
   }

   public void k(String s) {
      this.abT = s;
   }
}
