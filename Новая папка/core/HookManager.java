package core;

import enum.HookType;
import hook.BossBarHudHook;
import hook.CameraHook;
import hook.ChatHudHook;
import hook.GameRendererHook;
import hook.GameRendererInitHook;
import hook.HeldItemRendererHook;
import hook.Hook;
import hook.InGameHudHook;
import hook.PlayerListHudHook;
import hook.TextRendererHook;
import hook.WorldRendererHook;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.minecraft.client.MinecraftClient;

public class HookManager {
   private final List<Hook> hooks = new ArrayList<Hook>();
   private static volatile boolean hooksInitialized = false;
   private static HookManager pendingInstance;

   public HookManager() {
      this.hooks
         .addAll(
            Arrays.asList(
               hook.ProfilerHook.INSTANCE,
               new GameRendererInitHook(),
               new GameRendererHook(),
               new WorldRendererHook(),
               new CameraHook(),
               new InGameHudHook(),
               new PlayerListHudHook(),
               new BossBarHudHook(),
               new HeldItemRendererHook(),
               new ChatHudHook(),
               new TextRendererHook()
            )
         );
      pendingInstance = this;
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)minecraftclient -> {
         tryInitializeHooks();
      });
   }

   private static void tryInitializeHooks() {
      if (!hooksInitialized && pendingInstance != null && MinecraftClient.getInstance().getResourceManager() != null) {
         new we();
         pendingInstance.initHook();
         hooksInitialized = true;
         pendingInstance = null;
      }
   }

   public void initHook() {
      this.hooks.stream().filter(hook -> {
         return hook.getType() == HookType.Init;
      }).forEach(Hook::hook);
   }

   public void unHook() {
      this.hooks.forEach(Hook::unHook);
   }
}
