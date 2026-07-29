import a.Loader;
import core.ApiClient;
import core.AuthConfig;
import java.util.List;

public class gm extends ApiClient {
   private static String[] zs6JS4CdWsooThlX = new String[4];

   public gm() {
      super(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native List<wi> a();

   private native List<wi> b(String s);

   static {
      Loader.init(gm.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
