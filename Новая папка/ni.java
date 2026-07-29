import core.ClientMain;
import module.ParrotModule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.model.ParrotEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.ParrotEntityModel.Pose;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.ParrotEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity.Variant;

@Environment(EnvType.CLIENT)
public class ni extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
   private final ParrotEntityModel model;
   private final ParrotEntityRenderState parrotState = new ParrotEntityRenderState();

   public ni(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> featurerenderercontext, LoadedEntityModels loadedentitymodels) {
      super(featurerenderercontext);
      this.model = new ParrotEntityModel(loadedentitymodels.getModelPart(EntityModelLayers.PARROT));
      this.parrotState.parrotPose = Pose.ON_SHOULDER;
   }

   public void render(
      MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, PlayerEntityRenderState playerentityrenderstate, float f, float f1
   ) {
      if (MinecraftClient.getInstance().getGameProfile().getName().equals(playerentityrenderstate.name)) {
         ParrotModule parrotmodule = ClientMain.getInstance().getModuleManager().<ParrotModule>getModule(ParrotModule.class);
         if (parrotmodule != null && parrotmodule.isEnabled()) {
            String s = parrotmodule.b().getFirst();
            Variant variant = this.getVariantByName(s);
            String s1 = parrotmodule.a().getFirst();
            switch (s1) {
               case "Справа":
                  this.render(matrixstack, vertexconsumerprovider, i, playerentityrenderstate, variant, f, f1, true);
                  return;
               case "Слева":
                  this.render(matrixstack, vertexconsumerprovider, i, playerentityrenderstate, variant, f, f1, false);
            }
         }
      }
   }

   private Variant getVariantByName(String s) {
      switch (s) {
         case "Синий":
            return Variant.BLUE;
         case "Зеленый":
            return Variant.GREEN;
         case "Красно-синий":
            return Variant.RED_BLUE;
         default:
            return Variant.YELLOW_BLUE;
      }
   }

   private void render(
      MatrixStack matrixstack,
      VertexConsumerProvider vertexconsumerprovider,
      int i,
      PlayerEntityRenderState playerentityrenderstate,
      Variant variant,
      float f,
      float f1,
      boolean flag
   ) {
      matrixstack.push();
      matrixstack.translate(flag ? 0.4F : -0.4F, playerentityrenderstate.isInSneakingPose ? -1.3F : -1.5F, 0.0F);
      this.parrotState.age = playerentityrenderstate.age;
      this.parrotState.limbFrequency = playerentityrenderstate.limbFrequency;
      this.parrotState.limbAmplitudeMultiplier = playerentityrenderstate.limbAmplitudeMultiplier;
      this.parrotState.yawDegrees = f;
      this.parrotState.pitch = f1;
      this.model.setAngles(this.parrotState);
      this.model
         .render(matrixstack, vertexconsumerprovider.getBuffer(this.model.getLayer(ParrotEntityRenderer.getTexture(variant))), i, OverlayTexture.DEFAULT_UV);
      matrixstack.pop();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void render(MatrixStack matrixstack, VertexConsumerProvider vertexconsumerprovider, int i, EntityRenderState entityrenderstate, float f, float f1) {
      this.render(matrixstack, vertexconsumerprovider, i, (PlayerEntityRenderState)entityrenderstate, f, f1);
   }
}
