public enum ws {
   tg("E", "Введите никнейм..."),
   ahz("D", "Введите ключ конфигурации...");

   private final String eg;
   private final String aeV;

   public static ws a(String s) {
      return Enum.<ws>valueOf(ws.class, s);
   }

   private ws(String s1, String s2) {
      this.eg = s1;
      this.aeV = s2;
   }

   public String b() {
      return this.eg;
   }

   public String c() {
      return this.aeV;
   }

   // $VF: synthetic method
   private static ws[] d() {
      return new ws[]{tg, ahz};
   }
}
