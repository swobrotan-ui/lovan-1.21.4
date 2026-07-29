import a.Loader;

public class vr {
   private String action;
   private String pcName;
   private String hwid;
   private String ip;
   private String lastMinecraftName;
   private String imageName;
   private String[] imageNames;

   public native String a();

   public native String b();

   public native String c();

   public native String d();

   public native String e();

   public native String f();

   public native String[] g();

   public native void h(String s);

   public native void i(String s);

   public native void j(String s);

   public native void k(String s);

   public native void l(String s);

   public native void m(String s);

   public native void n(String[] astring);

   static {
      Loader.init(vr.class);
   }

   public static native void guard();
}
