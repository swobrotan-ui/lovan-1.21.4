package core;

import b.ModInitializer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

@ModInitializer
public class ClientMain implements ClientModInitializer {
   private static ClientMain instance;
   private ModuleManager moduleManager;
   private KeyBindManager keyBindManager;
   private EventManager eventManager;
   private EventHandler events;
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
   private ConfigManager configManager;
   private ConfigSyncManager configSyncManager;
   private OnlineManager onlineManager;
   private ImageCache imageCache;
   private HookManager hookManager;
   public volatile boolean modulesLoaded = false;
   private boolean dev = false;

   public ClientMain() {
      this.authorization();
   }

   private void authorization() {
      try {
         AuthConfig.a();
         boolean flag = LicenseManager.b();
         if (!flag) {
            System.exit(1);
         } else {
            this.setup();
         }
      } catch (Exception exception) {
         System.exit(1);
      }
   }

   private void setup() {
      instance = this;
      if (this.dev) {
         ((Logger)LogManager.getLogger("net.minecraft.client.texture.PlayerSkinProvider")).setLevel(Level.ERROR);
         ((Logger)LogManager.getLogger("net.minecraft.client.network.ClientPlayNetworkHandler")).setLevel(Level.ERROR);
      }

      SoundManager.getInstance().loadSounds();
      SoundManager.getInstance().setMuted(true);
      this.moduleManager = new ModuleManager();
      this.eventManager = new EventManager();
      this.keyBindManager = KeyBindManager.getInstance();
      this.events = new EventHandler();
      this.configManager = new ConfigManager(this.moduleManager);
      this.configSyncManager = new ConfigSyncManager(this.executor);
      this.onlineManager = new OnlineManager(this.executor);
      this.imageCache = new ImageCache();
      this.keyBindManager.registerTickListener();
      this.executor.schedule(() -> {
         this.moduleManager.init();
      }, 3L, TimeUnit.SECONDS);
      this.executor.execute(() -> {
         Localization.a().b();
      });
      this.hookManager = new HookManager();
   }

   public void onInitializeClient() {
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public KeyBindManager getKeyBindManager() {
      return this.keyBindManager;
   }

   public EventManager getEventManager() {
      return this.eventManager;
   }

   public EventHandler getEvents() {
      return this.events;
   }

   public ScheduledExecutorService getExecutor() {
      return this.executor;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public ConfigSyncManager getConfigSyncManager() {
      return this.configSyncManager;
   }

   public OnlineManager getOnlineManager() {
      return this.onlineManager;
   }

   public ImageCache getImageCache() {
      return this.imageCache;
   }

   public HookManager getHookManager() {
      return this.hookManager;
   }

   public boolean isModulesLoaded() {
      return this.modulesLoaded;
   }

   public static ClientMain getInstance() {
      return instance;
   }

   public void setModuleManager(ModuleManager modulemanager) {
      this.moduleManager = modulemanager;
   }

   public void setKeyBindManager(KeyBindManager keybindmanager) {
      this.keyBindManager = keybindmanager;
   }

   public void setEventManager(EventManager eventmanager) {
      this.eventManager = eventmanager;
   }

   public void setEvents(EventHandler eventhandler) {
      this.events = eventhandler;
   }

   public void setConfigManager(ConfigManager configmanager) {
      this.configManager = configmanager;
   }

   public void setConfigSyncManager(ConfigSyncManager configsyncmanager) {
      this.configSyncManager = configsyncmanager;
   }

   public void setOnlineManager(OnlineManager onlinemanager) {
      this.onlineManager = onlinemanager;
   }

   public boolean isDev() {
      return this.dev;
   }
}
