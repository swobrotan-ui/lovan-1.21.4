package render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;

@Environment(EnvType.CLIENT)
class ChatMessageEntry {
   private final String messageContent;
   private int count;
   private int creationTick;
   private MessageSignatureData signature;
   private MessageIndicator indicator;

   public ChatMessageEntry(String s, int i, MessageSignatureData messagesignaturedata, MessageIndicator messageindicator) {
      this.messageContent = s;
      this.count = 1;
      this.creationTick = i;
      this.signature = messagesignaturedata;
      this.indicator = messageindicator;
   }

   public void increment() {
      this.count++;
   }

   public String getMessageContent() {
      return this.messageContent;
   }

   public int getCount() {
      return this.count;
   }

   public int getCreationTick() {
      return this.creationTick;
   }

   public MessageSignatureData getSignature() {
      return this.signature;
   }

   public MessageIndicator getIndicator() {
      return this.indicator;
   }

   public void updateTick(int i) {
      this.creationTick = i;
   }
}
