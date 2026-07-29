package ru.levin.mixin.chat;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.events.Event;
import ru.levin.events.impl.player.EventChat;
import ru.levin.events.impl.player.EventSendChat;
import ru.levin.manager.ClientManager;
import ru.levin.manager.Manager;
import ru.levin.manager.commandManager.CommandManager;


@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPlayNetworkHandler {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(@NotNull String message, CallbackInfo ci) {
        EventSendChat sendChatEvent = new EventSendChat(message);
        Event.call(sendChatEvent);
        if (sendChatEvent.isCancel()) {
            ci.cancel();
            return;
        }
        if (!ClientManager.legitMode) {
            CommandManager commandManager = Manager.COMMAND_MANAGER;
            if (message.startsWith(commandManager.getPrefix())) {
                try {
                    commandManager.getDispatcher().execute(message.substring(commandManager.getPrefix().length()), commandManager.getSource());
                } catch (CommandSyntaxException ignored) {
                }
                ci.cancel();
            }
        }
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"))
    private void onGameMessageHook(GameMessageS2CPacket packet, CallbackInfo ci) {
        Text message = packet.content();
        boolean overlay = packet.overlay();
        Event.call(new EventChat(message, overlay));
    }
}