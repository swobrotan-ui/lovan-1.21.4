import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class lrv {
   private static final long PH = 104857600L;
   private static String[] Zz = new String[9];

   public static native boolean a(String s);

   public static native char b(String s);

   public static native void c(String s);

   private static native CallSite d(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(lrv.class);
      f();
   }

   private static native String e(char[] achar, long i, int j);

   private static native void f();

   public static native void guard();
}
