package render;

import core.ClientMain;
import core.FriendManager;
import java.util.regex.Pattern;
import module.NameTagsModule;
import module.PlayerScalerModule;
import module.ProtestModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.ParrotEntity.Variant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.consume.UseAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CustomPlayerEntityRenderer extends PlayerEntityRenderer {
   public CustomPlayerEntityRenderer(Context context, boolean flag) {
      super(context, flag);
      this.features.clear();
      this.addFeature(
         new ArmorFeatureRenderer(
            this,
            new ArmorEntityModel(context.getPart(flag ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)),
            new ArmorEntityModel(context.getPart(flag ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)),
            context.getEquipmentRenderer()
         )
      );
      this.addFeature(new PlayerHeldItemFeatureRenderer(this));
      this.addFeature(new StuckArrowsFeatureRenderer(this, context));
      this.addFeature(new tpi(this, context.getEntityModels(), context.getEquipmentModelLoader()));
      this.addFeature(new ni(this, context.getEntityModels()));
      this.addFeature(new HeadFeatureRenderer(this, context.getEntityModels()));
      this.addFeature(new ElytraFeatureRenderer(this, context.getEntityModels(), context.getEquipmentRenderer()));
      this.addFeature(new TridentRiptideFeatureRenderer(this, context.getEntityModels()));
      this.addFeature(new StuckStingersFeatureRenderer(this, context));
   }

   protected boolean shouldRenderFeatures(PlayerEntityRenderState playerentityrenderstate) {
      return !playerentityrenderstate.spectator;
   }

   public Vec3d getPositionOffset(PlayerEntityRenderState playerentityrenderstate) {
      Vec3d vec3d = super.getPositionOffset(playerentityrenderstate);
      return playerentityrenderstate.isInSneakingPose ? vec3d.add(0.0, playerentityrenderstate.baseScale * -2.0F / 16.0, 0.0) : vec3d;
   }

   private static ArmPose getArmPose(AbstractClientPlayerEntity abstractclientplayerentity, Arm arm) {
      ItemStack itemstack = abstractclientplayerentity.getStackInHand(Hand.MAIN_HAND);
      ItemStack itemstack1 = abstractclientplayerentity.getStackInHand(Hand.OFF_HAND);
      ArmPose armpose = getArmPose(abstractclientplayerentity, itemstack, Hand.MAIN_HAND);
      ArmPose armpose1 = getArmPose(abstractclientplayerentity, itemstack1, Hand.OFF_HAND);
      if (armpose.isTwoHanded()) {
         armpose1 = itemstack1.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
      }

      return abstractclientplayerentity.getMainArm() == arm ? armpose : armpose1;
   }

   private static ArmPose getArmPose(PlayerEntity playerentity, ItemStack itemstack, Hand hand) {
      if (itemstack.isEmpty()) {
         return ArmPose.EMPTY;
      } else {
         if (playerentity.getActiveHand() == hand && playerentity.getItemUseTimeLeft() > 0) {
            UseAction useaction = itemstack.getUseAction();
            if (useaction == UseAction.BLOCK) {
               return ArmPose.BLOCK;
            }

            if (useaction == UseAction.BOW) {
               return ArmPose.BOW_AND_ARROW;
            }

            if (useaction == UseAction.SPEAR) {
               return ArmPose.THROW_SPEAR;
            }

            if (useaction == UseAction.CROSSBOW) {
               return ArmPose.CROSSBOW_CHARGE;
            }

            if (useaction == UseAction.SPYGLASS) {
               return ArmPose.SPYGLASS;
            }

            if (useaction == UseAction.TOOT_HORN) {
               return ArmPose.TOOT_HORN;
            }

            if (useaction == UseAction.BRUSH) {
               return ArmPose.BRUSH;
            }
         } else if (!playerentity.handSwinging && itemstack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack)) {
            return ArmPose.CROSSBOW_HOLD;
         }

         return ArmPose.ITEM;
      }
   }

   public Identifier getTexture(PlayerEntityRenderState playerentityrenderstate) {
      return playerentityrenderstate.skinTextures.texture();
   }

   protected void scale(PlayerEntityRenderState playerentityrenderstate, MatrixStack matrixstack) {
      PlayerScalerModule playerscalermodule = ClientMain.getInstance().getModuleManager().<PlayerScalerModule>getModule(PlayerScalerModule.class);
      boolean flag = MinecraftClient.getInstance().player != null && MinecraftClient.getInstance().player.getId() == playerentityrenderstate.id;
      if (playerscalermodule == null || !playerscalermodule.isEnabled() || flag) {
         matrixstack.scale(0.9375F, 0.9375F, 0.9375F);
      } else if (playerscalermodule.a().getValue()) {
         float f3 = playerscalermodule.b().getFloatValue();
         matrixstack.scale(f3, f3, f3);
      } else {
         float f = playerscalermodule.c().getFloatValue();
         float f1 = playerscalermodule.d().getFloatValue();
         float f2 = playerscalermodule.e().getFloatValue();
         matrixstack.scale(f, f1, f2);
      }
   }

   protected void renderLabelIfPresent(
      PlayerEntityRenderState playerentityrenderstate, Text text, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i
   ) {
      NameTagsModule nametagsmodule = ClientMain.getInstance().getModuleManager().<NameTagsModule>getModule(NameTagsModule.class);
      if (nametagsmodule == null || !nametagsmodule.isEnabled()) {
         text = this.protectText(text);
         matrixstack.push();
         if (playerentityrenderstate.playerName != null) {
            super.renderLabelIfPresent(playerentityrenderstate, this.protectText(playerentityrenderstate.playerName), matrixstack, vertexconsumerprovider, i);
            matrixstack.translate(0.0F, 0.25875F, 0.0F);
         }

         super.renderLabelIfPresent(playerentityrenderstate, text, matrixstack, vertexconsumerprovider, i);
         matrixstack.pop();
      }
   }

   private Text protectText(Text text) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player == null) {
         return text;
      } else {
         ProtestModule protestmodule = ClientMain.getInstance().getModuleManager().<ProtestModule>getModule(ProtestModule.class);
         if (protestmodule != null && protestmodule.isEnabled()) {
            String s = text.getString();
            boolean flag = false;
            if (protestmodule.d().getValue()) {
               String s1 = minecraftclient.player.getGameProfile().getName();
               if (s.contains(s1)) {
                  s = s.replace(s1, "SуstemPlayer");
                  flag = true;
               }
            }

            if (protestmodule.e().getValue()) {
               for (String s2 : FriendManager.getInstance().getFriends()) {
                  String s4 = Pattern.quote(s2);
                  String s3 = s.replaceAll("(?i)" + s4, "SуstemFriend");
                  if (!s3.equals(s)) {
                     s = s3;
                     flag = true;
                  }
               }
            }

            return (Text)(flag ? Text.literal(s).setStyle(text.getStyle()) : text);
         } else {
            return text;
         }
      }
   }

   public PlayerEntityRenderState createRenderState() {
      return new PlayerEntityRenderState();
   }

   public void updateRenderState(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
      super.updateRenderState(abstractclientplayerentity, playerentityrenderstate, f);
      BipedEntityRenderer.updateBipedRenderState(abstractclientplayerentity, playerentityrenderstate, f, this.itemModelResolver);
      playerentityrenderstate.leftArmPose = getArmPose(abstractclientplayerentity, Arm.LEFT);
      playerentityrenderstate.rightArmPose = getArmPose(abstractclientplayerentity, Arm.RIGHT);
      playerentityrenderstate.skinTextures = abstractclientplayerentity.getSkinTextures();
      playerentityrenderstate.stuckArrowCount = abstractclientplayerentity.getStuckArrowCount();
      playerentityrenderstate.stingerCount = abstractclientplayerentity.getStingerCount();
      playerentityrenderstate.itemUseTimeLeft = abstractclientplayerentity.getItemUseTimeLeft();
      playerentityrenderstate.handSwinging = abstractclientplayerentity.handSwinging;
      playerentityrenderstate.spectator = abstractclientplayerentity.isSpectator();
      playerentityrenderstate.hatVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.HAT);
      playerentityrenderstate.jacketVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.JACKET);
      playerentityrenderstate.leftPantsLegVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
      playerentityrenderstate.rightPantsLegVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
      playerentityrenderstate.leftSleeveVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
      playerentityrenderstate.rightSleeveVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
      playerentityrenderstate.capeVisible = abstractclientplayerentity.isPartVisible(PlayerModelPart.CAPE);
      updateGliding(abstractclientplayerentity, playerentityrenderstate, f);
      updateCape(abstractclientplayerentity, playerentityrenderstate, f);
      if (playerentityrenderstate.squaredDistanceToCamera < 100.0) {
         Scoreboard scoreboard = abstractclientplayerentity.getScoreboard();
         ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
         if (scoreboardobjective != null) {
            ReadableScoreboardScore readablescoreboardscore = scoreboard.getScore(abstractclientplayerentity, scoreboardobjective);
            MutableText mutabletext = ReadableScoreboardScore.getFormattedScore(
               readablescoreboardscore, scoreboardobjective.getNumberFormatOr(StyledNumberFormat.EMPTY)
            );
            playerentityrenderstate.playerName = Text.empty().append(mutabletext).append(ScreenTexts.SPACE).append(scoreboardobjective.getDisplayName());
         } else {
            playerentityrenderstate.playerName = null;
         }
      } else {
         playerentityrenderstate.playerName = null;
      }

      playerentityrenderstate.leftShoulderParrotVariant = getShoulderParrotVariant(abstractclientplayerentity, true);
      playerentityrenderstate.rightShoulderParrotVariant = getShoulderParrotVariant(abstractclientplayerentity, false);
      playerentityrenderstate.id = abstractclientplayerentity.getId();
      playerentityrenderstate.name = abstractclientplayerentity.getGameProfile().getName();
      playerentityrenderstate.spyglassState.clear();
      if (playerentityrenderstate.isUsingItem) {
         ItemStack itemstack = abstractclientplayerentity.getStackInHand(playerentityrenderstate.activeHand);
         if (itemstack.isOf(Items.SPYGLASS)) {
            this.itemModelResolver
               .updateForLivingEntity(playerentityrenderstate.spyglassState, itemstack, ModelTransformationMode.HEAD, false, abstractclientplayerentity);
         }
      }
   }

   private static void updateGliding(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
      playerentityrenderstate.glidingTicks = abstractclientplayerentity.getGlidingTicks() + f;
      Vec3d vec3d = abstractclientplayerentity.getRotationVec(f);
      Vec3d vec3d1 = abstractclientplayerentity.lerpVelocity(f);
      double d0 = vec3d1.horizontalLengthSquared();
      double d1 = vec3d.horizontalLengthSquared();
      if (d0 > 0.0 && d1 > 0.0) {
         playerentityrenderstate.applyFlyingRotation = true;
         double d2 = Math.min(1.0, (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / Math.sqrt(d0 * d1));
         double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
         playerentityrenderstate.flyingRotation = (float)(Math.signum(d3) * Math.acos(d2));
      } else {
         playerentityrenderstate.applyFlyingRotation = false;
         playerentityrenderstate.flyingRotation = 0.0F;
      }
   }

   private static void updateCape(AbstractClientPlayerEntity abstractclientplayerentity, PlayerEntityRenderState playerentityrenderstate, float f) {
      double d0 = MathHelper.lerp(f, abstractclientplayerentity.prevCapeX, abstractclientplayerentity.capeX)
         - MathHelper.lerp(f, abstractclientplayerentity.prevX, abstractclientplayerentity.getX());
      double d1 = MathHelper.lerp(f, abstractclientplayerentity.prevCapeY, abstractclientplayerentity.capeY)
         - MathHelper.lerp(f, abstractclientplayerentity.prevY, abstractclientplayerentity.getY());
      double d2 = MathHelper.lerp(f, abstractclientplayerentity.prevCapeZ, abstractclientplayerentity.capeZ)
         - MathHelper.lerp(f, abstractclientplayerentity.prevZ, abstractclientplayerentity.getZ());
      float f1 = MathHelper.lerpAngleDegrees(f, abstractclientplayerentity.prevBodyYaw, abstractclientplayerentity.bodyYaw);
      double d3 = MathHelper.sin(f1 * (float) (Math.PI / 180.0));
      double d4 = -MathHelper.cos(f1 * (float) (Math.PI / 180.0));
      playerentityrenderstate.field_53536 = (float)d1 * 10.0F;
      playerentityrenderstate.field_53536 = MathHelper.clamp(playerentityrenderstate.field_53536, -6.0F, 32.0F);
      playerentityrenderstate.field_53537 = (float)(d0 * d3 + d2 * d4) * 100.0F;
      playerentityrenderstate.field_53537 = playerentityrenderstate.field_53537 * (1.0F - playerentityrenderstate.getGlidingProgress());
      playerentityrenderstate.field_53537 = MathHelper.clamp(playerentityrenderstate.field_53537, 0.0F, 150.0F);
      playerentityrenderstate.field_53538 = (float)(d0 * d4 - d2 * d3) * 100.0F;
      playerentityrenderstate.field_53538 = MathHelper.clamp(playerentityrenderstate.field_53538, -20.0F, 20.0F);
      float f2 = MathHelper.lerp(f, abstractclientplayerentity.prevStrideDistance, abstractclientplayerentity.strideDistance);
      float f3 = MathHelper.lerp(f, abstractclientplayerentity.lastDistanceMoved, abstractclientplayerentity.distanceMoved);
      playerentityrenderstate.field_53536 = playerentityrenderstate.field_53536 + MathHelper.sin(f3 * 6.0F) * 32.0F * f2;
   }

   @Nullable
   private static Variant getShoulderParrotVariant(AbstractClientPlayerEntity abstractclientplayerentity, boolean flag) {
      NbtCompound nbtcompound = flag ? abstractclientplayerentity.getShoulderEntityLeft() : abstractclientplayerentity.getShoulderEntityRight();
      return EntityType.get(nbtcompound.getString("id")).filter(entitytype -> {
         return entitytype == EntityType.PARROT;
      }).isPresent() ? Variant.byIndex(nbtcompound.getInt("Variant")) : null;
   }

   public void renderRightArm(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, boolean flag) {
      this.renderArm(matrixstack, vertexconsumerprovider, i, identifier, ((PlayerEntityModel)this.model).rightArm, flag);
   }

   public void renderLeftArm(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, boolean flag) {
      this.renderArm(matrixstack, vertexconsumerprovider, i, identifier, ((PlayerEntityModel)this.model).leftArm, flag);
   }

   private void renderArm(
      MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Identifier identifier, ModelPart modelpart, boolean flag
   ) {
      PlayerEntityModel playerentitymodel = (PlayerEntityModel)this.getModel();
      modelpart.resetTransform();
      modelpart.visible = true;
      playerentitymodel.leftSleeve.visible = flag;
      playerentitymodel.rightSleeve.visible = flag;
      playerentitymodel.leftArm.roll = -0.1F;
      playerentitymodel.rightArm.roll = 0.1F;
      modelpart.render(matrixstack, vertexconsumerprovider.getBuffer(RenderLayer.getEntityTranslucent(identifier)), i, OverlayTexture.DEFAULT_UV);
   }

   protected void setupTransforms(PlayerEntityRenderState playerentityrenderstate, MatrixStack matrixstack, float f, float f1) {
      super.setupTransforms(playerentityrenderstate, matrixstack, f, f1);
      if (playerentityrenderstate.isGliding) {
         if (!playerentityrenderstate.usingRiptide) {
            matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(0.0F));
         }

         if (playerentityrenderstate.applyFlyingRotation) {
            matrixstack.multiply(RotationAxis.POSITIVE_Y.rotation(playerentityrenderstate.flyingRotation));
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void updateRenderState(LivingEntity livingentity, LivingEntityRenderState livingentityrenderstate, float f) {
      this.updateRenderState((AbstractClientPlayerEntity)livingentity, (PlayerEntityRenderState)livingentityrenderstate, f);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected void scale(LivingEntityRenderState livingentityrenderstate, MatrixStack matrixstack) {
      this.scale((PlayerEntityRenderState)livingentityrenderstate, matrixstack);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected void setupTransforms(LivingEntityRenderState livingentityrenderstate, MatrixStack matrixstack, float f, float f1) {
      this.setupTransforms((PlayerEntityRenderState)livingentityrenderstate, matrixstack, f, f1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Identifier getTexture(LivingEntityRenderState livingentityrenderstate) {
      return this.getTexture((PlayerEntityRenderState)livingentityrenderstate);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected boolean shouldRenderFeatures(LivingEntityRenderState livingentityrenderstate) {
      return this.shouldRenderFeatures((PlayerEntityRenderState)livingentityrenderstate);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void updateRenderState(Entity entity, EntityRenderState entityrenderstate, float f) {
      this.updateRenderState((AbstractClientPlayerEntity)entity, (PlayerEntityRenderState)entityrenderstate, f);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected float getShadowRadius(EntityRenderState entityrenderstate) {
      return super.getShadowRadius((LivingEntityRenderState)entityrenderstate);
   }

   // $VF: synthetic method
   // $VF: bridge method
   protected void renderLabelIfPresent(
      EntityRenderState entityrenderstate, Text text, MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i
   ) {
      this.renderLabelIfPresent((PlayerEntityRenderState)entityrenderstate, text, matrixstack, vertexconsumerprovider, i);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Vec3d getPositionOffset(EntityRenderState entityrenderstate) {
      return this.getPositionOffset((PlayerEntityRenderState)entityrenderstate);
   }
}
