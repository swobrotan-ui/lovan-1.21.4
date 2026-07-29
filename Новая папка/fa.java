import a.Loader;

public class fa {
   private static String[] dz = new String[1];

   public static native void a(String s);

   static {
      Loader.init(fa.class);
      c();
   }

   private static native String b(char[] achar, long i, int j);

   private static native void c();

   public static native void guard();
}
