import a.Loader;
import core.ApiClient;
import core.AuthConfig;
import java.util.Map;

public class gg extends ApiClient {
   private static String[] DRF6beijJD9fBsYR = new String[4];

   public gg() {
      super(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native Map<String, byte[]> a();

   private native Map<String, byte[]> b(String s);

   static {
      Loader.init(gg.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
