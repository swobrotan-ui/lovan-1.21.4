package core;

import a.Loader;
import java.util.concurrent.ScheduledExecutorService;

public class OnlineManager {
   private final ClientMain main = ClientMain.getInstance();
   private final ScheduledExecutorService executor;
   private static String[] itt9256QV9aFdBuT = new String[1];

   public OnlineManager(ScheduledExecutorService scheduledexecutorservice) {
      this.executor = scheduledexecutorservice;
      Thread thread = Thread.ofVirtual().name(itt9256QV9aFdBuT[0]).unstarted(this::c);
      Runtime.getRuntime().addShutdownHook(thread);
      this.a();
   }

   public native void a();

   private native void b();

   private native void c();

   // $VF: synthetic method
   private native void d();

   static {
      Loader.init(OnlineManager.class);
      f();
   }

   private static native String e(char[] achar, long i, int j);

   private static native void f();

   public static native void guard();
}
