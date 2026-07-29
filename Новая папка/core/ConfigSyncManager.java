package core;

import a.Loader;
import com.google.gson.Gson;
import config.Config;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ConfigSyncManager {
   private final Gson gson = new Gson();
   private final ScheduledExecutorService executor;
   private final k configClient;
   private final ClientMain main = ClientMain.getInstance();
   private ScheduledFuture<?> debouncedTask;
   private String lastSavedHash = "";
   private volatile boolean dirty = false;
   private volatile boolean locked = false;
   private long lastSaveTimestamp = System.currentTimeMillis();
   private static final long DEBOUNCE_SECONDS = 30L;
   private static final long PERIODIC_CHECK_MINUTES = 5L;
   private static final long MAX_DIRTY_MILLIS = 300000L;
   private static String[] UBvoyX7A4BwqQtqf = new String[2];

   public ConfigSyncManager(ScheduledExecutorService scheduledexecutorservice) {
      this.executor = scheduledexecutorservice;
      this.configClient = new k(AuthConfig.getHost(), AuthConfig.getPort());
      this.c();
   }

   public native void a();

   public native void b();

   private native void c();

   public native void d(Config config);

   private native void e();

   private native void f();

   public native void g();

   private native String h(ConfigManager configmanager);

   private native boolean i();

   private native String j(Config config);

   public native boolean k();

   // $VF: synthetic method
   private static native boolean l(String s, so so);

   // $VF: synthetic method
   private native void m();

   static {
      Loader.init(ConfigSyncManager.class);
      o();
   }

   private static native String n(char[] achar, long i, int j);

   private static native void o();

   public static native void guard();
}
