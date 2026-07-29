import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class oz {
   private static final Map<String, String> amU = Map.<String, String>ofEntries(
      Map.<String, String>entry("speed", "Speed"),
      Map.<String, String>entry("slowness", "Slowness"),
      Map.<String, String>entry("haste", "Haste"),
      Map.<String, String>entry("mining_fatigue", "Mining Fatigue"),
      Map.<String, String>entry("strength", "Strength"),
      Map.<String, String>entry("regeneration", "Regeneration"),
      Map.<String, String>entry("fire_resistance", "Fire Resistance"),
      Map.<String, String>entry("water_breathing", "Water Breathing"),
      Map.<String, String>entry("night_vision", "Night Vision"),
      Map.<String, String>entry("invisibility", "Invisibility"),
      Map.<String, String>entry("poison", "Poison"),
      Map.<String, String>entry("wither", "Wither"),
      Map.<String, String>entry("absorption", "Absorption"),
      Map.<String, String>entry("jump_boost", "Jump Boost"),
      Map.<String, String>entry("resistance", "Resistance")
   );
   private static final Map<String, String> xS = Map.<String, String>ofEntries(
      Map.<String, String>entry("speed", "Speed"),
      Map.<String, String>entry("slowness", "Slowness"),
      Map.<String, String>entry("haste", "Haste"),
      Map.<String, String>entry("strength", "Strength"),
      Map.<String, String>entry("regeneration", "Regen"),
      Map.<String, String>entry("fire_resistance", "Fire Res"),
      Map.<String, String>entry("water_breathing", "Water Br."),
      Map.<String, String>entry("night_vision", "Night Vis."),
      Map.<String, String>entry("invisibility", "Invis."),
      Map.<String, String>entry("poison", "Poison"),
      Map.<String, String>entry("wither", "Wither"),
      Map.<String, String>entry("absorption", "Absorption"),
      Map.<String, String>entry("jump_boost", "Jump Boost"),
      Map.<String, String>entry("resistance", "Resistance")
   );

   private oz() {
   }

   public static String a(int i) {
      switch (i) {
         case 1:
            return "I";
         case 2:
            return "II";
         case 3:
            return "III";
         case 4:
            return "IV";
         case 5:
            return "V";
         case 6:
            return "VI";
         case 7:
            return "VII";
         case 8:
            return "VIII";
         case 9:
            return "IX";
         case 10:
            return "X";
         default:
            return String.valueOf(i);
      }
   }

   public static String b(int i) {
      int j = i / 20;
      int k = j / 60;
      int l = j % 60;
      return String.format("%d:%02d", k, l);
   }

   public static String c(StatusEffect statuseffect) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.world == null) {
         return "";
      } else {
         Registry registry = minecraftclient.world.getRegistryManager().getOrThrow(RegistryKeys.STATUS_EFFECT);
         Identifier identifier = registry.getId(statuseffect);
         return identifier != null ? identifier.getPath() : "";
      }
   }

   public static String d(StatusEffect statuseffect) {
      String s = c(statuseffect);
      return amU.getOrDefault(s, statuseffect.getName().getString());
   }

   public static String e(StatusEffect statuseffect) {
      String s = c(statuseffect);
      return xS.getOrDefault(s, statuseffect.getName().getString());
   }

   public static String f(StatusEffect statuseffect, int i) {
      String s = d(statuseffect);
      if (s.length() > i) {
         String s1 = s.substring(0, i - 2);
         return s1 + "..";
      } else {
         return s;
      }
   }
}
