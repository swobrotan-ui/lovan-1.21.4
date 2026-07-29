import a.Loader;
import java.nio.charset.StandardCharsets;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class xih {
   private static String ALGORITHM = xih.eUrDut4TMnaMUyye[3];
   private static String KEY = xih.eUrDut4TMnaMUyye[0];
   private static String IV = xih.eUrDut4TMnaMUyye[2];
   private static final SecretKeySpec KEY_SPEC = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), xih.eUrDut4TMnaMUyye[1]);
   private static final IvParameterSpec IV_SPEC = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
   private static String[] eUrDut4TMnaMUyye = new String[4];

   public static native String a(String s);

   public static native String b(String s);

   static {
      Loader.init(xih.class);
      d();
   }

   private static native String c(char[] achar, long i, int j);

   private static native void d();

   public static native void guard();
}
