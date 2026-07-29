package render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import core.ClientMain;
import java.util.Map;
import java.util.function.Supplier;
import module.HitboxTweaksModule;
import module.NoOverlayModule;
import module.PlayerOutlinesModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures.Model;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class CustomEntityRenderDispatcher extends EntityRenderDispatcher {
   private final Map<Model, EntityRendererFactory<AbstractClientPlayerEntity>> PLAYER_RENDERER_FACTORIES = Map.<Model, EntityRendererFactory<AbstractClientPlayerEntity>>of(
      Model.WIDE, context -> {
         return new CustomPlayerEntityRenderer(context, false);
      }, Model.SLIM, context -> {
         return new CustomPlayerEntityRenderer(context, true);
      }
   );
   private static final RenderLayer SHADOW_LAYER = RenderLayer.getEntityShadow(Identifier.ofVanilla("textures/misc/shadow.png"));
   private Map<Model, EntityRenderer<? extends PlayerEntity, ?>> modelRenderers = Map.<Model, EntityRenderer<? extends PlayerEntity, ?>>of();
   private final ItemModelManager itemModelManager;
   private final MapRenderer mapRenderer;
   private final BlockRenderManager blockRenderManager;
   private final TextRenderer textRenderer;
   public final TextureManager textureManager;
   public final GameOptions gameOptions;
   private final Supplier<LoadedEntityModels> entityModelsGetter;
   private final EquipmentModelLoader equipmentModelLoader;
   private final HeldItemRenderer itemInHandRenderer;
   private World world;
   private Quaternionf rotation;
   private boolean renderHitboxes;
   private boolean renderShadows = true;

   public CustomEntityRenderDispatcher(
      MinecraftClient minecraftclient,
      TextureManager texturemanager,
      ItemModelManager itemmodelmanager,
      ItemRenderer itemrenderer,
      MapRenderer maprenderer,
      BlockRenderManager blockrendermanager,
      TextRenderer textrenderer,
      GameOptions gameoptions,
      Supplier<LoadedEntityModels> supplier,
      EquipmentModelLoader equipmentmodelloader
   ) {
      super(
         minecraftclient,
         texturemanager,
         itemmodelmanager,
         itemrenderer,
         maprenderer,
         blockrendermanager,
         textrenderer,
         gameoptions,
         supplier,
         equipmentmodelloader
      );
      this.textureManager = texturemanager;
      this.itemModelManager = itemmodelmanager;
      this.mapRenderer = maprenderer;
      this.blockRenderManager = blockrendermanager;
      this.textRenderer = textrenderer;
      this.itemInHandRenderer = new HeldItemRenderer(minecraftclient, this, itemrenderer, itemmodelmanager);
      this.gameOptions = gameoptions;
      this.entityModelsGetter = supplier;
      this.equipmentModelLoader = equipmentmodelloader;
   }

   public void configure(World world, Camera camera, Entity entity) {
      this.world = worldx;
      this.camera = camera;
      this.rotation = camera.getRotation();
      this.targetedEntity = entity;
   }

   public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T entity) {
      if (entity instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
         Model model = abstractclientplayerentity.getSkinTextures().model();
         EntityRenderer entityrenderer = this.modelRenderers.get(model);
         return entityrenderer != null ? entityrenderer : this.modelRenderers.get(Model.WIDE);
      } else {
         return super.getRenderer(entity);
      }
   }

   public <E extends Entity> void render(
      E entity, double d0, double d1, double d2, float f, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i
   ) {
      EntityRenderer entityrenderer = this.<Entity>getRenderer(entity);
      this.render(entity, d0, d1, d2, f, matrixstack, vertexconsumerprovider, i, entityrenderer);
   }

   private <E extends Entity, S extends EntityRenderState> void render(
      E entity,
      double d0,
      double d1,
      double d2,
      float f,
      MatrixStack matrixstack,
      VertexConsumerProvider vertexconsumerprovider,
      int i,
      EntityRenderer<? super E, S> entityrenderer
   ) {
      try {
         if (vertexconsumerprovider instanceof OutlineVertexConsumerProvider outlinevertexconsumerprovider) {
            PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
            if (playeroutlinesmodule != null && playeroutlinesmodule.isEnabled() && playeroutlinesmodule.f(entity)) {
               int[] aint = playeroutlinesmodule.g();
               outlinevertexconsumerprovider.setColor(aint[0], aint[1], aint[2], aint[3]);
            }
         }

         EntityRenderState entityrenderstate = entityrenderer.getAndUpdateRenderState(entity, f);
         Vec3d vec3d = entityrenderer.getPositionOffset(entityrenderstate);
         double d6 = d0 + vec3d.getX();
         double d3 = d1 + vec3d.getY();
         double d4 = d2 + vec3d.getZ();
         matrixstack.push();
         matrixstack.translate(d6, d3, d4);
         entityrenderer.render(entityrenderstate, matrixstack, vertexconsumerprovider, i);
         if (entityrenderstate.onFire) {
            this.renderFire(
               matrixstack, vertexconsumerprovider, entityrenderstate, MathHelper.rotateAround(MathHelper.Y_AXIS, this.rotation, new Quaternionf())
            );
         }

         if (entity instanceof PlayerEntity) {
            matrixstack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
         }

         if ((Boolean)this.gameOptions.getEntityShadows().getValue() && this.renderShadows && !entityrenderstate.invisible) {
            float f1 = 0.15F;
            if (f1 > 0.0F) {
               double d5 = entityrenderstate.squaredDistanceToCamera;
               float f2 = (float)((1.0 - d5 / 256.0) * 1.0);
               if (f2 > 0.0F) {
                  renderShadow(matrixstack, vertexconsumerprovider, entityrenderstate, f2, f, this.world, Math.min(f1, 32.0F));
               }
            }
         }

         if (!(entity instanceof PlayerEntity)) {
            matrixstack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
         }

         if (this.renderHitboxes && !entityrenderstate.invisible && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
            HitboxTweaksModule hitboxtweaksmodule = ClientMain.getInstance().getModuleManager().<HitboxTweaksModule>getModule(HitboxTweaksModule.class);
            if (hitboxtweaksmodule != null && hitboxtweaksmodule.isEnabled()) {
               hitboxtweaksmodule.a();
               hitboxtweaksmodule.b(matrixstack, vertexconsumerprovider, entity, f);
            } else {
               renderHitbox(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.getLines()), entity, f, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }

         matrixstack.pop();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.create(throwable, "Rendering entity in world");
         CrashReportSection crashreportsection = crashreport.addElement("Entity being rendered");
         entity.populateCrashReport(crashreportsection);
         CrashReportSection crashreportsection1 = crashreport.addElement("Renderer details");
         crashreportsection1.add("Assigned renderer", entityrenderer);
         crashreportsection1.add("Location", CrashReportSection.createPositionString(this.world, d0, d1, d2));
         crashreportsection1.add("Delta", f);
         throw new CrashException(crashreport);
      }
   }

   private static void renderServerSideHitbox(MatrixStack matrixstack, Entity entity, VertexConsumerProvider vertexconsumerprovider) {
      Entity entity1 = getIntegratedServerEntity(entity);
      if (entity1 == null) {
         DebugRenderer.drawString(matrixstack, vertexconsumerprovider, "Missing", entity.getX(), entity.getBoundingBox().maxY + 1.5, entity.getZ(), -65536);
      } else {
         matrixstack.push();
         matrixstack.translate(entity1.getX() - entity.getX(), entity1.getY() - entity.getY(), entity1.getZ() - entity.getZ());
         renderHitbox(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.getLines()), entity1, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F);
         VertexRendering.drawVector(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.getLines()), new Vector3f(), entity1.getVelocity(), -256);
         matrixstack.pop();
      }
   }

   @Nullable
   private static Entity getIntegratedServerEntity(Entity entity) {
      IntegratedServer integratedserver = MinecraftClient.getInstance().getServer();
      if (integratedserver != null) {
         ServerWorld serverworld = integratedserver.getWorld(entity.getWorld().getRegistryKey());
         if (serverworld != null) {
            return serverworld.getEntityById(entity.getId());
         }
      }

      return null;
   }

   private static void renderHitbox(MatrixStack matrixstack, VertexConsumer vertexconsumer, Entity entity, float f, float f1, float f2, float f3, float f4) {
      Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
      VertexRendering.drawBox(matrixstack, vertexconsumer, box, f1, f2, f3, f4);
      if (entity instanceof EnderDragonEntity) {
         double d0 = -MathHelper.lerp(f, entity.lastRenderX, entity.getX());
         double d1 = -MathHelper.lerp(f, entity.lastRenderY, entity.getY());
         double d2 = -MathHelper.lerp(f, entity.lastRenderZ, entity.getZ());

         for (EnderDragonPart enderdragonpart : ((EnderDragonEntity)entity).getBodyParts()) {
            matrixstack.push();
            double d3 = d0 + MathHelper.lerp(f, enderdragonpart.lastRenderX, enderdragonpart.getX());
            double d4 = d1 + MathHelper.lerp(f, enderdragonpart.lastRenderY, enderdragonpart.getY());
            double d5 = d2 + MathHelper.lerp(f, enderdragonpart.lastRenderZ, enderdragonpart.getZ());
            matrixstack.translate(d3, d4, d5);
            VertexRendering.drawBox(
               matrixstack,
               vertexconsumer,
               enderdragonpart.getBoundingBox().offset(-enderdragonpart.getX(), -enderdragonpart.getY(), -enderdragonpart.getZ()),
               0.25F,
               1.0F,
               0.0F,
               1.0F
            );
            matrixstack.pop();
         }
      }

      if (entity instanceof LivingEntity) {
         float f6 = 0.01F;
      }

      Entity entity1 = entity.getVehicle();
      if (entity1 != null) {
         float f5 = Math.min(entity1.getWidth(), entity.getWidth()) / 2.0F;
         float f7 = 0.0625F;
         Vec3d vec3d = entity1.getPassengerRidingPos(entity).subtract(entity.getPos());
         VertexRendering.drawBox(
            matrixstack, vertexconsumer, vec3d.x - f5, vec3d.y, vec3d.z - f5, vec3d.x + f5, vec3d.y + 0.0625, vec3d.z + f5, 1.0F, 0.0F, 0.0F, 1.0F
         );
      }
   }

   private static void renderShadow(
      MatrixStack matrixstack,
      VertexConsumerProvider vertexconsumerprovider,
      EntityRenderState entityrenderstate,
      float f,
      float f4,
      WorldView worldview,
      float f1
   ) {
      float f2 = Math.min(f / 0.5F, f1);
      int i = MathHelper.floor(entityrenderstate.x - f1);
      int j = MathHelper.floor(entityrenderstate.x + f1);
      int k = MathHelper.floor(entityrenderstate.y - f2);
      int l = MathHelper.floor(entityrenderstate.y);
      int i1 = MathHelper.floor(entityrenderstate.z - f1);
      int j1 = MathHelper.floor(entityrenderstate.z + f1);
      Entry entry = matrixstack.peek();
      VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(SHADOW_LAYER);
      Mutable mutable = new Mutable();

      for (int k1 = i1; k1 <= j1; k1++) {
         for (int l1 = i; l1 <= j; l1++) {
            mutable.set(l1, 0, k1);
            Chunk chunk = worldview.getChunk(mutable);

            for (int i2 = k; i2 <= l; i2++) {
               mutable.setY(i2);
               float f3 = f - (float)(entityrenderstate.y - mutable.getY()) * 0.5F;
               renderShadowPart(entry, vertexconsumer, chunk, worldview, mutable, entityrenderstate.x, entityrenderstate.y, entityrenderstate.z, f1, f3);
            }
         }
      }
   }

   private static void renderShadowPart(
      Entry entry, VertexConsumer vertexconsumer, Chunk chunk, WorldView worldview, BlockPos blockpos, double d0, double d1, double d2, float f, float f1
   ) {
      BlockPos blockpos1 = blockpos.down();
      BlockState blockstate = chunk.getBlockState(blockpos1);
      if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && worldview.getLightLevel(blockpos) > 3 && blockstate.isFullCube(chunk, blockpos1)) {
         VoxelShape voxelshape = blockstate.getOutlineShape(chunk, blockpos1);
         if (!voxelshape.isEmpty()) {
            float f2 = LightmapTextureManager.getBrightness(worldview.getDimension(), worldview.getLightLevel(blockpos));
            float f3 = f1 * 0.5F * f2;
            if (f3 >= 0.0F) {
               if (f3 > 1.0F) {
                  f3 = 1.0F;
               }

               int i = ColorHelper.getArgb(MathHelper.floor(f3 * 255.0F), 255, 255, 255);
               Box box = voxelshape.getBoundingBox();
               double d3 = blockpos.getX() + box.minX;
               double d4 = blockpos.getX() + box.maxX;
               double d5 = blockpos.getY() + box.minY;
               double d6 = blockpos.getZ() + box.minZ;
               double d7 = blockpos.getZ() + box.maxZ;
               float f4 = (float)(d3 - d0);
               float f5 = (float)(d4 - d0);
               float f6 = (float)(d5 - d1);
               float f7 = (float)(d6 - d2);
               float f8 = (float)(d7 - d2);
               float f9 = -f4 / 2.0F / f + 0.5F;
               float f10 = -f5 / 2.0F / f + 0.5F;
               float f11 = -f7 / 2.0F / f + 0.5F;
               float f12 = -f8 / 2.0F / f + 0.5F;
               drawShadowVertex(entry, vertexconsumer, i, f4, f6, f7, f9, f11);
               drawShadowVertex(entry, vertexconsumer, i, f4, f6, f8, f9, f12);
               drawShadowVertex(entry, vertexconsumer, i, f5, f6, f8, f10, f12);
               drawShadowVertex(entry, vertexconsumer, i, f5, f6, f7, f10, f11);
            }
         }
      }
   }

   private static void drawShadowVertex(Entry entry, VertexConsumer vertexconsumer, int i, float f, float f1, float f2, float f3, float f4) {
      Vector3f vector3f = entry.getPositionMatrix().transformPosition(f, f1, f2, new Vector3f());
      vertexconsumer.vertex(vector3f.x(), vector3f.y(), vector3f.z(), i, f3, f4, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 1.0F, 0.0F);
   }

   private void renderFire(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, EntityRenderState entityrenderstate, Quaternionf quaternionf) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled()) {
         Sprite sprite = ModelBaker.FIRE_0.getSprite();
         Sprite sprite1 = ModelBaker.FIRE_1.getSprite();
         matrixstack.push();
         float f = entityrenderstate.width * 1.4F;
         matrixstack.scale(f, f, f);
         float f1 = 0.5F;
         float f2 = 0.0F;
         float f3 = entityrenderstate.height / f;
         float f4 = 0.0F;
         matrixstack.multiply(quaternionf);
         matrixstack.translate(0.0F, 0.0F, 0.3F - (int)f3 * 0.02F);
         float f5 = 0.0F;
         int i = 0;
         VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(TexturedRenderLayers.getEntityCutout());

         for (Entry entry = matrixstack.peek(); f3 > 0.0F; i++) {
            Sprite sprite2 = i % 2 == 0 ? sprite : sprite1;
            float f6 = sprite2.getMinU();
            float f7 = sprite2.getMinV();
            float f8 = sprite2.getMaxU();
            float f9 = sprite2.getMaxV();
            if (i / 2 % 2 == 0) {
               float f10 = f8;
               f8 = f6;
               f6 = f10;
            }

            drawFireVertex(entry, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            drawFireVertex(entry, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            drawFireVertex(entry, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            drawFireVertex(entry, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 -= 0.03F;
         }

         matrixstack.pop();
      }
   }

   private static void drawFireVertex(Entry entry, VertexConsumer vertexconsumer, float f, float f1, float f2, float f3, float f4) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled()) {
         vertexconsumer.vertex(entry, f, f1, f2).color(-1).texture(f3, f4).overlay(0, 10).light(240).normal(entry, 0.0F, 1.0F, 0.0F);
      }
   }

   public Map<Model, EntityRenderer<? extends PlayerEntity, ?>> reloadPlayerRenderers(Context context) {
      Builder builder = ImmutableMap.builder();
      this.PLAYER_RENDERER_FACTORIES.forEach((model, entityrendererfactory) -> {
         try {
            builder.put(model, entityrendererfactory.create(context));
         } catch (Exception exception) {
            String s = String.valueOf(model);
            throw new IllegalArgumentException("Failed to create player model for " + s, exception);
         }
      });
      return builder.build();
   }

   public void setRenderHitboxes(boolean flag) {
      this.renderHitboxes = flag;
      super.setRenderHitboxes(flag);
   }

   public boolean shouldRenderHitboxes() {
      return this.renderHitboxes;
   }

   public void setRenderShadows(boolean flag) {
      this.renderShadows = flag;
      super.setRenderShadows(flag);
   }

   public void reload(ResourceManager resourcemanager) {
      Context context = new Context(
         this,
         this.itemModelManager,
         this.mapRenderer,
         this.blockRenderManager,
         resourcemanager,
         this.entityModelsGetter.get(),
         this.equipmentModelLoader,
         this.textRenderer
      );
      this.modelRenderers = this.reloadPlayerRenderers(context);
      super.reload(resourcemanager);
   }

   public Quaternionf getRotation() {
      return this.rotation;
   }

   public void setRotation(Quaternionf quaternionf) {
      this.rotation = quaternionf;
   }
}
