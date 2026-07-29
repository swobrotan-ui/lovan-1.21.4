package shader;

import a.Loader;
import core.ApiClient;
import core.AuthConfig;
import java.util.List;

public class ShaderDataFetcher extends ApiClient {
   private static String[] eMWOlxWqqpdWDjp4 = new String[4];

   public ShaderDataFetcher() {
      super(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native List<ShaderData> a();

   private native List<ShaderData> b(String s);

   static {
      Loader.init(ShaderDataFetcher.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
