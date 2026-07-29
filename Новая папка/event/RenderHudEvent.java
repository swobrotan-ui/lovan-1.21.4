package event;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class RenderHudEvent {
   private final DrawContext context;
   private final RenderTickCounter tickCounter;

   public RenderHudEvent(DrawContext drawcontext, RenderTickCounter rendertickcounter) {
      this.context = drawcontext;
      this.tickCounter = rendertickcounter;
   }

   public DrawContext getContext() {
      return this.context;
   }

   public RenderTickCounter getTickCounter() {
      return this.tickCounter;
   }
}
