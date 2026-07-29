package module;

import core.FriendManager;
import enum.Category;
import enum.PacketDirection;
import event.PacketEvent;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import setting.BooleanSetting;
import setting.SliderSetting;
import setting.TextSetting;

public class AutoAcceptModule extends Module {
   private final BooleanSetting onlyFriendsSetting = new BooleanSetting("Только друзья", "Принимать тп только от друзей", true);
   private final SliderSetting delaySetting = new SliderSetting("Задержка", "Задержка перед принятием тп", 0.0, 0.0, 40.0, 1.0, " тиков", 0);
   private final TextSetting commandSetting = new TextSetting("Команда", "Команда для принятия тп", "tpaccept", "/команда");
   private int jL = 0;
   private boolean Im = false;

   public AutoAcceptModule() {
      super("АутоАссепт", "Автоматически принимает запросы на телепортацию", Category.PLAYER);
      this.addSettings(this.onlyFriendsSetting, this.delaySetting, this.commandSetting);
   }

   @Override
   public void onEnable() {
      this.Im = false;
      this.jL = 0;
   }

   @Override
   public void onDisable() {
      this.Im = false;
      this.jL = 0;
   }

   @Override
   public void onEndTick() {
      if (!this.isNotInWorld() && this.Im) {
         if (this.jL > 0) {
            this.jL--;
         } else {
            String s = this.commandSetting.getValue().trim();
            if (!s.isEmpty() && this.getNetworkHandler() != null) {
               this.getNetworkHandler().sendChatCommand(s);
            }

            this.Im = false;
         }
      }
   }

   @Override
   public void onPacket(PacketEvent packetevent) {
      if (packetevent.getType() == PacketDirection.RECEIVE) {
         if (packetevent.getPacket() instanceof GameMessageS2CPacket gamemessages2cpacket) {
            GameMessageS2CPacket gamemessages2cpacket1 = gamemessages2cpacket;

            try {
               text1 = gamemessages2cpacket1.content();
            } catch (Throwable throwable1) {
               throw new MatchException(throwable1.toString(), throwable1);
            }

            Text text = text1;
            gamemessages2cpacket1 = gamemessages2cpacket;

            try {
               flag1 = gamemessages2cpacket1.overlay();
            } catch (Throwable throwable) {
               throw new MatchException(throwable.toString(), throwable);
            }

            boolean flag = flag1;
            if (!flag) {
               String s = text.getString().toLowerCase();
               if (this.b(s)) {
                  if (!this.onlyFriendsSetting.getValue() || this.c(s)) {
                     this.jL = (int)this.delaySetting.getValue();
                     this.Im = true;
                  }
               }
            }
         }
      }
   }

   private boolean b(String s) {
      return (s.contains("teleport") || s.contains("телепорт") || s.contains("tpa") || s.contains("tpask"))
         && (s.contains("request") || s.contains("просит") || s.contains("запрос") || s.contains("has requested") || s.contains("wants to"));
   }

   private boolean c(String s) {
      for (String s1 : FriendManager.getInstance().getFriends()) {
         if (s.contains(s1.toLowerCase())) {
            return true;
         }
      }

      return false;
   }
}
