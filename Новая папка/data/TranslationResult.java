package data;

public record TranslationResult(
   String text,
   String detectedLanguage,
   String targetLanguage,
   String text,
   String detectedLanguage,
   String targetLanguage,
   String text,
   String detectedLanguage,
   String targetLanguage,
   String text,
   String detectedLanguage,
   String targetLanguage
) {
   public TranslationResult(String s, String s1, String s2) {
      this.text = s;
      this.detectedLanguage = s1;
      this.targetLanguage = s2;
   }

   public String getText() {
      return this.text;
   }

   public String getDetectedLanguage() {
      return this.detectedLanguage;
   }

   public String getTargetLanguage() {
      return this.targetLanguage;
   }
}
