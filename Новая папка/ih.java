import com.google.common.annotations.VisibleForTesting;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
@VisibleForTesting
enum ih {
   RENDER_BOTH_HANDS(true, true),
   RENDER_MAIN_HAND_ONLY(true, false),
   RENDER_OFF_HAND_ONLY(false, true);

   final boolean renderMainHand;
   final boolean renderOffHand;

   private ih(boolean flag, boolean flag1) {
      this.renderMainHand = flag;
      this.renderOffHand = flag1;
   }

   public static ih shouldOnlyRender(Hand hand) {
      return hand == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
   }

   // $VF: synthetic method
   private static ih[] $values() {
      return new ih[]{RENDER_BOTH_HANDS, RENDER_MAIN_HAND_ONLY, RENDER_OFF_HAND_ONLY};
   }
}
