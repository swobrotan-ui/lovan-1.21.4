import a.Loader;
import core.ApiClient;

public class rhu extends ApiClient {
   private static String[] gidwc7OZOVgLiI5y = new String[10];

   public rhu(String s, int i) {
      super(s, i);
   }

   public native e a();

   public native void b(String s, String s1, String s2, String s3);

   public native void c(String s, String s1, String s2, String s3);

   private native e d(String s);

   static {
      Loader.init(rhu.class);
      g();
   }

   private static native String f(char[] achar, long i, int j);

   private static native void g();

   public static native void guard();
}
