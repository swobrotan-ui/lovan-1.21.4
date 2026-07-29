import net.minecraft.client.MinecraftClient;
import util.ChatUtil;

class gd implements uy {
   // $VF: synthetic field
   final si mi;

   gd(si si) {
      this.mi = si;
   }

   @Override
   public void onResult(String s, String s1, String s2) {
      MinecraftClient.getInstance().execute(() -> {
         si.access$000(this.mi).setText(s);
         si.access$100(this.mi).setCursor(s.length(), false);
         ChatUtil.sendSuccess("Переведено (" + s1 + " → " + s2 + ")");
         this.mi.ajB = false;
      });
   }

   @Override
   public void onError(String s) {
      MinecraftClient.getInstance().execute(() -> {
         ChatUtil.sendError(s);
         this.mi.ajB = false;
      });
   }
}
