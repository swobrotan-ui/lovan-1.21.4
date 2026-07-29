import a.Loader;
import com.google.gson.reflect.TypeToken;
import java.util.Map;

class ys extends TypeToken<Map<String, Boolean>> {
   static {
      Loader.init(ys.class);
   }

   public static native void guard();
}
