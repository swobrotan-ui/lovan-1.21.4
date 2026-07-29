import java.util.ArrayList;
import java.util.List;

public class vo {
   private final String HK;
   private final String aoN;
   private final List<String> RY;

   public vo(String s, String s1, List<String> list) {
      this.HK = s;
      this.aoN = s1;
      this.RY = (List<String>)(list != null ? list : new ArrayList<String>());
   }

   public String a() {
      return this.HK;
   }

   public String b() {
      return this.aoN;
   }

   public List<String> c() {
      return this.RY;
   }
}
