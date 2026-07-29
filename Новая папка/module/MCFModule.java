package module;

import command.FriendCommand;
import core.FriendManager;
import enum.Category;
import event.ChatMessageEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import setting.ActionKeySetting;

public class MCFModule extends Module {
   private final ActionKeySetting buttonSetting = new ActionKeySetting("Кнопка", "Кнопка для добавления/удаления из друзей", 2, this::a);
   private final FriendCommand vp = new FriendCommand();

   public MCFModule() {
      super("МСФ", "Добавление в друзья по нажатию кнопки. Команды: .friend", Category.CLIENT);
      this.addSettings(this.buttonSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onChatMessage(ChatMessageEvent chatmessageevent) {
      this.vp.handleMessage(chatmessageevent);
   }

   private void a() {
      if (!this.isNotInWorld()) {
         Entity entity = this.getClient().targetedEntity;
         if (entity != null) {
            if (entity instanceof PlayerEntity playerentity) {
               if (playerentity != this.getPlayer()) {
                  String s = playerentity.getName().getString();
                  FriendManager friendmanager = FriendManager.getInstance();
                  friendmanager.toggleFriend(s);
               }
            }
         }
      }
   }
}
