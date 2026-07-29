package hook;

import enum.HookType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import render.CustomGameRenderer;
import render.CustomHeldItemRenderer;
import util.UnsafeFieldAccessor;

public class GameRendererHook extends Hook {
   public GameRendererHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      CustomHeldItemRenderer customhelditemrenderer = new CustomHeldItemRenderer(
         this.mc, this.mc.getEntityRenderDispatcher(), this.mc.getItemRenderer(), this.mc.getItemModelManager()
      );
      new UnsafeFieldAccessor<CustomGameRenderer>(this.mc, MinecraftClient.class, GameRenderer.class)
         .check(new CustomGameRenderer(this.mc, customhelditemrenderer, this.mc.getResourceManager(), this.mc.getBufferBuilders()));
   }

   @Override
   public void unHook() {
   }
}
