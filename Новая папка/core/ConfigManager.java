package core;

import a.Loader;
import config.Config;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
   private ModuleManager moduleManager;
   private k configClient;
   private Config currentConfig;
   private List<so> availableConfigs = new ArrayList<so>();
   private String activeConfigName;
   private static String[] jg7WxrrVqT2NbtdR = new String[3];

   public ConfigManager(ModuleManager modulemanager) {
      this.moduleManager = modulemanager;
      this.currentConfig = new Config();
      this.currentConfig.a();
      this.configClient = new k(AuthConfig.getHost(), AuthConfig.getPort());
   }

   public native Config a();

   private native void b(Config config, Config config1);

   public native void c(Config config);

   private native void d(Config config);

   private native void e(Config config);

   private native void f(Config config);

   private native void g(Config config);

   public native boolean h(String s, String s1);

   public native boolean i(String s, String s1);

   public native boolean j(String s);

   public native void k(String s);

   public native boolean l();

   public native void m(String s);

   public native String n(String s);

   public native String o(String s, String s1);

   public native void p();

   public native boolean q();

   public native boolean r();

   public native void s();

   public native void t();

   private native void u(Config config);

   private native void v(Config config);

   public native void w(Config config);

   public native Config x();

   public native List<so> y();

   public native String z();

   public native void A(String s);

   static {
      Loader.init(ConfigManager.class);
      C();
   }

   private static native String B(char[] achar, long i, int j);

   private static native void C();

   public static native void guard();
}
