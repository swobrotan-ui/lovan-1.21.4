package module;

import enum.Category;
import java.util.List;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class PlayerChamsModule extends Module {
   public PlayerChamsModule() {
      super("ПлайерСхамз", "Рендерит игроков сквозь стены", Category.RENDER);
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
      PlayerEntity playerentity = this.getPlayer();
      Vec3d vec3d = playerentity.getPos();
      List list = this.getWorld().getPlayers();
      int i = wu.e(list, playerentity, vec3d, 128.0, false, false, this);
      if (i != 0) {
         MatrixStack matrixstack = worldrendercontext.matrixStack();
         float f = worldrendercontext.tickCounter().getTickDelta(true);
         Vec3d vec3d1 = worldrendercontext.camera().getPos();
         double d0 = vec3d1.x;
         double d1 = vec3d1.y;
         double d2 = vec3d1.z;
         MinecraftClient minecraftclient = this.getClient();
         BufferBuilderStorage bufferbuilderstorage = minecraftclient.getBufferBuilders();
         EntityRenderDispatcher entityrenderdispatcher = minecraftclient.getEntityRenderDispatcher();
         Immediate immediate = bufferbuilderstorage.getEntityVertexConsumers();
         GL11.glDepthRange(0.0, 0.01);

         try {
            for (PlayerEntity playerentity1 : list) {
               if (wu.b(playerentity1, playerentity, vec3d, 128.0, false, false, this)) {
                  double d3 = playerentity1.prevX + (playerentity1.getX() - playerentity1.prevX) * f - d0;
                  double d4 = playerentity1.prevY + (playerentity1.getY() - playerentity1.prevY) * f - d1;
                  double d5 = playerentity1.prevZ + (playerentity1.getZ() - playerentity1.prevZ) * f - d2;
                  matrixstack.push();
                  matrixstack.translate(d3, d4, d5);
                  EntityRenderer entityrenderer = entityrenderdispatcher.getRenderer(playerentity1);
                  EntityRenderState entityrenderstate = entityrenderer.createRenderState();
                  entityrenderer.updateRenderState(playerentity1, entityrenderstate, f);
                  entityrenderer.render(entityrenderstate, matrixstack, immediate, 15728880);
                  matrixstack.pop();
               }
            }

            immediate.draw();
         } finally {
            GL11.glDepthRange(0.0, 1.0);
         }
      }
   }
}
