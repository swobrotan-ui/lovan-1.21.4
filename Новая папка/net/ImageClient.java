package net;

import a.Loader;
import core.ApiClient;
import core.AuthConfig;
import java.util.Map;

public class ImageClient extends ApiClient {
   private static String[] wN8z9JYRY0nZpiuf = new String[4];

   public ImageClient() {
      super(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native Map<String, byte[]> a(String[] astring);

   private native Map<String, byte[]> b(String s);

   static {
      Loader.init(ImageClient.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
