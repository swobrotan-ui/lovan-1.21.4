import a.Loader;

class mt {
   String action;
   String hwid;
   String configData;
   String configName;
   String configDescription;
   String configKey;

   mt(String s, String s1) {
      this.action = s;
      this.hwid = s1;
   }

   native void a(String s);

   native void b(String s);

   native void c(String s);

   native void d(String s);

   static {
      Loader.init(mt.class);
   }

   public static native void guard();
}
