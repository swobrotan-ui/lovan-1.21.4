package ru.levin.mixin.display;

import net.minecraft.client.render.*;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements IMinecraft {

    @Shadow protected abstract void renderMain(FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean hasEntitiesToRender, RenderTickCounter renderTickCounter, Profiler profiler);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderMain(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/Camera;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/render/Fog;ZZLnet/minecraft/client/render/RenderTickCounter;Lnet/minecraft/util/profiler/Profiler;)V"))
    private void onRender(WorldRenderer instance, FrameGraphBuilder frameGraphBuilder, Frustum frustum, Camera camera, Matrix4f positionMatrix, Matrix4f projectionMatrix, Fog fog, boolean renderBlockOutline, boolean hasEntitiesToRender, RenderTickCounter renderTickCounter, Profiler profiler) {
        this.renderMain(frameGraphBuilder, frustum, camera, positionMatrix, projectionMatrix, fog, !Manager.FUNCTION_MANAGER.blockHighLight.isState(), hasEntitiesToRender, renderTickCounter, profiler);
    }

    // @Inject(method = "renderBlockBreaking", at = @At("HEAD"), cancellable = true)
    // private void onRenderBlockBreaking(BlockBreakingInfo info, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Camera camera, CallbackInfo ci) {
    //     if (Manager.FUNCTION_MANAGER.noMineAnimation != null && Manager.FUNCTION_MANAGER.noMineAnimation.state && mc.player != null && info.getActorId() != mc.player.getId()) {
    //         ci.cancel();
    //     }
    // }
}
