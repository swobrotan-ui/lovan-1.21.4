import a.Loader;
import com.google.gson.annotations.SerializedName;

public class wi {
   @SerializedName("name")
   private String name;
   @SerializedName("jsonData")
   private String jsonData;

   public native byte[] a();

   public native String b();

   public native String c();

   static {
      Loader.init(wi.class);
   }

   public static native void guard();
}
