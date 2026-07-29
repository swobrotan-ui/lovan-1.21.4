import hook.CustomBossBarHud;
import java.util.UUID;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.entity.boss.BossBar.Color;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket.Consumer;
import net.minecraft.text.Text;

class jz implements Consumer {
   // $VF: synthetic field
   final CustomBossBarHud this$0;

   jz(CustomBossBarHud custombossbarhud) {
      this.this$0 = custombossbarhud;
   }

   public void add(UUID uuid, Text text, float f, Color color, Style style, boolean flag, boolean flag1, boolean flag2) {
      this.this$0.bossBars.put(uuid, new ClientBossBar(uuid, text, f, color, style, flag, flag1, flag2));
   }

   public void remove(UUID uuid) {
      this.this$0.bossBars.remove(uuid);
   }

   public void updateProgress(UUID uuid, float f) {
      this.this$0.bossBars.get(uuid).setPercent(f);
   }

   public void updateName(UUID uuid, Text text) {
      this.this$0.bossBars.get(uuid).setName(text);
   }

   public void updateStyle(UUID uuid, Color color, Style style) {
      ClientBossBar clientbossbar = this.this$0.bossBars.get(uuid);
      clientbossbar.setColor(color);
      clientbossbar.setStyle(style);
   }

   public void updateProperties(UUID uuid, boolean flag, boolean flag1, boolean flag2) {
      ClientBossBar clientbossbar = this.this$0.bossBars.get(uuid);
      clientbossbar.setDarkenSky(flag);
      clientbossbar.setDragonMusic(flag1);
      clientbossbar.setThickenFog(flag2);
   }
}
