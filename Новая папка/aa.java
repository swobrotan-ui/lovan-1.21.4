import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class aa {
   private static String[] mp = new String[4];

   public static native void a();

   private static native CallSite b(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite c(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite d(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(aa.class);
      f();
   }

   private static native String e(char[] achar, long i, int j);

   private static native void f();

   public static native void guard();
}
