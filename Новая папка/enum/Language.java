package enum;

public enum Language {
   RUSSIAN("Русский", "RU"),
   ENGLISH("English", "EN");

   private final String displayName;
   private final String code;

   private Language(String s1, String s2) {
      this.displayName = s1;
      this.code = s2;
   }

   public Language next() {
      Language[] alanguage = values();
      return alanguage[(this.ordinal() + 1) % alanguage.length];
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getCode() {
      return this.code;
   }

   // $VF: synthetic method
   private static Language[] e() {
      return new Language[]{RUSSIAN, ENGLISH};
   }
}
