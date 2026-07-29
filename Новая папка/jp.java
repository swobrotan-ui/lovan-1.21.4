import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
record jp(
   Text name, Text score, int scoreWidth, Text name, Text score, int scoreWidth, Text name, Text score, int scoreWidth, Text name, Text score, int scoreWidth
) {
   jp(Text text, Text text1, int i) {
      this.name = text;
      this.score = text1;
      this.scoreWidth = i;
   }
}
