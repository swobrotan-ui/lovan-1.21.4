import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.ChatHudLine;

@Environment(EnvType.CLIENT)
public class ll {
   final List<ChatHudLine> messages;
   final List<String> messageHistory;
   final List<io> removalQueue;

   public ll(List<ChatHudLine> list, List<String> list1, List<io> list2) {
      this.messages = list;
      this.messageHistory = list1;
      this.removalQueue = list2;
   }
}
