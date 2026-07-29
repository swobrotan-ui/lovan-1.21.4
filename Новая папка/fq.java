import net.minecraft.client.MinecraftClient;
import util.ChatUtil;

class fq implements uy {
   // $VF: synthetic field
   final MinecraftClient yN;
   // $VF: synthetic field
   final si jJ;

   fq(si si, MinecraftClient minecraftclient) {
      this.jJ = si;
      this.yN = minecraftclient;
   }

   @Override
   public void onResult(String s, String s1, String s2) {
      this.yN.execute(() -> {
         ChatUtil.sendMessage("§7[§b" + s1 + " → " + s2 + "§7] §f" + s);
         this.jJ.ajB = false;
      });
   }

   @Override
   public void onError(String s) {
      this.yN.execute(() -> {
         ChatUtil.sendError(s);
         this.jJ.ajB = false;
      });
   }
}
