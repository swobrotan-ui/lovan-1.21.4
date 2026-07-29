import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;

public class ago {
   private static String[] Ao = new String[12];

   public static native boolean a();

   private static native String b();

   private static native String c();

   private static native String d(int i, int j, char c0);

   private static native char e();

   private static native String f(String s);

   private static native String g(String s);

   private static native CallSite h(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(ago.class);
      j();
   }

   private static native String i(char[] achar, long i, int j);

   private static native void j();

   public static native void guard();
}
