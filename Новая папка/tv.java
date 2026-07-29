enum tv {
   Ew("Smооth"),
   gg("Punсh"),
   ln("Slidе"),
   aqp("Свэговая"),
   ahc("SеlfBack");

   private String acr;

   public static tv a(String s) {
      return Enum.<tv>valueOf(tv.class, s);
   }

   private tv(String s1) {
      this.acr = s1;
   }

   public static tv b(String s) {
      for (tv tv : values()) {
         if (tv.acr.equals(s)) {
            return tv;
         }
      }

      return Ew;
   }

   public String c() {
      return this.acr;
   }

   // $VF: synthetic method
   private static tv[] d() {
      return new tv[]{Ew, gg, ln, aqp, ahc};
   }
}
