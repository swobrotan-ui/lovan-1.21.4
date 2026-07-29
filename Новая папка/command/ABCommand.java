package command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import data.ABItem;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import module.ABRaidmineModule;
import util.ChatUtil;

public class ABCommand extends Command {
   private final ABRaidmineModule abModule;
   private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
   private static final String CONFIG_FILE_NAME = "_config.json";

   public ABCommand(ABRaidmineModule abraidminemodule) {
      super(".ab");
      this.abModule = abraidminemodule;
      this.b("create", "save", "load", "download", "list", "itemlist", "delete", "help");
   }

   @Override
   protected void executeCommand(String s, String[] astring) {
      switch (s) {
         case "create":
         case "c":
            this.createTemplate();
            return;
         case "save":
         case "s":
            this.saveConfig(astring);
            return;
         case "load":
         case "l":
            this.loadConfig(astring);
            return;
         case "download":
         case "dl":
            this.downloadConfig(astring);
            return;
         case "list":
         case "ls":
            this.listConfigs();
            return;
         case "itemlist":
         case "il":
            this.listItems();
            return;
         case "delete":
         case "del":
         case "d":
            this.deleteConfig(astring);
            return;
         case "help":
         case "h":
            this.showUsage();
            return;
         default:
            ChatUtil.sendError("Неизвестная подкоманда! Используйте .ab help");
      }
   }

   private void createTemplate() {
      String s = System.getProperty("user.home");
      String s1 = Paths.get(s, "Desktop", "_config.json").toString();
      File file1 = new File(s1);
      if (file1.exists()) {
         String s2 = ChatUtil.formatWhite("_config.json");
         ChatUtil.sendError("Файл " + s2 + " §cуже существует на рабочем столе!");
         ChatUtil.sendMessage(ChatUtil.formatGray("Переименуйте или удалите существующий файл."));
      } else {
         pm pm = this.createExampleConfig();

         try {
            try (FileWriter filewriter = new FileWriter(file1)) {
               gson.toJson(pm, filewriter);
               ChatUtil.sendSuccess("Пример конфига создан на рабочем столе!");
               String s3 = ChatUtil.formatWhite("_config.json");
               ChatUtil.sendMessage(ChatUtil.formatGray("Файл: " + s3));
               String s4 = ChatUtil.formatHighlight(".ab save <название>");
               ChatUtil.sendMessage(ChatUtil.formatGray("Отредактируйте файл и сохраните через " + s4));
            }
         } catch (Exception exception) {
            String s5 = exception.getMessage();
            ChatUtil.sendError("Ошибка при создании файла: " + s5);
         }
      }
   }

