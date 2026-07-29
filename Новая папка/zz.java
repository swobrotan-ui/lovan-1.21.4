import a.Loader;

public class zz {
   private String action;
   private String hwid;
   private String pcName;
   private String ip;
   private String lastMinecraftName;
   private static String[] GVoNJjHR1CYpAq20 = new String[1];

   public zz(String s, String s1, String s2, String s3) {
      this.action = GVoNJjHR1CYpAq20[0];
      this.hwid = s;
      this.pcName = s1;
      this.ip = s2;
      this.lastMinecraftName = s3;
   }

   public native String a();

   public native String b();

   public native String c();

   public native String d();

   public native String e();

   public native void f(String s);

   public native void g(String s);

   public native void h(String s);

   public native void i(String s);

   public native void j(String s);

   static {
      Loader.init(zz.class);
      l();
   }

   private static native String k(char[] achar, long i, int j);

   private static native void l();

   public static native void guard();
}
