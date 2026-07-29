package render;

import com.mojang.authlib.GameProfile;
import core.ClientMain;
import core.FriendManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import module.TabTweaksModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScoreboardCriterion.RenderType;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nullables;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomPlayerListHud extends PlayerListHud {
   private static final Identifier PING_UNKNOWN_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_unknown");
   private static final Identifier PING_1_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_1");
   private static final Identifier PING_2_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_2");
   private static final Identifier PING_3_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_3");
   private static final Identifier PING_4_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_4");
   private static final Identifier PING_5_ICON_TEXTURE = Identifier.ofVanilla("icon/ping_5");
   private static final Identifier CONTAINER_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/container_blinking");
   private static final Identifier CONTAINER_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/container");
   private static final Identifier FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/full_blinking");
   private static final Identifier HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/half_blinking");
   private static final Identifier ABSORBING_FULL_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/absorbing_full_blinking");
   private static final Identifier FULL_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/full");
   private static final Identifier ABSORBING_HALF_HEART_BLINKING_TEXTURE = Identifier.ofVanilla("hud/heart/absorbing_half_blinking");
   private static final Identifier HALF_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/half");
   private static final Comparator<PlayerListEntry> ENTRY_ORDERING = Comparator.<PlayerListEntry>comparingInt(playerlistentry -> {
      return -playerlistentry.getListOrder();
   }).thenComparingInt(playerlistentry -> {
      return playerlistentry.getGameMode() == GameMode.SPECTATOR ? 1 : 0;
   }).thenComparing(playerlistentry -> {
      return (String)Nullables.mapOrElse(playerlistentry.getScoreboardTeam(), Team::getName, "");
   }).thenComparing(playerlistentry -> {
      return playerlistentry.getProfile().getName();
   }, String::compareToIgnoreCase);
   public static final int MAX_ROWS = 20;
   private final MinecraftClient client;
   private final InGameHud inGameHud;
   @Nullable
   private Text footer;
   @Nullable
   private Text header;
   private boolean visible;
   private final Map<UUID, w> hearts = new Object2ObjectOpenHashMap();
   private kq tabAnimation = null;
   private long lastVisibilityChange = 0L;
   private boolean wasVisible = false;
   private boolean shouldRenderForAnimation = false;

   public CustomPlayerListHud(MinecraftClient minecraftclient, InGameHud ingamehud) {
      super(minecraftclient, ingamehud);
      this.client = minecraftclient;
      this.inGameHud = ingamehud;
   }

   public Text getPlayerName(PlayerListEntry playerlistentry) {
      return playerlistentry.getDisplayName() != null
         ? this.applyGameModeFormatting(playerlistentry, playerlistentry.getDisplayName().copy())
         : this.applyGameModeFormatting(
            playerlistentry, Team.decorateName(playerlistentry.getScoreboardTeam(), Text.literal(playerlistentry.getProfile().getName()))
         );
   }

   private Text applyGameModeFormatting(PlayerListEntry playerlistentry, MutableText mutabletext) {
      return playerlistentry.getGameMode() == GameMode.SPECTATOR ? mutabletext.formatted(Formatting.ITALIC) : mutabletext;
   }

   public void setVisible(boolean flag) {
      if (this.tabAnimation == null) {
         this.tabAnimation = new kq();
         this.tabAnimation.a();
      }

      if (this.wasVisible != flag) {
         this.hearts.clear();
         this.wasVisible = flag;
         this.tabAnimation.e();
         this.tabAnimation.f(flag);
         this.lastVisibilityChange = System.currentTimeMillis();
         if (flag) {
            MutableText mutabletext = Texts.join(this.collectPlayerEntries(), Text.literal(", "), this::getPlayerName);
            this.client.getNarratorManager().narrate(Text.translatable("multiplayer.player.list.narration", new Object[]{mutabletext}));
         }
      }

      TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().<TabTweaksModule>getModule(TabTweaksModule.class);
      boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
      if (flag1 && this.tabAnimation.h() > 0.001F) {
         this.visible = true;
         this.shouldRenderForAnimation = !flag;
      } else {
         this.visible = flag;
         this.shouldRenderForAnimation = false;
      }
   }

   private List<PlayerListEntry> collectPlayerEntries() {
      return this.client.player.networkHandler.getListedPlayerListEntries().stream().sorted(ENTRY_ORDERING).limit(80L).toList();
   }

   public void render(DrawContext drawcontext, int i, Scoreboard scoreboard, @Nullable ScoreboardObjective scoreboardobjective) {
      TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().<TabTweaksModule>getModule(TabTweaksModule.class);
      boolean flag = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
      float f = flag && tabtweaksmodule != null ? tabtweaksmodule.d().getFloatValue() : 6.0F;
      float f1 = flag && tabtweaksmodule != null ? 300.0F : 150.0F;
      if (this.tabAnimation == null) {
         this.tabAnimation = new kq();
         this.tabAnimation.a();
         this.tabAnimation.f(this.visible);
         if (this.visible) {
            this.tabAnimation.d(1.0F);
         }
      }

      this.tabAnimation.i(f);
      if (flag) {
         this.tabAnimation.b();
         if (this.tabAnimation.h() < 0.001F) {
            if (this.shouldRenderForAnimation) {
               this.visible = false;
               this.shouldRenderForAnimation = false;
            }

            return;
         }

         if (this.shouldRenderForAnimation && this.tabAnimation.h() > 0.001F) {
            this.visible = true;
         }
      } else {
         this.tabAnimation.d(this.visible ? 1.0F : 0.0F);
      }

      float f2 = this.tabAnimation.h();
      float f3 = xsx.a(f2);
      if (flag || this.visible) {
         drawcontext.getMatrices().push();
         if (flag) {
            float f4 = (1.0F - f3) * -f1;
            drawcontext.getMatrices().translate(0.0F, f4, 0.0F);
         }

         List list1 = this.collectPlayerEntries();
         ArrayList arraylist = new ArrayList(list1.size());
         int j = this.client.textRenderer.getWidth(" ");
         int k = 0;
         int l = 0;

         for (PlayerListEntry playerlistentry : list1) {
            Text text = this.getPlayerName(playerlistentry);
            k = Math.max(k, this.client.textRenderer.getWidth(text));
            int i1 = 0;
            MutableText mutabletext = null;
            int j1 = 0;
            if (scoreboardobjective != null) {
               ScoreHolder scoreholder = ScoreHolder.fromProfile(playerlistentry.getProfile());
               ReadableScoreboardScore readablescoreboardscore = scoreboard.getScore(scoreholder, scoreboardobjective);
               if (readablescoreboardscore != null) {
                  i1 = readablescoreboardscore.getScore();
               }

               if (scoreboardobjective.getRenderType() != RenderType.HEARTS) {
                  NumberFormat numberformat = scoreboardobjective.getNumberFormatOr(StyledNumberFormat.YELLOW);
                  mutabletext = ReadableScoreboardScore.getFormattedScore(readablescoreboardscore, numberformat);
                  j1 = this.client.textRenderer.getWidth(mutabletext);
                  l = Math.max(l, j1 > 0 ? j + j1 : 0);
               }
            }

            arraylist.add(new tp(text, i1, mutabletext, j1));
         }

         if (!this.hearts.isEmpty()) {
            Set set = list1.stream().<UUID>map(playerlistentry3 -> {
               return playerlistentry3.getProfile().getId();
            }).collect(Collectors.toSet());
            this.hearts.keySet().removeIf(uuid -> {
               return !set.contains(uuid);
            });
         }

         int i3 = list1.size();
         int j3 = i3;
         int k3;
         if (tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.f().getValue()) {
            k3 = tabtweaksmodule.g().getIntValue();
            j3 = (i3 + k3 - 1) / k3;
         } else {
            for (k3 = 1; j3 > 20; j3 = (i3 + k3 - 1) / k3) {
               k3++;
            }
         }

         boolean flag4 = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
         boolean flag5 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.e().getValue();
         int l3 = 13;
         if (flag5) {
            for (PlayerListEntry playerlistentry2 : list1) {
               int k4 = playerlistentry2.getLatency();
               String s = k4 == 0 ? "?" : String.valueOf(k4);
               l3 = Math.max(l3, this.client.textRenderer.getWidth(s) + 4);
            }
         }

         int i4;
         if (scoreboardobjective != null) {
            if (scoreboardobjective.getRenderType() == RenderType.HEARTS) {
               i4 = 90;
            } else {
               i4 = l;
            }
         } else {
            i4 = 0;
         }

         int j4 = Math.min(k3 * ((flag4 ? 9 : 0) + k + i4 + l3), i - 50) / k3;
         int l4 = i / 2 - (j4 * k3 + (k3 - 1) * 5) / 2;
         int i5 = 10;
         int k1 = j4 * k3 + (k3 - 1) * 5;
         boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.b().getValue();
         List list = null;
         if (this.header != null && !flag1) {
            list = this.client.textRenderer.wrapLines(this.header, i - 50);

            for (OrderedText orderedtext : list) {
               k1 = Math.max(k1, this.client.textRenderer.getWidth(orderedtext));
            }
         }

         List list2 = null;
         if (this.footer != null && !flag1) {
            list2 = this.client.textRenderer.wrapLines(this.footer, i - 50);

            for (OrderedText orderedtext1 : list2) {
               k1 = Math.max(k1, this.client.textRenderer.getWidth(orderedtext1));
            }
         }

         if (list != null) {
            drawcontext.fill(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + list.size() * 9, Integer.MIN_VALUE);

            for (OrderedText orderedtext2 : list) {
               int l1 = this.client.textRenderer.getWidth(orderedtext2);
               drawcontext.drawTextWithShadow(this.client.textRenderer, orderedtext2, i / 2 - l1 / 2, i5, -1);
               i5 += 9;
            }

            i5++;
         }

         drawcontext.fill(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + j3 * 9, Integer.MIN_VALUE);
         int j5 = this.client.options.getTextBackgroundColor(553648127);

         for (int k5 = 0; k5 < i3; k5++) {
            int l5 = k5 / j3;
            int i2 = k5 % j3;
            int j2 = l4 + l5 * j4 + l5 * 5;
            int k2 = i5 + i2 * 9;
            PlayerListEntry playerlistentry1 = k5 < list1.size() ? (PlayerListEntry)list1.get(k5) : null;
            boolean flag2 = playerlistentry1 != null && this.isFriend(playerlistentry1.getProfile().getName());
            int l2;
            if (flag2 && tabtweaksmodule != null && tabtweaksmodule.isEnabled()) {
               l2 = new Color(0, 255, 234, 80).getRGB();
            } else {
               l2 = j5;
            }

            drawcontext.fill(j2, k2, j2 + j4, k2 + 8, l2);
            if (k5 < list1.size()) {
               tp tp = (tp)arraylist.get(k5);
               GameProfile gameprofile = playerlistentry1.getProfile();
               if (flag4) {
                  PlayerEntity playerentity = this.client.world.getPlayerByUuid(gameprofile.getId());
                  boolean flag3 = playerentity != null && LivingEntityRenderer.shouldFlipUpsideDown(playerentity);
                  PlayerSkinDrawer.draw(drawcontext, playerlistentry1.getSkinTextures().texture(), j2, k2, 8, playerlistentry1.shouldShowHat(), flag3, -1);
                  j2 += 9;
               }

               drawcontext.drawTextWithShadow(
                  this.client.textRenderer, tp.name, j2, k2, playerlistentry1.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1
               );
               if (scoreboardobjective != null && playerlistentry1.getGameMode() != GameMode.SPECTATOR) {
                  int j6 = j2 + k + 1;
                  int k6 = j6 + i4;
                  if (k6 - j6 > 5) {
                     this.renderScoreboardObjective(scoreboardobjective, k2, tp, j6, k6, gameprofile.getId(), drawcontext);
                  }
               }

               this.renderLatencyIcon(drawcontext, j4, j2 - (flag4 ? 9 : 0), k2, playerlistentry1);
            }
         }

         if (list2 != null) {
            i5 += j3 * 9 + 1;
            drawcontext.fill(i / 2 - k1 / 2 - 1, i5 - 1, i / 2 + k1 / 2 + 1, i5 + list2.size() * 9, Integer.MIN_VALUE);

            for (OrderedText orderedtext3 : list2) {
               int i6 = this.client.textRenderer.getWidth(orderedtext3);
               drawcontext.drawTextWithShadow(this.client.textRenderer, orderedtext3, i / 2 - i6 / 2, i5, -1);
               i5 += 9;
            }
         }

         drawcontext.getMatrices().pop();
      }
   }

   private boolean isFriend(String s) {
      FriendManager friendmanager = FriendManager.getInstance();
      return friendmanager != null && friendmanager.isFriend(s);
   }

   protected void renderLatencyIcon(DrawContext drawcontext, int i, int j, int k, PlayerListEntry playerlistentry) {
      TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().<TabTweaksModule>getModule(TabTweaksModule.class);
      boolean flag = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.e().getValue();
      if (flag) {
         int j1 = playerlistentry.getLatency();
         String s = j1 == 0 ? "?" : String.valueOf(j1);
         int l;
         if (j1 < 0 || j1 == 0) {
            l = -5592406;
         } else if (j1 < 75) {
            l = -16711936;
         } else if (j1 < 150) {
            l = -11141291;
         } else if (j1 < 300) {
            l = -256;
         } else if (j1 < 600) {
            l = -43691;
         } else {
            l = -65536;
         }

         int i1 = this.client.textRenderer.getWidth(s);
         drawcontext.getMatrices().push();
         drawcontext.getMatrices().translate(0.0F, 0.0F, 100.0F);
         drawcontext.drawTextWithShadow(this.client.textRenderer, s, j + i - i1 - 1, k, l);
         drawcontext.getMatrices().pop();
      } else {
         Identifier identifier;
         if (playerlistentry.getLatency() < 0) {
            identifier = PING_UNKNOWN_ICON_TEXTURE;
         } else if (playerlistentry.getLatency() < 150) {
            identifier = PING_5_ICON_TEXTURE;
         } else if (playerlistentry.getLatency() < 300) {
            identifier = PING_4_ICON_TEXTURE;
         } else if (playerlistentry.getLatency() < 600) {
            identifier = PING_3_ICON_TEXTURE;
         } else if (playerlistentry.getLatency() < 1000) {
            identifier = PING_2_ICON_TEXTURE;
         } else {
            identifier = PING_1_ICON_TEXTURE;
         }

         drawcontext.getMatrices().push();
         drawcontext.getMatrices().translate(0.0F, 0.0F, 100.0F);
         drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier, j + i - 11, k, 10, 8);
         drawcontext.getMatrices().pop();
      }
   }

   private void renderScoreboardObjective(ScoreboardObjective scoreboardobjective, int i, tp tp, int j, int k, UUID uuid, DrawContext drawcontext) {
      if (scoreboardobjective.getRenderType() == RenderType.HEARTS) {
         this.renderHearts(i, j, k, uuid, drawcontext, tp.score);
      } else {
         if (tp.formattedScore != null) {
            drawcontext.drawTextWithShadow(this.client.textRenderer, tp.formattedScore, k - tp.scoreWidth, i, 16777215);
         }
      }
   }

   private void renderHearts(int i, int j, int k, UUID uuid, DrawContext drawcontext, int l) {
      w w = this.hearts.computeIfAbsent(uuid, uuid1 -> {
         return new w(l);
      });
      w.tick(l, this.inGameHud.getTicks());
      int i1 = MathHelper.ceilDiv(Math.max(l, w.getPrevScore()), 2);
      int j1 = Math.max(l, Math.max(w.getPrevScore(), 20)) / 2;
      boolean flag = w.useHighlighted(this.inGameHud.getTicks());
      if (i1 > 0) {
         int k1 = MathHelper.floor(Math.min((float)(k - j - 4) / j1, 9.0F));
         if (k1 <= 3) {
            float f1 = MathHelper.clamp(l / 20.0F, 0.0F, 1.0F);
            int j2 = (int)((1.0F - f1) * 255.0F) << 16 | (int)(f1 * 255.0F) << 8;
            float f = l / 2.0F;
            MutableText mutabletext = Text.translatable("multiplayer.player.list.hp", new Object[]{f});
            MutableText mutabletext1;
            if (k - this.client.textRenderer.getWidth(mutabletext) >= j) {
               mutabletext1 = mutabletext;
            } else {
               mutabletext1 = Text.literal(Float.toString(f));
            }

            drawcontext.drawTextWithShadow(this.client.textRenderer, mutabletext1, (k + j - this.client.textRenderer.getWidth(mutabletext1)) / 2, i, j2);
            return;
         }

         Identifier identifier = flag ? CONTAINER_HEART_BLINKING_TEXTURE : CONTAINER_HEART_TEXTURE;

         for (int l1 = i1; l1 < j1; l1++) {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier, j + l1 * k1, i, 9, 9);
         }

         for (int i2 = 0; i2 < i1; i2++) {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier, j + i2 * k1, i, 9, 9);
            if (flag) {
               if (i2 * 2 + 1 < w.getPrevScore()) {
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, FULL_HEART_BLINKING_TEXTURE, j + i2 * k1, i, 9, 9);
               }

               if (i2 * 2 + 1 == w.getPrevScore()) {
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HALF_HEART_BLINKING_TEXTURE, j + i2 * k1, i, 9, 9);
               }
            }

            if (i2 * 2 + 1 < l) {
               drawcontext.drawGuiTexture(
                  RenderLayer::getGuiTextured, i2 >= 10 ? ABSORBING_FULL_HEART_BLINKING_TEXTURE : FULL_HEART_TEXTURE, j + i2 * k1, i, 9, 9
               );
            }

            if (i2 * 2 + 1 == l) {
               drawcontext.drawGuiTexture(
                  RenderLayer::getGuiTextured, i2 >= 10 ? ABSORBING_HALF_HEART_BLINKING_TEXTURE : HALF_HEART_TEXTURE, j + i2 * k1, i, 9, 9
               );
            }
         }
      }
   }

   public void setFooter(@Nullable Text text) {
      this.footer = text;
   }

   public void setHeader(@Nullable Text text) {
      this.header = text;
   }

   public void clear() {
      this.header = null;
      this.footer = null;
   }

   public boolean isVisible() {
      return this.visible;
   }
}
