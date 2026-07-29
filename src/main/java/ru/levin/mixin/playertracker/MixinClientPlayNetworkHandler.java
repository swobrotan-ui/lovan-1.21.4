package ru.levin.mixin.playertracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.playertracker.PlayerDimensionTracker;
import ru.levin.playertracker.PlayerMovementTracker;

import java.util.UUID;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "onEntityPosition", at = @At("HEAD"))
    private void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Entity entity = client.world.getEntityById(packet.entityId());
        if (!(entity instanceof PlayerEntity player)) return;

        PlayerMovementTracker.update(player.getUuid(), packet.change().position());
        PlayerDimensionTracker.get().setDimension(player.getUuid(), client.world.getRegistryKey());
    }

    @Inject(method = "onEntityPositionSync", at = @At("HEAD"))
    private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Entity entity = client.world.getEntityById(packet.id());
        if (!(entity instanceof PlayerEntity player)) return;

        PlayerMovementTracker.update(player.getUuid(), packet.values().position());
        PlayerDimensionTracker.get().setDimension(player.getUuid(), client.world.getRegistryKey());
    }
}
