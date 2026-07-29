package core;

import event.ChatMessageEvent;
import event.MouseMoveEvent;
import hook.CustomMultiplayerScreen;
import hook.CustomTitleScreen;
import module.PanicModule;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.StartTick;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents.AllowChat;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents.Chat;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Join;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Start;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents.AfterDeath;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;

public class EventHandler {
   private boolean isConnectedToServer = false;
   private boolean scrollCallbackInstalled = false;
   private GLFWScrollCallback originalScrollCallback;
   private GLFWScrollCallback customScrollCallback;

   public EventHandler() {
      this.registerEvents();
   }

   public void registerEvents() {
      EventManager eventmanager = ClientMain.getInstance().getEventManager();
      KeyBindManager keybindmanager = ClientMain.getInstance().getKeyBindManager();
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraftclient -> {
         if (!this.scrollCallbackInstalled && minecraftclient.getWindow() != null) {
            this.installScrollCallback(eventmanager);
         }

         if (minecraftclient.currentScreen != null && minecraftclient.currentScreen.getClass() == MultiplayerScreen.class) {
            minecraftclient.setScreen(new CustomMultiplayerScreen(new CustomTitleScreen()));
         }

         if (minecraftclient.player != null) {
            eventmanager.onEndTick();
         }
      });
      ClientTickEvents.START_CLIENT_TICK.register((StartTick)minecraftclient -> {
         if (minecraftclient.player != null) {
            eventmanager.onStartTick();
         }
      });
      ServerLivingEntityEvents.AFTER_DEATH.register((AfterDeath)(livingentity, damagesource) -> {
         if (livingentity instanceof PlayerEntity) {
            eventmanager.onPlayerDeath((PlayerEntity)livingentity);
            keybindmanager.l();
         }
      });
      ClientPlayConnectionEvents.JOIN.register((Join)(clientplaynetworkhandler, packetsender, minecraftclient) -> {
         if (!this.isConnected()) {
            this.isConnectedToServer = true;
            eventmanager.onJoinServer();
         }
      });
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(clientplaynetworkhandler, minecraftclient) -> {
         if (this.isConnected()) {
            this.isConnectedToServer = false;
            eventmanager.onDisconnect();
         }
      });
      ClientSendMessageEvents.ALLOW_CHAT.register((AllowChat)s -> {
         if (s.startsWith(".")) {
            PanicModule panicmodule = ClientMain.getInstance().getModuleManager().<PanicModule>getModule(PanicModule.class);
            if (panicmodule == null || !panicmodule.c()) {
               ChatMessageEvent chatmessageevent = eventmanager.onChatMessage(s);
               if (!chatmessageevent.isCancelled()) {
                  return true;
               }

               return false;
            }
         }

         return true;
      });
      ClientSendMessageEvents.CHAT.register((Chat)s -> {
         if (!s.startsWith(".")) {
            eventmanager.onChatSent(s);
         }
      });
      WorldRenderEvents.AFTER_ENTITIES.register(eventmanager::onAfterEntities);
      WorldRenderEvents.BEFORE_ENTITIES.register(eventmanager::onBeforeEntities);
      WorldRenderEvents.START.register((Start)worldrendercontext -> {
         eventmanager.onWorldRenderStart();
      });
      WorldRenderEvents.END.register(eventmanager::q);
      WorldRenderEvents.AFTER_SETUP.register(eventmanager::r);
      WorldRenderEvents.AFTER_TRANSLUCENT.register(eventmanager::s);
      UseBlockCallback.EVENT.register(eventmanager::u);
      UseEntityCallback.EVENT.register(eventmanager::v);
      AttackEntityCallback.EVENT.register(eventmanager::i);
   }

   private void installScrollCallback(EventManager eventmanager) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.getWindow() != null) {
         long i = minecraftclient.getWindow().getHandle();
         if (i != 0L) {
            this.originalScrollCallback = GLFW.glfwSetScrollCallback(i, null);
            this.customScrollCallback = GLFWScrollCallback.create((j, d0, d1) -> {
               MouseMoveEvent mousemoveevent = eventmanager.onMouseScroll(d0, d1);
               if (!mousemoveevent.isCancelled() && this.originalScrollCallback != null) {
                  this.originalScrollCallback.invoke(j, d0, d1);
               }
            });
            GLFW.glfwSetScrollCallback(i, this.customScrollCallback);
            this.scrollCallbackInstalled = true;
         }
      }
   }

   public boolean isConnected() {
      return this.isConnectedToServer;
   }

   public boolean isScrollInstalled() {
      return this.scrollCallbackInstalled;
   }

   public GLFWScrollCallback getOriginalScrollCallback() {
      return this.originalScrollCallback;
   }

   public GLFWScrollCallback getCustomScrollCallback() {
      return this.customScrollCallback;
   }
}
