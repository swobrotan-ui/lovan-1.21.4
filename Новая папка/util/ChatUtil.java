package util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatUtil {
   private static final MinecraftClient mc = MinecraftClient.getInstance();
   private static final String GREEN = "§a";
   private static final String RED = "§c";
   private static final String YELLOW = "§e";
   private static final String WHITE = "§f";
   private static final String GRAY = "§7";
   private static final String ACCENT = "§e";

   public static void sendSuccess(String s) {
      sendMessage("§a" + s);
   }

   public static void sendError(String s) {
      sendMessage("§c" + s);
   }

   public static void sendWarning(String s) {
      sendMessage("§e" + s);
   }

   public static void sendMessage(String s) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(s), false);
      }
   }

   public static String formatHighlight(String s) {
      return "§e" + s;
   }

   public static String formatWhite(String s) {
      return "§f" + s;
   }

   public static String formatGray(String s) {
      return "§7" + s;
   }

   public static void sendUsage(String s) {
      String s1 = formatHighlight(s);
      sendError("Использование: " + s1);
   }

   public static void sendHeader(String s) {
      sendMessage("§e=== " + s + " ===");
   }

   public static void sendEntry(String s, String s1) {
      String s4 = formatHighlight(s);
      String s2 = formatGray("- " + s1);
      String s3 = s4;
      sendMessage(s3 + " " + s2);
   }

   public static boolean validateArgs(String[] astring, int i, String s) {
      if (astring.length < i) {
         sendUsage(s);
         return false;
      } else {
         return true;
      }
   }

   public static void sendList(String s, Iterable<String> iterable) {
      sendHeader(s);

      for (String s1 : iterable) {
         sendMessage("§7- §f" + s1);
      }
   }

   public static void sendToggle(String s, boolean flag) {
      String s1 = flag ? "§aвключен" : "§cвыключен";
      sendMessage("§f" + s + " " + s1);
   }
}
