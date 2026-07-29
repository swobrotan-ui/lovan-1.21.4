package core;

import a.Loader;
import com.google.gson.Gson;
import java.util.function.Function;

public abstract class ApiClient {
   protected String host;
   protected int port;
   protected Gson gson = new Gson();
   private static String[] 4dSoclJj4mePJqzv = new String[1];

   protected ApiClient(String s, int i) {
      this.host = s;
      this.port = i;
   }

   protected native <T> T a(Object object, Function<String, T> function, T t);

   protected native <T> T b(Object object, Function<String, T> function, T t);

   protected native void c(Object object);

   protected native void d(Exception exception);

   static {
      Loader.init(ApiClient.class);
      f();
   }

   private static native String e(char[] achar, long i, int j);

   private static native void f();

   public static native void guard();
}
