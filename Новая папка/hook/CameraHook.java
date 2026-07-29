package hook;

import enum.HookType;
import net.minecraft.client.render.Camera;
import render.CustomGameRenderer;
import util.UnsafeFieldAccessor;

public class CameraHook extends Hook {
   public CameraHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      new UnsafeFieldAccessor<CustomCamera>(this.mc.gameRenderer, CustomGameRenderer.class, Camera.class).check(new CustomCamera());
   }

   @Override
   public void unHook() {
   }
}
