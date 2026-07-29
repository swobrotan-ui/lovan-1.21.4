enum lg {
   tw,
   KK,
   Km,
   hU,
   ZG,
   uB;

   public static lg a(String s) {
      return Enum.<lg>valueOf(lg.class, s);
   }

   // $VF: synthetic method
   private static lg[] b() {
      return new lg[]{tw, KK, Km, hU, ZG, uB};
   }
}
