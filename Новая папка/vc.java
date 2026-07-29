import core.ClientMain;
import module.NoOverlayModule;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.ModelBaker;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.BlockPos.Mutable;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class vc {
   private static final Identifier UNDERWATER_TEXTURE = Identifier.ofVanilla("textures/misc/underwater.png");

   public static void renderOverlays(MinecraftClient minecraftclient, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
      ClientPlayerEntity clientplayerentity = minecraftclient.player;
      if (!clientplayerentity.noClip) {
         BlockState blockstate = getInWallBlockState(clientplayerentity);
         if (blockstate != null) {
            renderInWallOverlay(minecraftclient.getBlockRenderManager().getModels().getModelParticleSprite(blockstate), matrixstack, vertexconsumerprovider);
         }
      }

      if (!minecraftclient.player.isSpectator()) {
         if (minecraftclient.player.isSubmergedIn(FluidTags.WATER)) {
            renderUnderwaterOverlay(minecraftclient, matrixstack, vertexconsumerprovider);
         }

         if (minecraftclient.player.isOnFire()) {
            renderFireOverlay(matrixstack, vertexconsumerprovider);
         }
      }
   }

   @Nullable
   private static BlockState getInWallBlockState(PlayerEntity playerentity) {
      Mutable mutable = new Mutable();

      for (int i = 0; i < 8; i++) {
         double d0 = playerentity.getX() + ((i >> 0) % 2 - 0.5F) * playerentity.getWidth() * 0.8F;
         double d1 = playerentity.getEyeY() + ((i >> 1) % 2 - 0.5F) * 0.1F * playerentity.getScale();
         double d2 = playerentity.getZ() + ((i >> 2) % 2 - 0.5F) * playerentity.getWidth() * 0.8F;
         mutable.set(d0, d1, d2);
         BlockState blockstate = playerentity.getWorld().getBlockState(mutable);
         if (blockstate.getRenderType() != BlockRenderType.INVISIBLE && blockstate.shouldBlockVision(playerentity.getWorld(), mutable)) {
            return blockstate;
         }
      }

      return null;
   }

   private static void renderInWallOverlay(Sprite sprite, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled()) {
         float f = 0.1F;
         int i = ColorHelper.fromFloats(1.0F, 0.1F, 0.1F, 0.1F);
         float f1 = -1.0F;
         float f2 = 1.0F;
         float f3 = -1.0F;
         float f4 = 1.0F;
         float f5 = -0.5F;
         float f6 = sprite.getMinU();
         float f7 = sprite.getMaxU();
         float f8 = sprite.getMinV();
         float f9 = sprite.getMaxV();
         Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
         VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.getBlockScreenEffect(sprite.getAtlasId()));
         vertexconsumer.vertex(matrix4f, -1.0F, -1.0F, -0.5F).texture(f7, f9).color(i);
         vertexconsumer.vertex(matrix4f, 1.0F, -1.0F, -0.5F).texture(f6, f9).color(i);
         vertexconsumer.vertex(matrix4f, 1.0F, 1.0F, -0.5F).texture(f6, f8).color(i);
         vertexconsumer.vertex(matrix4f, -1.0F, 1.0F, -0.5F).texture(f7, f8).color(i);
      }
   }

   private static void renderUnderwaterOverlay(MinecraftClient minecraftclient, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
      BlockPos blockpos = BlockPos.ofFloored(minecraftclient.player.getX(), minecraftclient.player.getEyeY(), minecraftclient.player.getZ());
      float f = LightmapTextureManager.getBrightness(
         minecraftclient.player.getWorld().getDimension(), minecraftclient.player.getWorld().getLightLevel(blockpos)
      );
      int i = ColorHelper.fromFloats(0.1F, f, f, f);
      float f1 = 4.0F;
      float f2 = -1.0F;
      float f3 = 1.0F;
      float f4 = -1.0F;
      float f5 = 1.0F;
      float f6 = -0.5F;
      float f7 = -minecraftclient.player.getYaw() / 64.0F;
      float f8 = minecraftclient.player.getPitch() / 64.0F;
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.getBlockScreenEffect(UNDERWATER_TEXTURE));
      vertexconsumer.vertex(matrix4f, -1.0F, -1.0F, -0.5F).texture(4.0F + f7, 4.0F + f8).color(i);
      vertexconsumer.vertex(matrix4f, 1.0F, -1.0F, -0.5F).texture(0.0F + f7, 4.0F + f8).color(i);
      vertexconsumer.vertex(matrix4f, 1.0F, 1.0F, -0.5F).texture(0.0F + f7, 0.0F + f8).color(i);
      vertexconsumer.vertex(matrix4f, -1.0F, 1.0F, -0.5F).texture(4.0F + f7, 0.0F + f8).color(i);
   }

   private static void renderFireOverlay(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider) {
      NoOverlayModule nooverlaymodule = ClientMain.getInstance().getModuleManager().<NoOverlayModule>getModule(NoOverlayModule.class);
      if (nooverlaymodule == null || !nooverlaymodule.isEnabled() || !nooverlaymodule.fireSetting.getValue()) {
         Sprite sprite = ModelBaker.FIRE_1.getSprite();
         VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.getFireScreenEffect(sprite.getAtlasId()));
         float f = sprite.getMinU();
         float f1 = sprite.getMaxU();
         float f2 = (f + f1) / 2.0F;
         float f3 = sprite.getMinV();
         float f4 = sprite.getMaxV();
         float f5 = (f3 + f4) / 2.0F;
         float f6 = sprite.getAnimationFrameDelta();
         float f7 = MathHelper.lerp(f6, f, f2);
         float f8 = MathHelper.lerp(f6, f1, f2);
         float f9 = MathHelper.lerp(f6, f3, f5);
         float f10 = MathHelper.lerp(f6, f4, f5);
         float f11 = 1.0F;

         for (int i = 0; i < 2; i++) {
            matrixstack.push();
            float f12 = -0.5F;
            float f13 = 0.5F;
            float f14 = -0.5F;
            float f15 = 0.5F;
            float f16 = -0.5F;
            matrixstack.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((i * 2 - 1) * 10.0F));
            Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
            vertexconsumer.vertex(matrix4f, -0.5F, -0.5F, -0.5F).texture(f8, f10).color(1.0F, 1.0F, 1.0F, 0.9F);
            vertexconsumer.vertex(matrix4f, 0.5F, -0.5F, -0.5F).texture(f7, f10).color(1.0F, 1.0F, 1.0F, 0.9F);
            vertexconsumer.vertex(matrix4f, 0.5F, 0.5F, -0.5F).texture(f7, f9).color(1.0F, 1.0F, 1.0F, 0.9F);
            vertexconsumer.vertex(matrix4f, -0.5F, 0.5F, -0.5F).texture(f8, f9).color(1.0F, 1.0F, 1.0F, 0.9F);
            matrixstack.pop();
         }
      }
   }
}
