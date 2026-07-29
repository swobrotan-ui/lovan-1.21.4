package shader;

import a.Loader;
import com.google.gson.annotations.SerializedName;

public class ShaderData {
   @SerializedName("name")
   private String name;
   @SerializedName("jsonData")
   private String jsonData;
   @SerializedName("fshData")
   private String fshData;
   @SerializedName("vshData")
   private String vshData;

   public native byte[] getJsonBytes();

   public native byte[] getFshBytes();

   public native byte[] getVshBytes();

   public native String getName();

   public native String e();

   public native String f();

   public native String g();

   static {
      Loader.init(ShaderData.class);
   }

   public static native void guard();
}
