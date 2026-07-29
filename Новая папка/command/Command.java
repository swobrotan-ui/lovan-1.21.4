package command;

import event.ChatMessageEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class Command {
   protected final String[] aliases;
   protected final List<String> completions;

   public Command(String... astring) {
      this.aliases = astring;
      this.completions = new ArrayList<String>();
      this.register();
   }

   private void register() {
      if (this.aliases.length != 0) {
         String s = this.aliases[0];
         List list = this.getCompletions();
         String s1 = this.getDescription();
         CommandRegistry.getInstance().registerCommand(s, s1, list);

         for (int i = 1; i < this.aliases.length; i++) {
            CommandRegistry.getInstance().registerCommand(this.aliases[i], s1, list);
         }
      }
   }

   protected void b(String... astring) {
      for (String s : astring) {
         if (!this.completions.contains(s)) {
            this.completions.add(s);
         }
      }

      this.updateRegistration();
   }

   private void updateRegistration() {
      if (this.aliases.length != 0) {
         String s = this.aliases[0];
         List list = this.getCompletions();
         String s1 = this.getDescription();
         CommandRegistry.getInstance().registerCommand(s, s1, list);

         for (int i = 1; i < this.aliases.length; i++) {
            CommandRegistry.getInstance().registerCommand(this.aliases[i], s1, list);
         }
      }
   }

   protected List<String> getCompletions() {
      return new ArrayList<String>(this.completions);
   }

   protected String getDescription() {
      return "";
   }

   public boolean handleMessage(ChatMessageEvent chatmessageevent) {
      String s = chatmessageevent.getMessage();
      String s1 = null;

      for (String s2 : this.aliases) {
         if (s.startsWith(s2)) {
            s1 = s2;
            break;
         }
      }

      if (s1 == null) {
         return false;
      } else {
         chatmessageevent.cancel();
         String[] astring = s.trim().split("\\s+");
         if (astring.length < 2) {
            this.showUsage();
            return true;
         } else {
            String s3 = astring[1].toLowerCase();
            this.executeCommand(s3, astring);
            return true;
         }
      }
   }

   protected abstract void executeCommand(String s, String[] astring);

   protected abstract void showUsage();
}
