package render;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import core.ClientMain;
import module.SwingAnimationModule;
import module.ViewModelModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.MapRenderState;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class CustomHeldItemRenderer extends HeldItemRenderer {
   private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(Identifier.ofVanilla("textures/map/map_background.png"));
   private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(Identifier.ofVanilla("textures/map/map_background_checkerboard.png"));
   private static final float field_32735 = -0.4F;
   private static final float field_32736 = 0.2F;
   private static final float field_32737 = -0.2F;
   private static final float field_32738 = -0.6F;
   private static final float EQUIP_OFFSET_TRANSLATE_X = 0.56F;
   private static final float EQUIP_OFFSET_TRANSLATE_Y = -0.52F;
   private static final float EQUIP_OFFSET_TRANSLATE_Z = -0.72F;
   private static final float field_32742 = 45.0F;
   private static final float field_32743 = -80.0F;
   private static final float field_32744 = -20.0F;
   private static final float field_32745 = -20.0F;
   private static final float EAT_OR_DRINK_X_ANGLE_MULTIPLIER = 10.0F;
   private static final float EAT_OR_DRINK_Y_ANGLE_MULTIPLIER = 90.0F;
   private static final float EAT_OR_DRINK_Z_ANGLE_MULTIPLIER = 30.0F;
   private static final float field_32749 = 0.6F;
   private static final float field_32750 = -0.5F;
   private static final float field_32751 = 0.0F;
   private static final double field_32752 = 27.0;
   private static final float field_32753 = 0.8F;
   private static final float field_32754 = 0.1F;
   private static final float field_32755 = -0.3F;
   private static final float field_32756 = 0.4F;
   private static final float field_32757 = -0.4F;
   private static final float ARM_HOLDING_ITEM_SECOND_Y_ANGLE_MULTIPLIER = 70.0F;
   private static final float ARM_HOLDING_ITEM_FIRST_Z_ANGLE_MULTIPLIER = -20.0F;
   private static final float field_32690 = -0.6F;
   private static final float field_32691 = 0.8F;
   private static final float field_32692 = 0.8F;
   private static final float field_32693 = -0.75F;
   private static final float field_32694 = -0.9F;
   private static final float field_32695 = 45.0F;
   private static final float field_32696 = -1.0F;
   private static final float field_32697 = 3.6F;
   private static final float field_32698 = 3.5F;
   private static final float ARM_HOLDING_ITEM_TRANSLATE_X = 5.6F;
   private static final int ARM_HOLDING_ITEM_X_ANGLE_MULTIPLIER = 200;
   private static final int ARM_HOLDING_ITEM_THIRD_Y_ANGLE_MULTIPLIER = -135;
   private static final int ARM_HOLDING_ITEM_SECOND_Z_ANGLE_MULTIPLIER = 120;
   private static final float field_32703 = -0.4F;
   private static final float field_32704 = -0.2F;
   private static final float field_32705 = 0.0F;
   private static final float field_32706 = 0.04F;
   private static final float field_32707 = -0.72F;
   private static final float field_32708 = -1.2F;
   private static final float field_32709 = -0.5F;
   private static final float field_32710 = 45.0F;
   private static final float field_32711 = -85.0F;
   private static final float ARM_X_ANGLE_MULTIPLIER = 45.0F;
   private static final float ARM_Y_ANGLE_MULTIPLIER = 92.0F;
   private static final float ARM_Z_ANGLE_MULTIPLIER = -41.0F;
   private static final float ARM_TRANSLATE_X = 0.3F;
   private static final float ARM_TRANSLATE_Y = -1.1F;
   private static final float ARM_TRANSLATE_Z = 0.45F;
   private static final float field_32718 = 20.0F;
   private static final float FIRST_PERSON_MAP_FIRST_SCALE = 0.38F;
   private static final float FIRST_PERSON_MAP_TRANSLATE_X = -0.5F;
   private static final float FIRST_PERSON_MAP_TRANSLATE_Y = -0.5F;
   private static final float FIRST_PERSON_MAP_TRANSLATE_Z = 0.0F;
   private static final float FIRST_PERSON_MAP_SECOND_SCALE = 0.0078125F;
   private static final int field_32724 = 7;
   private static final int field_32725 = 128;
   private static final int field_32726 = 128;
   private static final float field_32727 = 0.0F;
   private static final float field_32728 = 0.0F;
   private static final float field_32729 = 0.04F;
   private static final float field_32730 = 0.0F;
   private static final float field_32731 = 0.004F;
   private static final float field_32732 = 0.0F;
   private static final float field_32733 = 0.2F;
   private static final float field_32734 = 0.1F;
   private final MinecraftClient client;
   private final MapRenderState mapRenderState = new MapRenderState();
   private ItemStack mainHand = ItemStack.EMPTY;
   private ItemStack offHand = ItemStack.EMPTY;
   private float equipProgressMainHand;
   private float prevEquipProgressMainHand;
   private float equipProgressOffHand;
   private float prevEquipProgressOffHand;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;
   private final ItemModelManager itemModelManager;

   public CustomHeldItemRenderer(
      MinecraftClient minecraftclient, EntityRenderDispatcher entityrenderdispatcher, ItemRenderer itemrenderer, ItemModelManager itemmodelmanager
   ) {
      super(minecraftclient, entityrenderdispatcher, itemrenderer, itemmodelmanager);
      this.client = minecraftclient;
      this.entityRenderDispatcher = entityrenderdispatcher;
      this.itemRenderer = itemrenderer;
      this.itemModelManager = itemmodelmanager;
   }

   public void renderItem(
      LivingEntity livingentity,
      ItemStack itemstack,
      ModelTransformationMode modeltransformationmode,
      boolean flag,
      MatrixStack matrixstack,
      VertexConsumerProvider vertexconsumerprovider,
      int i
   ) {
      if (!itemstack.isEmpty()) {
         this.itemRenderer
            .renderItem(
               livingentity,
               itemstack,
               modeltransformationmode,
               flag,
               matrixstack,
               vertexconsumerprovider,
               livingentity.getWorld(),
               i,
               OverlayTexture.DEFAULT_UV,
               livingentity.getId() + modeltransformationmode.ordinal()
            );
      }
   }

   private float getMapAngle(float f) {
      float f1 = 1.0F - f / 45.0F + 0.1F;
      f1 = MathHelper.clamp(f1, 0.0F, 1.0F);
      return -MathHelper.cos(f1 * (float) Math.PI) * 0.5F + 0.5F;
   }

   private void renderArm(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, Arm arm) {
      PlayerEntityRenderer playerentityrenderer = (PlayerEntityRenderer)this.entityRenderDispatcher.getRenderer(this.client.player);
      matrixstack.push();
      float f = arm == Arm.RIGHT ? 1.0F : -1.0F;
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(92.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * -41.0F));
      matrixstack.translate(f * 0.3F, -1.1F, 0.45F);
      Identifier identifier = this.client.player.getSkinTextures().texture();
      if (arm == Arm.RIGHT) {
         playerentityrenderer.renderRightArm(matrixstack, vertexconsumerprovider, i, identifier, this.client.player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE));
      } else {
         playerentityrenderer.renderLeftArm(matrixstack, vertexconsumerprovider, i, identifier, this.client.player.isPartVisible(PlayerModelPart.LEFT_SLEEVE));
      }

      matrixstack.pop();
   }

   private void renderMapInOneHand(
      MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, float f, Arm arm, float f1, ItemStack itemstack
   ) {
      float f2 = arm == Arm.RIGHT ? 1.0F : -1.0F;
      matrixstack.translate(f2 * 0.125F, -0.125F, 0.0F);
      if (!this.client.player.isInvisible()) {
         matrixstack.push();
         matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * 10.0F));
         this.renderArmHoldingItem(matrixstack, vertexconsumerprovider, i, f, f1, arm);
         matrixstack.pop();
      }

      matrixstack.push();
      matrixstack.translate(f2 * 0.51F, -0.08F + f * -1.2F, -0.75F);
      float f3 = MathHelper.sqrt(f1);
      float f4 = MathHelper.sin(f3 * (float) Math.PI);
      float f5 = -0.5F * f4;
      float f6 = 0.4F * MathHelper.sin(f3 * (float) (Math.PI * 2));
      float f7 = -0.3F * MathHelper.sin(f1 * (float) Math.PI);
      matrixstack.translate(f2 * f5, f6 - 0.3F * f4, f7);
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f4 * -45.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * f4 * -30.0F));
      this.renderFirstPersonMap(matrixstack, vertexconsumerprovider, i, itemstack);
      matrixstack.pop();
   }

   private void renderMapInBothHands(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, float f, float f1, float f2) {
      float f3 = MathHelper.sqrt(f2);
      float f4 = -0.2F * MathHelper.sin(f2 * (float) Math.PI);
      float f5 = -0.4F * MathHelper.sin(f3 * (float) Math.PI);
      matrixstack.translate(0.0F, -f4 / 2.0F, f5);
      float f6 = this.getMapAngle(f);
      matrixstack.translate(0.0F, 0.04F + f1 * -1.2F + f6 * -0.5F, -0.72F);
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f6 * -85.0F));
      if (!this.client.player.isInvisible()) {
         matrixstack.push();
         matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         this.renderArm(matrixstack, vertexconsumerprovider, i, Arm.RIGHT);
         this.renderArm(matrixstack, vertexconsumerprovider, i, Arm.LEFT);
         matrixstack.pop();
      }

      float f7 = MathHelper.sin(f3 * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f7 * 20.0F));
      matrixstack.scale(2.0F, 2.0F, 2.0F);
      this.renderFirstPersonMap(matrixstack, vertexconsumerprovider, i, this.mainHand);
   }

   private void renderFirstPersonMap(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, ItemStack itemstack) {
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
      matrixstack.scale(0.38F, 0.38F, 0.38F);
      matrixstack.translate(-0.5F, -0.5F, 0.0F);
      matrixstack.scale(0.0078125F, 0.0078125F, 0.0078125F);
      MapIdComponent mapidcomponent = (MapIdComponent)itemstack.get(DataComponentTypes.MAP_ID);
      MapState mapstate = FilledMapItem.getMapState(mapidcomponent, this.client.world);
      VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(mapstate == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(-1).texture(0.0F, 1.0F).light(i);
      vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(-1).texture(1.0F, 1.0F).light(i);
      vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(-1).texture(1.0F, 0.0F).light(i);
      vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(-1).texture(0.0F, 0.0F).light(i);
      if (mapstate != null) {
         MapRenderer maprenderer = this.client.getMapRenderer();
         maprenderer.update(mapidcomponent, mapstate, this.mapRenderState);
         maprenderer.draw(this.mapRenderState, matrixstack, vertexconsumerprovider, false, i);
      }
   }

   private void renderArmHoldingItem(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, float f, float f1, Arm arm) {
      boolean flag = arm != Arm.LEFT;
      float f2 = flag ? 1.0F : -1.0F;
      float f3 = MathHelper.sqrt(f1);
      float f4 = -0.3F * MathHelper.sin(f3 * (float) Math.PI);
      float f5 = 0.4F * MathHelper.sin(f3 * (float) (Math.PI * 2));
      float f6 = -0.4F * MathHelper.sin(f1 * (float) Math.PI);
      matrixstack.translate(f2 * (f4 + 0.64000005F), f5 + -0.6F + f * -0.6F, f6 + -0.71999997F);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * 45.0F));
      float f7 = MathHelper.sin(f1 * f1 * (float) Math.PI);
      float f8 = MathHelper.sin(f3 * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * f8 * 70.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * f7 * -20.0F));
      ClientPlayerEntity clientplayerentity = this.client.player;
      matrixstack.translate(f2 * -1.0F, 3.6F, 3.5F);
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f2 * 120.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(200.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f2 * -135.0F));
      matrixstack.translate(f2 * 5.6F, 0.0F, 0.0F);
      PlayerEntityRenderer playerentityrenderer = (PlayerEntityRenderer)this.entityRenderDispatcher.getRenderer(clientplayerentity);
      Identifier identifier = clientplayerentity.getSkinTextures().texture();
      if (flag) {
         playerentityrenderer.renderRightArm(matrixstack, vertexconsumerprovider, i, identifier, clientplayerentity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE));
      } else {
         playerentityrenderer.renderLeftArm(matrixstack, vertexconsumerprovider, i, identifier, clientplayerentity.isPartVisible(PlayerModelPart.LEFT_SLEEVE));
      }
   }

   private void applyEatOrDrinkTransformation(MatrixStack matrixstack, float f, Arm arm, ItemStack itemstack, PlayerEntity playerentity) {
      float f1 = playerentity.getItemUseTimeLeft() - f + 1.0F;
      float f2 = f1 / itemstack.getMaxUseTime(playerentity);
      if (f2 < 0.8F) {
         float f3 = MathHelper.abs(MathHelper.cos(f1 / 4.0F * (float) Math.PI) * 0.1F);
         matrixstack.translate(0.0F, f3, 0.0F);
      }

      float f4 = 1.0F - (float)Math.pow(f2, 27.0);
      int i = arm == Arm.RIGHT ? 1 : -1;
      matrixstack.translate(f4 * 0.6F * i, f4 * -0.5F, f4 * 0.0F);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * f4 * 90.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f4 * 10.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f4 * 30.0F));
   }

   private void applyBrushTransformation(MatrixStack matrixstack, float f, Arm arm, ItemStack itemstack, PlayerEntity playerentity, float f1) {
      this.applyEquipOffset(matrixstack, arm, f1);
      float f2 = playerentity.getItemUseTimeLeft() % 10;
      float f3 = f2 - f + 1.0F;
      float f4 = 1.0F - f3 / 10.0F;
      float f5 = -90.0F;
      float f6 = 60.0F;
      float f7 = 150.0F;
      float f8 = -15.0F;
      byte b0 = 2;
      float f9 = -15.0F + 75.0F * MathHelper.cos(f4 * 2.0F * (float) Math.PI);
      if (arm != Arm.RIGHT) {
         matrixstack.translate(0.1, 0.83, 0.35);
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90.0F));
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f9));
         matrixstack.translate(-0.3, 0.22, 0.35);
      } else {
         matrixstack.translate(-0.25, 0.22, 0.35);
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
         matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
         matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));
         matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f9));
      }
   }

   private void applySwingOffset(MatrixStack matrixstack, Arm arm, float f) {
      int i = arm == Arm.RIGHT ? 1 : -1;
      float f1 = MathHelper.sin(f * f * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * (45.0F + f1 * -20.0F)));
      float f2 = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
      matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i * f2 * -20.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f2 * -80.0F));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(i * -45.0F));
   }

   private void applyEquipOffset(MatrixStack matrixstack, Arm arm, float f) {
      int i = arm == Arm.RIGHT ? 1 : -1;
      matrixstack.translate(i * 0.56F, -0.52F + f * -0.6F, -0.72F);
   }

   public void renderItem(float f, MatrixStack matrixstack, Immediate immediate, ClientPlayerEntity clientplayerentity, int i) {
      float f1 = clientplayerentity.getHandSwingProgress(f);
      Hand hand = (Hand)MoreObjects.firstNonNull(clientplayerentity.preferredHand, Hand.MAIN_HAND);
      float f2 = clientplayerentity.getLerpedPitch(f);
      ih ih = getHandRenderType(clientplayerentity);
      float f3 = MathHelper.lerp(f, clientplayerentity.lastRenderPitch, clientplayerentity.renderPitch);
      float f4 = MathHelper.lerp(f, clientplayerentity.lastRenderYaw, clientplayerentity.renderYaw);
      matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((clientplayerentity.getPitch(f) - f3) * 0.1F));
      matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((clientplayerentity.getYaw(f) - f4) * 0.1F));
      if (ih.renderMainHand) {
         float f5 = hand == Hand.MAIN_HAND ? f1 : 0.0F;
         float f6 = 1.0F - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
         this.renderFirstPersonItem(clientplayerentity, f, f2, Hand.MAIN_HAND, f5, this.mainHand, f6, matrixstack, immediate, i);
      }

      if (ih.renderOffHand) {
         float f7 = hand == Hand.OFF_HAND ? f1 : 0.0F;
         float f8 = 1.0F - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
         this.renderFirstPersonItem(clientplayerentity, f, f2, Hand.OFF_HAND, f7, this.offHand, f8, matrixstack, immediate, i);
      }

      immediate.draw();
   }

   @VisibleForTesting
   static ih getHandRenderType(ClientPlayerEntity clientplayerentity) {
      ItemStack itemstack = clientplayerentity.getMainHandStack();
      ItemStack itemstack1 = clientplayerentity.getOffHandStack();
      boolean flag = itemstack.isOf(Items.BOW) || itemstack1.isOf(Items.BOW);
      boolean flag1 = itemstack.isOf(Items.CROSSBOW) || itemstack1.isOf(Items.CROSSBOW);
      if (!flag && !flag1) {
         return ih.RENDER_BOTH_HANDS;
      } else if (clientplayerentity.isUsingItem()) {
         return getUsingItemHandRenderType(clientplayerentity);
      } else {
         return isChargedCrossbow(itemstack) ? ih.RENDER_MAIN_HAND_ONLY : ih.RENDER_BOTH_HANDS;
      }
   }

   private static ih getUsingItemHandRenderType(ClientPlayerEntity clientplayerentity) {
      ItemStack itemstack = clientplayerentity.getActiveItem();
      Hand hand = clientplayerentity.getActiveHand();
      if (itemstack.isOf(Items.BOW) || itemstack.isOf(Items.CROSSBOW)) {
         return ih.shouldOnlyRender(hand);
      } else {
         return hand == Hand.MAIN_HAND && isChargedCrossbow(clientplayerentity.getOffHandStack()) ? ih.RENDER_MAIN_HAND_ONLY : ih.RENDER_BOTH_HANDS;
      }
   }

   private static boolean isChargedCrossbow(ItemStack itemstack) {
      return itemstack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void renderFirstPersonItem(
      AbstractClientPlayerEntity abstractclientplayerentity,
      float f,
      float f1,
      Hand hand,
      float f2,
      ItemStack itemstack,
      float f3,
      MatrixStack matrixstack,
      VertexConsumerProvider vertexconsumerprovider,
      int i
   ) {
      ViewModelModule viewmodelmodule = ClientMain.getInstance().getModuleManager().<ViewModelModule>getModule(ViewModelModule.class);
      if (!abstractclientplayerentity.isUsingSpyglass()) {
         boolean flag = hand == Hand.MAIN_HAND;
         Arm arm = flag ? abstractclientplayerentity.getMainArm() : abstractclientplayerentity.getMainArm().getOpposite();
         boolean flag1 = arm == Arm.RIGHT;
         int j = flag1 ? 1 : -1;
         matrixstack.push();
         if (viewmodelmodule != null && viewmodelmodule.isEnabled()) {
            boolean flag2 = !flag1 && viewmodelmodule.p().getValue();
            float f4;
            float f5;
            float f6;
            float f7;
            float f8;
            float f9;
            float f10;
            if (flag2) {
               f4 = -viewmodelmodule.b().getFloatValue();
               f5 = viewmodelmodule.c().getFloatValue();
               f6 = viewmodelmodule.d().getFloatValue();
               f7 = viewmodelmodule.e().getFloatValue();
               f8 = viewmodelmodule.f().getFloatValue();
               f9 = -viewmodelmodule.g().getFloatValue();
               f10 = -viewmodelmodule.h().getFloatValue();
            } else if (flag1) {
               f4 = viewmodelmodule.b().getFloatValue();
               f5 = viewmodelmodule.c().getFloatValue();
               f6 = viewmodelmodule.d().getFloatValue();
               f7 = viewmodelmodule.e().getFloatValue();
               f8 = viewmodelmodule.f().getFloatValue();
               f9 = viewmodelmodule.g().getFloatValue();
               f10 = viewmodelmodule.h().getFloatValue();
            } else {
               f4 = viewmodelmodule.i().getFloatValue();
               f5 = viewmodelmodule.j().getFloatValue();
               f6 = viewmodelmodule.k().getFloatValue();
               f7 = viewmodelmodule.l().getFloatValue();
               f8 = viewmodelmodule.m().getFloatValue();
               f9 = viewmodelmodule.n().getFloatValue();
               f10 = viewmodelmodule.o().getFloatValue();
            }

            matrixstack.translate(f4, f5, f6);
            if (f8 != 0.0F) {
               matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f8));
            }

            if (f9 != 0.0F) {
               matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f9));
            }

            if (f10 != 0.0F) {
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f10));
            }

            if (f7 != 1.0F) {
               matrixstack.scale(f7, f7, f7);
            }
         }

         if (itemstack.isEmpty()) {
            if (flag && !abstractclientplayerentity.isInvisible()) {
               this.renderArmHoldingItem(matrixstack, vertexconsumerprovider, i, f3, f2, arm);
            }
         } else if (itemstack.contains(DataComponentTypes.MAP_ID)) {
            if (flag && this.offHand.isEmpty()) {
               this.renderMapInBothHands(matrixstack, vertexconsumerprovider, i, f1, f3, f2);
            } else {
               this.renderMapInOneHand(matrixstack, vertexconsumerprovider, i, f3, arm, f2, itemstack);
            }
         } else if (itemstack.isOf(Items.CROSSBOW)) {
            boolean flag3 = CrossbowItem.isCharged(itemstack);
            boolean flag4 = arm == Arm.RIGHT;
            int k = flag4 ? 1 : -1;
            if (abstractclientplayerentity.isUsingItem()
               && abstractclientplayerentity.getItemUseTimeLeft() > 0
               && abstractclientplayerentity.getActiveHand() == hand) {
               this.applyEquipOffset(matrixstack, arm, f3);
               matrixstack.translate(k * -0.48F, -0.094F, 0.057F);
               matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-12.0F));
               matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k * 65.3F));
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(k * -9.78F));
               float f17 = itemstack.getMaxUseTime(abstractclientplayerentity) - (abstractclientplayerentity.getItemUseTimeLeft() - f + 1.0F);
               float f18 = f17 / CrossbowItem.getPullTime(itemstack, abstractclientplayerentity);
               if (f18 > 1.0F) {
                  f18 = 1.0F;
               }

               if (f18 > 0.1F) {
                  float f19 = MathHelper.sin((f17 - 0.1F) * 1.3F);
                  float f20 = f18 - 0.1F;
                  float f21 = f19 * f20;
                  matrixstack.translate(f21 * 0.0F, f21 * 0.004F, f21 * 0.0F);
               }

               matrixstack.translate(f18 * 0.0F, f18 * 0.0F, f18 * 0.04F);
               matrixstack.scale(1.0F, 1.0F, 1.0F + f18 * 0.2F);
               matrixstack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k * 45.0F));
            } else {
               this.swingArm(f2, f3, matrixstack, k, arm, flag);
               if (flag3 && f2 < 0.001F && flag) {
                  matrixstack.translate(k * -0.64F, 0.0F, 0.0F);
                  matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k * 10.0F));
               }
            }

            this.renderItem(
               abstractclientplayerentity,
               itemstack,
               flag4 ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
               !flag4,
               matrixstack,
               vertexconsumerprovider,
               i
            );
         } else {
            if (abstractclientplayerentity.isUsingItem()
               && abstractclientplayerentity.getItemUseTimeLeft() > 0
               && abstractclientplayerentity.getActiveHand() == hand) {
               switch (itemstack.getUseAction()) {
                  case NONE:
                     this.applyEquipOffset(matrixstack, arm, f3);
                     break;
                  case EAT:
                  case DRINK:
                     this.applyEatOrDrinkTransformation(matrixstack, f, arm, itemstack, abstractclientplayerentity);
                     this.applyEquipOffset(matrixstack, arm, f3);
                     break;
                  case BLOCK:
                     this.applyEquipOffset(matrixstack, arm, f3);
                     if (!(itemstack.getItem() instanceof ShieldItem)) {
                        matrixstack.translate(j * -0.14F, 0.08F, 0.14F);
                        matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-102.25F));
                        matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 13.36F));
                        matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * 78.05F));
                     }
                     break;
                  case BOW:
                     this.applyEquipOffset(matrixstack, arm, f3);
                     matrixstack.translate(j * -0.28F, 0.18F, 0.16F);
                     matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.93F));
                     matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 35.3F));
                     matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -9.78F));
                     float f12 = itemstack.getMaxUseTime(abstractclientplayerentity) - (abstractclientplayerentity.getItemUseTimeLeft() - f + 1.0F);
                     float f14 = f12 / 20.0F;
                     f14 = (f14 * f14 + f14 * 2.0F) / 3.0F;
                     if (f14 > 1.0F) {
                        f14 = 1.0F;
                     }

                     if (f14 > 0.1F) {
                        float f15 = MathHelper.sin((f12 - 0.1F) * 1.3F);
                        float f16 = f15 * (f14 - 0.1F);
                        matrixstack.translate(f16 * 0.0F, f16 * 0.004F, f16 * 0.0F);
                     }

                     matrixstack.translate(f14 * 0.0F, f14 * 0.0F, f14 * 0.04F);
                     matrixstack.scale(1.0F, 1.0F, 1.0F + f14 * 0.2F);
                     matrixstack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(j * 45.0F));
                     break;
                  case SPEAR:
                     this.applyEquipOffset(matrixstack, arm, f3);
                     matrixstack.translate(j * -0.5F, 0.7F, 0.1F);
                     matrixstack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0F));
                     matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 35.3F));
                     matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -9.785F));
                     float f11 = (itemstack.getMaxUseTime(abstractclientplayerentity) - (abstractclientplayerentity.getItemUseTimeLeft() - f + 1.0F)) / 10.0F;
                     if (f11 > 1.0F) {
                        f11 = 1.0F;
                     }

                     if (f11 > 0.1F) {
                        float f13 = MathHelper.sin((f11 - 0.1F) * 1.3F) * (f11 - 0.1F);
                        matrixstack.translate(f13 * 0.0F, f13 * 0.004F, f13 * 0.0F);
                     }

                     matrixstack.translate(0.0F, 0.0F, f11 * 0.2F);
                     matrixstack.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
                     matrixstack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(j * 45.0F));
                     break;
                  case BRUSH:
                     this.applyBrushTransformation(matrixstack, f, arm, itemstack, abstractclientplayerentity, f3);
                     break;
                  case BUNDLE:
                     this.swingArm(f2, f3, matrixstack, j, arm, flag);
               }
            } else if (abstractclientplayerentity.isUsingRiptide()) {
               this.applyEquipOffset(matrixstack, arm, f3);
               matrixstack.translate(j * -0.4F, 0.8F, 0.3F);
               matrixstack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j * 65.0F));
               matrixstack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(j * -85.0F));
            } else {
               this.swingArm(f2, f3, matrixstack, j, arm, flag);
            }

            this.renderItem(
               abstractclientplayerentity,
               itemstack,
               arm == Arm.RIGHT ? ModelTransformationMode.FIRST_PERSON_RIGHT_HAND : ModelTransformationMode.FIRST_PERSON_LEFT_HAND,
               arm != Arm.RIGHT,
               matrixstack,
               vertexconsumerprovider,
               i
            );
         }

         matrixstack.pop();
      }
   }

   private void swingArm(float f, float f1, MatrixStack matrixstack, int i, Arm arm, boolean flag) {
      SwingAnimationModule swinganimationmodule = ClientMain.getInstance().getModuleManager().<SwingAnimationModule>getModule(SwingAnimationModule.class);
      boolean flag1 = swinganimationmodule != null && swinganimationmodule.a();
      if (flag1 && swinganimationmodule.onlyMainHandSetting.getValue() && !flag) {
         flag1 = false;
      }

      if (flag1) {
         float f5 = swinganimationmodule.f(f);
         swinganimationmodule.c(matrixstack, f5, arm);
      } else {
         float f2 = -0.4F * MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
         float f3 = 0.2F * MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2));
         float f4 = -0.2F * MathHelper.sin(f * (float) Math.PI);
         matrixstack.translate(i * f2, f3, f4);
         this.applyEquipOffset(matrixstack, arm, f1);
         this.applySwingOffset(matrixstack, arm, f);
      }
   }

   private boolean shouldSkipHandAnimationOnSwap(ItemStack itemstack, ItemStack itemstack1) {
      return ItemStack.areEqual(itemstack, itemstack1) ? true : !this.itemModelManager.hasHandAnimationOnSwap(itemstack1);
   }

   public void updateHeldItems() {
      this.prevEquipProgressMainHand = this.equipProgressMainHand;
      this.prevEquipProgressOffHand = this.equipProgressOffHand;
      ClientPlayerEntity clientplayerentity = this.client.player;
      ItemStack itemstack = clientplayerentity.getMainHandStack();
      ItemStack itemstack1 = clientplayerentity.getOffHandStack();
      if (this.shouldSkipHandAnimationOnSwap(this.mainHand, itemstack)) {
         this.mainHand = itemstack;
      }

      if (this.shouldSkipHandAnimationOnSwap(this.offHand, itemstack1)) {
         this.offHand = itemstack1;
      }

      if (clientplayerentity.isRiding()) {
         this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4F, 0.0F, 1.0F);
         this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4F, 0.0F, 1.0F);
      } else {
         float f = clientplayerentity.getAttackCooldownProgress(1.0F);
         float f1 = this.mainHand != itemstack ? 0.0F : f * f * f;
         float f2 = this.offHand != itemstack1 ? 0.0F : 1.0F;
         this.equipProgressMainHand = this.equipProgressMainHand + MathHelper.clamp(f1 - this.equipProgressMainHand, -0.4F, 0.4F);
         this.equipProgressOffHand = this.equipProgressOffHand + MathHelper.clamp(f2 - this.equipProgressOffHand, -0.4F, 0.4F);
      }

      if (this.equipProgressMainHand < 0.1F) {
         this.mainHand = itemstack;
      }

      if (this.equipProgressOffHand < 0.1F) {
         this.offHand = itemstack1;
      }
   }

   public void resetEquipProgress(Hand hand) {
      if (hand == Hand.MAIN_HAND) {
         this.equipProgressMainHand = 0.0F;
      } else {
         this.equipProgressOffHand = 0.0F;
      }
   }
}
