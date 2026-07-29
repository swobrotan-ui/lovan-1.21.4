package render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import core.ClientMain;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import module.AspectRatioModule;
import module.BlockOutlineModule;
import module.HandTweaksModule;
import module.NoOverlayModule;
import module.PlayerOutlinesModule;
import module.ZoomModule;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.gl.ShaderLoader.LoadException;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.Pool;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceFactory;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import net.minecraft.util.profiler.ScopedProfiler;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class CustomGameRenderer extends GameRenderer {
   private static final Identifier field_53899 = Identifier.ofVanilla("blur");
   public static final int field_49904 = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean field_32688 = false;
   public static final float CAMERA_DEPTH = 0.05F;
   private static final float field_44940 = 1000.0F;
   private final MinecraftClient client;
   private final ResourceManager resourceManager;
   private final Random random = Random.create();
   private float viewDistance;
   public final CustomHeldItemRenderer firstPersonRenderer;
   private final BufferBuilderStorage buffers;
   private int ticks;
   private float fovMultiplier;
   private float lastFovMultiplier;
   private float skyDarkness;
   private float lastSkyDarkness;
   private boolean renderHand = true;
   private boolean blockOutlineEnabled = true;
   private long lastWorldIconUpdate;
   private boolean hasWorldIcon;
   private long lastWindowFocusedTime = Util.getMeasuringTimeMs();
   private final LightmapTextureManager lightmapTextureManager;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean renderingPanorama;
   private float zoom = 1.0F;
   private float zoomX;
   private float zoomY;
   public static final int field_32687 = 40;
   @Nullable
   private ItemStack floatingItem;
   private int floatingItemTimeLeft;
   private float floatingItemWidth;
   private float floatingItemHeight;
   private final Pool pool = new Pool(3);
   @Nullable
   private Identifier postProcessorId;
   private boolean postProcessorEnabled;
   private final Camera camera = new Camera();
   public static CustomGameRenderer instance;

   public CustomHeldItemRenderer getFirstPersonRenderer() {
      return this.firstPersonRenderer;
   }

   public CustomGameRenderer(
      MinecraftClient minecraftclient,
      CustomHeldItemRenderer customhelditemrenderer,
      ResourceManager resourcemanager,
      BufferBuilderStorage bufferbuilderstorage
   ) {
      super(minecraftclient, customhelditemrenderer, resourcemanager, bufferbuilderstorage);
      this.client = minecraftclient;
      this.resourceManager = resourcemanager;
      this.firstPersonRenderer = customhelditemrenderer;
      this.lightmapTextureManager = new LightmapTextureManager(this, minecraftclient);
      this.buffers = bufferbuilderstorage;
      instance = this;
   }

   public void close() {
      this.lightmapTextureManager.close();
      this.overlayTexture.close();
      this.pool.close();
   }

   public void setRenderHand(boolean flag) {
      this.renderHand = flag;
   }

   public void setBlockOutlineEnabled(boolean flag) {
      this.blockOutlineEnabled = flag;
   }

   public void setRenderingPanorama(boolean flag) {
      this.renderingPanorama = flag;
   }

   public boolean isRenderingPanorama() {
      return this.renderingPanorama;
   }

   public void clearPostProcessor() {
      this.postProcessorId = null;
   }

   public void togglePostProcessorEnabled() {
      this.postProcessorEnabled = !this.postProcessorEnabled;
   }

   public void onCameraEntitySet(@Nullable Entity entity) {
      this.postProcessorId = null;
      if (entity instanceof CreeperEntity) {
         this.setPostProcessor(Identifier.ofVanilla("creeper"));
      } else if (entity instanceof SpiderEntity) {
         this.setPostProcessor(Identifier.ofVanilla("spider"));
      } else {
         if (entity instanceof EndermanEntity) {
            this.setPostProcessor(Identifier.ofVanilla("invert"));
         }
      }
   }

   private void setPostProcessor(Identifier identifier) {
      this.postProcessorId = identifier;
      this.postProcessorEnabled = true;
   }

   public void renderBlur() {
      float f = this.client.options.getMenuBackgroundBlurrinessValue();
      if (!(f < 1.0F)) {
         PostEffectProcessor posteffectprocessor = this.client.getShaderLoader().loadPostEffect(field_53899, DefaultFramebufferSet.MAIN_ONLY);
         if (posteffectprocessor != null) {
            posteffectprocessor.setUniforms("Radius", f);
            posteffectprocessor.render(this.client.getFramebuffer(), this.pool);
         }
      }
   }

   public void preloadPrograms(ResourceFactory resourcefactory) {
      try {
         this.client
            .getShaderLoader()
            .preload(
               resourcefactory,
               new ShaderProgramKey[]{ShaderProgramKeys.RENDERTYPE_GUI, ShaderProgramKeys.RENDERTYPE_GUI_OVERLAY, ShaderProgramKeys.POSITION_TEX_COLOR}
            );
      } catch (LoadException loadexception) {
         throw new RuntimeException("Could not preload shaders for loading UI", loadexception);
      } catch (IOException ioexception) {
         throw new RuntimeException("Could not preload shaders for loading UI", ioexception);
      } catch (Exception exception) {
         throw new RuntimeException("Could not preload shaders for loading UI", exception);
      }
   }

   public void tick() {
      this.updateFovMultiplier();
      this.lightmapTextureManager.tick();
      if (this.client.getCameraEntity() == null) {
         this.client.setCameraEntity(this.client.player);
      }

      this.camera.updateEyeHeight();
      this.firstPersonRenderer.updateHeldItems();
      this.ticks++;
      if (this.client.world.getTickManager().shouldTick()) {
         this.lastSkyDarkness = this.skyDarkness;
         if (this.client.inGameHud.getBossBarHud().shouldDarkenSky()) {
            this.skyDarkness += 0.05F;
            if (this.skyDarkness > 1.0F) {
               this.skyDarkness = 1.0F;
            }
         } else if (this.skyDarkness > 0.0F) {
            this.skyDarkness -= 0.0125F;
         }

         if (this.floatingItemTimeLeft > 0) {
            this.floatingItemTimeLeft--;
            if (this.floatingItemTimeLeft == 0) {
               this.floatingItem = null;
            }
         }
      }
   }

   @Nullable
   public Identifier getPostProcessorId() {
      return this.postProcessorId;
   }

   public void onResized(int i, int j) {
      this.pool.clear();
      this.client.worldRenderer.onResized(i, j);
   }

   public void updateCrosshairTarget(float f) {
      Entity entity = this.client.getCameraEntity();
      if (entity != null && this.client.world != null && this.client.player != null) {
         Profilers.get().push("pick");
         double d0 = this.client.player.getBlockInteractionRange();
         double d1 = this.client.player.getEntityInteractionRange();
         HitResult hitresult = this.findCrosshairTarget(entity, d0, d1, f);
         this.client.crosshairTarget = hitresult;
         MinecraftClient minecraftclient = this.client;
         Entity entity1;
         if (hitresult instanceof EntityHitResult entityhitresult) {
            entity1 = entityhitresult.getEntity();
         } else {
            entity1 = null;
         }

         minecraftclient.targetedEntity = entity1;
         Profilers.get().pop();
      }
   }

   private HitResult findCrosshairTarget(Entity entity, double d0, double d1, float f) {
      double d2 = Math.max(d0, d1);
      double d3 = MathHelper.square(d2);
      Vec3d vec3d = entity.getCameraPosVec(f);
      HitResult hitresult = entity.raycast(d2, f, false);
      double d4 = hitresult.getPos().squaredDistanceTo(vec3d);
      if (hitresult.getType() != Type.MISS) {
         d3 = d4;
         d2 = Math.sqrt(d4);
      }

      Vec3d vec3d1 = entity.getRotationVec(f);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * d2, vec3d1.y * d2, vec3d1.z * d2);
      float f1 = 1.0F;
      Box box = entity.getBoundingBox().stretch(vec3d1.multiply(d2)).expand(1.0, 1.0, 1.0);
      EntityHitResult entityhitresult = ProjectileUtil.raycast(entity, vec3d, vec3d2, box, EntityPredicates.CAN_HIT, d3);
      return entityhitresult != null && entityhitresult.getPos().squaredDistanceTo(vec3d) < d4
         ? ensureTargetInRange(entityhitresult, vec3d, d1)
         : ensureTargetInRange(hitresult, vec3d, d0);
   }

   private static HitResult ensureTargetInRange(HitResult hitresult, Vec3d vec3d, double d0) {
      Vec3d vec3d1 = hitresult.getPos();
      if (!vec3d1.isInRange(vec3d, d0)) {
         Vec3d vec3d2 = hitresult.getPos();
         Direction direction = Direction.getFacing(vec3d2.x - vec3d.x, vec3d2.y - vec3d.y, vec3d2.z - vec3d.z);
         return BlockHitResult.createMissed(vec3d2, direction, BlockPos.ofFloored(vec3d2));
      } else {
         return hitresult;
      }
   }

   private void updateFovMultiplier() {
      float f;
      if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
         GameOptions gameoptions = this.client.options;
         boolean flag = gameoptions.getPerspective().isFirstPerson();
         float f1 = ((Double)gameoptions.getFovEffectScale().getValue()).floatValue();
         f = abstractclientplayerentity.getFovMultiplier(flag, f1);
      } else {
         f = 1.0F;
      }

      this.lastFovMultiplier = this.fovMultiplier;
      this.fovMultiplier = this.fovMultiplier + (f - this.fovMultiplier) * 0.5F;
      this.fovMultiplier = MathHelper.clamp(this.fovMultiplier, 0.1F, 1.5F);
   }

   public float getFov(Camera camera, float f, boolean flag) {
      if (this.renderingPanorama) {
         return 90.0F;
      } else {
         float f1 = 70.0F;
         if (flag) {
            f1 = ((Integer)this.client.options.getFov().getValue()).intValue();
            f1 *= MathHelper.lerp(f, this.lastFovMultiplier, this.fovMultiplier);
         }

         if (camerax.getFocusedEntity() instanceof LivingEntity livingentity && livingentity.isDead()) {
            float f2 = Math.min(livingentity.deathTime + f, 125.0F);
            f1 /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
         }

         CameraSubmersionType camerasubmersiontype = camerax.getSubmersionType();
         if (camerasubmersiontype == CameraSubmersionType.LAVA || camerasubmersiontype == CameraSubmersionType.WATER) {
            float f3 = ((Double)this.client.options.getFovEffectScale().getValue()).floatValue();
            f1 *= MathHelper.lerp(f3, 1.0F, 0.85714287F);
         }

         ZoomModule zoommodule = ZoomModule.o();
         if (zoommodule != null) {
            f1 *= zoommodule.b();
         }

         return f1;
      }
   }

   private void tiltViewWhenHurt(MatrixStack matrixstack, float f) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.hurtShakeSetting.getValue()) {
         if (this.client.getCameraEntity() instanceof LivingEntity livingentity) {
            float f1 = livingentity.hurtTime - f;
            if (livingentity.isDead()) {
               float f2 = Math.min(livingentity.deathTime + f, 20.0F);
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(40.0F - 8000.0F / (f2 + 200.0F)));
            }

            if (f1 < 0.0F) {
               return;
            }

            f1 /= livingentity.maxHurtTime;
            f1 = MathHelper.sin(f1 * f1 * f1 * f1 * (float) Math.PI);
            float f4 = livingentity.getDamageTiltYaw();
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f4));
            float f3 = (float)(-f1 * 14.0 * (Double)this.client.options.getDamageTiltStrength().getValue());
            matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f3));
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f4));
         }
      }
   }

   private void bobView(MatrixStack matrixstack, float f) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.runShakeSetting.getValue()) {
         if (this.client.getCameraEntity() instanceof AbstractClientPlayerEntity abstractclientplayerentity) {
            float f1 = abstractclientplayerentity.distanceMoved - abstractclientplayerentity.lastDistanceMoved;
            float f2 = -(abstractclientplayerentity.distanceMoved + f1 * f);
            float f3 = MathHelper.lerp(f, abstractclientplayerentity.prevStrideDistance, abstractclientplayerentity.strideDistance);
            matrixstack.translate(MathHelper.sin(f2 * (float) Math.PI) * f3 * 0.5F, -Math.abs(MathHelper.cos(f2 * (float) Math.PI) * f3), 0.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(f2 * (float) Math.PI) * f3 * 3.0F));
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Math.abs(MathHelper.cos(f2 * (float) Math.PI - 0.2F) * f3) * 5.0F));
         }
      }
   }

   public void renderWithZoom(float f, float f1, float f2) {
      this.zoom = f;
      this.zoomX = f1;
      this.zoomY = f2;
      this.setBlockOutlineEnabled(false);
      this.setRenderHand(false);
      this.renderWorld(RenderTickCounter.ZERO);
      this.zoom = 1.0F;
   }

   private void renderHand(Camera camera, float f, Matrix4f matrix4f) {
      if (!this.renderingPanorama) {
         Matrix4f matrix4f1 = this.getBasicProjectionMatrix(this.getFov(camerax, f, false));
         RenderSystem.setProjectionMatrix(matrix4f1, ProjectionType.PERSPECTIVE);
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.push();
         matrixstack.multiplyPositionMatrix(matrix4f.invert(new Matrix4f()));
         Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
         matrix4fstack.pushMatrix().mul(matrix4f);
         this.tiltViewWhenHurt(matrixstack, f);
         if ((Boolean)this.client.options.getBobView().getValue()) {
            this.bobView(matrixstack, f);
         }

         boolean flag = this.client.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.client.getCameraEntity()).isSleeping();
         if (this.client.options.getPerspective().isFirstPerson()
            && !flag
            && !this.client.options.hudHidden
            && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
            this.lightmapTextureManager.enable();
            this.firstPersonRenderer
               .renderItem(
                  f,
                  matrixstack,
                  this.buffers.getEntityVertexConsumers(),
                  this.client.player,
                  this.client.getEntityRenderDispatcher().getLight(this.client.player, f)
               );
            this.lightmapTextureManager.disable();
         }

         matrix4fstack.popMatrix();
         matrixstack.pop();
         if (this.client.options.getPerspective().isFirstPerson() && !flag) {
            Immediate immediate = this.buffers.getEntityVertexConsumers();
            vc.renderOverlays(this.client, matrixstack, immediate);
            immediate.draw();
         }
      }
   }

   public Matrix4f getBasicProjectionMatrix(float f) {
      Matrix4f matrix4f = new Matrix4f();
      if (this.zoom != 1.0F) {
         matrix4f.translate(this.zoomX, -this.zoomY, 0.0F);
         matrix4f.scale(this.zoom, this.zoom, 1.0F);
      }

      AspectRatioModule aspectratiomodule = ClientMain.getInstance().getModuleManager().<AspectRatioModule>getModule(AspectRatioModule.class);
      float f1;
      if (aspectratiomodule != null && aspectratiomodule.isEnabled()) {
         f1 = AspectRatioModule.a().getFloatValue();
      } else {
         f1 = (float)this.client.getWindow().getFramebufferWidth() / this.client.getWindow().getFramebufferHeight();
      }

      return matrix4f.perspective(f * (float) (Math.PI / 180.0), f1, 0.05F, this.getFarPlaneDistance());
   }

   public float getFarPlaneDistance() {
      return this.viewDistance * 4.0F;
   }

   public void render(RenderTickCounter rendertickcounter, boolean flag) {
      if (!this.client.isWindowFocused()
         && this.client.options.pauseOnLostFocus
         && (!(Boolean)this.client.options.getTouchscreen().getValue() || !this.client.mouse.wasRightButtonClicked())) {
         if (Util.getMeasuringTimeMs() - this.lastWindowFocusedTime > 500L) {
            this.client.openGameMenu(false);
         }
      } else {
         this.lastWindowFocusedTime = Util.getMeasuringTimeMs();
      }

      if (!this.client.skipGameRender) {
         Profiler profiler = Profilers.get();
         boolean flag1 = this.client.isFinishedLoading();
         int i = (int)(this.client.mouse.getX() * this.client.getWindow().getScaledWidth() / this.client.getWindow().getWidth());
         int j = (int)(this.client.mouse.getY() * this.client.getWindow().getScaledHeight() / this.client.getWindow().getHeight());
         RenderSystem.viewport(0, 0, this.client.getWindow().getFramebufferWidth(), this.client.getWindow().getFramebufferHeight());
         if (flag1 && flag && this.client.world != null) {
            profiler.push("level");
            this.renderWorld(rendertickcounter);
            this.updateWorldIcon();
            PlayerOutlinesModule playeroutlinesmodule = PlayerOutlinesModule.h();
            boolean flag2 = playeroutlinesmodule != null && playeroutlinesmodule.isEnabled();
            NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
            boolean flag3 = nooverlaymodule != null && nooverlaymodule.isEnabled() && nooverlaymodule.glowSetting.getValue();
            if (flag2 && !flag3) {
               this.client.worldRenderer.drawEntityOutlinesFramebuffer();
            }

            if (this.postProcessorId != null && this.postProcessorEnabled) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               PostEffectProcessor posteffectprocessor = this.client.getShaderLoader().loadPostEffect(this.postProcessorId, DefaultFramebufferSet.MAIN_ONLY);
               if (posteffectprocessor != null) {
                  posteffectprocessor.render(this.client.getFramebuffer(), this.pool);
               }
            }

            this.client.getFramebuffer().beginWrite(true);
         }

         Window window = this.client.getWindow();
         RenderSystem.clear(256);
         Matrix4f matrix4f = new Matrix4f()
            .setOrtho(
               0.0F,
               (float)(window.getFramebufferWidth() / window.getScaleFactor()),
               (float)(window.getFramebufferHeight() / window.getScaleFactor()),
               0.0F,
               1000.0F,
               21000.0F
            );
         RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.ORTHOGRAPHIC);
         Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
         matrix4fstack.pushMatrix();
         matrix4fstack.translation(0.0F, 0.0F, -11000.0F);
         DiffuseLighting.enableGuiDepthLighting();
         DrawContext drawcontext = new DrawContext(this.client, this.buffers.getEntityVertexConsumers());
         if (flag1 && flag && this.client.world != null) {
            profiler.swap("gui");
            if (!this.client.options.hudHidden) {
               this.renderFloatingItem(drawcontext, rendertickcounter.getTickDelta(false));
            }

            this.client.inGameHud.render(drawcontext, rendertickcounter);
            drawcontext.draw();
            RenderSystem.clear(256);
            profiler.pop();
         }

         if (this.client.getOverlay() != null) {
            try {
               this.client.getOverlay().render(drawcontext, i, j, rendertickcounter.getLastFrameDuration());
            } catch (Throwable throwable3) {
               LOGGER.error("Error rendering overlay", throwable3);
            }
         } else if (flag1 && this.client.currentScreen != null) {
            try {
               this.client.currentScreen.renderWithTooltip(drawcontext, i, j, rendertickcounter.getLastFrameDuration());
               ClientMain.getInstance().getEventManager().onRenderHud(drawcontext, rendertickcounter);
            } catch (Throwable throwable2) {
               LOGGER.error("Error rendering screen", throwable2);
            }

            try {
               if (this.client.currentScreen != null) {
                  this.client.currentScreen.updateNarrator();
               }
            } catch (Throwable throwable1) {
               LOGGER.error("Error narrating screen", throwable1);
            }
         }

         if (flag1 && flag && this.client.world != null) {
            this.client.inGameHud.renderAutosaveIndicator(drawcontext, rendertickcounter);
         }

         if (flag1) {
            ScopedProfiler scopedprofiler = profiler.scoped("toasts");

            try {
               this.client.getToastManager().draw(drawcontext);
            } catch (Throwable throwable4) {
               if (scopedprofiler != null) {
                  try {
                     scopedprofiler.close();
                  } catch (Throwable throwable) {
                     throwable4.addSuppressed(throwable);
                  }
               }

               throw throwable4;
            }

            if (scopedprofiler != null) {
               scopedprofiler.close();
            }
         }

         drawcontext.draw();
         matrix4fstack.popMatrix();
         this.pool.decrementLifespan();
      }
   }

   private void updateWorldIcon() {
      if (!this.hasWorldIcon && this.client.isInSingleplayer()) {
         long i = Util.getMeasuringTimeMs();
         if (i - this.lastWorldIconUpdate >= 1000L) {
            this.lastWorldIconUpdate = i;
            IntegratedServer integratedserver = this.client.getServer();
            if (integratedserver != null && !integratedserver.isStopped()) {
               integratedserver.getIconFile().ifPresent(path -> {
                  if (Files.isRegularFile(path)) {
                     this.hasWorldIcon = true;
                  } else {
                     this.updateWorldIcon(path);
                  }
               });
            }
         }
      }
   }

   private void updateWorldIcon(Path path) {
      if (this.client.worldRenderer.getCompletedChunkCount() > 10 && this.client.worldRenderer.isTerrainRenderComplete()) {
         NativeImage nativeimage = ScreenshotRecorder.takeScreenshot(this.client.getFramebuffer());
         Util.getIoWorkerExecutor().execute(() -> {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = 0;
            int l = 0;
            if (i > j) {
               k = (i - j) / 2;
               i = j;
            } else {
               l = (j - i) / 2;
               j = i;
            }

            try {
               NativeImage nativeimage2 = new NativeImage(64, 64, false);

               try {
                  nativeimage.resizeSubRectTo(k, l, i, j, nativeimage2);
                  nativeimage2.writeTo(path);
               } catch (Throwable throwable1) {
                  try {
                     nativeimage2.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }

                  throw throwable1;
               }

               nativeimage2.close();
               return;
            } catch (IOException ioexception) {
               LOGGER.warn("Couldn't save auto screenshot", ioexception);
            } finally {
               nativeimage.close();
            }
         });
      }
   }

   private boolean shouldRenderBlockOutline() {
      BlockOutlineModule blockoutlinemodule = ClientMain.getInstance().getModuleManager().<BlockOutlineModule>getModule(BlockOutlineModule.class);
      if (this.blockOutlineEnabled && (blockoutlinemodule == null || !blockoutlinemodule.isEnabled())) {
         Entity entity = this.client.getCameraEntity();
         boolean flag = entity instanceof PlayerEntity && !this.client.options.hudHidden;
         if (flag && !((PlayerEntity)entity).getAbilities().allowModifyWorld) {
            ItemStack itemstack = ((LivingEntity)entity).getMainHandStack();
            HitResult hitresult = this.client.crosshairTarget;
            if (hitresult != null && hitresult.getType() == Type.BLOCK) {
               BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
               BlockState blockstate = this.client.world.getBlockState(blockpos);
               if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
                  flag = blockstate.createScreenHandlerFactory(this.client.world, blockpos) != null;
               } else {
                  CachedBlockPosition cachedblockposition = new CachedBlockPosition(this.client.world, blockpos, false);
                  Registry registry = this.client.world.getRegistryManager().getOrThrow(RegistryKeys.BLOCK);
                  flag = !itemstack.isEmpty() && (itemstack.canBreak(cachedblockposition) || itemstack.canPlaceOn(cachedblockposition));
               }
            }
         }

         return flag;
      } else {
         return false;
      }
   }

   public void renderWorld(RenderTickCounter rendertickcounter) {
      float f = rendertickcounter.getTickDelta(true);
      this.lightmapTextureManager.update(f);
      if (this.client.getCameraEntity() == null) {
         this.client.setCameraEntity(this.client.player);
      }

      this.updateCrosshairTarget(f);
      Profiler profiler = Profilers.get();
      profiler.push("center");
      boolean flag = this.shouldRenderBlockOutline();
      profiler.swap("camera");
      Camera camerax = this.camera;
      Object object = this.client.getCameraEntity() == null ? this.client.player : this.client.getCameraEntity();
      float f1 = this.client.world.getTickManager().shouldSkipTick((Entity)object) ? 1.0F : f;
      camerax.update(
         this.client.world, (Entity)object, !this.client.options.getPerspective().isFirstPerson(), this.client.options.getPerspective().isFrontView(), f1
      );
      this.viewDistance = this.client.options.getClampedViewDistance() * 16;
      float f2 = this.getFov(camerax, f, true);
      Matrix4f matrix4f = this.getBasicProjectionMatrix(f2);
      MatrixStack matrixstack = new MatrixStack();
      this.tiltViewWhenHurt(matrixstack, camerax.getLastTickDelta());
      if ((Boolean)this.client.options.getBobView().getValue()) {
         this.bobView(matrixstack, camerax.getLastTickDelta());
      }

      matrix4f.mul(matrixstack.peek().getPositionMatrix());
      float f3 = ((Double)this.client.options.getDistortionEffectScale().getValue()).floatValue();
      float f4 = MathHelper.lerp(f, this.client.player.prevNauseaIntensity, this.client.player.nauseaIntensity) * (f3 * f3);
      if (f4 > 0.0F) {
         int i = this.client.player.hasStatusEffect(StatusEffects.NAUSEA) ? 7 : 20;
         float f5 = 5.0F / (f4 * f4 + 5.0F) - f4 * 0.04F;
         f5 *= f5;
         Vector3f vector3f = new Vector3f(0.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F, MathHelper.SQUARE_ROOT_OF_TWO / 2.0F);
         float f6 = (this.ticks + f) * i * (float) (Math.PI / 180.0);
         matrix4f.rotate(f6, vector3f);
         matrix4f.scale(1.0F / f5, 1.0F, 1.0F);
         matrix4f.rotate(-f6, vector3f);
      }

      float f7 = Math.max(f2, (float)((Integer)this.client.options.getFov().getValue()).intValue());
      Matrix4f matrix4f1 = this.getBasicProjectionMatrix(f7);
      RenderSystem.setProjectionMatrix(matrix4f, ProjectionType.PERSPECTIVE);
      Quaternionf quaternionf = camerax.getRotation().conjugate(new Quaternionf());
      Matrix4f matrix4f2 = new Matrix4f().rotation(quaternionf);
      this.client.worldRenderer.setupFrustum(camerax.getPos(), matrix4f2, matrix4f1);
      this.client.getFramebuffer().beginWrite(true);
      this.client.worldRenderer.render(this.pool, rendertickcounter, flag, camerax, this, matrix4f2, matrix4f);
      profiler.swap("hand");
      if (this.renderHand) {
         HandTweaksModule handtweaksmodule = ClientMain.getInstance().getModuleManager().<HandTweaksModule>getModule(HandTweaksModule.class);
         boolean flag1 = handtweaksmodule != null && handtweaksmodule.isEnabled() && handtweaksmodule.c();
         if (flag1) {
            handtweaksmodule.e();
         }

         RenderSystem.clear(256);
         this.renderHand(camerax, f, matrix4f2);
         if (flag1) {
            handtweaksmodule.f();
         }
      }

      profiler.pop();
   }

   public void reset() {
      this.floatingItem = null;
      this.client.getMapTextureManager().clear();
      this.camera.reset();
      this.hasWorldIcon = false;
   }

   public void showFloatingItem(ItemStack itemstack) {
      this.floatingItem = itemstack;
      this.floatingItemTimeLeft = 40;
      this.floatingItemWidth = this.random.nextFloat() * 2.0F - 1.0F;
      this.floatingItemHeight = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderFloatingItem(DrawContext drawcontext, float f) {
      if (this.floatingItem != null && this.floatingItemTimeLeft > 0) {
         int i = 40 - this.floatingItemTimeLeft;
         float f1 = (i + f) / 40.0F;
         float f2 = f1 * f1;
         float f3 = f1 * f2;
         float f4 = 10.25F * f3 * f2 - 24.95F * f2 * f2 + 25.5F * f3 - 13.8F * f2 + 4.0F * f1;
         float f5 = f4 * (float) Math.PI;
         float f6 = this.floatingItemWidth * (drawcontext.getScaledWindowWidth() / 4);
         float f7 = this.floatingItemHeight * (drawcontext.getScaledWindowHeight() / 4);
         MatrixStack matrixstack = drawcontext.getMatrices();
         matrixstack.push();
         matrixstack.translate(
            drawcontext.getScaledWindowWidth() / 2 + f6 * MathHelper.abs(MathHelper.sin(f5 * 2.0F)),
            drawcontext.getScaledWindowHeight() / 2 + f7 * MathHelper.abs(MathHelper.sin(f5 * 2.0F)),
            -50.0F
         );
         float f8 = 50.0F + 175.0F * MathHelper.sin(f5);
         matrixstack.scale(f8, -f8, f8);
         matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(900.0F * MathHelper.abs(MathHelper.sin(f5))));
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(6.0F * MathHelper.cos(f1 * 8.0F)));
         matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(6.0F * MathHelper.cos(f1 * 8.0F)));
         drawcontext.draw(
            vertexconsumerprovider -> {
               this.client
                  .getItemRenderer()
                  .renderItem(
                     this.floatingItem,
                     ModelTransformationMode.FIXED,
                     15728880,
                     OverlayTexture.DEFAULT_UV,
                     matrixstack,
                     vertexconsumerprovider,
                     this.client.world,
                     0
                  );
            }
         );
         matrixstack.pop();
      }
   }

   public MinecraftClient getClient() {
      return this.client;
   }

   public float getSkyDarkness(float f) {
      return MathHelper.lerp(f, this.lastSkyDarkness, this.skyDarkness);
   }

   public float getViewDistance() {
      return this.viewDistance;
   }

   public Camera getCamera() {
      return this.camera;
   }

   public LightmapTextureManager getLightmapTextureManager() {
      return this.lightmapTextureManager;
   }

   public OverlayTexture getOverlayTexture() {
      return this.overlayTexture;
   }

   public static CustomGameRenderer getInstance() {
      return instance;
   }
}
