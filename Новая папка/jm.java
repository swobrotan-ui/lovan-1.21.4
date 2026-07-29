enum jm {
   qC,
   sr,
   ako;

   public static jm a(String s) {
      return Enum.<jm>valueOf(jm.class, s);
   }

   // $VF: synthetic method
   private static jm[] b() {
      return new jm[]{qC, sr, ako};
   }
}
