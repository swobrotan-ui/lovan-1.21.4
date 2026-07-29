import a.Loader;

class erz {
   String xc;
   String Ie;
   String uU;

   erz(String s, String s1, String s2) {
      this.xc = s;
      this.Ie = s1;
      this.uU = s2;
   }

   static {
      Loader.init(erz.class);
   }

   public static native void guard();
}
