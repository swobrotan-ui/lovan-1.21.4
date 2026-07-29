package hook;

import enum.HookType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import render.CustomInGameHud;
import util.UnsafeFieldAccessor;

public class InGameHudHook extends Hook {
   public static MinecraftClient mc = MinecraftClient.getInstance();

   public InGameHudHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      new UnsafeFieldAccessor<CustomInGameHud>(mc, MinecraftClient.class, InGameHud.class).check(new CustomInGameHud(mc));
   }

   @Override
   public void unHook() {
   }
}
