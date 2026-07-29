import a.Loader;
import java.util.Map;

class ntz {
   boolean success;
   String message;
   String configData;
   Map<String, Object> data;

   private ntz() {
   }

   native boolean a();

   native String b();

   native Map<String, Object> c();

   static {
      Loader.init(ntz.class);
   }

   public static native void guard();
}
