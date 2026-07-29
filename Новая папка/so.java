import a.Loader;

public class so {
   private String configName;
   private String description;
   private String configKey;
   private boolean isActive;
   private String createdAt;
   private String updatedAt;

   public native String a();

   public native String b();

   public native String c();

   public native boolean d();

   public native String e();

   public native String f();

   public native void g(String s);

   public native void h(String s);

   public native void i(String s);

   public native void j(boolean flag);

   public native void k(String s);

   public native void l(String s);

   static {
      Loader.init(so.class);
   }

   public static native void guard();
}
