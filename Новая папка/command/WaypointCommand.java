package command;

import java.awt.Color;
import java.util.List;
import module.WaypointsModule;
import net.minecraft.util.math.Vec3d;
import util.ChatUtil;

public class WaypointCommand extends Command {
   private final WaypointsModule waypointsModule;

   public WaypointCommand(WaypointsModule waypointsmodule) {
      super(".wp", ".waypoint", ".w");
      this.waypointsModule = waypointsmodule;
      this.b("add", "create", "remove", "delete", "list", "clear", "toggle", "help");
   }

   @Override
   protected void executeCommand(String s, String[] astring) {
      if (!this.waypointsModule.isEnabled() && !s.equals("help") && !s.equals("h")) {
         ChatUtil.sendError("Модуль Waypoints выключен! Включите его для использования команд.");
         String s1 = ChatUtil.formatHighlight(".wp help");
         ChatUtil.sendMessage(ChatUtil.formatGray("Вы можете использовать " + s1 + " даже с выключенным модулем."));
      } else {
         switch (s) {
            case "add":
            case "create":
            case "a":
               this.addWaypoint(astring);
               return;
            case "remove":
            case "delete":
            case "rem":
            case "r":
               this.removeWaypoint(astring);
               return;
            case "list":
            case "ls":
            case "l":
               this.listWaypoints();
               return;
            case "clear":
            case "c":
               this.clearWaypoints();
               return;
            case "toggle":
            case "t":
               this.toggleWaypoint(astring);
               return;
            case "help":
            case "h":
               this.showUsage();
               return;
            default:
               ChatUtil.sendError("Неизвестная подкоманда! Используйте .wp help");
         }
      }
   }

   private void addWaypoint(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".wp add <имя> [x] [y] [z] [цвет]")) {
         String s = astring[2];
         if (this.waypointsModule.j().h(s)) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendError("Waypoint с именем " + s1 + " §cуже существует!");
         } else {
            double d0;
            double d1;
            double d2;
            if (astring.length >= 6) {
               try {
                  d0 = Double.parseDouble(astring[3]);
                  d1 = Double.parseDouble(astring[4]);
                  d2 = Double.parseDouble(astring[5]);
               } catch (NumberFormatException numberformatexception) {
                  ChatUtil.sendError("Неверный формат координат!");
                  return;
               }
            } else {
               Vec3d vec3d = this.waypointsModule.getPlayer().getPos();
               d0 = vec3d.x;
               d1 = vec3d.y;
               d2 = vec3d.z;
            }

            String s4 = this.waypointsModule.c();
            g g = new g(s, d0, d1, d2, s4);
            this.waypointsModule.j().b(g);
            String s5 = ChatUtil.formatWhite(s);
            String s2 = ChatUtil.formatWhite(String.format("[%.1f, %.1f, %.1f]", d0, d1, d2));
            String s3 = s5;
            ChatUtil.sendSuccess("Waypoint " + s3 + " §aдобавлен на координаты " + s2);
         }
      }
   }

   private void removeWaypoint(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".wp remove <имя>")) {
         String s = astring[2];
         if (!this.waypointsModule.j().h(s)) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendError("Waypoint " + s1 + " §cне найден!");
         } else {
            this.waypointsModule.j().c(s);
            String s2 = ChatUtil.formatWhite(s);
            ChatUtil.sendSuccess("Waypoint " + s2 + " §aудален!");
         }
      }
   }

   private void listWaypoints() {
      List list = this.waypointsModule.j().j();
      if (list.isEmpty()) {
         ChatUtil.sendError("Список waypoints пуст!");
      } else {
         String s = this.waypointsModule.c();
         List list1 = this.waypointsModule.j().f(s);
         int k = list1.size();
         int i = list.size();
         int j = k;
         ChatUtil.sendHeader("Waypoints (" + j + "/" + i + ")");
         if (list1.isEmpty()) {
            ChatUtil.sendError("Нет waypoints в текущем измерении!");
         } else {
            for (g g : list1) {
               Vec3d vec3d = this.waypointsModule.getPlayer().getPos();
               double d0 = g.d(vec3d);
               String s1 = g.h() ? "§a✓" : "§c✗";
               ChatUtil.sendMessage(String.format("%s %s §7[%.1f, %.1f, %.1f] §f%.1fm", s1, ChatUtil.formatWhite(g.e()), g.a(), g.b(), g.c(), d0));
            }
         }
      }
   }

   private void clearWaypoints() {
      int i = this.waypointsModule.j().j().size();
      if (i == 0) {
         ChatUtil.sendError("Список waypoints уже пуст!");
      } else {
         this.waypointsModule.j().g();
         String s = ChatUtil.formatWhite(String.valueOf(i));
         ChatUtil.sendSuccess("Удалено " + s + " §awaypoints!");
      }
   }

   private void toggleWaypoint(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".wp toggle <имя>")) {
         String s = astring[2];
         g g = this.waypointsModule.j().e(s);
         if (g == null) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendError("Waypoint " + s1 + " §cне найден!");
         } else {
            g.m(!g.h());
            String s2 = ChatUtil.formatWhite(s);
            ChatUtil.sendToggle("Waypoint " + s2, g.h());
         }
      }
   }

   private Color parseColor(String s, Color color) {
      String s1 = s.toLowerCase();
      switch (s1) {
         case "red":
            return Color.RED;
         case "green":
            return Color.GREEN;
         case "blue":
            return Color.BLUE;
         case "yellow":
            return Color.YELLOW;
         case "white":
            return Color.WHITE;
         case "cyan":
            return Color.CYAN;
         case "magenta":
            return Color.MAGENTA;
         case "orange":
            return Color.ORANGE;
         case "pink":
            return Color.PINK;
         default:
            try {
               if (s.startsWith("#")) {
                  s = s.substring(1);
               }

               return Color.decode("#" + s);
            } catch (Exception exception) {
               return color;
            }
      }
   }

   @Override
   protected void showUsage() {
      ChatUtil.sendHeader("Waypoints Команды");
      ChatUtil.sendEntry(".wp add <имя> [x y z] [цвет]", "Добавить waypoint");
      ChatUtil.sendEntry(".wp remove <имя>", "Удалить waypoint");
      ChatUtil.sendEntry(".wp list", "Список waypoints");
      ChatUtil.sendEntry(".wp toggle <имя>", "Переключить видимость");
      ChatUtil.sendEntry(".wp clear", "Очистить все waypoints");
      ChatUtil.sendEntry(".wp help", "Показать справку");
      ChatUtil.sendMessage(ChatUtil.formatGray("Цвета: red, green, blue, yellow, white, cyan, magenta или #RRGGBB"));
      ChatUtil.sendMessage(ChatUtil.formatGray("Алиасы: .waypoint, .w, add/a, remove/r/rem/del, list/ls/l, clear/c, toggle/t"));
   }

   @Override
   protected String getDescription() {
      return "Управление waypoints (маркеры в мире)";
   }
}
