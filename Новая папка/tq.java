public enum tq {
   KV,
   act,
   CU,
   ayR,
   agP,
   auP,
   LS,
   BM,
   WG,
   Xx,
   yO,
   aft;

   public static tq a(String s) {
      return Enum.<tq>valueOf(tq.class, s);
   }

   public abstract float b(float f, float f1, float f2, float f3);

   // $VF: synthetic method
   private static tq[] c() {
      return new tq[]{KV, act, CU, ayR, agP, auP, LS, BM, WG, Xx, yO, aft};
   }
}
