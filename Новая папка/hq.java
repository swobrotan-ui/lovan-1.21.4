import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class hq {
   private static String[] avg = new String[10];

   public static native void a();

   private static native boolean b();

   private static native boolean c();

   private static native boolean d();

   private static native boolean e();

   private static native String f(String s);

   private static native CallSite g(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite h(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite i(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(hq.class);
      k();
   }

   private static native String j(char[] achar, long i, int j);

   private static native void k();

   public static native void guard();
}
