import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
record tp(
   Text name,
   int score,
   @Nullable Text formattedScore,
   int scoreWidth,
   Text name,
   int score,
   @Nullable Text formattedScore,
   int scoreWidth,
   Text name,
   int score,
   @Nullable Text formattedScore,
   int scoreWidth,
   Text name,
   int score,
   @Nullable Text formattedScore,
   int scoreWidth
) {
   tp(Text text, int i, @Nullable Text text1, int j) {
      this.name = text;
      this.score = i;
      this.formattedScore = text1;
      this.scoreWidth = j;
   }
}
