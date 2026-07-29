enum yb {
   NR,
   azA,
   gS,
   go;

   public static yb a(String s) {
      return Enum.<yb>valueOf(yb.class, s);
   }

   // $VF: synthetic method
   private static yb[] b() {
      return new yb[]{NR, azA, gS, go};
   }
}
