package render;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import enum.HeartType;
import hook.CustomBossBarHud;
import hud.HudManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import module.AimAssistModule;
import module.AutoSwapModule;
import module.CleanHudModule;
import module.HitboxModule;
import module.HudModule;
import module.NoOverlayModule;
import module.TabTweaksModule;
import module.TracersModule;
import module.WaypointsModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.LayeredDrawer;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fStack;

@Environment(EnvType.CLIENT)
public class CustomInGameHud extends InGameHud {
   private static final Identifier CROSSHAIR_TEXTURE = Identifier.ofVanilla("hud/crosshair");
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_full");
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_background");
   private static final Identifier CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/crosshair_attack_indicator_progress");
   private static final Identifier EFFECT_BACKGROUND_AMBIENT_TEXTURE = Identifier.ofVanilla("hud/effect_background_ambient");
   private static final Identifier EFFECT_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/effect_background");
   private static final Identifier HOTBAR_TEXTURE = Identifier.ofVanilla("hud/hotbar");
   private static final Identifier HOTBAR_SELECTION_TEXTURE = Identifier.ofVanilla("hud/hotbar_selection");
   private static final Identifier HOTBAR_OFFHAND_LEFT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_left");
   private static final Identifier HOTBAR_OFFHAND_RIGHT_TEXTURE = Identifier.ofVanilla("hud/hotbar_offhand_right");
   private static final Identifier HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/hotbar_attack_indicator_background");
   private static final Identifier HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/hotbar_attack_indicator_progress");
   private static final Identifier JUMP_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/jump_bar_background");
   private static final Identifier JUMP_BAR_COOLDOWN_TEXTURE = Identifier.ofVanilla("hud/jump_bar_cooldown");
   private static final Identifier JUMP_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/jump_bar_progress");
   private static final Identifier EXPERIENCE_BAR_BACKGROUND_TEXTURE = Identifier.ofVanilla("hud/experience_bar_background");
   private static final Identifier EXPERIENCE_BAR_PROGRESS_TEXTURE = Identifier.ofVanilla("hud/experience_bar_progress");
   private static final Identifier ARMOR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/armor_empty");
   private static final Identifier ARMOR_HALF_TEXTURE = Identifier.ofVanilla("hud/armor_half");
   private static final Identifier ARMOR_FULL_TEXTURE = Identifier.ofVanilla("hud/armor_full");
   private static final Identifier FOOD_EMPTY_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_empty_hunger");
   private static final Identifier FOOD_HALF_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_half_hunger");
   private static final Identifier FOOD_FULL_HUNGER_TEXTURE = Identifier.ofVanilla("hud/food_full_hunger");
   private static final Identifier FOOD_EMPTY_TEXTURE = Identifier.ofVanilla("hud/food_empty");
   private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
   private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");
   private static final Identifier AIR_TEXTURE = Identifier.ofVanilla("hud/air");
   private static final Identifier AIR_BURSTING_TEXTURE = Identifier.ofVanilla("hud/air_bursting");
   private static final Identifier AIR_EMPTY_TEXTURE = Identifier.ofVanilla("hud/air_empty");
   private static final Identifier VEHICLE_CONTAINER_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_container");
   private static final Identifier VEHICLE_FULL_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_full");
   private static final Identifier VEHICLE_HALF_HEART_TEXTURE = Identifier.ofVanilla("hud/heart/vehicle_half");
   private static final Identifier VIGNETTE_TEXTURE = Identifier.ofVanilla("textures/misc/vignette.png");
   public static final Identifier NAUSEA_TEXTURE = Identifier.ofVanilla("textures/misc/nausea.png");
   private static final Identifier SPYGLASS_SCOPE = Identifier.ofVanilla("textures/misc/spyglass_scope.png");
   private static final Identifier POWDER_SNOW_OUTLINE = Identifier.ofVanilla("textures/misc/powder_snow_outline.png");
   private static final Comparator<ScoreboardEntry> SCOREBOARD_ENTRY_COMPARATOR = Comparator.comparing(ScoreboardEntry::value)
      .reversed()
      .thenComparing(ScoreboardEntry::owner, String.CASE_INSENSITIVE_ORDER);
   private static final Text DEMO_EXPIRED_MESSAGE = Text.translatable("demo.demoExpired");
   private static final Text SAVING_LEVEL_TEXT = Text.translatable("menu.savingLevel");
   private static final float field_32168 = 5.0F;
   private static final int field_32169 = 10;
   private static final int field_32170 = 10;
   private static final String SCOREBOARD_JOINER = ": ";
   private static final float field_32172 = 0.2F;
   private static final int field_33942 = 9;
   private static final int field_33943 = 8;
   private static final int field_54914 = 10;
   private static final int field_54915 = 9;
   private static final int field_54916 = 8;
   private static final int field_54917 = 2;
   private static final int SUBMERGED_IN_WATER_AIR_BUBBLE_DELAY = 1;
   private static final float field_54920 = 0.5F;
   private static final float field_54921 = 0.1F;
   private static final float field_54922 = 1.0F;
   private static final float field_54923 = 0.1F;
   private static final int field_54924 = 3;
   private static final int field_54925 = 5;
   private static final float field_35431 = 0.2F;
   private static final int field_52769 = 5;
   private static final int field_52770 = 5;
   private final Random random = Random.create();
   private final MinecraftClient client;
   private final CustomChatHud chatHud;
   private int ticks;
   @Nullable
   private Text overlayMessage;
   private int overlayRemaining;
   private boolean overlayTinted;
   private boolean canShowChatDisabledScreen;
   public float vignetteDarkness = 1.0F;
   private int heldItemTooltipFade;
   private ItemStack currentStack = ItemStack.EMPTY;
   private final DebugHud debugHud;
   private final SubtitlesHud subtitlesHud;
   private final SpectatorHud spectatorHud;
   private final CustomPlayerListHud PlayerListHudClass;
   private final CustomBossBarHud bossBarHud;
   private int titleRemainTicks;
   @Nullable
   private Text title;
   @Nullable
   private Text subtitle;
   private int titleFadeInTicks;
   private int titleStayTicks;
   private int titleFadeOutTicks;
   private int lastHealthValue;
   private int renderHealthValue;
   private long lastHealthCheckTime;
   private long heartJumpEndTick;
   private int lastBurstBubble;
   private float autosaveIndicatorAlpha;
   private float lastAutosaveIndicatorAlpha;
   private final LayeredDrawer layeredDrawer = new LayeredDrawer();
   private float spyglassScale;
   private wa hotBar;

