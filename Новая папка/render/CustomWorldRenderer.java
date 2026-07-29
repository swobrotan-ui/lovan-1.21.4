package render;

import com.mojang.blaze3d.systems.RenderSystem;
import core.ClientMain;
import java.lang.reflect.Method;
import java.util.List;
import module.CustomFogModule;
import module.CustomTimeModule;
import module.NameTagsModule;
import module.PlayerOutlinesModule;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterSetup;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.AfterTranslucent;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.BeforeEntities;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.End;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents.Start;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.SimpleFramebufferFactory;
import net.minecraft.client.option.CloudRenderMode;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.Fog;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPass;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.BackgroundRenderer.FogType;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.Handle;
import net.minecraft.client.util.ObjectAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.world.World;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;
import util.UnsafeFieldAccessor;

public class CustomWorldRenderer extends WorldRenderer {
   private static final Identifier ENTITY_OUTLINE = Identifier.ofVanilla("entity_outline");
   private static final Identifier CUSTOM_ENTITY_OUTLINE = Identifier.ofVanilla("custom_entity_outline");
   private static final Identifier TRANSPARENCY = Identifier.ofVanilla("transparency");
   private final MinecraftClient client;
   private UnsafeFieldAccessor<Frustum> frustumField;
   private UnsafeFieldAccessor<Frustum> capturedFrustumField;
   private UnsafeFieldAccessor<Boolean> shouldCaptureFrustumField;
   private UnsafeFieldAccessor<DefaultFramebufferSet> framebufferSetField;
   private UnsafeFieldAccessor<Framebuffer> entityOutlineFramebufferField;
   private UnsafeFieldAccessor<List<Entity>> renderedEntitiesField;
   private UnsafeFieldAccessor<Integer> renderedEntitiesCountField;
   private UnsafeFieldAccessor<Integer> ticksField;
   private UnsafeFieldAccessor<EntityRenderDispatcher> entityRenderDispatcherField;
   private UnsafeFieldAccessor<BlockEntityRenderDispatcher> blockEntityRenderDispatcherField;
   private Method getEntitiesToRenderMethod;
   private Method setupTerrainMethod;
   private Method updateChunksMethod;
   private Method getTransparencyPostEffectProcessorMethod;
   private Method renderSkyMethod;
   private Method renderMainMethod;
   private Method renderParticlesMethod;
   private Method renderCloudsMethod;
   private Method renderWeatherMethod;
   private Method renderLateDebugMethod;
   private Method renderLayerMethod;
   private Method renderEntitiesMethod;
   private Method renderBlockEntitiesMethod;
   private Method renderBlockDamageMethod;
   private Method renderTargetBlockOutlineMethod;
   private Method checkEmptyMethod;
   private Method canDrawEntityOutlinesMethod;
   private UnsafeFieldAccessor<BufferBuilderStorage> bufferBuildersField;
   private UnsafeFieldAccessor<Object> weatherRenderingField;
   private boolean initialized = false;
   private final gk fabricContext = new gk();
   String getEntitiesName = ClientMain.getInstance().isDev() ? "getEntitiesToRender" : "method_62211";
   String setupTerrainName = ClientMain.getInstance().isDev() ? "setupTerrain" : "method_3273";
   String updateChunksName = ClientMain.getInstance().isDev() ? "updateChunks" : "method_3269";
   String getTransparencyName = ClientMain.getInstance().isDev() ? "getTransparencyPostEffectProcessor" : "method_62907";
   String renderSkyName = ClientMain.getInstance().isDev() ? "renderSky" : "method_62200";
   String renderMainName = ClientMain.getInstance().isDev() ? "renderMain" : "method_62202";
   String renderParticlesName = ClientMain.getInstance().isDev() ? "renderParticles" : "method_62201";
   String renderCloudsName = ClientMain.getInstance().isDev() ? "renderClouds" : "method_62204";
   String renderWeatherName = ClientMain.getInstance().isDev() ? "renderWeather" : "method_62203";
   String renderLateDebugName = ClientMain.getInstance().isDev() ? "renderLateDebug" : "method_62199";
   String renderLayerName = ClientMain.getInstance().isDev() ? "renderLayer" : "method_3251";
   String renderEntitiesName = ClientMain.getInstance().isDev() ? "renderEntities" : "method_62207";
   String renderBlockEntitiesName = ClientMain.getInstance().isDev() ? "renderBlockEntities" : "method_62208";
   String renderBlockDamageName = ClientMain.getInstance().isDev() ? "renderBlockDamage" : "method_62206";
   String renderTargetBlockOutlineName = ClientMain.getInstance().isDev() ? "renderTargetBlockOutline" : "method_62210";
   String checkEmptyName = ClientMain.getInstance().isDev() ? "checkEmpty" : "method_22979";
   String canDrawEntityOutlinesName = ClientMain.getInstance().isDev() ? "canDrawEntityOutlines" : "method_3270";

