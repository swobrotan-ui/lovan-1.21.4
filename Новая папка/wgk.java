enum wgk {
   MT,
   IH,
   su;

   public static wgk a(String s) {
      return Enum.<wgk>valueOf(wgk.class, s);
   }

   // $VF: synthetic method
   private static wgk[] b() {
      return new wgk[]{MT, IH, su};
   }
}
