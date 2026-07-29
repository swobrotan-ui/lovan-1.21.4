package module;

import com.mojang.blaze3d.systems.RenderSystem;
import enum.Category;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import setting.SliderSetting;

public class ShowInvisibleModule extends Module {
   private final SliderSetting alphaSetting = new SliderSetting("Альфа", "Прозрачность модельки", 0.5, 0.1, 1.0, 0.05);

   public ShowInvisibleModule() {
      super("ЗхошИнвизибле", "Показывает невидимых игроков", Category.RENDER);
      this.addSettings(this.alphaSetting);
   }

   @Override
   public void onEnable() {
   }

   @Override
   public void onDisable() {
   }

   @Override
   public void onRenderAfterTranslucent(WorldRenderContext worldrendercontext) {
      if (!this.isNotInWorld()) {
         try {
            this.a(worldrendercontext);
         } catch (Exception exception) {
         }
      }
   }

   private void a(WorldRenderContext worldrendercontext) {
      MinecraftClient minecraftclient = this.getClient();
      PlayerEntity playerentity = this.getPlayer();
      if (playerentity != null) {
         List list = this.getWorld().getPlayers();
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         float f = worldrendercontext.tickCounter().getTickDelta(true);
         Vec3d vec3d = worldrendercontext.camera().getPos();
         double d0 = vec3d.x;
         double d1 = vec3d.y;
         double d2 = vec3d.z;
         EntityRenderDispatcher entityrenderdispatcher = minecraftclient.getEntityRenderDispatcher();
         Immediate immediate = minecraftclient.getBufferBuilders().getEntityVertexConsumers();
         float f1 = this.alphaSetting.getFloatValue();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, f1);

         try {
            for (PlayerEntity playerentity1 : list) {
               if (playerentity1 != playerentity && playerentity1.isInvisible()) {
                  double d3 = playerentity1.prevX + (playerentity1.getX() - playerentity1.prevX) * f - d0;
                  double d4 = playerentity1.prevY + (playerentity1.getY() - playerentity1.prevY) * f - d1;
                  double d5 = playerentity1.prevZ + (playerentity1.getZ() - playerentity1.prevZ) * f - d2;
                  matrixstack.push();
                  matrixstack.translate(d3, d4, d5);
                  EntityRenderer entityrenderer = entityrenderdispatcher.getRenderer(playerentity1);
                  EntityRenderState entityrenderstate = entityrenderer.createRenderState();
                  entityrenderer.updateRenderState(playerentity1, entityrenderstate, f);
                  entityrenderstate.invisible = false;
                  entityrenderer.render(entityrenderstate, matrixstack, immediate, 15728880);
                  matrixstack.pop();
               }
            }

            immediate.draw();
         } finally {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();
         }
      }
   }
}
