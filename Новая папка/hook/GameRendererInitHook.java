package hook;

import enum.HookType;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.util.Identifier;
import render.CustomEntityRenderDispatcher;
import render.TextureCache;
import util.UnsafeFieldAccessor;

public class GameRendererInitHook extends Hook {
   public static final Identifier cape = Identifier.of("minecraft", "textures/cape.png");
   public static final Identifier glow = Identifier.of("minecraft", "textures/glow.png");
   public static final Identifier glow_polygon = Identifier.of("minecraft", "textures/glow_polygon.png");
   public static final Identifier glow_ellipse = Identifier.of("minecraft", "textures/glow_ellipse.png");
   public static final Identifier glow_square = Identifier.of("minecraft", "textures/glow_square.png");
   public static final Identifier glow_star = Identifier.of("minecraft", "textures/glow_star.png");
   public static final Identifier arrow = Identifier.of("minecraft", "textures/arrow.png");
   public static final Identifier arrow_3d = Identifier.of("minecraft", "textures/arrow_3d.png");
   public static final Identifier glow_line = Identifier.of("minecraft", "textures/glow_line.png");

   public GameRendererInitHook() {
      super(HookType.Init);
   }

   @Override
   public void hook() {
      this.registerTexture("cape", cape, TextureCache.getCapeTexture());
      this.registerTexture("glow", glow, TextureCache.getGlowTexture());
      this.registerTexture("glow_polygon", glow_polygon, TextureCache.getGlowPolygonTexture());
      this.registerTexture("glow_ellipse", glow_ellipse, TextureCache.getGlowEllipseTexture());
      this.registerTexture("glow_square", glow_square, TextureCache.getGlowSquareTexture());
      this.registerTexture("glow_star", glow_star, TextureCache.getGlowStarTexture());
      this.registerTexture("arrow", arrow, TextureCache.getArrowTexture());
      this.registerTexture("arrow_3d", arrow_3d, TextureCache.getArrow3dTexture());
      this.registerTexture("glow_line", glow_line, TextureCache.getGlowLineTexture());
      EntityRenderDispatcher entityrenderdispatcher = this.mc.getEntityRenderDispatcher();
      EquipmentModelLoader equipmentmodelloader = new UnsafeFieldAccessor<EquipmentModelLoader>(
            entityrenderdispatcher, EntityRenderDispatcher.class, EquipmentModelLoader.class
         )
         .getValue();
      CustomEntityRenderDispatcher customentityrenderdispatcher = new CustomEntityRenderDispatcher(
         this.mc,
         this.mc.getTextureManager(),
         this.mc.getItemModelManager(),
         this.mc.getItemRenderer(),
         this.mc.getMapRenderer(),
         this.mc.getBlockRenderManager(),
         this.mc.textRenderer,
         this.mc.options,
         () -> {
            return this.mc.getLoadedEntityModels();
         },
         equipmentmodelloader
      );
      new UnsafeFieldAccessor<CustomEntityRenderDispatcher>(this.mc, MinecraftClient.class, EntityRenderDispatcher.class)
         .setValue(customentityrenderdispatcher);
      new UnsafeFieldAccessor<CustomEntityRenderDispatcher>(this.mc.worldRenderer, WorldRenderer.class, EntityRenderDispatcher.class)
         .setValue(customentityrenderdispatcher);
      new UnsafeFieldAccessor<CustomEntityRenderDispatcher>(
            this.mc.getBlockEntityRenderDispatcher(), BlockEntityRenderDispatcher.class, EntityRenderDispatcher.class
         )
         .setValue(customentityrenderdispatcher);
      ((ReloadableResourceManagerImpl)this.mc.getResourceManager()).registerReloader(customentityrenderdispatcher);
      this.mc.reloadResources();
      customentityrenderdispatcher.camera = this.mc.gameRenderer.getCamera();
      new UnsafeFieldAccessor<HeldItemRenderer>(this.mc.gameRenderer, GameRenderer.class, HeldItemRenderer.class)
         .setValue(customentityrenderdispatcher.getHeldItemRenderer());
   }

   private void registerTexture(String s, Identifier identifier, byte[] abyte) {
      if (TextureCache.hasTexture(s)) {
         try {
            if (abyte != null && abyte.length != 0) {
               ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte);
               BufferedImage bufferedimage = ImageIO.read(bytearrayinputstream);
               if (bufferedimage != null) {
                  NativeImage nativeimage = this.convertToNativeImage(bufferedimage);
                  NativeImageBackedTexture nativeimagebackedtexture = new NativeImageBackedTexture(nativeimage);
                  this.mc.getTextureManager().registerTexture(identifier, nativeimagebackedtexture);
               }
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   private NativeImage convertToNativeImage(BufferedImage bufferedimage) {
      int i = bufferedimage.getWidth();
      int j = bufferedimage.getHeight();
      NativeImage nativeimage = new NativeImage(i, j, false);

      for (int k = 0; k < i; k++) {
         for (int l = 0; l < j; l++) {
            nativeimage.setColorArgb(k, l, bufferedimage.getRGB(k, l));
         }
      }

      return nativeimage;
   }

   @Override
   public void unHook() {
   }
}
