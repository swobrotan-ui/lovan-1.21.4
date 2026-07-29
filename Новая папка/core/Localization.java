package core;

import enum.Language;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Localization {
   private static final Localization INSTANCE = new Localization();
   private volatile Language Hz = Language.RUSSIAN;
   private final Map<String, String> azN = new ConcurrentHashMap<String, String>();
   private final List<Runnable> uY = new CopyOnWriteArrayList<Runnable>();
   private volatile boolean akT = false;

   private Localization() {
   }

   public static Localization a() {
      return INSTANCE;
   }

   public void b() {
      try {
         bm bm = new bm(AuthConfig.getHost(), AuthConfig.getPort());
         Map map = bm.a("en");
         if (map != null && !map.isEmpty()) {
            this.azN.clear();
            this.azN.putAll(map);
            this.akT = true;
         }
      } catch (Exception exception) {
         PrintStream printstream = System.err;
         String s = exception.getMessage();
         printstream.println("Failed to load translations: " + s);
      }
   }

   public String c(String s) {
      if (s == null || s.isEmpty()) {
         return s;
      } else {
         return this.Hz != Language.RUSSIAN && this.akT ? this.azN.getOrDefault(s, s) : s;
      }
   }

   public void d() {
      this.Hz = this.Hz.next();
      this.f();
   }

   public void e(Language language) {
      if (this.Hz != language) {
         this.Hz = language;
         this.f();
      }
   }

   private void f() {
      for (Runnable runnable : this.uY) {
         try {
            runnable.run();
         } catch (Exception exception) {
            PrintStream printstream = System.err;
            String s = exception.getMessage();
            printstream.println("er 1 : " + s);
         }
      }
   }

   public Language g() {
      return this.Hz;
   }
}
