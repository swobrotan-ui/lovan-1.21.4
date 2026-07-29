package module;

import data.TranslationResult;
import enum.Category;
import java.lang.Character.UnicodeBlock;
import java.util.List;
import java.util.Map;
import net.TranslationClient;
import setting.ListSetting;
import util.ChatUtil;

public class TranslatorModule extends Module {
   private static final Map<String, String> anW = Map.<String, String>of(
      "Russian", "ru", "English", "en", "German", "de", "French", "fr", "Spanish", "es", "Ukrainian", "uk", "Chinese", "zh"
   );
   private ListSetting languageSetting = new ListSetting(
      "Язык перевода",
      "На какой язык переводить (для СКМ по сообщению)",
      List.<String>of("Russian", "English", "German", "French", "Spanish", "Ukrainian", "Chinese"),
      List.<String>of("Russian"),
      false
   );

   public TranslatorModule() {
      super("Транзлатор", "Перевод сообщений. СКМ по сообщению / Ctrl+T", Category.CLIENT);
      this.addSettings(this.languageSetting);
   }

   @Override
   public void onEnable() {
      ChatUtil.sendMessage("§7СКМ §f(средняя кнопка мыши) §7по сообщению в чате — перевести");
      ChatUtil.sendMessage("§7Ctrl+T §fв поле ввода — русский<->английский");
   }

   @Override
   public void onDisable() {
   }

   public String c() {
      String s = this.languageSetting.getFirst();
      return anW.getOrDefault(s, "ru");
   }

   public static String d(String s) {
      return s.replaceAll("§.", "").trim();
   }

   private static boolean e(String s) {
      int i = 0;
      int j = 0;

      for (char c0 : s.toCharArray()) {
         if (UnicodeBlock.of(c0) == UnicodeBlock.CYRILLIC) {
            i++;
         } else if (Character.isLetter(c0)) {
            j++;
         }
      }

      return i > j;
   }

   public void f(String s, uy uy) {
      String s1 = d(s);
      if (s1.isEmpty()) {
         uy.onError("Пустой текст");
      } else {
         boolean flag = e(s1);
         String s2 = flag ? "ru" : "en";
         String s3 = flag ? "en" : "ru";
         new Thread(() -> {
            String s7 = TranslationClient.a(s1, s2, s3);
            if (s7 != null && !s7.isEmpty()) {
               uy.onResult(s7, s2, s3);
            } else {
               uy.onError("Не удалось перевести");
            }
         }).start();
      }
   }

   public void g(String s, uy uy) {
      String s1 = d(s);
      if (s1.isEmpty()) {
         uy.onError("Пустой текст");
      } else {
         String s2 = this.c();
         new Thread(() -> {
            TranslationResult translationresult = TranslationClient.d(s1, "autodetect", s2);
            if (translationresult == null || translationresult.getText() == null) {
               uy.onError("Не удалось перевести");
            } else if (!s2.equalsIgnoreCase(translationresult.getDetectedLanguage()) && !s1.equals(translationresult.getText())) {
               String s7 = translationresult.getDetectedLanguage() != null ? translationresult.getDetectedLanguage() : "?";
               uy.onResult(translationresult.getText(), s7, s2);
            } else {
               String s5 = s2.equals("en") ? "ru" : "en";
               String s6 = TranslationClient.a(s1, s2, s5);
               if (s6 != null) {
                  uy.onResult(s6, s2, s5);
               } else {
                  uy.onError("Не удалось перевести");
               }
            }
         }).start();
      }
   }

   public ListSetting h() {
      return this.languageSetting;
   }
}
