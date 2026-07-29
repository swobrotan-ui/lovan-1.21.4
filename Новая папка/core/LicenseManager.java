package core;

import a.Loader;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LicenseManager {
   private static final ReadWriteLock lock = new ReentrantReadWriteLock();
   private static Set<Integer> allowedModuleIds = new HashSet<Integer>();
   private static String userGroup = LicenseManager.LgJcyqU7BAWXtgvv[1];
   private static Map<String, Boolean> permissions = new HashMap<String, Boolean>();
   private static final Gson gson = new Gson();
   private static String[] LgJcyqU7BAWXtgvv = new String[4];

   public static native Set<Integer> a();

   public static native boolean b();

   private static native void c(e e);

   private static native void d(e e);

   private static native void e(e e);

   public static native boolean f(String s);

   static {
      Loader.init(LicenseManager.class);
      h();
   }

   private static native String g(char[] achar, long i, int j);

   private static native void h();

   public static native void guard();
}
