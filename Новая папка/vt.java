public enum vt {
   azP(0),
   TR(1),
   arm(2);

   private final int akA;

   public static vt a(String s) {
      return Enum.<vt>valueOf(vt.class, s);
   }

   private vt(int j) {
      this.akA = j;
   }

   public int b() {
      return this.akA;
   }

   // $VF: synthetic method
   private static vt[] c() {
      return new vt[]{azP, TR, arm};
   }
}
