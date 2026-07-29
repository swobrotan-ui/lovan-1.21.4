package ru.levin.mixin.locator;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.manager.locator.TimingScanner;

/**
 * Перехват входящих ответов автозаполнения (CommandSuggestionsS2CPacket).
 * Замеряет время отклика по transactionId и глушит технический пакет,
 * чтобы игрок не видел всплывающих подсказок.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class MixinGlobalLocatorNetworkHandler {

    @Inject(method = "onCommandSuggestions(Lnet/minecraft/network/packet/s2c/play/CommandSuggestionsS2CPacket;)V",
            at = @At("HEAD"), cancellable = true)
    private void onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
        TimingScanner scanner = TimingScanner.get();
        if (!scanner.isScanning()) return;

        int id = packet.id();
        if (scanner.isTracked(id)) {
            scanner.recordResponse(id);
            ci.cancel(); // скрываем технический ответ сервера
        }
    }
}
