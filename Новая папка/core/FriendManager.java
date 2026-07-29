package core;

import config.Config;
import enum.SoundType;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class FriendManager {
   private static final FriendManager instance = new FriendManager();
   private final Set<String> friends = ConcurrentHashMap.<String>newKeySet();

   private FriendManager() {
   }

   public void addFriend(String s) {
      if (s != null && !s.trim().isEmpty()) {
         String s1 = s.trim().toLowerCase();
         boolean flag = this.friends.add(s1);
         if (flag) {
            this.saveConfig();
            new zj().getFriends().b();
            SoundManager.getInstance().c(SoundType.FRIEND_ADD);
         }
      }
   }

   public void addFriendSilent(String s) {
      if (s != null && !s.trim().isEmpty()) {
         this.friends.add(s.trim().toLowerCase());
      }
   }

   public void removeFriend(String s) {
      if (s != null && !s.trim().isEmpty()) {
         String s1 = s.trim().toLowerCase();
         boolean flag = this.friends.remove(s1);
         if (flag) {
            this.saveConfig();
            new zj().getFriends().b();
            SoundManager.getInstance().c(SoundType.FRIEND_REMOVE);
         }
      }
   }

   public void removeFriendSilent(String s) {
      if (s != null && !s.trim().isEmpty()) {
         String s1 = s.trim().toLowerCase();
         boolean flag = this.friends.remove(s1);
         if (flag) {
            this.saveConfig();
         }
      }
   }

   public void toggleFriend(String s) {
      if (this.isFriend(s)) {
         this.removeFriend(s);
      } else {
         this.addFriend(s);
      }
   }

   public boolean isFriend(String s) {
      return s != null && !s.trim().isEmpty() ? this.friends.contains(s.trim().toLowerCase()) : false;
   }

   public boolean isFriendPlayer(PlayerEntity playerentity) {
      return playerentity == null ? false : this.isFriend(playerentity.getName().getString());
   }

   public boolean isFriendLiving(LivingEntity livingentity) {
      if (livingentity == null) {
         return false;
      } else {
         return livingentity instanceof PlayerEntity playerentity ? this.isFriendPlayer(playerentity) : false;
      }
   }

   public Set<String> getFriends() {
      return new HashSet<String>(this.friends);
   }

   public void clearFriends() {
      this.friends.clear();
   }

   private void saveConfig() {
      try {
         Config config = ClientMain.getInstance().getConfigManager().x();
         if (!ClientMain.getInstance().getConfigSyncManager().k()) {
            ClientMain.getInstance().getConfigSyncManager().d(config);
         }
      } catch (Exception exception) {
      }
   }

   public static FriendManager getInstance() {
      return instance;
   }
}
