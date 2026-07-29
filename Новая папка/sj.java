import a.Loader;
import com.google.gson.annotations.SerializedName;

public class sj {
   @SerializedName("название")
   private String name;
   @SerializedName("максимальная_цена")
   private double maxPricePerItem;
   @SerializedName("частичное_совпадение")
   private boolean partialMatch;

   public sj() {
      this.partialMatch = false;
   }

   public sj(String s, double d0, boolean flag) {
      this.name = s;
      this.maxPricePerItem = d0;
      this.partialMatch = flag;
   }

   public native String a();

   public native double b();

   public native boolean c();

   public native void d(String s);

   public native void e(double d0);

   public native void f(boolean flag);

   static {
      Loader.init(sj.class);
   }

   public static native void guard();
}
