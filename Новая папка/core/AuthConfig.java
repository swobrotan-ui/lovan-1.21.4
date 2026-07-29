package core;

import a.Loader;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class AuthConfig {
   private static String host;
   private static int port;
   private static String pcName;
   private static String hwid;
   private static String ip;
   private static String mcName;
   private static String cachedHWID = null;
   private static String[] RKn3HBiXtxGyriV0 = new String[15];

   public static native void a() throws IOException;

   private static native String b();

   private static native String c();

   private static native String d() throws Exception;

   private static native String e();

   public static native String getHost();

   public static native int getPort();

   public static native String h();

   public static native String i();

   public static native String j();

   public static native String k();

   static {
      Loader.init(AuthConfig.class);
      n();
   }

   private static native CallSite l(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native String m(char[] achar, long i, int j);

   private static native void n();

   public static native void guard();
}
