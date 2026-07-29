enum lzl {
   Lt,
   BQ,
   tX,
   jB;

   public static lzl a(String s) {
      return Enum.<lzl>valueOf(lzl.class, s);
   }

   // $VF: synthetic method
   private static lzl[] b() {
      return new lzl[]{Lt, BQ, tX, jB};
   }
}
