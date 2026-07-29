package hook;

import enum.HookType;
import net.minecraft.client.MinecraftClient;

public abstract class Hook {
   protected MinecraftClient mc = MinecraftClient.getInstance();
   private final HookType type;

   public Hook(HookType hooktype) {
      this.type = hooktype;
   }

   public abstract void hook();

   public abstract void unHook();

   public HookType getType() {
      return this.type;
   }
}
