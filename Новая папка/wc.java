import a.Loader;
import java.io.File;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class wc {
   private static String[] sC = new String[12];

   public static native void a();

   private static native boolean b(File file1, int i);

   private static native CallSite c(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite d(Lookup lookup, String s, MethodType methodtype, String s1);

   private static native CallSite e(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(wc.class);
      g();
   }

   private static native String f(char[] achar, long i, int j);

   private static native void g();

   public static native void guard();
}
