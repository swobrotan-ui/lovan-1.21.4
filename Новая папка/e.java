import a.Loader;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.util.HashMap;
import java.util.Map;

public class e {
   private boolean success;
   private String message;
   private String status;
   private Map<String, Object> data;
   private static String[] 6LJqjdNWUIWRnFRF = new String[7];

   public e(boolean flag, String s, String s1) {
      this.success = flag;
      this.message = s;
      this.status = s1;
      this.data = new HashMap<String, Object>();
   }

   public e(boolean flag, String s, String s1, Map<String, Object> map) {
      this.success = flag;
      this.message = s;
      this.status = s1;
      this.data = (Map<String, Object>)(map != null ? map : new HashMap<String, Object>());
   }

   public native boolean a();

   public native boolean b();

   public native Object c(String s);

   public native boolean d(String s);

   @Override
   public native String toString();

   public native boolean e();

   public native String f();

   public native String g();

   public native Map<String, Object> h();

   private static native CallSite i(Lookup lookup, String s, MethodType methodtype, String s1);

   static {
      Loader.init(e.class);
      k();
   }

   private static native String j(char[] achar, long i, int j);

   private static native void k();

   public static native void guard();
}
