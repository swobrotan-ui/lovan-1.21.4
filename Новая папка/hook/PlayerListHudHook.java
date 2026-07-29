package hook;

import enum.HookType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import render.CustomInGameHud;
import render.CustomPlayerListHud;
import util.UnsafeFieldAccessor;

public class PlayerListHudHook extends Hook {
   public static MinecraftClient mc = MinecraftClient.getInstance();

   public PlayerListHudHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      new UnsafeFieldAccessor<CustomPlayerListHud>(mc.inGameHud, CustomInGameHud.class, PlayerListHud.class).check(new CustomPlayerListHud(mc, mc.inGameHud));
   }

   @Override
   public void unHook() {
   }
}
