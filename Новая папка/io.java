import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.message.MessageSignatureData;

@Environment(EnvType.CLIENT)
record io(
   MessageSignatureData signature,
   int deletableAfter,
   MessageSignatureData signature,
   int deletableAfter,
   MessageSignatureData signature,
   int deletableAfter,
   MessageSignatureData signature,
   int deletableAfter
) {
   public io(MessageSignatureData messagesignaturedata, int i) {
      this.signature = messagesignaturedata;
      this.deletableAfter = i;
   }
}
