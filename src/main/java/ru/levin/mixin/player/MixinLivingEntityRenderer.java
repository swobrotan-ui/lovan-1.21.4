package ru.levin.mixin.player;

import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventPlayerRender;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.Manager;
import ru.levin.modules.render.HitColor;
import ru.levin.modules.render.SeeInvisible;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState> implements IMinecraft {
    @Unique
    private float originalPrevHeadYaw, originalHeadYaw, originalPrevHeadPitch, originalHeadPitch, originalBodyYaw, originalPrevBodyYaw;
    @Unique
    private boolean replaced;

    @Unique
    private static final Map<LivingEntityRenderState, LivingEntity> STATE_TO_ENTITY = new WeakHashMap<>();

    @Unique
    private boolean seeInvisibleApplied = false;
    @Unique
    private boolean hitColorApplied = false;

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("HEAD"))
    private void onUpdateRenderStatePre(T livingEntity, S state, float tickDelta, CallbackInfo ci) {
        if (mc == null || mc.player == null || livingEntity != mc.player) return;

        if (mc.currentScreen instanceof InventoryScreen) return;

        EventPlayerRender playerRender = new EventPlayerRender(livingEntity);
        Event.call(playerRender);

        originalPrevHeadYaw = livingEntity.prevHeadYaw;
        originalHeadYaw = livingEntity.headYaw;
        originalPrevHeadPitch = livingEntity.prevPitch;
        originalHeadPitch = livingEntity.getPitch();
        originalBodyYaw = livingEntity.bodyYaw;
        originalPrevBodyYaw = livingEntity.prevBodyYaw;

        livingEntity.prevHeadYaw = playerRender.getPrevYaw();
        livingEntity.headYaw = playerRender.getYaw();
        livingEntity.prevPitch = playerRender.getPrevPitch();
        livingEntity.setPitch(playerRender.getPitch());
        livingEntity.prevBodyYaw = playerRender.getPrevBodyYaw();
        livingEntity.bodyYaw = playerRender.getBodyYaw();

        replaced = true;
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderHitColorPre(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (mc == null || mc.player == null) return;
        LivingEntity ent = STATE_TO_ENTITY.get(state);
        if (ent == null) return;

        SeeInvisible seeInvisible = Manager.FUNCTION_MANAGER.seeInvisible;
        if (seeInvisible != null && seeInvisible.shouldRenderInvisible(ent)) {
            float a = seeInvisible.getOpacity();
            RenderSystem.setShaderColor(1f, 1f, 1f, a);
            seeInvisibleApplied = true;
        }

        HitColor hitColor = Manager.FUNCTION_MANAGER.hitColor;
        if (hitColor != null && hitColor.state && HitColor.isHit(ent)) {
            float intensity = HitColor.getIntensity();
            RenderSystem.setShaderColor(HitColor.getHitRed(), HitColor.getHitGreen(), HitColor.getHitBlue(), 1f);
            hitColorApplied = true;
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("TAIL"))
    private void onRenderHitColorPost(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (seeInvisibleApplied) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        seeInvisibleApplied = false;

        if (hitColorApplied) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        hitColorApplied = false;
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    private void onUpdateRenderStatePost(T livingEntity, S state, float tickDelta, CallbackInfo ci) {
        if (mc != null && mc.player != null && livingEntity != null) {
            STATE_TO_ENTITY.put(state, livingEntity);
        }
        if (!replaced || mc == null || mc.player == null || livingEntity != mc.player) return;

        livingEntity.prevHeadYaw = originalPrevHeadYaw;
        livingEntity.headYaw = originalHeadYaw;
        livingEntity.prevPitch = originalPrevHeadPitch;
        livingEntity.setPitch(originalHeadPitch);
        livingEntity.prevBodyYaw = originalPrevBodyYaw;
        livingEntity.bodyYaw = originalBodyYaw;

        replaced = false;
    }
}