   public CustomInGameHud(MinecraftClient minecraftclient) {
      super(minecraftclient);
      this.client = minecraftclient;
      this.debugHud = new DebugHud(minecraftclient);
      this.spectatorHud = new SpectatorHud(minecraftclient);
      this.chatHud = new CustomChatHud(minecraftclient);
      this.PlayerListHudClass = new CustomPlayerListHud(minecraftclient, this);
      this.bossBarHud = new CustomBossBarHud(minecraftclient);
      this.subtitlesHud = new SubtitlesHud(minecraftclient);
      this.setDefaultTitleFade();
      LayeredDrawer layereddrawer = new LayeredDrawer()
         .addLayer(this::renderMiscOverlays)
         .addLayer(this::renderCrosshair)
         .addLayer(this::renderMainHud)
         .addLayer(this::renderExperienceLevel)
         .addLayer(this::renderStatusEffectOverlay)
         .addLayer((drawcontext, rendertickcounter) -> {
            this.bossBarHud.render(drawcontext);
         });
      LayeredDrawer layereddrawer1 = new LayeredDrawer()
         .addLayer(this::renderDemoTimer)
         .addLayer((drawcontext, rendertickcounter) -> {
            if (this.debugHud.shouldShowDebugHud()) {
               this.debugHud.render(drawcontext);
            }
         })
         .addLayer(this::renderScoreboardSidebar)
         .addLayer(this::renderOverlayMessage)
         .addLayer(this::renderTitleAndSubtitle)
         .addLayer(this::renderChat)
         .addLayer((drawcontext, rendertickcounter) -> {
            this.subtitlesHud.render(drawcontext);
         });
      this.layeredDrawer.addSubDrawer(layereddrawer, () -> {
         return !minecraftclient.options.hudHidden;
      }).addLayer(this::renderSleepOverlay).addSubDrawer(layereddrawer1, () -> {
         return !minecraftclient.options.hudHidden;
      });
      this.hotBar = new wa();
      this.hotBar.dc.a(0);
   }

   public void setDefaultTitleFade() {
      this.titleFadeInTicks = 10;
      this.titleStayTicks = 70;
      this.titleFadeOutTicks = 20;
   }

   public void render(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      this.layeredDrawer.render(drawcontext, rendertickcounter);
      AutoSwapModule autoswapmodule = ClientMain.getInstance().getModuleManager().<AutoSwapModule>getModule(AutoSwapModule.class);
      if (autoswapmodule != null && autoswapmodule.isEnabled()) {
         autoswapmodule.M(drawcontext);
      }

      WaypointsModule waypointsmodule = ClientMain.getInstance().getModuleManager().<WaypointsModule>getModule(WaypointsModule.class);
      if (waypointsmodule != null) {
         waypointsmodule.d(drawcontext);
      }

      TracersModule tracersmodule = ClientMain.getInstance().getModuleManager().<TracersModule>getModule(TracersModule.class);
      if (tracersmodule != null) {
         tracersmodule.b(drawcontext);
      }

      AimAssistModule aimassistmodule = ClientMain.getInstance().getModuleManager().<AimAssistModule>getModule(AimAssistModule.class);
      if (aimassistmodule != null) {
         aimassistmodule.d(drawcontext);
      }

      this.renderPlayerList(drawcontext, rendertickcounter);
      HudManager.b().e(drawcontext);
   }

   private void renderMiscOverlays(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if (MinecraftClient.isFancyGraphicsOrBetter()) {
         this.renderVignetteOverlay(drawcontext, this.client.getCameraEntity());
      }

      float f = rendertickcounter.getLastFrameDuration();
      this.spyglassScale = MathHelper.lerp(0.5F * f, this.spyglassScale, 1.125F);
      if (this.client.options.getPerspective().isFirstPerson()) {
         if (this.client.player.isUsingSpyglass()) {
            this.renderSpyglassOverlay(drawcontext, this.spyglassScale);
         } else {
            this.spyglassScale = 0.5F;

            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
               ItemStack itemstack = this.client.player.getEquippedStack(equipmentslot);
               EquippableComponent equippablecomponent = (EquippableComponent)itemstack.get(DataComponentTypes.EQUIPPABLE);
               if (equippablecomponent != null && equippablecomponent.slot() == equipmentslot && equippablecomponent.cameraOverlay().isPresent()) {
                  this.renderOverlay(drawcontext, ((Identifier)equippablecomponent.cameraOverlay().get()).withPath(s -> {
                     return "textures/" + s + ".png";
                  }), 1.0F);
               }
            }
         }
      }

      if (this.client.player.getFrozenTicks() > 0) {
         this.renderOverlay(drawcontext, POWDER_SNOW_OUTLINE, this.client.player.getFreezingScale());
      }

