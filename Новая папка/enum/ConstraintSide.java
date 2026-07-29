package enum;

public enum ConstraintSide {
   KC,
   Kq,
   tO,
   uN;

   public static ConstraintSide a(String s) {
      return Enum.<ConstraintSide>valueOf(ConstraintSide.class, s);
   }

   // $VF: synthetic method
   private static ConstraintSide[] b() {
      return new ConstraintSide[]{KC, Kq, tO, uN};
   }
}
