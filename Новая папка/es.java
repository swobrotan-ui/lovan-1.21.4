import a.Loader;
import core.ApiClient;
import core.AuthConfig;
import java.io.File;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.Map;
import java.util.Random;

public class es extends ApiClient {
   private static char[] zW = es.awF[11].toCharArray();
   private static String[] aqj = new String[]{es.awF[12], es.awF[5], es.awF[7], es.awF[6], es.awF[4], es.awF[8]};
   private static Random ape = new Random();
   private static Map<String, String> Um = Map.<String, String>of(es.awF[10], es.awF[9]);
   private static String[] awF = new String[13];

   public es() {
      super(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native File a(String s);

   private native File b(String s);

   private static native String c();

   private static native String d();

   private native File e();

   static {
      Loader.init(es.class);
      h();
   }

   private static native CallSite f(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native String g(char[] achar, long i, int j);

   private static native void h();

   public static native void guard();
}
