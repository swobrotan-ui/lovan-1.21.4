import a.Loader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class nf {
   private static List<String> Wb = List.<String>of(nf.qL[1], nf.qL[2], nf.qL[0]);
   private static volatile boolean LK = false;
   private static final Map<String, Integer> bI = new ConcurrentHashMap<String, Integer>();
   private static String[] qL = new String[7];

   public static native void a();

   private static native void b();

   private static native void c(String s, int i);

   private static native int d(String s);

   static {
      Loader.init(nf.class);
      f();
   }

   private static native String e(char[] achar, long i, int j);

   private static native void f();

   public static native void guard();
}