      float f1 = MathHelper.lerp(rendertickcounter.getTickDelta(false), this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity);
      if (f1 > 0.0F) {
         if (!this.client.player.hasStatusEffect(StatusEffects.NAUSEA)) {
            this.renderPortalOverlay(drawcontext, f1);
            return;
         }

         float f2 = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
         if (f2 < 1.0F) {
            float f3 = f1 * (1.0F - f2);
            this.renderNauseaOverlay(drawcontext, f3);
         }
      }
   }

   private void renderSleepOverlay(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if (this.client.player.getSleepTimer() > 0) {
         Profilers.get().push("sleep");
         float f = this.client.player.getSleepTimer();
         float f1 = f / 100.0F;
         if (f1 > 1.0F) {
            f1 = 1.0F - (f - 100.0F) / 10.0F;
         }

         int i = (int)(220.0F * f1) << 24 | 1052704;
         drawcontext.fill(RenderLayer.getGuiOverlay(), 0, 0, drawcontext.getScaledWindowWidth(), drawcontext.getScaledWindowHeight(), i);
         Profilers.get().pop();
      }
   }

   private void renderOverlayMessage(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      TextRenderer textrenderer = this.getTextRenderer();
      if (this.overlayMessage != null && this.overlayRemaining > 0) {
         Profilers.get().push("overlayMessage");
         float f = this.overlayRemaining - rendertickcounter.getTickDelta(false);
         int i = (int)(f * 255.0F / 20.0F);
         if (i > 255) {
            i = 255;
         }

         if (i > 8) {
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().translate(drawcontext.getScaledWindowWidth() / 2, drawcontext.getScaledWindowHeight() - 68, 0.0F);
            int j;
            if (this.overlayTinted) {
               j = MathHelper.hsvToArgb(f / 50.0F, 0.7F, 0.6F, i);
            } else {
               j = ColorHelper.withAlpha(i, -1);
            }

            int k = textrenderer.getWidth(this.overlayMessage);
            drawcontext.drawTextWithBackground(textrenderer, this.overlayMessage, -k / 2, -4, k, j);
            drawcontext.getMatrices().pop();
         }

         Profilers.get().pop();
      }
   }

   private void renderTitleAndSubtitle(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if (this.title != null && this.titleRemainTicks > 0) {
         TextRenderer textrenderer = this.getTextRenderer();
         Profilers.get().push("titleAndSubtitle");
         float f = this.titleRemainTicks - rendertickcounter.getTickDelta(false);
         int i = 255;
         if (this.titleRemainTicks > this.titleFadeOutTicks + this.titleStayTicks) {
            float f1 = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks - f;
            i = (int)(f1 * 255.0F / this.titleFadeInTicks);
         }

         if (this.titleRemainTicks <= this.titleFadeOutTicks) {
            i = (int)(f * 255.0F / this.titleFadeOutTicks);
         }

         i = MathHelper.clamp(i, 0, 255);
         if (i > 8) {
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().translate(drawcontext.getScaledWindowWidth() / 2, drawcontext.getScaledWindowHeight() / 2, 0.0F);
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().scale(4.0F, 4.0F, 4.0F);
            int l = textrenderer.getWidth(this.title);
            int j = ColorHelper.withAlpha(i, -1);
            drawcontext.drawTextWithBackground(textrenderer, this.title, -l / 2, -10, l, j);
            drawcontext.getMatrices().pop();
            if (this.subtitle != null) {
               drawcontext.getMatrices().push();
               drawcontext.getMatrices().scale(2.0F, 2.0F, 2.0F);
               int k = textrenderer.getWidth(this.subtitle);
               drawcontext.drawTextWithBackground(textrenderer, this.subtitle, -k / 2, 5, k, j);
               drawcontext.getMatrices().pop();
            }

            drawcontext.getMatrices().pop();
         }

         Profilers.get().pop();
      }
   }

   private void renderChat(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         if (!this.chatHud.isChatFocused()) {
            Window window = this.client.getWindow();
            int i = MathHelper.floor(this.client.mouse.getX() * window.getScaledWidth() / window.getWidth());
            int j = MathHelper.floor(this.client.mouse.getY() * window.getScaledHeight() / window.getHeight());
            this.chatHud.render(drawcontext, this.ticks, i, j, false);
         }
      }
   }

   private void renderPlayerList(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      Scoreboard scoreboard = this.client.world.getScoreboard();
      ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.LIST);
      boolean flag = this.client.options.playerListKey.isPressed()
         && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getListedPlayerListEntries().size() > 1 || scoreboardobjective != null);
      TabTweaksModule tabtweaksmodule = ClientMain.getInstance().getModuleManager().<TabTweaksModule>getModule(TabTweaksModule.class);
      boolean flag1 = tabtweaksmodule != null && tabtweaksmodule.isEnabled() && tabtweaksmodule.c().getValue();
      if (flag1) {
         if (flag) {
            this.PlayerListHudClass.setVisible(true);
         } else if (!this.PlayerListHudClass.isVisible()) {
            this.PlayerListHudClass.setVisible(false);
         } else {
            this.PlayerListHudClass.setVisible(false);
         }

         if (this.PlayerListHudClass.isVisible()) {
            this.PlayerListHudClass.render(drawcontext, drawcontext.getScaledWindowWidth(), scoreboard, scoreboardobjective);
            return;
         }
      } else {
         if (!flag) {
            this.PlayerListHudClass.setVisible(false);
            return;
         }

         this.PlayerListHudClass.setVisible(true);
         this.PlayerListHudClass.render(drawcontext, drawcontext.getScaledWindowWidth(), scoreboard, scoreboardobjective);
      }
   }

   private void renderCrosshair(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      GameOptions gameoptions = this.client.options;
      if (gameoptions.getPerspective().isFirstPerson()
         && (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR || this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget))) {
         if (this.debugHud.shouldShowDebugHud() && !this.client.player.hasReducedDebugInfo() && !(Boolean)gameoptions.getReducedDebugInfo().getValue()) {
            Camera camera1 = this.client.gameRenderer.getCamera();
            Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
            matrix4fstack.pushMatrix();
            matrix4fstack.mul(drawcontext.getMatrices().peek().getPositionMatrix());
            matrix4fstack.translate(drawcontext.getScaledWindowWidth() / 2, drawcontext.getScaledWindowHeight() / 2, 0.0F);
            matrix4fstack.rotateX(-camera1.getPitch() * (float) (Math.PI / 180.0));
            matrix4fstack.rotateY(camera1.getYaw() * (float) (Math.PI / 180.0));
            matrix4fstack.scale(-1.0F, -1.0F, -1.0F);
            RenderSystem.renderCrosshair(10);
            matrix4fstack.popMatrix();
            return;
         }

         byte b0 = 15;
         drawcontext.drawGuiTexture(
            RenderLayer::getCrosshair, CROSSHAIR_TEXTURE, (drawcontext.getScaledWindowWidth() - 15) / 2, (drawcontext.getScaledWindowHeight() - 15) / 2, 15, 15
         );
         if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.CROSSHAIR) {
            HitboxModule hitboxmodule = ClientMain.getInstance().getModuleManager().<HitboxModule>getModule(HitboxModule.class);
            NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
            if (nooverlaymodule != null && nooverlaymodule.isEnabled() && nooverlaymodule.attackIndicatorSetting.getValue()) {
               return;
            }

            if (hitboxmodule != null
               && hitboxmodule.isEnabled()
               && hitboxmodule.Y().getValue()
               && this.client.targetedEntity != null
               && this.client.targetedEntity instanceof LivingEntity livingentity) {
               Box box = livingentity.getType().getDimensions().getBoxAt(livingentity.getPos());
               Camera camera = this.client.gameRenderer.getCamera();
               Vec3d vec3d = camera.getPos();
               float f = camera.getPitch();
               float f1 = camera.getYaw();
               Vec3d vec3d1 = Vec3d.fromPolar(f, f1);
               double d0 = this.client.interactionManager.getCurrentGameMode().isCreative() ? 5.0 : 4.5;
               Vec3d vec3d2 = vec3d.add(vec3d1.multiply(d0));
               Optional optional = box.raycast(vec3d, vec3d2);
               if (optional.isEmpty()) {
                  return;
               }
            }

            float f2 = this.client.player.getAttackCooldownProgress(0.0F);
            boolean flag = false;
            if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f2 >= 1.0F) {
               flag = this.client.player.getAttackCooldownProgressPerTick() > 5.0F;
               flag &= this.client.targetedEntity.isAlive();
            }

            int i = drawcontext.getScaledWindowHeight() / 2 - 7 + 16;
            int j = drawcontext.getScaledWindowWidth() / 2 - 8;
            if (flag) {
               drawcontext.drawGuiTexture(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_FULL_TEXTURE, j, i, 16, 16);
               return;
            }

            if (f2 < 1.0F) {
               int k = (int)(f2 * 17.0F);
               drawcontext.drawGuiTexture(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, j, i, 16, 4);
               drawcontext.drawGuiTexture(RenderLayer::getCrosshair, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 16, 4, 0, 0, j, i, k, 4);
            }
         }
      }
   }

   private boolean shouldRenderSpectatorCrosshair(@Nullable HitResult hitresult) {
      if (hitresult == null) {
         return false;
      } else if (hitresult.getType() == Type.ENTITY) {
         return ((EntityHitResult)hitresult).getEntity() instanceof NamedScreenHandlerFactory;
      } else if (hitresult.getType() == Type.BLOCK) {
         BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
         ClientWorld clientworld = this.client.world;
         return clientworld.getBlockState(blockpos).createScreenHandlerFactory(clientworld, blockpos) != null;
      } else {
         return false;
      }
   }

   private void renderStatusEffectOverlay(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.effectsSetting.getValue()) {
         Collection collection = this.client.player.getStatusEffects();
         if (!collection.isEmpty() && (this.client.currentScreen == null || !this.client.currentScreen.shouldHideStatusEffectHud())) {
            int i = 0;
            int j = 0;
            StatusEffectSpriteManager statuseffectspritemanager = this.client.getStatusEffectSpriteManager();
            ArrayList arraylist = Lists.newArrayListWithExpectedSize(collection.size());

            for (StatusEffectInstance statuseffectinstance : Ordering.natural().reverse().sortedCopy(collection)) {
               RegistryEntry registryentry = statuseffectinstance.getEffectType();
               if (statuseffectinstance.shouldShowIcon()) {
                  int k = drawcontext.getScaledWindowWidth();
                  byte b0 = 1;
                  if (this.client.isDemo()) {
                     b0 += 15;
                  }

                  if (((StatusEffect)registryentry.value()).isBeneficial()) {
                     i++;
                     k -= 25 * i;
                  } else {
                     j++;
                     k -= 25 * j;
                     b0 += 26;
                  }

                  float f = 1.0F;
                  if (statuseffectinstance.isAmbient()) {
                     drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, EFFECT_BACKGROUND_AMBIENT_TEXTURE, k, b0, 24, 24);
                  } else {
                     drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, EFFECT_BACKGROUND_TEXTURE, k, b0, 24, 24);
                     if (statuseffectinstance.isDurationBelow(200)) {
                        int l = statuseffectinstance.getDuration();
                        int i1 = 10 - l / 20;
                        f = MathHelper.clamp(l / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                           + MathHelper.cos(l * (float) Math.PI / 5.0F) * MathHelper.clamp(i1 / 10.0F * 0.25F, 0.0F, 0.25F);
                        f = MathHelper.clamp(f, 0.0F, 1.0F);
                     }
                  }

                  Sprite sprite = statuseffectspritemanager.getSprite(registryentry);
                  int j1 = k;
                  byte b1 = b0;
                  float f1 = f;
                  arraylist.add(() -> {
                     int i2 = ColorHelper.getWhite(f1);
                     drawcontext.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, j1 + 3, b1 + 3, 18, 18, i2);
                  });
               }
            }

            arraylist.forEach(Runnable::run);
         }
      }
   }

   private void renderMainHud(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
         this.spectatorHud.renderSpectatorMenu(drawcontext);
      } else {
         this.renderHotbar(drawcontext, rendertickcounter);
      }

      int i = drawcontext.getScaledWindowWidth() / 2 - 91;
      JumpingMount jumpingmount = this.client.player.getJumpingMount();
      if (jumpingmount != null) {
         this.renderMountJumpBar(jumpingmount, drawcontext, i);
      } else if (this.shouldRenderExperience()) {
         this.renderExperienceBar(drawcontext, i);
      }

      if (this.client.interactionManager.hasStatusBars()) {
         this.renderStatusBars(drawcontext);
      }

      this.renderMountHealth(drawcontext);
      if (this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
         this.renderHeldItemTooltip(drawcontext);
      } else {
         if (this.client.player.isSpectator()) {
            this.spectatorHud.render(drawcontext);
         }
      }
   }

   private void renderHotbar(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         PlayerEntity playerentity = this.getCameraPlayer();
         if (playerentity != null) {
            ItemStack itemstack = playerentity.getOffHandStack();
            Arm arm = playerentity.getMainArm().getOpposite();
            int i = drawcontext.getScaledWindowWidth() / 2;
            int j = drawcontext.getScaledWindowHeight();
            HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
            boolean flag = hudmodule != null && hudmodule.isEnabled() && hudmodule.n().getValue();
            if (flag) {
               this.hotBar.a(drawcontext, rendertickcounter, playerentity, itemstack, arm, i, j);
               return;
            }

            this.renderVanillaHotbar(drawcontext, rendertickcounter, playerentity, itemstack, arm, i, j);
         }
      }
   }

   private void renderVanillaHotbar(
      DrawContext drawcontext, RenderTickCounter rendertickcounter, PlayerEntity playerentity, ItemStack itemstack, Arm arm, int i, int j
   ) {
      int k = i;
      short short1 = 182;
      byte b0 = 91;
      drawcontext.getMatrices().push();
      drawcontext.getMatrices().translate(0.0F, 0.0F, -90.0F);
      drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_TEXTURE, i - 91, j - 22, 182, 22);
      drawcontext.drawGuiTexture(
         RenderLayer::getGuiTextured, HOTBAR_SELECTION_TEXTURE, i - 91 - 1 + playerentity.getInventory().selectedSlot * 20, j - 22 - 1, 24, 23
      );
      if (!itemstack.isEmpty()) {
         if (arm == Arm.LEFT) {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_OFFHAND_LEFT_TEXTURE, i - 91 - 29, j - 23, 29, 24);
         } else {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_OFFHAND_RIGHT_TEXTURE, i + 91, j - 23, 29, 24);
         }
      }

      drawcontext.getMatrices().pop();
      int l = 1;

      for (int i1 = 0; i1 < 9; i1++) {
         int j1 = k - 90 + i1 * 20 + 2;
         int k1 = j - 16 - 3;
         this.renderHotbarItem(drawcontext, j1, k1, rendertickcounter, playerentity, (ItemStack)playerentity.getInventory().main.get(i1), l++);
      }

      if (!itemstack.isEmpty()) {
         int i2 = j - 16 - 3;
         if (arm == Arm.LEFT) {
            this.renderHotbarItem(drawcontext, k - 91 - 26, i2, rendertickcounter, playerentity, itemstack, l++);
         } else {
            this.renderHotbarItem(drawcontext, k + 91 + 10, i2, rendertickcounter, playerentity, itemstack, l++);
         }
      }

      if (this.client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
         float f = this.client.player.getAttackCooldownProgress(0.0F);
         if (f < 1.0F) {
            int j2 = j - 20;
            int k2 = k + 91 + 6;
            if (arm == Arm.RIGHT) {
               k2 = k - 91 - 22;
            }

            int l1 = (int)(f * 19.0F);
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_ATTACK_INDICATOR_BACKGROUND_TEXTURE, k2, j2, 18, 18);
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, HOTBAR_ATTACK_INDICATOR_PROGRESS_TEXTURE, 18, 18, 0, 18 - l1, k2, j2 + 18 - l1, 18, l1);
         }
      }
   }

   private void renderMountJumpBar(JumpingMount jumpingmount, DrawContext drawcontext, int i) {
      Profilers.get().push("jumpBar");
      float f = this.client.player.getMountJumpStrength();
      short short1 = 182;
      int j = (int)(f * 183.0F);
      int k = drawcontext.getScaledWindowHeight() - 32 + 3;
      drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, JUMP_BAR_BACKGROUND_TEXTURE, i, k, 182, 5);
      if (jumpingmount.getJumpCooldown() > 0) {
         drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, JUMP_BAR_COOLDOWN_TEXTURE, i, k, 182, 5);
      } else if (j > 0) {
         drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, JUMP_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, i, k, j, 5);
      }

      Profilers.get().pop();
   }

   private void renderExperienceBar(DrawContext drawcontext, int i) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         Profilers.get().push("expBar");
         HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
         boolean flag = hudmodule != null && hudmodule.isEnabled() && hudmodule.n().getValue();
         if (!flag) {
            int j = this.client.player.getNextLevelExperience();
            if (j > 0) {
               short short1 = 182;
               int k = (int)(this.client.player.experienceProgress * 183.0F);
               int l = drawcontext.getScaledWindowHeight() - 32 + 3;
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, EXPERIENCE_BAR_BACKGROUND_TEXTURE, i, l, 182, 5);
               if (k > 0) {
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, EXPERIENCE_BAR_PROGRESS_TEXTURE, 182, 5, 0, 0, i, l, k, 5);
               }
            }
         }

         Profilers.get().pop();
      }
   }

   private void renderExperienceLevel(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      int i = this.client.player.experienceLevel;
      if (this.shouldRenderExperience() && i > 0) {
         Profilers.get().push("expLevel");
         String s = "" + i;
         int j = (drawcontext.getScaledWindowWidth() - this.getTextRenderer().getWidth(s)) / 2;
         int k = drawcontext.getScaledWindowHeight() - 31 - 4;
         drawcontext.drawText(this.getTextRenderer(), s, j + 1, k, 0, false);
         drawcontext.drawText(this.getTextRenderer(), s, j - 1, k, 0, false);
         drawcontext.drawText(this.getTextRenderer(), s, j, k + 1, 0, false);
         drawcontext.drawText(this.getTextRenderer(), s, j, k - 1, 0, false);
         drawcontext.drawText(this.getTextRenderer(), s, j, k, 8453920, false);
         Profilers.get().pop();
      }
   }

   private boolean shouldRenderExperience() {
      return this.client.player.getJumpingMount() == null && this.client.interactionManager.hasExperienceBar();
   }

   private void renderHeldItemTooltip(DrawContext drawcontext) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         Profilers.get().push("selectedItemName");
         if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            MutableText mutabletext = Text.empty().append(this.currentStack.getName()).formatted(this.currentStack.getRarity().getFormatting());
            if (this.currentStack.contains(DataComponentTypes.CUSTOM_NAME)) {
               mutabletext.formatted(Formatting.ITALIC);
            }

            int i = this.getTextRenderer().getWidth(mutabletext);
            int j = (drawcontext.getScaledWindowWidth() - i) / 2;
            int k = drawcontext.getScaledWindowHeight() - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
               k += 14;
            }

            int l = (int)(this.heldItemTooltipFade * 256.0F / 10.0F);
            if (l > 255) {
               l = 255;
            }

            if (l > 0) {
               drawcontext.drawTextWithBackground(this.getTextRenderer(), mutabletext, j, k, i, ColorHelper.withAlpha(l, -1));
            }
         }

         Profilers.get().pop();
      }
   }

   private void renderDemoTimer(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if (this.client.isDemo()) {
         Profilers.get().push("demo");
         Object object;
         if (this.client.world.getTime() >= 120500L) {
            object = DEMO_EXPIRED_MESSAGE;
         } else {
            object = Text.translatable(
               "demo.remainingTime",
               new Object[]{StringHelper.formatTicks((int)(120500L - this.client.world.getTime()), this.client.world.getTickManager().getTickRate())}
            );
         }

         int i = this.getTextRenderer().getWidth((StringVisitable)object);
         int j = drawcontext.getScaledWindowWidth() - i - 10;
         byte b0 = 5;
         drawcontext.drawTextWithBackground(this.getTextRenderer(), (Text)object, j, 5, i, -1);
         Profilers.get().pop();
      }
   }

   private void renderScoreboardSidebar(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
         if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.scoreboardSetting.getValue()) {
            Scoreboard scoreboard = this.client.world.getScoreboard();
            ScoreboardObjective scoreboardobjective = null;
            Team team = scoreboard.getScoreHolderTeam(this.client.player.getNameForScoreboard());
            if (team != null) {
               ScoreboardDisplaySlot scoreboarddisplayslot = ScoreboardDisplaySlot.fromFormatting(team.getColor());
               if (scoreboarddisplayslot != null) {
                  scoreboardobjective = scoreboard.getObjectiveForSlot(scoreboarddisplayslot);
               }
            }

            ScoreboardObjective scoreboardobjective1 = scoreboardobjective != null
               ? scoreboardobjective
               : scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            if (scoreboardobjective1 != null) {
               this.renderScoreboardSidebar(drawcontext, scoreboardobjective1);
            }
         }
      }
   }

   private void renderScoreboardSidebar(DrawContext drawcontext, ScoreboardObjective scoreboardobjective) {
      Scoreboard scoreboard = scoreboardobjective.getScoreboard();
      NumberFormat numberformat = scoreboardobjective.getNumberFormatOr(StyledNumberFormat.RED);
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         jp[] ajp = scoreboard.getScoreboardEntries(scoreboardobjective).stream().filter(scoreboardentry -> {
            return !scoreboardentry.hidden();
         }).sorted(SCOREBOARD_ENTRY_COMPARATOR).limit(15L).<jp>map(scoreboardentry -> {
            Team team = scoreboard.getScoreHolderTeam(scoreboardentry.owner());
            Text text1 = scoreboardentry.name();
            MutableText mutabletext = Team.decorateName(team, text1);
            MutableText mutabletext1 = scoreboardentry.formatted(numberformat);
            int j3 = this.getTextRenderer().getWidth(mutabletext1);
            return new jp(mutabletext, mutabletext1, j3);
         }).<jp>toArray(jp[]::new);
         Text text = scoreboardobjective.getDisplayName();
         int i = this.getTextRenderer().getWidth(text);
         int j = i;
         int k = this.getTextRenderer().getWidth(": ");

         for (jp jpx : ajp) {
            j = Math.max(j, this.getTextRenderer().getWidth(jpx.name) + (jpx.scoreWidth > 0 ? k + jpx.scoreWidth : 0));
         }

         int k2 = ajp.length;
         int l2 = k2 * 9;
         int i3 = drawcontext.getScaledWindowHeight() / 2 + l2 / 3;
         byte b0 = 3;
         int l = drawcontext.getScaledWindowWidth() - j - 3;
         int i1 = drawcontext.getScaledWindowWidth() - 3 + 2;
         int j1 = this.client.options.getTextBackgroundColor(0.3F);
         int k1 = this.client.options.getTextBackgroundColor(0.4F);
         int l1 = i3 - k2 * 9;
         drawcontext.fill(l - 2, l1 - 9 - 1, i1, l1 - 1, k1);
         drawcontext.fill(l - 2, l1 - 1, i1, i3, j1);
         drawcontext.drawText(this.getTextRenderer(), text, l + j / 2 - i / 2, l1 - 9, -1, false);

         for (int i2 = 0; i2 < k2; i2++) {
            jp jp = ajp[i2];
            int j2 = i3 - (k2 - i2) * 9;
            drawcontext.drawText(this.getTextRenderer(), jp.name, l, j2, -1, false);
            drawcontext.drawText(this.getTextRenderer(), jp.score, i1 - jp.scoreWidth, j2, -1, false);
         }
      }
   }

   @Nullable
   private PlayerEntity getCameraPlayer() {
      return this.client.getCameraEntity() instanceof PlayerEntity playerentity ? playerentity : null;
   }

   @Nullable
   private LivingEntity getRiddenEntity() {
      PlayerEntity playerentity = this.getCameraPlayer();
      if (playerentity != null) {
         Entity entity = playerentity.getVehicle();
         if (entity == null) {
            return null;
         }

         if (entity instanceof LivingEntity) {
            return (LivingEntity)entity;
         }
      }

      return null;
   }

   private int getHeartCount(@Nullable LivingEntity livingentity) {
      if (livingentity != null && livingentity.isLiving()) {
         float f = livingentity.getMaxHealth();
         int i = (int)(f + 0.5F) / 2;
         if (i > 30) {
            i = 30;
         }

         return i;
      } else {
         return 0;
      }
   }

   private int getHeartRows(int i) {
      return (int)Math.ceil(i / 10.0);
   }

   private void renderStatusBars(DrawContext drawcontext) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         PlayerEntity playerentity = this.getCameraPlayer();
         if (playerentity != null) {
            int i = MathHelper.ceil(playerentity.getHealth());
            boolean flag = this.heartJumpEndTick > this.ticks && (this.heartJumpEndTick - this.ticks) / 3L % 2L == 1L;
            long j = Util.getMeasuringTimeMs();
            if (i < this.lastHealthValue && playerentity.timeUntilRegen > 0) {
               this.lastHealthCheckTime = j;
               this.heartJumpEndTick = this.ticks + 20;
            } else if (i > this.lastHealthValue && playerentity.timeUntilRegen > 0) {
               this.lastHealthCheckTime = j;
               this.heartJumpEndTick = this.ticks + 10;
            }

            if (j - this.lastHealthCheckTime > 1000L) {
               this.renderHealthValue = i;
               this.lastHealthCheckTime = j;
            }

            this.lastHealthValue = i;
            int k = this.renderHealthValue;
            this.random.setSeed(this.ticks * 312871);
            int l = drawcontext.getScaledWindowWidth() / 2 - 91;
            int i1 = drawcontext.getScaledWindowWidth() / 2 + 91;
            int j1 = drawcontext.getScaledWindowHeight() - 39;
            float f = Math.max((float)playerentity.getAttributeValue(EntityAttributes.MAX_HEALTH), (float)Math.max(k, i));
            int k1 = MathHelper.ceil(playerentity.getAbsorptionAmount());
            int l1 = MathHelper.ceil((f + k1) / 2.0F / 10.0F);
            int i2 = Math.max(10 - (l1 - 2), 3);
            int j2 = j1 - 10;
            int k2 = -1;
            if (playerentity.hasStatusEffect(StatusEffects.REGENERATION)) {
               k2 = this.ticks % MathHelper.ceil(f + 5.0F);
            }

            Profilers.get().push("armor");
            renderArmor(drawcontext, playerentity, j1, l1, i2, l);
            Profilers.get().swap("health");
            this.renderHealthBar(drawcontext, playerentity, l, j1, i2, k2, f, i, k, k1, flag);
            LivingEntity livingentity = this.getRiddenEntity();
            int l2 = this.getHeartCount(livingentity);
            if (l2 == 0) {
               Profilers.get().swap("food");
               this.renderFood(drawcontext, playerentity, j1, i1);
               j2 -= 10;
            }

            Profilers.get().swap("air");
            this.renderAirBubbles(drawcontext, playerentity, l2, j2, i1);
            Profilers.get().pop();
         }
      }
   }

   private static void renderArmor(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k, int l) {
      int i1 = playerentity.getArmor();
      if (i1 > 0) {
         int j1 = i - (j - 1) * k - 10;

         for (int k1 = 0; k1 < 10; k1++) {
            int l1 = l + k1 * 8;
            if (k1 * 2 + 1 < i1) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, ARMOR_FULL_TEXTURE, l1, j1, 9, 9);
            }

            if (k1 * 2 + 1 == i1) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, ARMOR_HALF_TEXTURE, l1, j1, 9, 9);
            }

            if (k1 * 2 + 1 > i1) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, ARMOR_EMPTY_TEXTURE, l1, j1, 9, 9);
            }
         }
      }
   }

   private void renderHealthBar(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k, int l, float f, int i1, int j1, int k1, boolean flag) {
      HeartType hearttype = HeartType.fromPlayerState(playerentity);
      boolean flag1 = playerentity.getWorld().getLevelProperties().isHardcore();
      int l1 = MathHelper.ceil(f / 2.0);
      int i2 = MathHelper.ceil(k1 / 2.0);
      int j2 = l1 * 2;

      for (int k2 = l1 + i2 - 1; k2 >= 0; k2--) {
         int l2 = k2 / 10;
         int i3 = k2 % 10;
         int j3 = i + i3 * 8;
         int k3 = j - l2 * k;
         if (i1 + k1 <= 4) {
            k3 += this.random.nextInt(2);
         }

         if (k2 < l1 && k2 == l) {
            k3 -= 2;
         }

         this.drawHeart(drawcontext, HeartType.CONTAINER, j3, k3, flag1, flag, false);
         int l3 = k2 * 2;
         boolean flag2 = k2 >= l1;
         if (flag2) {
            int i4 = l3 - j2;
            if (i4 < k1) {
               boolean flag3 = i4 + 1 == k1;
               this.drawHeart(drawcontext, hearttype == HeartType.WITHERED ? hearttype : HeartType.ABSORBING, j3, k3, flag1, false, flag3);
            }
         }

         if (flag && l3 < j1) {
            boolean flag4 = l3 + 1 == j1;
            this.drawHeart(drawcontext, hearttype, j3, k3, flag1, true, flag4);
         }

         if (l3 < i1) {
            boolean flag5 = l3 + 1 == i1;
            this.drawHeart(drawcontext, hearttype, j3, k3, flag1, false, flag5);
         }
      }
   }

   private void drawHeart(DrawContext drawcontext, HeartType hearttype, int i, int j, boolean flag, boolean flag1, boolean flag2) {
      drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, hearttype.getTexture(flag, flag2, flag1), i, j, 9, 9);
   }

   private void renderAirBubbles(DrawContext drawcontext, PlayerEntity playerentity, int i, int j, int k) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.bubblesSetting.getValue()) {
         int l = playerentity.getMaxAir();
         int i1 = Math.clamp((long)playerentity.getAir(), 0, l);
         boolean flag = playerentity.isSubmergedIn(FluidTags.WATER);
         if (flag || i1 < l) {
            j = this.getAirBubbleY(i, j);
            int j1 = getAirBubbles(i1, l, -2);
            int k1 = getAirBubbles(i1, l, 0);
            int l1 = 10 - getAirBubbles(i1, l, getAirBubbleDelay(i1, flag));
            boolean flag1 = j1 != k1;
            if (!flag) {
               this.lastBurstBubble = 0;
            }

            for (int i2 = 1; i2 <= 10; i2++) {
               int j2 = k - (i2 - 1) * 8 - 9;
               if (i2 <= j1) {
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, AIR_TEXTURE, j2, j, 9, 9);
               } else if (flag1 && i2 == k1 && flag) {
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, AIR_BURSTING_TEXTURE, j2, j, 9, 9);
                  this.playBurstSound(i2, playerentity, l1);
               } else if (i2 > 10 - l1) {
                  int k2 = l1 == 10 && this.ticks % 2 == 0 ? this.random.nextInt(2) : 0;
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, AIR_EMPTY_TEXTURE, j2, j + k2, 9, 9);
               }
            }
         }
      }
   }

   private int getAirBubbleY(int i, int j) {
      int k = this.getHeartRows(i) - 1;
      return j - k * 10;
   }

   private static int getAirBubbles(int i, int j, int k) {
      return MathHelper.ceil((float)((i + k) * 10) / j);
   }

   private static int getAirBubbleDelay(int i, boolean flag) {
      return i != 0 && flag ? 1 : 0;
   }

   private void playBurstSound(int i, PlayerEntity playerentity, int j) {
      if (this.lastBurstBubble != i) {
         float f = 0.5F + 0.1F * Math.max(0, j - 3 + 1);
         float f1 = 1.0F + 0.1F * Math.max(0, j - 5 + 1);
         playerentity.playSound(SoundEvents.UI_HUD_BUBBLE_POP, f, f1);
         this.lastBurstBubble = i;
      }
   }

   private void renderFood(DrawContext drawcontext, PlayerEntity playerentity, int i, int j) {
      HungerManager hungermanager = playerentity.getHungerManager();
      int k = hungermanager.getFoodLevel();
      int l = (int)Math.ceil(hungermanager.getSaturationLevel());
      HudModule hudmodule = ClientMain.getInstance().getModuleManager().<HudModule>getModule(HudModule.class);
      if (l > 0 && hudmodule != null && hudmodule.isEnabled() && hudmodule.p().getValue()) {
         int i1 = i - 10;

         for (int j1 = 0; j1 < 10; j1++) {
            int k1 = j - j1 * 8 - 9;
            if (j1 * 2 + 1 <= k) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, FOOD_EMPTY_TEXTURE, k1, i1, 9, 9);
            }

            if (j1 * 2 + 1 < l) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, FOOD_FULL_TEXTURE, k1, i1, 9, 9);
            } else if (j1 * 2 + 1 == l) {
               drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, FOOD_HALF_TEXTURE, k1, i1, 9, 9);
            }
         }
      }

      for (int i2 = 0; i2 < 10; i2++) {
         int j2 = i;
         Identifier identifier;
         Identifier identifier1;
         Identifier identifier2;
         if (playerentity.hasStatusEffect(StatusEffects.HUNGER)) {
            identifier2 = FOOD_EMPTY_HUNGER_TEXTURE;
            identifier = FOOD_HALF_HUNGER_TEXTURE;
            identifier1 = FOOD_FULL_HUNGER_TEXTURE;
         } else {
            identifier2 = FOOD_EMPTY_TEXTURE;
            identifier = FOOD_HALF_TEXTURE;
            identifier1 = FOOD_FULL_TEXTURE;
         }

         if (playerentity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (k * 3 + 1) == 0) {
            j2 = i + (this.random.nextInt(3) - 1);
         }

         int l1 = j - i2 * 8 - 9;
         drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier2, l1, j2, 9, 9);
         if (i2 * 2 + 1 < k) {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier1, l1, j2, 9, 9);
         }

         if (i2 * 2 + 1 == k) {
            drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, identifier, l1, j2, 9, 9);
         }
      }
   }

   private void renderMountHealth(DrawContext drawcontext) {
      LivingEntity livingentity = this.getRiddenEntity();
      if (livingentity != null) {
         int i = this.getHeartCount(livingentity);
         if (i != 0) {
            int j = (int)Math.ceil(livingentity.getHealth());
            Profilers.get().swap("mountHealth");
            int k = drawcontext.getScaledWindowHeight() - 39;
            int l = drawcontext.getScaledWindowWidth() / 2 + 91;
            int i1 = k;

            for (byte b0 = 0; i > 0; b0 += 20) {
               int j1 = Math.min(i, 10);
               i -= j1;

               for (int k1 = 0; k1 < j1; k1++) {
                  int l1 = l - k1 * 8 - 9;
                  drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, VEHICLE_CONTAINER_HEART_TEXTURE, l1, i1, 9, 9);
                  if (k1 * 2 + 1 + b0 < j) {
                     drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, VEHICLE_FULL_HEART_TEXTURE, l1, i1, 9, 9);
                  }

                  if (k1 * 2 + 1 + b0 == j) {
                     drawcontext.drawGuiTexture(RenderLayer::getGuiTextured, VEHICLE_HALF_HEART_TEXTURE, l1, i1, 9, 9);
                  }
               }

               i1 -= 10;
            }
         }
      }
   }

   private void renderOverlay(DrawContext drawcontext, Identifier identifier, float f) {
      int i = ColorHelper.getWhite(f);
      drawcontext.drawTexture(
         RenderLayer::getGuiTexturedOverlay,
         identifier,
         0,
         0,
         0.0F,
         0.0F,
         drawcontext.getScaledWindowWidth(),
         drawcontext.getScaledWindowHeight(),
         drawcontext.getScaledWindowWidth(),
         drawcontext.getScaledWindowHeight(),
         i
      );
   }

   private void renderSpyglassOverlay(DrawContext drawcontext, float f) {
      float f1 = Math.min(drawcontext.getScaledWindowWidth(), drawcontext.getScaledWindowHeight());
      float f2 = Math.min(drawcontext.getScaledWindowWidth() / f1, drawcontext.getScaledWindowHeight() / f1) * f;
      int i = MathHelper.floor(f1 * f2);
      int j = MathHelper.floor(f1 * f2);
      int k = (drawcontext.getScaledWindowWidth() - i) / 2;
      int l = (drawcontext.getScaledWindowHeight() - j) / 2;
      int i1 = k + i;
      int j1 = l + j;
      drawcontext.drawTexture(RenderLayer::getGuiTextured, SPYGLASS_SCOPE, k, l, 0.0F, 0.0F, i, j, i, j);
      drawcontext.fill(RenderLayer.getGuiOverlay(), 0, j1, drawcontext.getScaledWindowWidth(), drawcontext.getScaledWindowHeight(), -90, -16777216);
      drawcontext.fill(RenderLayer.getGuiOverlay(), 0, 0, drawcontext.getScaledWindowWidth(), l, -90, -16777216);
      drawcontext.fill(RenderLayer.getGuiOverlay(), 0, l, k, j1, -90, -16777216);
      drawcontext.fill(RenderLayer.getGuiOverlay(), i1, l, drawcontext.getScaledWindowWidth(), j1, -90, -16777216);
   }

   private void updateVignetteDarkness(Entity entity) {
      CleanHudModule cleanhudmodule = ClientMain.getInstance().getModuleManager().<CleanHudModule>getModule(CleanHudModule.class);
      if (cleanhudmodule == null || !cleanhudmodule.isEnabled()) {
         NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
         if (nooverlaymodule != null && nooverlaymodule.isEnabled() && nooverlaymodule.vignetteSetting.getValue()) {
            this.vignetteDarkness = 0.0F;
         } else {
            BlockPos blockpos = BlockPos.ofFloored(entity.getX(), entity.getEyeY(), entity.getZ());
            float f = LightmapTextureManager.getBrightness(entity.getWorld().getDimension(), entity.getWorld().getLightLevel(blockpos));
            float f1 = MathHelper.clamp(1.0F - f, 0.0F, 1.0F);
            this.vignetteDarkness = this.vignetteDarkness + (f1 - this.vignetteDarkness) * 0.01F;
         }
      }
   }

   private void renderVignetteOverlay(DrawContext drawcontext, @Nullable Entity entity) {
      WorldBorder worldborder = this.client.world.getWorldBorder();
      float f = 0.0F;
      if (entity != null) {
         float f1 = (float)worldborder.getDistanceInsideBorder(entity);
         double d0 = Math.min(
            worldborder.getShrinkingSpeed() * worldborder.getWarningTime() * 1000.0, Math.abs(worldborder.getSizeLerpTarget() - worldborder.getSize())
         );
         double d1 = Math.max((double)worldborder.getWarningBlocks(), d0);
         if (f1 < d1) {
            f = 1.0F - (float)(f1 / d1);
         }
      }

      int i;
      if (f > 0.0F) {
         f = MathHelper.clamp(f, 0.0F, 1.0F);
         i = ColorHelper.fromFloats(1.0F, 0.0F, f, f);
      } else {
         float f2 = this.vignetteDarkness;
         f2 = MathHelper.clamp(f2, 0.0F, 1.0F);
         i = ColorHelper.fromFloats(1.0F, f2, f2, f2);
      }

      drawcontext.drawTexture(
         RenderLayer::getVignette,
         VIGNETTE_TEXTURE,
         0,
         0,
         0.0F,
         0.0F,
         drawcontext.getScaledWindowWidth(),
         drawcontext.getScaledWindowHeight(),
         drawcontext.getScaledWindowWidth(),
         drawcontext.getScaledWindowHeight(),
         i
      );
   }

   private void renderPortalOverlay(DrawContext drawcontext, float f) {
      if (f < 1.0F) {
         f *= f;
         f *= f;
         f = f * 0.8F + 0.2F;
      }

      int i = ColorHelper.getWhite(f);
      Sprite sprite = this.client.getBlockRenderManager().getModels().getModelParticleSprite(Blocks.NETHER_PORTAL.getDefaultState());
      drawcontext.drawSpriteStretched(
         RenderLayer::getGuiTexturedOverlay, sprite, 0, 0, drawcontext.getScaledWindowWidth(), drawcontext.getScaledWindowHeight(), i
      );
   }

   private void renderNauseaOverlay(DrawContext drawcontext, float f) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.nauseaSetting.getValue()) {
         int i = drawcontext.getScaledWindowWidth();
         int j = drawcontext.getScaledWindowHeight();
         drawcontext.getMatrices().push();
         float f1 = MathHelper.lerp(f, 2.0F, 1.0F);
         drawcontext.getMatrices().translate(i / 2.0F, j / 2.0F, 0.0F);
         drawcontext.getMatrices().scale(f1, f1, f1);
         drawcontext.getMatrices().translate(-i / 2.0F, -j / 2.0F, 0.0F);
         float f2 = 0.2F * f;
         float f3 = 0.4F * f;
         float f4 = 0.2F * f;
         drawcontext.drawTexture(identifier -> {
            return RenderLayer.getGuiNauseaOverlay();
         }, NAUSEA_TEXTURE, 0, 0, 0.0F, 0.0F, i, j, i, j, ColorHelper.fromFloats(1.0F, f2, f3, f4));
         drawcontext.getMatrices().pop();
      }
   }

   private void renderHotbarItem(
      DrawContext drawcontext, int i, int j, RenderTickCounter rendertickcounter, PlayerEntity playerentity, ItemStack itemstack, int k
   ) {
      if (!itemstack.isEmpty()) {
         float f = itemstack.getBobbingAnimationTime() - rendertickcounter.getTickDelta(false);
         if (f > 0.0F) {
            float f1 = 1.0F + f / 5.0F;
            drawcontext.getMatrices().push();
            drawcontext.getMatrices().translate(i + 8, j + 12, 0.0F);
            drawcontext.getMatrices().scale(1.0F / f1, (f1 + 1.0F) / 2.0F, 1.0F);
            drawcontext.getMatrices().translate(-(i + 8), -(j + 12), 0.0F);
         }

         drawcontext.drawItem(playerentity, itemstack, i, j, k);
         if (f > 0.0F) {
            drawcontext.getMatrices().pop();
         }

         drawcontext.drawStackOverlay(this.client.textRenderer, itemstack, i, j);
      }
   }

   public void tick(boolean flag) {
      this.tickAutosaveIndicator();
      if (!flag) {
         this.tick();
      }
   }

   private void tick() {
      if (this.overlayRemaining > 0) {
         this.overlayRemaining--;
      }

      if (this.titleRemainTicks > 0) {
         this.titleRemainTicks--;
         if (this.titleRemainTicks <= 0) {
            this.title = null;
            this.subtitle = null;
         }
      }

      this.ticks++;
      Entity entity = this.client.getCameraEntity();
      if (entity != null) {
         this.updateVignetteDarkness(entity);
      }

      if (this.client.player != null) {
         ItemStack itemstack = this.client.player.getInventory().getMainHandStack();
         if (itemstack.isEmpty()) {
            this.heldItemTooltipFade = 0;
         } else if (this.currentStack.isEmpty() || !itemstack.isOf(this.currentStack.getItem()) || !itemstack.getName().equals(this.currentStack.getName())) {
            this.heldItemTooltipFade = (int)(40.0 * (Double)this.client.options.getNotificationDisplayTime().getValue());
         } else if (this.heldItemTooltipFade > 0) {
            this.heldItemTooltipFade--;
         }

         this.currentStack = itemstack;
      }

      this.chatHud.tickRemovalQueueIfExists();
   }

   private void tickAutosaveIndicator() {
      IntegratedServer integratedserver = this.client.getServer();
      boolean flag = integratedserver != null && integratedserver.isSaving();
      this.lastAutosaveIndicatorAlpha = this.autosaveIndicatorAlpha;
      this.autosaveIndicatorAlpha = MathHelper.lerp(0.2F, this.autosaveIndicatorAlpha, flag ? 1.0F : 0.0F);
   }

   public void setRecordPlayingOverlay(Text text) {
      MutableText mutabletext = Text.translatable("record.nowPlaying", new Object[]{text});
      this.setOverlayMessage(mutabletext, true);
      this.client.getNarratorManager().narrate(mutabletext);
   }

   public void setOverlayMessage(Text text, boolean flag) {
      this.setCanShowChatDisabledScreen(false);
      this.overlayMessage = text;
      this.overlayRemaining = 60;
      this.overlayTinted = flag;
   }

   public void setCanShowChatDisabledScreen(boolean flag) {
      this.canShowChatDisabledScreen = flag;
   }

   public boolean shouldShowChatDisabledScreen() {
      return this.canShowChatDisabledScreen && this.overlayRemaining > 0;
   }

   public void setTitleTicks(int i, int j, int k) {
      if (i >= 0) {
         this.titleFadeInTicks = i;
      }

      if (j >= 0) {
         this.titleStayTicks = j;
      }

      if (k >= 0) {
         this.titleFadeOutTicks = k;
      }

      if (this.titleRemainTicks > 0) {
         this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
      }
   }

   public void setSubtitle(Text text) {
      this.subtitle = text;
   }

   public void setTitle(Text text) {
      this.title = text;
      this.titleRemainTicks = this.titleFadeInTicks + this.titleStayTicks + this.titleFadeOutTicks;
   }

   public void clearTitle() {
      this.title = null;
      this.subtitle = null;
      this.titleRemainTicks = 0;
   }

   public CustomChatHud getChatHud() {
      return this.chatHud;
   }

   public int getTicks() {
      return this.ticks;
   }

   public TextRenderer getTextRenderer() {
      return this.client.textRenderer;
   }

   public SpectatorHud getSpectatorHud() {
      return this.spectatorHud;
   }

   public PlayerListHud getPlayerListHud() {
      return this.PlayerListHudClass;
   }

   public CustomPlayerListHud getPlayerListHudClass() {
      return this.PlayerListHudClass;
   }

   public void clear() {
      this.PlayerListHudClass.clear();
      this.bossBarHud.clear();
      this.client.getToastManager().clear();
      this.debugHud.clear();
      this.chatHud.clear(true);
      this.clearTitle();
      this.setDefaultTitleFade();
   }

   public BossBarHud getBossBarHud() {
      return this.bossBarHud;
   }

   public DebugHud getDebugHud() {
      return this.debugHud;
   }

   public void resetDebugHudChunk() {
      this.debugHud.resetChunk();
   }

   public void renderAutosaveIndicator(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      if ((Boolean)this.client.options.getShowAutosaveIndicator().getValue() && (this.autosaveIndicatorAlpha > 0.0F || this.lastAutosaveIndicatorAlpha > 0.0F)) {
         int i = MathHelper.floor(
            255.0F
               * MathHelper.clamp(
                  MathHelper.lerp(rendertickcounter.getLastDuration(), this.lastAutosaveIndicatorAlpha, this.autosaveIndicatorAlpha), 0.0F, 1.0F
               )
         );
         if (i > 8) {
            TextRenderer textrenderer = this.getTextRenderer();
            int j = textrenderer.getWidth(SAVING_LEVEL_TEXT);
            int k = ColorHelper.withAlpha(i, -1);
            int l = drawcontext.getScaledWindowWidth() - j - 5;
            int i1 = drawcontext.getScaledWindowHeight() - 9 - 5;
            drawcontext.drawTextWithBackground(textrenderer, SAVING_LEVEL_TEXT, l, i1, j, k);
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public ChatHud getChatHud() {
      return this.getChatHud();
   }
}