   public CustomWorldRenderer(
      MinecraftClient minecraftclient,
      EntityRenderDispatcher entityrenderdispatcher,
      BlockEntityRenderDispatcher blockentityrenderdispatcher,
      BufferBuilderStorage bufferbuilderstorage
   ) {
      super(minecraftclient, entityrenderdispatcher, blockentityrenderdispatcher, bufferbuilderstorage);
      this.client = minecraftclient;
   }

   private void initReflection() {
      if (!this.initialized) {
         try {
            Class<WorldRenderer> oclass = WorldRenderer.class;
            this.entityRenderDispatcherField = new UnsafeFieldAccessor<EntityRenderDispatcher>(this, oclass, 8);
            this.blockEntityRenderDispatcherField = new UnsafeFieldAccessor<BlockEntityRenderDispatcher>(this, oclass, 9);
            this.ticksField = new UnsafeFieldAccessor<Integer>(this, oclass, 21);
            this.entityOutlineFramebufferField = new UnsafeFieldAccessor<Framebuffer>(this, oclass, 24);
            this.framebufferSetField = new UnsafeFieldAccessor<DefaultFramebufferSet>(this, oclass, 25);
            this.renderedEntitiesField = new UnsafeFieldAccessor<List<Entity>>(this, oclass, 36);
            this.renderedEntitiesCountField = new UnsafeFieldAccessor<Integer>(this, oclass, 37);
            this.frustumField = new UnsafeFieldAccessor<Frustum>(this, oclass, 38);
            this.shouldCaptureFrustumField = new UnsafeFieldAccessor<Boolean>(this, oclass, 39);
            this.capturedFrustumField = new UnsafeFieldAccessor<Frustum>(this, oclass, 40);
            this.getEntitiesToRenderMethod = oclass.getDeclaredMethod(this.getEntitiesName, Camera.class, Frustum.class, List.class);
            this.getEntitiesToRenderMethod.setAccessible(true);
            this.setupTerrainMethod = oclass.getDeclaredMethod(this.setupTerrainName, Camera.class, Frustum.class, boolean.class, boolean.class);
            this.setupTerrainMethod.setAccessible(true);
            this.updateChunksMethod = oclass.getDeclaredMethod(this.updateChunksName, Camera.class);
            this.updateChunksMethod.setAccessible(true);
            this.getTransparencyPostEffectProcessorMethod = oclass.getDeclaredMethod(this.getTransparencyName);
            this.getTransparencyPostEffectProcessorMethod.setAccessible(true);
            this.renderSkyMethod = oclass.getDeclaredMethod(this.renderSkyName, FrameGraphBuilder.class, Camera.class, float.class, Fog.class);
            this.renderSkyMethod.setAccessible(true);
            this.renderMainMethod = oclass.getDeclaredMethod(
               this.renderMainName,
               FrameGraphBuilder.class,
               Frustum.class,
               Camera.class,
               Matrix4f.class,
               Matrix4f.class,
               Fog.class,
               boolean.class,
               boolean.class,
               RenderTickCounter.class,
               Profiler.class
            );
            this.renderMainMethod.setAccessible(true);
            this.renderParticlesMethod = oclass.getDeclaredMethod(this.renderParticlesName, FrameGraphBuilder.class, Camera.class, float.class, Fog.class);
            this.renderParticlesMethod.setAccessible(true);
            this.renderCloudsMethod = oclass.getDeclaredMethod(
               this.renderCloudsName,
               FrameGraphBuilder.class,
               Matrix4f.class,
               Matrix4f.class,
               CloudRenderMode.class,
               Vec3d.class,
               float.class,
               int.class,
               float.class
            );
            this.renderCloudsMethod.setAccessible(true);
            this.renderWeatherMethod = oclass.getDeclaredMethod(this.renderWeatherName, FrameGraphBuilder.class, Vec3d.class, float.class, Fog.class);
            this.renderWeatherMethod.setAccessible(true);
            this.renderLateDebugMethod = oclass.getDeclaredMethod(this.renderLateDebugName, FrameGraphBuilder.class, Vec3d.class, Fog.class);
            this.renderLateDebugMethod.setAccessible(true);
            this.renderLayerMethod = oclass.getDeclaredMethod(
               this.renderLayerName, RenderLayer.class, double.class, double.class, double.class, Matrix4f.class, Matrix4f.class
            );
            this.renderLayerMethod.setAccessible(true);
            this.renderEntitiesMethod = oclass.getDeclaredMethod(
               this.renderEntitiesName, MatrixStack.class, Immediate.class, Camera.class, RenderTickCounter.class, List.class
            );
            this.renderEntitiesMethod.setAccessible(true);
            this.renderBlockEntitiesMethod = oclass.getDeclaredMethod(
               this.renderBlockEntitiesName, MatrixStack.class, Immediate.class, Immediate.class, Camera.class, float.class
            );
            this.renderBlockEntitiesMethod.setAccessible(true);
            this.renderBlockDamageMethod = oclass.getDeclaredMethod(this.renderBlockDamageName, MatrixStack.class, Camera.class, Immediate.class);
            this.renderBlockDamageMethod.setAccessible(true);
            this.renderTargetBlockOutlineMethod = oclass.getDeclaredMethod(
               this.renderTargetBlockOutlineName, Camera.class, Immediate.class, MatrixStack.class, boolean.class
            );
            this.renderTargetBlockOutlineMethod.setAccessible(true);
            this.checkEmptyMethod = oclass.getDeclaredMethod(this.checkEmptyName, MatrixStack.class);
            this.checkEmptyMethod.setAccessible(true);
            this.canDrawEntityOutlinesMethod = oclass.getDeclaredMethod(this.canDrawEntityOutlinesName);
            this.canDrawEntityOutlinesMethod.setAccessible(true);
            this.bufferBuildersField = new UnsafeFieldAccessor<BufferBuilderStorage>(this, oclass, BufferBuilderStorage.class);
            this.weatherRenderingField = new UnsafeFieldAccessor<Object>(this, oclass, 14);
            this.initialized = true;
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public void render(
      ObjectAllocator objectallocator,
      RenderTickCounter rendertickcounter,
      boolean flag,
      Camera camera,
      GameRenderer gamerenderer,
      Matrix4f matrix4f,
      Matrix4f matrix4f1
   ) {
      this.applyCustomTimeIfEnabled();
      CustomFogModule customfogmodule = this.getCustomFogModule();
      PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
      NameTagsModule nametagsmodule = null;

      try {
         nametagsmodule = ClientMain.getInstance().getModuleManager().<NameTagsModule>getModule(NameTagsModule.class);
      } catch (Exception exception1) {
      }

      boolean flag1 = customfogmodule != null && customfogmodule.e()
         || playeroutlinesmodule != null && playeroutlinesmodule.isEnabled()
         || nametagsmodule != null && nametagsmodule.isEnabled();
      if (!flag1) {
         super.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
      } else {
         this.initReflection();
         if (!this.initialized) {
            super.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
         } else {
            try {
               this.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1, customfogmodule);
            } catch (Exception exception) {
               exception.printStackTrace();
               super.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
            }
         }
      }
   }

   private void render(
      ObjectAllocator objectallocator,
      RenderTickCounter rendertickcounter,
      boolean flag,
      Camera camera,
      GameRenderer gamerenderer,
      Matrix4f matrix4f,
      Matrix4f matrix4f1,
      CustomFogModule customfogmodule
   ) throws Exception {
      Frustum frustum = this.frustumField.getValue();
      DefaultFramebufferSet defaultframebufferset = this.framebufferSetField.getValue();
      List list = this.renderedEntitiesField.getValue();
      BufferBuilderStorage bufferbuilderstorage = this.bufferBuildersField.getValue();
      if (frustum != null && defaultframebufferset != null && list != null && bufferbuilderstorage != null) {
         float f = rendertickcounter.getTickDelta(false);
         RenderSystem.setShaderGameTime(this.client.world.getTime(), f);
         this.blockEntityRenderDispatcherField.getValue().configure(this.client.world, camera, this.client.crosshairTarget);
         this.entityRenderDispatcherField.getValue().configure(this.client.world, camera, this.client.targetedEntity);
         Profiler profiler = Profilers.get();
         profiler.swap("light_update_queue");
         this.client.world.runQueuedChunkUpdates();
         profiler.swap("light_updates");
         this.client.world.getChunkManager().getLightingProvider().doLightUpdates();
         Vec3d vec3d = camera.getPos();
         double d0 = vec3d.getX();
         double d1 = vec3d.getY();
         double d2 = vec3d.getZ();
         profiler.swap("culling");
         boolean flag1 = this.capturedFrustumField.getValue() != null;
         Frustum frustum1 = flag1 ? this.capturedFrustumField.getValue() : frustum;
         Profilers.get().swap("captureFrustum");
         if (this.shouldCaptureFrustumField.getBoolean(this)) {
            if (flag1) {
               this.capturedFrustumField.setValue(new Frustum(matrix4f, matrix4f1));
            } else {
               this.capturedFrustumField.setValue(frustum1);
            }

            this.capturedFrustumField.getValue().setPosition(d0, d1, d2);
            this.shouldCaptureFrustumField.setBoolean(this, false);
         }

         this.frustumField.setValue(frustum1);
         profiler.swap("fog");
         float f1 = gamerenderer.getViewDistance();

         boolean flag2;
         Vector4f vector4f;
         try {
            flag2 = this.client.world.getDimensionEffects().useThickFog(MathHelper.floor(d0), MathHelper.floor(d1))
               || this.client.inGameHud.getBossBarHud().shouldThickenFog();
            vector4f = BackgroundRenderer.getFogColor(
               camera, f, this.client.world, this.client.options.getClampedViewDistance(), gamerenderer.getSkyDarkness(f)
            );
         } catch (Exception exception2) {
            flag2 = false;
            vector4f = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
         }

         Fog fog = customfogmodule != null ? customfogmodule.d() : null;
         Fog fog1 = customfogmodule != null ? customfogmodule.d() : null;
         if (fog == null) {
            fog = BackgroundRenderer.applyFog(camera, FogType.FOG_TERRAIN, vector4f, f1, flag2, f);
         }

         if (fog1 == null) {
            fog1 = BackgroundRenderer.applyFog(camera, FogType.FOG_SKY, vector4f, f1, flag2, f);
         }

         profiler.swap("cullEntities");
         boolean flag3 = (Boolean)this.getEntitiesToRenderMethod.invoke(this, camera, frustum1, list);
         this.renderedEntitiesCountField.setInt(this, list.size());
         PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
         boolean flag4 = playeroutlinesmodule != null && playeroutlinesmodule.isEnabled();
         if (flag4) {
            flag3 = true;
         } else {
            flag3 = false;
         }

         profiler.swap("terrain_setup");
         this.setupTerrainMethod.invoke(this, camera, frustum1, flag1, this.client.player.isSpectator());
         profiler.swap("compile_sections");
         this.updateChunksMethod.invoke(this, camera);
         boolean flag5 = MinecraftClient.isFabulousGraphicsOrBetter();
         this.fabricContext.prepare(this, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1, this.client.world, flag5, frustum1);

         try {
            ((Start)WorldRenderEvents.START.invoker()).onStart(this.fabricContext);
         } catch (Exception exception1) {
         }

         Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
         matrix4fstack.pushMatrix();
         matrix4fstack.mul(matrix4f);
         FrameGraphBuilder framegraphbuilder = new FrameGraphBuilder();
         defaultframebufferset.mainFramebuffer = framegraphbuilder.createObjectNode("main", this.client.getFramebuffer());
         int i = this.client.getFramebuffer().textureWidth;
         int j = this.client.getFramebuffer().textureHeight;
         SimpleFramebufferFactory simpleframebufferfactory = new SimpleFramebufferFactory(i, j, true);
         PostEffectProcessor posteffectprocessor = (PostEffectProcessor)this.getTransparencyPostEffectProcessorMethod.invoke(this);
         if (posteffectprocessor != null) {
            defaultframebufferset.translucentFramebuffer = framegraphbuilder.createResourceHandle("translucent", simpleframebufferfactory);
            defaultframebufferset.itemEntityFramebuffer = framegraphbuilder.createResourceHandle("item_entity", simpleframebufferfactory);
            defaultframebufferset.particlesFramebuffer = framegraphbuilder.createResourceHandle("particles", simpleframebufferfactory);
            defaultframebufferset.weatherFramebuffer = framegraphbuilder.createResourceHandle("weather", simpleframebufferfactory);
            defaultframebufferset.cloudsFramebuffer = framegraphbuilder.createResourceHandle("clouds", simpleframebufferfactory);
         }

         Framebuffer framebuffer = this.entityOutlineFramebufferField.getValue();
         if (framebuffer != null && framebuffer.fbo != -1) {
            defaultframebufferset.entityOutlineFramebuffer = framegraphbuilder.createObjectNode("entity_outline", framebuffer);
         }

         RenderPass renderpass = framegraphbuilder.createPass("clear");
         defaultframebufferset.mainFramebuffer = renderpass.transfer(defaultframebufferset.mainFramebuffer);
         Fog fog2 = fog;
         renderpass.setRenderer(() -> {
            RenderSystem.clearColor(fog2.red(), fog2.green(), fog2.blue(), 0.0F);
            RenderSystem.clear(16640);
         });
         if (!flag2) {
            this.renderSkyMethod.invoke(this, framegraphbuilder, camera, f, fog1);
         }

         this.renderMainWithFabricEvents(
            framegraphbuilder,
            frustum1,
            camera,
            matrix4f,
            matrix4f1,
            fog,
            flag,
            flag3,
            rendertickcounter,
            profiler,
            defaultframebufferset,
            bufferbuilderstorage,
            list,
            d0,
            d1,
            d2,
            f
         );
         PlayerOutlinesModule playeroutlinesmodule1 = PlayerOutlinesModule.h();
         boolean flag6 = playeroutlinesmodule1 != null && playeroutlinesmodule1.isEnabled();
         if (flag6 && flag3 && defaultframebufferset.entityOutlineFramebuffer != null) {
            PostEffectProcessor posteffectprocessor1 = this.client
               .getShaderLoader()
               .loadPostEffect(CUSTOM_ENTITY_OUTLINE, DefaultFramebufferSet.MAIN_AND_ENTITY_OUTLINE);
            if (posteffectprocessor1 != null) {
               float[] afloat = playeroutlinesmodule1.i();
               float[] afloat1 = playeroutlinesmodule1.j();
               posteffectprocessor1.setUniforms("BlurWeight0", afloat[0]);
               posteffectprocessor1.setUniforms("BlurWeight1", afloat[1]);
               posteffectprocessor1.setUniforms("BlurWeight2", afloat[2]);
               posteffectprocessor1.setUniforms("BlurWeight3", afloat[3]);
               posteffectprocessor1.setUniforms("BlurRadius1", afloat1[0]);
               posteffectprocessor1.setUniforms("BlurRadius2", afloat1[1]);
               posteffectprocessor1.setUniforms("BlurRadius3", afloat1[2]);
               posteffectprocessor1.setUniforms("Brightness", playeroutlinesmodule1.k());
               posteffectprocessor1.setUniforms("GlowIntensity", playeroutlinesmodule1.l());
               posteffectprocessor1.setUniforms("OriginalIntensity", playeroutlinesmodule1.m());
               posteffectprocessor1.render(framegraphbuilder, i, j, defaultframebufferset);
            }
         }

         this.renderParticlesMethod.invoke(this, framegraphbuilder, camera, f, fog);
         CloudRenderMode cloudrendermode = this.client.options.getCloudRenderModeValue();
         if (cloudrendermode != CloudRenderMode.OFF) {
            float f3 = this.client.world.getDimensionEffects().getCloudsHeight();
            if (!Float.isNaN(f3)) {
               int l = this.ticksField.getInt(this);
               float f2 = l + f;
               int k = this.client.world.getCloudsColor(f);
               this.renderCloudsMethod.invoke(this, framegraphbuilder, matrix4f, matrix4f1, cloudrendermode, camera.getPos(), f2, k, f3 + 0.33F);
            }
         }

         this.renderWeatherWithoutBorder(framegraphbuilder, camera.getPos(), f, fog);
         if (posteffectprocessor != null) {
            posteffectprocessor.render(framegraphbuilder, i, j, defaultframebufferset);
         }

         RenderPass renderpass1 = framegraphbuilder.createPass("after_clouds");
         defaultframebufferset.mainFramebuffer = renderpass1.transfer(defaultframebufferset.mainFramebuffer);
         renderpass1.setRenderer(() -> {
            try {
               MatrixStack matrixstack = new MatrixStack();
               Immediate immediate = bufferbuilderstorage.getEntityVertexConsumers();
               this.fabricContext.setMatrixStack(matrixstack);
               this.fabricContext.setConsumers(immediate);
               ClientMain.getInstance().getEventManager().t(this.fabricContext);
            } catch (Exception exception3) {
            }
         });
         this.renderLateDebugMethod.invoke(this, framegraphbuilder, vec3d, fog);
         profiler.swap("framegraph");
         framegraphbuilder.run(objectallocator, new hde(this, profiler));
         this.client.getFramebuffer().beginWrite(false);
         list.clear();
         defaultframebufferset.clear();
         matrix4fstack.popMatrix();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.setShaderFog(Fog.DUMMY);

         try {
            ((End)WorldRenderEvents.END.invoker()).onEnd(this.fabricContext);
         } catch (Exception exception) {
         }
      } else {
         super.render(objectallocator, rendertickcounter, flag, camera, gamerenderer, matrix4f, matrix4f1);
      }
   }

   private void renderMainWithFabricEvents(
      FrameGraphBuilder framegraphbuilder,
      Frustum frustum,
      Camera camera,
      Matrix4f matrix4f,
      Matrix4f matrix4f1,
      Fog fog,
      boolean flag,
      boolean flag1,
      RenderTickCounter rendertickcounter,
      Profiler profiler,
      DefaultFramebufferSet defaultframebufferset,
      BufferBuilderStorage bufferbuilderstorage,
      List<Entity> list,
      double d0,
      double d1,
      double d2,
      float f
   ) {
      RenderPass renderpass = framegraphbuilder.createPass("main");
      defaultframebufferset.mainFramebuffer = renderpass.transfer(defaultframebufferset.mainFramebuffer);
      if (defaultframebufferset.translucentFramebuffer != null) {
         defaultframebufferset.translucentFramebuffer = renderpass.transfer(defaultframebufferset.translucentFramebuffer);
      }

      if (defaultframebufferset.itemEntityFramebuffer != null) {
         defaultframebufferset.itemEntityFramebuffer = renderpass.transfer(defaultframebufferset.itemEntityFramebuffer);
      }

      if (defaultframebufferset.weatherFramebuffer != null) {
         defaultframebufferset.weatherFramebuffer = renderpass.transfer(defaultframebufferset.weatherFramebuffer);
      }

      if (flag1 && defaultframebufferset.entityOutlineFramebuffer != null) {
         defaultframebufferset.entityOutlineFramebuffer = renderpass.transfer(defaultframebufferset.entityOutlineFramebuffer);
      }

      Handle handle = defaultframebufferset.mainFramebuffer;
      Handle handle1 = defaultframebufferset.translucentFramebuffer;
      Handle handle2 = defaultframebufferset.itemEntityFramebuffer;
      Handle handle3 = defaultframebufferset.weatherFramebuffer;
      Handle handle4 = defaultframebufferset.entityOutlineFramebuffer;
      gk gk = this.fabricContext;
      renderpass.setRenderer(() -> {
         try {
            RenderSystem.setShaderFog(fog);
            float f = rendertickcounter.getTickDelta(false);
            Vec3d vec3d = camera.getPos();
            double d0 = vec3d.getX();
            double d1 = vec3d.getY();
            double d2 = vec3d.getZ();

            try {
               ((AfterSetup)WorldRenderEvents.AFTER_SETUP.invoker()).afterSetup(gk);
            } catch (Exception exception3) {
            }

            profiler.push("terrain");
            this.renderLayerMethod.invoke(this, RenderLayer.getSolid(), d0, d1, d2, matrix4f, matrix4f1);
            this.renderLayerMethod.invoke(this, RenderLayer.getCutoutMipped(), d0, d1, d2, matrix4f, matrix4f1);
            this.renderLayerMethod.invoke(this, RenderLayer.getCutout(), d0, d1, d2, matrix4f, matrix4f1);
            if (this.client.world.getDimensionEffects().isDarkened()) {
               DiffuseLighting.enableForLevel();
            } else {
               DiffuseLighting.disableForLevel();
            }

            if (handle2 != null) {
               ((Framebuffer)handle2.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               ((Framebuffer)handle2.get()).clear();
               ((Framebuffer)handle2.get()).copyDepthFrom(this.client.getFramebuffer());
               ((Framebuffer)handle.get()).beginWrite(false);
            }

            if (handle3 != null) {
               ((Framebuffer)handle3.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               ((Framebuffer)handle3.get()).clear();
            }

            boolean flag3 = (Boolean)this.canDrawEntityOutlinesMethod.invoke(this);
            if (flag3 && handle4 != null) {
               ((Framebuffer)handle4.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               ((Framebuffer)handle4.get()).clear();
               ((Framebuffer)handle.get()).beginWrite(false);
            }

            MatrixStack matrixstack = new MatrixStack();
            Immediate immediate = bufferbuilderstorage.getEntityVertexConsumers();
            Immediate immediate1 = bufferbuilderstorage.getEffectVertexConsumers();

            try {
               gk.setMatrixStack(matrixstack);
               gk.setConsumers(immediate);
               ((BeforeEntities)WorldRenderEvents.BEFORE_ENTITIES.invoker()).beforeEntities(gk);
            } catch (Exception exception2) {
            }

            profiler.swap("entities");
            this.renderEntitiesMethod.invoke(this, matrixstack, immediate, camera, rendertickcounter, list);
            immediate.drawCurrentLayer();
            this.checkEmptyMethod.invoke(this, matrixstack);

            try {
               gk.setMatrixStack(matrixstack);
               gk.setConsumers(immediate);
               ((AfterEntities)WorldRenderEvents.AFTER_ENTITIES.invoker()).afterEntities(gk);
            } catch (Exception exception1) {
            }

            profiler.swap("blockentities");
            this.renderBlockEntitiesMethod.invoke(this, matrixstack, immediate, immediate1, camera, f);
            immediate.drawCurrentLayer();
            this.checkEmptyMethod.invoke(this, matrixstack);
            immediate.draw(RenderLayer.getSolid());
            immediate.draw(RenderLayer.getEndPortal());
            immediate.draw(RenderLayer.getEndGateway());
            immediate.draw(TexturedRenderLayers.getEntitySolid());
            immediate.draw(TexturedRenderLayers.getEntityCutout());
            immediate.draw(TexturedRenderLayers.getBeds());
            immediate.draw(TexturedRenderLayers.getShulkerBoxes());
            immediate.draw(TexturedRenderLayers.getSign());
            immediate.draw(TexturedRenderLayers.getHangingSign());
            immediate.draw(TexturedRenderLayers.getChest());
            bufferbuilderstorage.getOutlineVertexConsumers().draw();
            if (flag) {
               this.renderTargetBlockOutlineMethod.invoke(this, camera, immediate, matrixstack, false);
            }

            profiler.swap("debug");
            this.client.debugRenderer.render(matrixstack, frustum, immediate, d0, d1, d2);
            immediate.drawCurrentLayer();
            this.checkEmptyMethod.invoke(this, matrixstack);
            immediate.draw(TexturedRenderLayers.getItemEntityTranslucentCull());
            immediate.draw(TexturedRenderLayers.getBannerPatterns());
            immediate.draw(TexturedRenderLayers.getShieldPatterns());
            immediate.draw(RenderLayer.getArmorEntityGlint());
            immediate.draw(RenderLayer.getGlint());
            immediate.draw(RenderLayer.getGlintTranslucent());
            immediate.draw(RenderLayer.getEntityGlint());
            profiler.swap("destroyProgress");
            this.renderBlockDamageMethod.invoke(this, matrixstack, camera, immediate1);
            immediate1.draw();
            this.checkEmptyMethod.invoke(this, matrixstack);
            immediate.draw(RenderLayer.getWaterMask());
            immediate.draw();
            if (handle1 != null) {
               ((Framebuffer)handle1.get()).setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               ((Framebuffer)handle1.get()).clear();
               ((Framebuffer)handle1.get()).copyDepthFrom((Framebuffer)handle.get());
            }

            profiler.swap("translucent");
            this.renderLayerMethod.invoke(this, RenderLayer.getTranslucent(), d0, d1, d2, matrix4f, matrix4f1);

            try {
               gk.setMatrixStack(matrixstack);
               gk.setConsumers(immediate);
               ((AfterTranslucent)WorldRenderEvents.AFTER_TRANSLUCENT.invoker()).afterTranslucent(gk);
            } catch (Exception exception) {
            }

            profiler.swap("string");
            this.renderLayerMethod.invoke(this, RenderLayer.getTripwire(), d0, d1, d2, matrix4f, matrix4f1);
            if (flag) {
               this.renderTargetBlockOutlineMethod.invoke(this, camera, immediate, matrixstack, true);
            }

            immediate.draw();
            profiler.pop();
         } catch (Exception exception4) {
            exception4.printStackTrace();
         }
      });
   }

   private CustomFogModule getCustomFogModule() {
      try {
         return ClientMain.getInstance().getModuleManager().<CustomFogModule>getModule(CustomFogModule.class);
      } catch (Exception exception) {
         return null;
      }
   }

   private void applyCustomTimeIfEnabled() {
      try {
         CustomTimeModule customtimemodule = ClientMain.getInstance().getModuleManager().<CustomTimeModule>getModule(CustomTimeModule.class);
         if (customtimemodule != null && customtimemodule.isEnabled() && this.client.world != null) {
            long i = customtimemodule.worldTimeSetting.getLongValue();
            this.client.world.setTime(this.client.world.getTime(), i, false);
         }
      } catch (Exception exception) {
      }
   }

   private void renderWeatherWithoutBorder(FrameGraphBuilder framegraphbuilder, Vec3d vec3d, float f, Fog fog) {
      try {
         DefaultFramebufferSet defaultframebufferset = this.framebufferSetField.getValue();
         BufferBuilderStorage bufferbuilderstorage = this.bufferBuildersField.getValue();
         int i = this.ticksField.getInt(this);
         RenderPass renderpass = framegraphbuilder.createPass("weather");
         if (defaultframebufferset.weatherFramebuffer != null) {
            defaultframebufferset.weatherFramebuffer = renderpass.transfer(defaultframebufferset.weatherFramebuffer);
         } else {
            defaultframebufferset.mainFramebuffer = renderpass.transfer(defaultframebufferset.mainFramebuffer);
         }

         renderpass.setRenderer(
            () -> {
               try {
                  RenderSystem.setShaderFog(fog);
                  Immediate immediate = bufferbuilderstorage.getEntityVertexConsumers();
                  Object object = this.weatherRenderingField.getValue();
                  if (object != null) {
                     Method method = object.getClass()
                        .getDeclaredMethod(
                           ClientMain.getInstance().isDev() ? "renderPrecipitation" : "method_62316",
                           World.class,
                           VertexConsumerProvider.class,
                           int.class,
                           float.class,
                           Vec3d.class
                        );
                     method.setAccessible(true);
                     method.invoke(object, this.client.world, immediate, i, f, vec3d);
                  }

                  immediate.draw();
               } catch (Exception exception2) {
                  exception2.printStackTrace();
               }
            }
         );
      } catch (Exception exception1) {
         exception1.printStackTrace();

         try {
            this.renderWeatherMethod.invoke(this, framegraphbuilder, vec3d, f, fog);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }
}
