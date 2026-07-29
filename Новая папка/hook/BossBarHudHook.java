package hook;

import enum.HookType;
import net.minecraft.client.gui.hud.BossBarHud;
import render.CustomInGameHud;
import util.UnsafeFieldAccessor;

public class BossBarHudHook extends Hook {
   public BossBarHudHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      new UnsafeFieldAccessor<CustomBossBarHud>(this.mc.inGameHud, CustomInGameHud.class, BossBarHud.class).check(new CustomBossBarHud(this.mc));
   }

   @Override
   public void unHook() {
   }
}
