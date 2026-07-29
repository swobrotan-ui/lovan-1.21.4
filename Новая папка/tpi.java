import core.ClientMain;
import core.FriendManager;
import hook.GameRendererInitHook;
import module.CapeModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.equipment.EquipmentModel.LayerType;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class tpi extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   private final BipedEntityModel<PlayerEntityRenderState> model;
   private final EquipmentModelLoader equipmentModelLoader;

   public tpi(
      FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featurerenderercontext,
      LoadedEntityModels loadedentitymodels,
      EquipmentModelLoader equipmentmodelloader
   ) {
      super(featurerenderercontext);
      this.model = new PlayerCapeModel(loadedentitymodels.getModelPart(EntityModelLayers.PLAYER_CAPE));
      this.equipmentModelLoader = equipmentmodelloader;
   }

   private boolean hasCustomModelForLayer(ItemStack itemstack, LayerType layertype) {
      EquippableComponent equippablecomponent = (EquippableComponent)itemstack.get(DataComponentTypes.EQUIPPABLE);
      if (equippablecomponent != null && !equippablecomponent.assetId().isEmpty()) {
         EquipmentModel equipmentmodel = this.equipmentModelLoader.get((RegistryKey)equippablecomponent.assetId().get());
         return !equipmentmodel.getLayers(layertype).isEmpty();
      } else {
         return false;
      }
   }

   public void render(
      MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, PlayerEntityRenderState playerentityrenderstate, float f, float f1
   ) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player != null && minecraftclient.world != null) {
         if (minecraftclient.world.getEntityById(playerentityrenderstate.id) instanceof PlayerEntity playerentity) {
            CapeModule capemodule = ClientMain.getInstance().getModuleManager().<CapeModule>getModule(CapeModule.class);
            if (capemodule != null && capemodule.isEnabled()) {
               if (!playerentityrenderstate.invisible
                  && playerentityrenderstate.capeVisible
                  && (playerentity.getId() == minecraftclient.player.getId() || FriendManager.getInstance().isFriendPlayer(playerentity))) {
                  Identifier identifier = GameRendererInitHook.cape;
                  if (!this.hasCustomModelForLayer(playerentityrenderstate.equippedChestStack, LayerType.WINGS)) {
                     matrixstack.push();
                     if (this.hasCustomModelForLayer(playerentityrenderstate.equippedChestStack, LayerType.HUMANOID)) {
                        matrixstack.translate(0.0F, -0.053125F, 0.06875F);
                     }

                     VertexConsumer vertexconsumer = vertexconsumerprovider.getBuffer(RenderLayer.getEntitySolid(identifier));
                     ((PlayerEntityModel)this.getContextModel()).copyTransforms(this.model);
                     this.model.setAngles(playerentityrenderstate);
                     this.model.render(matrixstack, vertexconsumer, i, OverlayTexture.DEFAULT_UV);
                     matrixstack.pop();
                  }
               }
            }
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, EntityRenderState entityrenderstate, float f, float f1) {
      this.render(matrixstack, vertexconsumerprovider, i, (PlayerEntityRenderState)entityrenderstate, f, f1);
   }
}
