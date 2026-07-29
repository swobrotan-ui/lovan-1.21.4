package ru.levin.mixin.player;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.events.Event;
import ru.levin.events.impl.move.EventMotion;
import ru.levin.manager.IMinecraft;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntityAimAssist implements IMinecraft {

    @Unique
    private float preYaw;
    @Unique
    private float prePitch;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPacketsHead(CallbackInfo ci) {
        EventMotion event = new EventMotion(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.getYaw(), mc.player.getPitch(), mc.player.isOnGround());
        Event.call(event);

        if (event.isCancel()) {
            ci.cancel();
            return;
        }
        preYaw = event.getYaw();
        prePitch = event.getPitch();
    }
}
