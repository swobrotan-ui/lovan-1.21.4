enum zq {
   atV,
   TI,
   se,
   Eu;

   public static zq a(String s) {
      return Enum.<zq>valueOf(zq.class, s);
   }

   // $VF: synthetic method
   private static zq[] b() {
      return new zq[]{atV, TI, se, Eu};
   }
}
