import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
class w {
   private static final long COOLDOWN_TICKS = 20L;
   private static final long SCORE_DECREASE_HIGHLIGHT_TICKS = 20L;
   private static final long SCORE_INCREASE_HIGHLIGHT_TICKS = 10L;
   private int score;
   private int prevScore;
   private long lastScoreChangeTick;
   private long highlightEndTick;

   public w(int i) {
      this.prevScore = i;
      this.score = i;
   }

   public void tick(int i, long j) {
      if (i != this.score) {
         long k = i < this.score ? 20L : 10L;
         this.highlightEndTick = j + k;
         this.score = i;
         this.lastScoreChangeTick = j;
      }

      if (j - this.lastScoreChangeTick > 20L) {
         this.prevScore = i;
      }
   }

   public int getPrevScore() {
      return this.prevScore;
   }

   public boolean useHighlighted(long i) {
      return this.highlightEndTick > i && (this.highlightEndTick - i) % 6L >= 3L;
   }
}
