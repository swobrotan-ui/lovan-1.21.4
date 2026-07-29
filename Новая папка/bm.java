import a.Loader;
import core.ApiClient;
import java.util.Map;

public class bm extends ApiClient {
   private static String[] Nfa1r6o2rpxWsDmn = new String[7];

   public bm(String s, int i) {
      super(s, i);
   }

   public native Map<String, String> a(String s);

   // $VF: synthetic method
   private static native Map b(String s);

   static {
      Loader.init(bm.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
