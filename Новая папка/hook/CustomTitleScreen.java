package hook;

import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.AccessibilityOnboardingButtons;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.CreditsAndAttributionScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.client.realms.gui.screen.RealmsNotificationsScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorage.Session;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class CustomTitleScreen extends TitleScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Text NARRATOR_SCREEN_TITLE = Text.translatable("narrator.screen.title");
   private static final Text COPYRIGHT = Text.translatable("title.credits");
   private static final String DEMO_WORLD_NAME = "Demo_World";
   private static final float field_49900 = 2000.0F;
   @Nullable
   private SplashTextRenderer splashText;
   private ButtonWidget buttonResetDemo;
   @Nullable
   private RealmsNotificationsScreen realmsNotificationGui;
   private float backgroundAlpha = 1.0F;
   private boolean doBackgroundFade;
   private long backgroundFadeStart;
   private final LogoDrawer logoDrawer;

   public CustomTitleScreen() {
      this(false);
   }

   public CustomTitleScreen(boolean flag) {
      this(flag, null);
   }

   public CustomTitleScreen(boolean flag, @Nullable LogoDrawer logodrawer) {
      super(flag, logodrawer);
      this.doBackgroundFade = flag;
      this.logoDrawer = Objects.<LogoDrawer>requireNonNullElseGet(logodrawer, () -> {
         return new LogoDrawer(false);
      });
   }

   private boolean isRealmsNotificationsGuiDisplayed() {
      return this.realmsNotificationGui != null;
   }

   public void tick() {
      if (this.isRealmsNotificationsGuiDisplayed()) {
         this.realmsNotificationGui.tick();
      }
   }

   public static void registerTextures(TextureManager texturemanager) {
      texturemanager.registerTexture(LogoDrawer.LOGO_TEXTURE);
      texturemanager.registerTexture(LogoDrawer.EDITION_TEXTURE);
      texturemanager.registerTexture(RotatingCubeMapRenderer.OVERLAY_TEXTURE);
      PANORAMA_RENDERER.registerTextures(texturemanager);
   }

   public boolean shouldPause() {
      return false;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      if (this.splashText == null) {
         this.splashText = this.client.getSplashTextLoader().get();
      }

      int i = this.textRenderer.getWidth(COPYRIGHT);
      int j = this.width - i - 2;
      byte b0 = 24;
      int k = this.height / 4 + 48;
      if (this.client.isDemo()) {
         k = this.addDemoWidgets(k, 24);
      } else {
         k = this.addNormalWidgets(k, 24);
      }

      k = this.addDevelopmentWidgets(k, 24);
      TextIconButtonWidget texticonbuttonwidget = (TextIconButtonWidget)this.addDrawableChild(
         AccessibilityOnboardingButtons.createLanguageButton(20, buttonwidget -> {
            this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
         }, true)
      );
      int l = this.width / 2 - 124;
      k += 36;
      texticonbuttonwidget.setPosition(l, k);
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), buttonwidget -> {
         this.client.setScreen(new OptionsScreen(this, this.client.options));
      }).dimensions(this.width / 2 - 100, k, 98, 20).build());
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), buttonwidget -> {
         this.client.scheduleStop();
      }).dimensions(this.width / 2 + 2, k, 98, 20).build());
      TextIconButtonWidget texticonbuttonwidget1 = (TextIconButtonWidget)this.addDrawableChild(
         AccessibilityOnboardingButtons.createAccessibilityButton(20, buttonwidget -> {
            this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
         }, true)
      );
      texticonbuttonwidget1.setPosition(this.width / 2 + 104, k);
      this.addDrawableChild(new PressableTextWidget(j, this.height - 10, i, 10, COPYRIGHT, buttonwidget -> {
         this.client.setScreen(new CreditsAndAttributionScreen(this));
      }, this.textRenderer));
      if (this.realmsNotificationGui == null) {
         this.realmsNotificationGui = new RealmsNotificationsScreen();
      }

      if (this.isRealmsNotificationsGuiDisplayed()) {
         this.realmsNotificationGui.init(this.client, this.width, this.height);
      }
   }

   private int addDevelopmentWidgets(int i, int j) {
      if (SharedConstants.isDevelopment) {
         this.addDrawableChild(ButtonWidget.builder(Text.literal("Create Test World"), buttonwidget -> {
            CreateWorldScreen.showTestWorld(this.client, this);
         }).dimensions(this.width / 2 - 100, i += j, 200, 20).build());
      }

      return i;
   }

   private int addNormalWidgets(int i, int j) {
      this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), buttonwidget -> {
         this.client.setScreen(new SelectWorldScreen(this));
      }).dimensions(this.width / 2 - 100, i, 200, 20).build());
      Text text = this.getMultiplayerDisabledText();
      boolean flag = text == null;
      Tooltip tooltip = text != null ? Tooltip.of(text) : null;
      int k;
      ((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), buttonwidget -> {
         Object object = this.client.options.skipMultiplayerWarning ? new CustomMultiplayerScreen(this) : new MultiplayerWarningScreen(this);
         this.client.setScreen((Screen)object);
      }).dimensions(this.width / 2 - 100, k = i + j, 200, 20).tooltip(tooltip).build())).active = flag;
      ((ButtonWidget)this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.online"), buttonwidget -> {
         this.client.setScreen(new RealmsMainScreen(this));
      }).dimensions(this.width / 2 - 100, i = k + j, 200, 20).tooltip(tooltip).build())).active = flag;
      return i;
   }

   @Nullable
   private Text getMultiplayerDisabledText() {
      if (this.client.isMultiplayerEnabled()) {
         return null;
      } else if (this.client.isUsernameBanned()) {
         return Text.translatable("title.multiplayer.disabled.banned.name");
      } else {
         BanDetails bandetails = this.client.getMultiplayerBanDetails();
         if (bandetails != null) {
            return bandetails.expires() != null
               ? Text.translatable("title.multiplayer.disabled.banned.temporary")
               : Text.translatable("title.multiplayer.disabled.banned.permanent");
         } else {
            return Text.translatable("title.multiplayer.disabled");
         }
      }
   }

   private int addDemoWidgets(int i, int j) {
      boolean flag = this.canReadDemoWorldData();
      this.addDrawableChild(
         ButtonWidget.builder(
               Text.translatable("menu.playdemo"),
               buttonwidget -> {
                  if (flag) {
                     this.client.createIntegratedServerLoader().start("Demo_World", () -> {
                        this.client.setScreen(this);
                     });
                  } else {
                     this.client
                        .createIntegratedServerLoader()
                        .createAndStart("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, GeneratorOptions.DEMO_OPTIONS, WorldPresets::createDemoOptions, this);
                  }
               }
            )
            .dimensions(this.width / 2 - 100, i, 200, 20)
            .build()
      );
      int k;
      this.buttonResetDemo = (ButtonWidget)this.addDrawableChild(
         ButtonWidget.builder(
               Text.translatable("menu.resetdemo"),
               buttonwidget -> {
                  LevelStorage levelstorage = this.client.getLevelStorage();

                  try {
                     Session session = levelstorage.createSessionWithoutSymlinkCheck("Demo_World");

                     try {
                        if (session.levelDatExists()) {
                           this.client
                              .setScreen(
                                 new ConfirmScreen(
                                    this::onDemoDeletionConfirmed,
                                    Text.translatable("selectWorld.deleteQuestion"),
                                    Text.translatable("selectWorld.deleteWarning", new Object[]{MinecraftServer.DEMO_LEVEL_INFO.getLevelName()}),
                                    Text.translatable("selectWorld.deleteButton"),
                                    ScreenTexts.CANCEL
                                 )
                              );
                        }
                     } catch (Throwable throwable1) {
                        if (session != null) {
                           try {
                              session.close();
                           } catch (Throwable throwable) {
                              throwable1.addSuppressed(throwable);
                           }
                        }

                        throw throwable1;
                     }

                     if (session != null) {
                        session.close();
                     }
                  } catch (IOException ioexception) {
                     SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
                     LOGGER.warn("Failed to access demo world", ioexception);
                  }
               }
            )
            .dimensions(this.width / 2 - 100, k = i + j, 200, 20)
            .build()
      );
      this.buttonResetDemo.active = flag;
      return k;
   }

   private boolean canReadDemoWorldData() {
      try {
         Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World");

         boolean flag;
         try {
            flag = session.levelDatExists();
         } catch (Throwable throwable1) {
            if (session != null) {
               try {
                  session.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (session != null) {
            session.close();
         }

         return flag;
      } catch (IOException ioexception) {
         SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
         LOGGER.warn("Failed to read demo world data", ioexception);
         return false;
      }
   }

   public void render(DrawContext drawcontext, int i, int j, float f) {
      if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
         this.backgroundFadeStart = Util.getMeasuringTimeMs();
      }

      float f1 = 1.0F;
      if (this.doBackgroundFade) {
         float f2 = (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 2000.0F;
         if (f2 > 1.0F) {
            this.doBackgroundFade = false;
            this.backgroundAlpha = 1.0F;
         } else {
            f2 = MathHelper.clamp(f2, 0.0F, 1.0F);
            f1 = MathHelper.clampedMap(f2, 0.5F, 1.0F, 0.0F, 1.0F);
            this.backgroundAlpha = MathHelper.clampedMap(f2, 0.0F, 0.5F, 0.0F, 1.0F);
         }

         this.setWidgetAlpha(f1);
      }

      this.renderPanoramaBackground(drawcontext, f);
      int k = MathHelper.ceil(f1 * 255.0F) << 24;
      if ((k & -67108864) != 0) {
         super.render(drawcontext, i, j, f);
         this.logoDrawer.draw(drawcontext, this.width, f1);
         if (this.splashText != null && !(Boolean)this.client.options.getHideSplashTexts().getValue()) {
            this.splashText.render(drawcontext, this.width, this.textRenderer, k);
         }

         String s1 = SharedConstants.getGameVersion().getName();
         String s = "Minecraft " + s1;
         if (this.client.isDemo()) {
            s = s + " Demo";
         } else {
            String s5;
            if ("release".equalsIgnoreCase(this.client.getVersionType())) {
               s5 = "";
            } else {
               String s2 = this.client.getVersionType();
               s5 = "/" + s2;
            }

            String s3 = s5;
            s = s + s3;
         }

         if (MinecraftClient.getModStatus().isModded()) {
            String s4 = I18n.translate("menu.modded", new Object[0]);
            s = s + s4;
         }

         drawcontext.drawTextWithShadow(this.textRenderer, s, 2, this.height - 10, 16777215 | k);
         if (this.isRealmsNotificationsGuiDisplayed() && f1 >= 1.0F) {
            this.realmsNotificationGui.render(drawcontext, i, j, f);
         }
      }
   }

   private void setWidgetAlpha(float f) {
      for (Element element : this.children()) {
         if (element instanceof ClickableWidget clickablewidget) {
            clickablewidget.setAlpha(f);
         }
      }
   }

   public void renderBackground(DrawContext drawcontext, int i, int j, float f) {
   }

   protected void renderPanoramaBackground(DrawContext drawcontext, float f) {
      ROTATING_PANORAMA_RENDERER.render(drawcontext, this.width, this.height, this.backgroundAlpha, f);
   }

   public boolean mouseClicked(double d0, double d1, int i) {
      return super.mouseClicked(d0, d1, i) ? true : this.isRealmsNotificationsGuiDisplayed() && this.realmsNotificationGui.mouseClicked(d0, d1, i);
   }

   public void removed() {
      if (this.realmsNotificationGui != null) {
         this.realmsNotificationGui.removed();
      }
   }

   public void onDisplayed() {
      super.onDisplayed();
      if (this.realmsNotificationGui != null) {
         this.realmsNotificationGui.onDisplayed();
      }
   }

   private void onDemoDeletionConfirmed(boolean flag) {
      if (flag) {
         try {
            Session session = this.client.getLevelStorage().createSessionWithoutSymlinkCheck("Demo_World");

            try {
               session.deleteSessionLock();
            } catch (Throwable throwable1) {
               if (session != null) {
                  try {
                     session.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (session != null) {
               session.close();
            }
         } catch (IOException ioexception) {
            SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
            LOGGER.warn("Failed to delete demo world", ioexception);
         }
      }

      this.client.setScreen(this);
   }
}
