package hook;

import enum.HookType;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.render.Camera;
import render.CustomChatHud;
import render.CustomInGameHud;
import util.UnsafeFieldAccessor;

public class ChatHudHook extends Hook {
   public ChatHudHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      new UnsafeFieldAccessor<CustomChatHud>(this.mc.inGameHud, CustomInGameHud.class, ChatHud.class).check(new CustomChatHud(this.mc));
   }

   @Override
   public void unHook() {
      new UnsafeFieldAccessor<Camera>(this.mc.gameRenderer, GameRendererHook.class, Camera.class).check(new Camera());
   }
}
