enum bc {
   hu,
   LQ,
   avp;

   public static bc a(String s) {
      return Enum.<bc>valueOf(bc.class, s);
   }

   // $VF: synthetic method
   private static bc[] b() {
      return new bc[]{hu, LQ, avp};
   }
}