   private void saveConfig(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".ab save <название>")) {
         String s = astring[2];
         String s1 = System.getProperty("user.home");
         String s2 = Paths.get(s1, "Desktop", "_config.json").toString();
         File file1 = new File(s2);
         if (!file1.exists()) {
            String s3 = ChatUtil.formatWhite("_config.json");
            ChatUtil.sendError("Файл " + s3 + " §cне найден на рабочем столе!");
            String s4 = ChatUtil.formatHighlight(".ab create");
            ChatUtil.sendMessage(ChatUtil.formatGray("Создайте файл командой: " + s4));
         } else {
            try {
               try (FileReader filereader = new FileReader(file1)) {
                  pm pm = (pm)gson.fromJson(filereader, pm.class);
                  if (pm == null || pm.c() == null) {
                     ChatUtil.sendError("Ошибка чтения конфига! Проверьте формат JSON.");
                     return;
                  }

                  this.abModule.r(pm);
                  this.abModule.s().a(s);
                  this.abModule.s().k(s);
                  String s5 = ChatUtil.formatWhite(s);
                  ChatUtil.sendSuccess("Конфиг " + s5 + " §aсохранён!");
                  String s6 = ChatUtil.formatWhite(String.valueOf(pm.c().size()));
                  ChatUtil.sendMessage(ChatUtil.formatGray("Загружено предметов: " + s6));
               }
            } catch (Exception exception) {
               String s7 = exception.getMessage();
               ChatUtil.sendError("Ошибка при чтении файла: " + s7);
            }
         }
      }
   }

   private void loadConfig(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".ab load <название>")) {
         String s = astring[2];

         try {
            ox ox = this.abModule.s();
            if (!ox.d(s)) {
               String s1 = ChatUtil.formatWhite(s);
               ChatUtil.sendError("Конфиг " + s1 + " §cне найден!");
               String s2 = ChatUtil.formatHighlight(".ab list");
               ChatUtil.sendMessage(ChatUtil.formatGray("Используйте " + s2 + " для просмотра доступных конфигов"));
            } else {
               ox.b(s);
               ox.k(s);
               pm pm = this.abModule.q();
               int i = pm != null && pm.c() != null ? pm.c().size() : 0;
               String s3 = ChatUtil.formatWhite(s);
               ChatUtil.sendSuccess("Конфиг " + s3 + " §aзагружен!");
               String s4 = ChatUtil.formatWhite(String.valueOf(i));
               ChatUtil.sendMessage(ChatUtil.formatGray("Загружено предметов: " + s4));
            }
         } catch (Exception exception) {
            String s5 = exception.getMessage();
            ChatUtil.sendError("Ошибка при загрузке конфига: " + s5);
         }
      }
   }

   private void downloadConfig(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".ab download <название>")) {
         String s = astring[2];

         try {
            ox ox = this.abModule.s();
            if (!ox.d(s)) {
               String s3 = ChatUtil.formatWhite(s);
               ChatUtil.sendError("Конфиг " + s3 + " §cне найден!");
            } else {
               pm pm = ox.e(s);
               if (pm != null && pm.c() != null) {
                  String s1 = System.getProperty("user.home");
                  String s2 = Paths.get(s1, "Desktop", "_config.json").toString();
                  File file1 = new File(s2);

                  try (FileWriter filewriter = new FileWriter(file1)) {
                     gson.toJson(pm, filewriter);
                     String s4 = ChatUtil.formatWhite(s);
                     ChatUtil.sendSuccess("Конфиг " + s4 + " §aзагружен на рабочий стол!");
                     String s5 = ChatUtil.formatWhite("_config.json");
                     ChatUtil.sendMessage(ChatUtil.formatGray("Файл: " + s5));
                     String s6 = ChatUtil.formatWhite(String.valueOf(pm.c().size()));
                     ChatUtil.sendMessage(ChatUtil.formatGray("Предметов: " + s6));
                  }
               } else {
                  ChatUtil.sendError("Конфиг пуст или поврежден!");
               }
            }
         } catch (Exception exception) {
            String s7 = exception.getMessage();
            ChatUtil.sendError("Ошибка: " + s7);
         }
      }
   }

   private void listConfigs() {
      try {
         ox ox = this.abModule.s();
         List list = ox.f();
         if (list.isEmpty()) {
            ChatUtil.sendError("Список конфигов пуст!");
            String s3 = ChatUtil.formatHighlight(".ab save <название>");
            ChatUtil.sendMessage(ChatUtil.formatGray("Создайте конфиг командой: " + s3));
         } else {
            String s = ox.j();
            int j = list.size();
            ChatUtil.sendHeader("AB Конфиги (" + j + ")");

            for (String s1 : list) {
               boolean flag = s1.equals(s);
               String s2 = flag ? "§a✓ §f" : "§7  ";
               pm pm = ox.e(s1);
               int i = pm != null && pm.c() != null ? pm.c().size() : 0;
               ChatUtil.sendMessage(String.format("%s%s §7(%d предметов)", s2, ChatUtil.formatWhite(s1), i));
            }

            ChatUtil.sendMessage("");
            ChatUtil.sendMessage(ChatUtil.formatGray("§a✓ §7- активный конфиг"));
         }
      } catch (Exception exception) {
         String s4 = exception.getMessage();
         ChatUtil.sendError("Ошибка при получении списка: " + s4);
      }
   }

   private void listItems() {
      pm pm = this.abModule.q();
      if (pm != null && pm.c() != null && !pm.c().isEmpty()) {
         String s = this.abModule.s().j();
         String s1 = s != null ? "AB Предметы (" + s + ")" : "AB Предметы";
         int i = pm.c().size();
         ChatUtil.sendHeader(s1 + " - " + i);

         for (ABItem abitem : pm.c()) {
            String s2 = abitem.isPartial() ? "§e~" : "§a=";
            String s3 = this.formatPrice(abitem.getMaxPrice());
            ChatUtil.sendMessage(String.format("%s §f%s §7(макс: §e%s§7)", s2, ChatUtil.formatWhite(abitem.getName()), s3));
         }

         ChatUtil.sendMessage("");
         ChatUtil.sendMessage(ChatUtil.formatGray("Легенда: §a= §7- точное совпадение, §e~ §7- частичное совпадение"));
      } else {
         ChatUtil.sendError("Список предметов пуст!");
         String s4 = ChatUtil.formatHighlight(".ab load <название>");
         ChatUtil.sendMessage(ChatUtil.formatGray("Загрузите конфиг командой: " + s4));
      }
   }

   private void deleteConfig(String[] astring) {
      if (ChatUtil.validateArgs(astring, 3, ".ab delete <название>")) {
         String s = astring[2];

         try {
            ox ox = this.abModule.s();
            if (!ox.d(s)) {
               String s2 = ChatUtil.formatWhite(s);
               ChatUtil.sendError("Конфиг " + s2 + " §cне найден!");
            } else {
               String s1 = ox.j();
               if (s.equals(s1)) {
                  ox.k(null);
               }

               ox.c(s);
               String s3 = ChatUtil.formatWhite(s);
               ChatUtil.sendSuccess("Конфиг " + s3 + " §aудалён!");
            }
         } catch (Exception exception) {
            String s4 = exception.getMessage();
            ChatUtil.sendError("Ошибка при удалении: " + s4);
         }
      }
   }

   private String formatPrice(double d0) {
      if (d0 >= 1000000.0) {
         return String.format("%.1fM", d0 / 1000000.0);
      } else {
         return d0 >= 1000.0 ? String.format("%.1fK", d0 / 1000.0) : String.format("%.0f", d0);
      }
   }

   private pm createExampleConfig() {
      pm pm = new pm();
      ArrayList arraylist = new ArrayList();
      ABItem abitem = new ABItem("Пузырек с 15 ур. опыта", 30000.0);
      arraylist.add(abitem);
      ABItem abitem1 = new ABItem("Поножи", 50000.0);
      abitem1.setPartial(true);
      arraylist.add(abitem1);
      ABItem abitem2 = new ABItem("Алмаз", 1000.0);
      arraylist.add(abitem2);
      pm.b(arraylist);
      return pm;
   }

   @Override
   protected void showUsage() {
      ChatUtil.sendHeader("AB Команды");
      ChatUtil.sendEntry(".ab create", "Создать пример конфига на рабочем столе");
      ChatUtil.sendEntry(".ab save <название>", "Сохранить конфиг на сервере");
      ChatUtil.sendEntry(".ab load <название>", "Загрузить конфиг с сервера");
      ChatUtil.sendEntry(".ab download <название>", "Скачать конфиг на рабочий стол");
      ChatUtil.sendEntry(".ab list", "Список всех конфигов");
      ChatUtil.sendEntry(".ab itemlist", "Список предметов в активном конфиге");
      ChatUtil.sendEntry(".ab delete <название>", "Удалить конфиг");
      ChatUtil.sendEntry(".ab help", "Показать справку");
      ChatUtil.sendMessage("");
      ChatUtil.sendMessage(ChatUtil.formatGray("Алиасы: create/c, save/s, load/l, download/dl, list/ls, itemlist/il, delete/del/d"));
   }

   @Override
   protected String getDescription() {
      return "Управление конфигом AB";
   }
}
