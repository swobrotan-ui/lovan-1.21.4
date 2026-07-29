package command;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CommandRegistry {
   private static CommandRegistry instance;
   private final List<vo> commands = new CopyOnWriteArrayList<vo>();

   private CommandRegistry() {
   }

   public static CommandRegistry getInstance() {
      if (instance == null) {
         instance = new CommandRegistry();
      }

      return instance;
   }

   public void registerCommand(String s, String s1, List<String> list) {
      this.commands.removeIf(vo -> {
         return vo.a().equals(s);
      });
      this.commands.add(new vo(s, s1, list));
   }

   public List<String> getCompletions(String s) {
      ArrayList arraylist = new ArrayList();
      if (!s.isEmpty() && !s.equals(".")) {
         boolean flag = s.endsWith(" ");
         String[] astring = s.trim().split("\\s+");
         String s1 = astring[0];
         if (astring.length == 1 && !flag) {
            for (vo vo : this.commands) {
               if (vo.a().startsWith(s1)) {
                  arraylist.add(vo.a());
               }
            }

            return arraylist;
         } else if (astring.length == 1 && flag) {
            for (vo vox : this.commands) {
               if (vox.a().equals(s1)) {
                  for (String s4 : vox.c()) {
                     arraylist.add(s1 + " " + s4);
                  }
                  break;
               }
            }

            return arraylist;
         } else {
            if (astring.length >= 2) {
               for (vo voxx : this.commands) {
                  if (voxx.a().equals(s1)) {
                     String s2 = astring[1].toLowerCase();

                     for (String s3 : voxx.c()) {
                        if (s3.startsWith(s2)) {
                           arraylist.add(s1 + " " + s3);
                        }
                     }
                     break;
                  }
               }
            }

            return arraylist;
         }
      } else {
         for (vo voxxx : this.commands) {
            arraylist.add(voxxx.a());
         }

         return arraylist;
      }
   }

   public String getDescription(String s) {
      for (vo vo : this.commands) {
         if (vo.a().equals(s)) {
            return vo.b();
         }
      }

      return null;
   }

   public List<vo> getCommands() {
      return this.commands;
   }
}
