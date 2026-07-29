package command;

import core.FriendManager;
import java.util.Set;
import util.ChatUtil;

class FriendCommand extends Command {
   public FriendCommand() {
      super(".friend", ".f");
      this.b("add", "remove", "list", "clear", "help");
   }

   @Override
   protected void executeCommand(String s, String[] astring) {
      FriendManager friendmanager = FriendManager.getInstance();
      switch (s) {
         case "add":
         case "a":
            this.addFriend(astring, friendmanager);
            return;
         case "remove":
         case "rem":
         case "r":
         case "delete":
         case "del":
            this.removeFriend(astring, friendmanager);
            return;
         case "list":
         case "ls":
         case "l":
            this.listFriends(friendmanager);
            return;
         case "clear":
         case "c":
            this.clearFriends(friendmanager);
            return;
         case "help":
         case "h":
            this.showUsage();
            return;
         default:
            ChatUtil.sendError("Неизвестная команда! Используйте .friend help");
      }
   }

   private void addFriend(String[] astring, FriendManager friendmanager) {
      if (ChatUtil.validateArgs(astring, 3, ".friend add <имя>")) {
         String s = astring[2];
         if (friendmanager.isFriend(s)) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendError("Игрок " + s1 + " §cуже в списке друзей!");
         } else {
            friendmanager.addFriend(s);
            String s2 = ChatUtil.formatWhite(s);
            ChatUtil.sendSuccess("Игрок " + s2 + " §aдобавлен в друзья!");
         }
      }
   }

   private void removeFriend(String[] astring, FriendManager friendmanager) {
      if (ChatUtil.validateArgs(astring, 3, ".friend remove <имя>")) {
         String s = astring[2];
         if (!friendmanager.isFriend(s)) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendError("Игрок " + s1 + " §cне найден в списке друзей!");
         } else {
            friendmanager.removeFriend(s);
            String s2 = ChatUtil.formatWhite(s);
            ChatUtil.sendSuccess("Игрок " + s2 + " §aудален из друзей!");
         }
      }
   }

   private void listFriends(FriendManager friendmanager) {
      Set set = friendmanager.getFriends();
      if (set.isEmpty()) {
         ChatUtil.sendError("Список друзей пуст!");
      } else {
         int i = set.size();
         ChatUtil.sendHeader("Друзья (" + i + ")");

         for (String s : set) {
            String s1 = ChatUtil.formatWhite(s);
            ChatUtil.sendMessage("§7- " + s1);
         }
      }
   }

   private void clearFriends(FriendManager friendmanager) {
      int i = friendmanager.getFriends().size();
      if (i == 0) {
         ChatUtil.sendError("Список друзей уже пуст!");
      } else {
         friendmanager.clearFriends();
         String s = ChatUtil.formatWhite(String.valueOf(i));
         ChatUtil.sendSuccess("Удалено " + s + " §aдрузей!");
      }
   }

   @Override
   protected void showUsage() {
      ChatUtil.sendHeader("Friend Команды");
      ChatUtil.sendEntry(".friend add <имя>", "Добавить друга");
      ChatUtil.sendEntry(".friend remove <имя>", "Удалить друга");
      ChatUtil.sendEntry(".friend list", "Список друзей");
      ChatUtil.sendEntry(".friend clear", "Очистить список друзей");
      ChatUtil.sendEntry(".friend help", "Показать справку");
      ChatUtil.sendMessage(ChatUtil.formatGray("Алиасы: .f, add/a, remove/r/rem/del, list/ls/l, clear/c"));
   }

   @Override
   protected String getDescription() {
      return "Управление списком друзей";
   }
}
