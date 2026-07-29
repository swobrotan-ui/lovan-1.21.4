import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class egb {
   private static final String COMPRESSED_PREFIX = "GZ:";
   private static final int BUFFER_SIZE = 8192;
   private static String[] qnPdjSWgWZMQANlr = new String[1];

   public static native String a(String s);

   public static native String b(String s);

   public static native byte[] c(String s);

   public static native String d(byte[] abyte);

   private static native String e(byte[] abyte) throws Exception;

   private static native boolean f(byte[] abyte);

   private static native CallSite g(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(egb.class);
      i();
   }

   private static native String h(char[] achar, long i, int j);

   private static native void i();

   public static native void guard();
}
