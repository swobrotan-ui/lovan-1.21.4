import a.Loader;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import shader.ShaderData;
import shader.ShaderDataFetcher;

class qb extends TypeToken<List<ShaderData>> {
   qb(ShaderDataFetcher shaderdatafetcher) {
   }

   static {
      Loader.init(qb.class);
   }

   public static native void guard();
}
