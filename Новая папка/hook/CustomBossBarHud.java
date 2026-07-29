package hook;

import com.google.common.collect.Maps;
import core.ClientMain;
import java.util.Map;
import java.util.UUID;
import module.NoOverlayModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBar.Style;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

@Environment(EnvType.CLIENT)
public class CustomBossBarHud extends BossBarHud {
   private static final int WIDTH = 182;
   private static final int HEIGHT = 5;
   private static final Identifier[] BACKGROUND_TEXTURES = new Identifier[]{
      Identifier.ofVanilla("boss_bar/pink_background"),
      Identifier.ofVanilla("boss_bar/blue_background"),
      Identifier.ofVanilla("boss_bar/red_background"),
      Identifier.ofVanilla("boss_bar/green_background"),
      Identifier.ofVanilla("boss_bar/yellow_background"),
      Identifier.ofVanilla("boss_bar/purple_background"),
      Identifier.ofVanilla("boss_bar/white_background")
   };
   private static final Identifier[] PROGRESS_TEXTURES = new Identifier[]{
      Identifier.ofVanilla("boss_bar/pink_progress"),
      Identifier.ofVanilla("boss_bar/blue_progress"),
      Identifier.ofVanilla("boss_bar/red_progress"),
      Identifier.ofVanilla("boss_bar/green_progress"),
      Identifier.ofVanilla("boss_bar/yellow_progress"),
      Identifier.ofVanilla("boss_bar/purple_progress"),
      Identifier.ofVanilla("boss_bar/white_progress")
   };
   private static final Identifier[] NOTCHED_BACKGROUND_TEXTURES = new Identifier[]{
      Identifier.ofVanilla("boss_bar/notched_6_background"),
      Identifier.ofVanilla("boss_bar/notched_10_background"),
      Identifier.ofVanilla("boss_bar/notched_12_background"),
      Identifier.ofVanilla("boss_bar/notched_20_background")
   };
   private static final Identifier[] NOTCHED_PROGRESS_TEXTURES = new Identifier[]{
      Identifier.ofVanilla("boss_bar/notched_6_progress"),
      Identifier.ofVanilla("boss_bar/notched_10_progress"),
      Identifier.ofVanilla("boss_bar/notched_12_progress"),
      Identifier.ofVanilla("boss_bar/notched_20_progress")
   };
   private final MinecraftClient client;
   final Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

   public CustomBossBarHud(MinecraftClient minecraftclient) {
      super(minecraftclient);
      this.client = minecraftclient;
   }

   public void render(DrawContext drawcontext) {
      if (!this.bossBars.isEmpty()) {
         Profiler profiler = Profilers.get();
         profiler.push("bossHealth");
         NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
         if (nooverlaymodule != null && nooverlaymodule.isEnabled() && nooverlaymodule.bossBarSetting.getValue()) {
            return;
         }

         int i = drawcontext.getScaledWindowWidth();
         byte b0 = 12;

         for (ClientBossBar clientbossbar : this.bossBars.values()) {
            int j = i / 2 - 91;
            this.renderBossBar(drawcontext, j, b0, clientbossbar);
            Text text = clientbossbar.getName();
            int k = this.client.textRenderer.getWidth(text);
            int l = i / 2 - k / 2;
            int i1 = b0 - 9;
            drawcontext.drawTextWithShadow(this.client.textRenderer, text, l, i1, 16777215);
            b0 += 19;
            if (b0 >= drawcontext.getScaledWindowHeight() / 3) {
               break;
            }
         }

         profiler.pop();
      }
   }

   private void renderBossBar(DrawContext drawcontext, int i, int j, BossBar bossbar) {
      this.renderBossBar(drawcontext, i, j, bossbar, 182, BACKGROUND_TEXTURES, NOTCHED_BACKGROUND_TEXTURES);
      int k = MathHelper.lerpPositive(bossbar.getPercent(), 0, 182);
      if (k > 0) {
         this.renderBossBar(drawcontext, i, j, bossbar, k, PROGRESS_TEXTURES, NOTCHED_PROGRESS_TEXTURES);
      }
   }

   private void renderBossBar(DrawContext drawcontext, int i, int j, BossBar bossbar, int k, Identifier[] aidentifier, Identifier[] aidentifier1) {
      drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, aidentifier[bossbar.getColor().ordinal()], 182, 5, 0, 0, i, j, k, 5);
      if (bossbar.getStyle() != Style.PROGRESS) {
         drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, aidentifier1[bossbar.getStyle().ordinal() - 1], 182, 5, 0, 0, i, j, k, 5);
      }
   }

   public void handlePacket(BossBarS2CPacket bossbars2cpacket) {
      bossbars2cpacket.accept(new jz(this));
   }

   public void clear() {
      this.bossBars.clear();
   }

   public boolean shouldPlayDragonMusic() {
      if (!this.bossBars.isEmpty()) {
         for (BossBar bossbar : this.bossBars.values()) {
            if (bossbar.hasDragonMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenSky() {
      if (!this.bossBars.isEmpty()) {
         for (BossBar bossbar : this.bossBars.values()) {
            if (bossbar.shouldDarkenSky()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldThickenFog() {
      if (!this.bossBars.isEmpty()) {
         for (BossBar bossbar : this.bossBars.values()) {
            if (bossbar.shouldThickenFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
