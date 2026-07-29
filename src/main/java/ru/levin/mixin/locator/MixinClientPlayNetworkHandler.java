package ru.levin.mixin.locator;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.manager.locator.LocatorManager;
import ru.levin.playertracker.TrackerManager;

import java.util.Locale;

@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {

    @Inject(method = "onGameMessage(Lnet/minecraft/network/packet/s2c/play/GameMessageS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text content = packet.content();
        if (content == null) return;

        String text = content.getString();
        LocatorManager manager = LocatorManager.get();

        if (manager.isScanning() && manager.isNearResponse(text)) {
            manager.parseResponse(text);
            ci.cancel();
        }
    }

    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"))
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        double soundX = packet.getX();
        double soundY = packet.getY();
        double soundZ = packet.getZ();

        double dx = client.player.getX() - soundX;
        double dy = client.player.getY() - soundY;
        double dz = client.player.getZ() - soundZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq <= 4096.0) return;

        String soundId = packet.getSound().value().id().toString();
        if (!isLocatorInterestingSound(soundId)) return;

        int x = (int) Math.floor(soundX);
        int y = (int) Math.floor(soundY);
        int z = (int) Math.floor(soundZ);

        TrackerManager.get().addSound(soundId, x, y, z,
                client.world.getRegistryKey(),
                java.util.Collections.emptyList());
    }

    private boolean isLocatorInterestingSound(String id) {
        String lower = id.toLowerCase(Locale.ROOT);
        if (lower.contains("player")) return true;
        if (lower.contains("chest")) return true;
        if (lower.contains("block")) return true;
        if (lower.contains("combat")) return true;
        if (lower.contains("hit")) return true;
        if (lower.contains("step")) return true;
        if (lower.contains("entity")) return true;
        return false;
    }
}
