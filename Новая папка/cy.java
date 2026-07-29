import a.Loader;

public class cy {
   private String action;
   private String soundName;
   private String hwid;

   public native String a();

   public native String b();

   public native String c();

   public native void d(String s);

   public native void e(String s);

   public native void f(String s);

   static {
      Loader.init(cy.class);
   }

   public static native void guard();
}
