package ru.levin.mixin.playertracker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.playertracker.*;

import java.util.*;

@Mixin(ClientPlayNetworkHandler.class)
public class AdvancedTrackerMixin {

    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"))
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        trackAmbientSound(packet.getX(), packet.getY(), packet.getZ(), packet.getSound(), null);
    }

    @Inject(method = "onPlaySoundFromEntity(Lnet/minecraft/network/packet/s2c/play/PlaySoundFromEntityS2CPacket;)V", at = @At("HEAD"))
    private void onPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        int entityId = packet.getEntityId();
        Entity entity = client.world.getEntityById(entityId);
        if (entity == null) return;

        trackAmbientSound(entity.getX(), entity.getY(), entity.getZ(), packet.getSound(), entityId);
    }

    @Inject(method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V", at = @At("HEAD"))
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        double x = packet.getX();
        double y = packet.getY();
        double z = packet.getZ();

        RegistryKey<World> dimension = client.world.getRegistryKey();
        Set<String> online = snapshotOnline();
        String entityType = packet.getEntityType().toString();

        Entity entity = client.world.getEntityById(packet.getEntityId());
        if (entity instanceof PlayerEntity player && client.player.getId() != player.getId()) {
            PlayerMovementTracker.update(player.getUuid(), x, y, z);
            PlayerDimensionTracker.get().setDimension(player.getUuid(), dimension);
        }

        String soundId = "spawn." + entityType;
        TrackerManager.get().addSound(soundId, x, y, z, dimension, online);
    }

    @Inject(method = "onEntityPosition", at = @At("HEAD"))
    private void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Entity entity = client.world.getEntityById(packet.entityId());
        if (!(entity instanceof PlayerEntity player)) return;

        Vec3d pos = packet.change().position();
        PlayerMovementTracker.update(player.getUuid(), pos);
        PlayerDimensionTracker.get().setDimension(player.getUuid(), client.world.getRegistryKey());
    }

    @Inject(method = "onEntityPositionSync", at = @At("HEAD"))
    private void onEntityPositionSync(EntityPositionSyncS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        Entity entity = client.world.getEntityById(packet.id());
        if (!(entity instanceof PlayerEntity player)) return;

        Vec3d pos = packet.values().position();
        PlayerMovementTracker.update(player.getUuid(), pos);
        PlayerDimensionTracker.get().setDimension(player.getUuid(), client.world.getRegistryKey());
    }

    @Inject(method = "onWorldEvent", at = @At("HEAD"))
    private void onWorldEvent(WorldEventS2CPacket packet, CallbackInfo ci) {
        int eventId = packet.getEventId();
        if (!isInterestingWorldEvent(eventId)) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        BlockPos pos = packet.getPos();
        TrackerManager.get().addWorld(eventId, pos.getX(), pos.getY(), pos.getZ(),
                client.world.getRegistryKey(), snapshotOnline());
    }

    private void trackAmbientSound(double x, double y, double z, RegistryEntry<SoundEvent> entry, Integer boundEntityId) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        if (entry == null || entry.value() == null) return;
        String soundId = entry.value().id().toString();
        if (soundId.isEmpty()) return;

        RegistryKey<World> dimension = client.world.getRegistryKey();
        Set<String> online = snapshotOnline();

        if (boundEntityId != null && boundEntityId != -1) {
            Entity entity = client.world.getEntityById(boundEntityId);
            if (entity instanceof PlayerEntity player && client.player.getId() != player.getId()) {
                PlayerMovementTracker.update(player.getUuid(), x, y, z);
                PlayerDimensionTracker.get().setDimension(player.getUuid(), dimension);
            }
        }

        TrackerManager.get().addSound(soundId, x, y, z, dimension, online);
    }

    private static boolean isInterestingWorldEvent(int id) {
        return switch (id) {
            case 1032, 1038, 1503, 2003, 3000, 3001, 1028, 2008, 1023, 1033, 1044, 1045, 1046 -> true;
            default -> false;
        };
    }

    private Set<String> snapshotOnline() {
        MinecraftClient client = MinecraftClient.getInstance();
        Set<String> set = new HashSet<>();
        if (client.player != null && client.getNetworkHandler() != null) {
            for (var entry : client.getNetworkHandler().getPlayerList()) {
                set.add(entry.getProfile().getName());
            }
        }
        return set;
    }
}
