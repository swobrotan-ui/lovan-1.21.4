package hook;

import enum.HookType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import render.CustomWorldRenderer;
import util.UnsafeFieldAccessor;

public class WorldRendererHook extends Hook {
   public WorldRendererHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      try {
         WorldRenderer worldrenderer = this.mc.worldRenderer;
         CustomWorldRenderer customworldrenderer = new CustomWorldRenderer(
            this.mc, this.mc.getEntityRenderDispatcher(), this.mc.getBlockEntityRenderDispatcher(), this.mc.getBufferBuilders()
         );
         if (worldrenderer != null) {
            this.copyFieldsFromOldRenderer(worldrenderer, customworldrenderer);
         }

         customworldrenderer.loadEntityOutlinePostProcessor();
         new UnsafeFieldAccessor<CustomWorldRenderer>(this.mc, MinecraftClient.class, WorldRenderer.class).setValue(customworldrenderer);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void copyFieldsFromOldRenderer(WorldRenderer worldrenderer, CustomWorldRenderer customworldrenderer) {
      try {
         Class<WorldRenderer> oclass = WorldRenderer.class;
         this.copyField(worldrenderer, customworldrenderer, oclass, 11);
         this.copyField(worldrenderer, customworldrenderer, oclass, 12);
         this.copyField(worldrenderer, customworldrenderer, oclass, 13);
         this.copyField(worldrenderer, customworldrenderer, oclass, 14);
         this.copyField(worldrenderer, customworldrenderer, oclass, 15);
         this.copyField(worldrenderer, customworldrenderer, oclass, 16);
         this.copyField(worldrenderer, customworldrenderer, oclass, 17);
         this.copyField(worldrenderer, customworldrenderer, oclass, 18);
         this.copyField(worldrenderer, customworldrenderer, oclass, 19);
         this.copyField(worldrenderer, customworldrenderer, oclass, 20);
         new UnsafeFieldAccessor(customworldrenderer, oclass, 21)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 21).getInt(worldrenderer));
         this.copyField(worldrenderer, customworldrenderer, oclass, 22);
         this.copyField(worldrenderer, customworldrenderer, oclass, 23);
         new UnsafeFieldAccessor(customworldrenderer, oclass, 26)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 26).getInt(worldrenderer));
         new UnsafeFieldAccessor(customworldrenderer, oclass, 27)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 27).getInt(worldrenderer));
         new UnsafeFieldAccessor(customworldrenderer, oclass, 28)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 28).getInt(worldrenderer));
         this.copyField(worldrenderer, customworldrenderer, oclass, 29);
         this.copyField(worldrenderer, customworldrenderer, oclass, 30);
         this.copyField(worldrenderer, customworldrenderer, oclass, 31);
         this.copyField(worldrenderer, customworldrenderer, oclass, 32);
         this.copyField(worldrenderer, customworldrenderer, oclass, 33);
         this.copyField(worldrenderer, customworldrenderer, oclass, 34);
         new UnsafeFieldAccessor(customworldrenderer, oclass, 35)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 35).getInt(worldrenderer));
         this.copyField(worldrenderer, customworldrenderer, oclass, 38);
         new UnsafeFieldAccessor(customworldrenderer, oclass, 39)
            .setBoolean(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 39).getBoolean(worldrenderer));
         this.copyField(worldrenderer, customworldrenderer, oclass, 40);
         this.copyField(worldrenderer, customworldrenderer, oclass, 41);
         new UnsafeFieldAccessor(customworldrenderer, oclass, 42)
            .setInt(customworldrenderer, new UnsafeFieldAccessor(worldrenderer, oclass, 42).getInt(worldrenderer));
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   private void copyField(Object object, Object object1, Class<?> oclass, int i) {
      try {
         Object object2 = new UnsafeFieldAccessor(object, oclass, i).getValue();
         if (object2 != null) {
            new UnsafeFieldAccessor<Object>(object1, oclass, i).setValue(object2);
         }
      } catch (Exception exception) {
      }
   }

   @Override
   public void unHook() {
   }
}
