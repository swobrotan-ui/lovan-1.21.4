import a.Loader;
import config.Config;
import core.ApiClient;
import java.util.List;

public class k extends ApiClient {
   private static String[] SCleLQduQ2Anb6De = new String[13];

   public k(String s, int i) {
      super(s, i);
   }

   public native String a(String s, String s1, Config config);

   public native boolean b();

   public native boolean c(String s, String s1, Config config);

   public native Config d(String s);

   public native List<so> e();

   public native boolean f(String s);

   public native boolean g(String s);

   public native String h();

   public native String i(String s);

   public native String j(String s, String s1);

   private native boolean k(String s);

   private native String l(String s);

   private native String m(String s);

   private native Config n(String s);

   private native List<so> o(String s);

   private native String p(String s);

   static {
      Loader.init(k.class);
      r();
   }

   private static native String q(char[] achar, long i, int j);

   private static native void r();

   public static native void guard();
}
