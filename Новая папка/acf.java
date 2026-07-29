enum acf {
   xu,
   Gb,
   adG;

   public static acf a(String s) {
      return Enum.<acf>valueOf(acf.class, s);
   }

   // $VF: synthetic method
   private static acf[] b() {
      return new acf[]{xu, Gb, adG};
   }
}
