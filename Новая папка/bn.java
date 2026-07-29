import com.google.gson.annotations.SerializedName;
import font.FontAtlas;
import font.FontMetrics;
import java.util.List;

public final class bn {
   private FontAtlas atlas;
   private FontMetrics metrics;
   private List<pqg> glyphs;
   @SerializedName("kerning")
   private List<um> kernings;

   public FontAtlas a() {
      return this.atlas;
   }

   public FontMetrics b() {
      return this.metrics;
   }

   public List<pqg> c() {
      return this.glyphs;
   }

   public List<um> d() {
      return this.kernings;
   }
}
