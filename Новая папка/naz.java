import a.Loader;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class naz {
   @SerializedName("предметы")
   private List<sj> items;

   public naz() {
      this.items = new ArrayList<sj>();
   }

   public naz(List<sj> list) {
      this.items = new ArrayList<sj>(list);
   }

   public native List<sj> a();

   public native void b(List<sj> list);

   static {
      Loader.init(naz.class);
   }

   public static native void guard();
}
