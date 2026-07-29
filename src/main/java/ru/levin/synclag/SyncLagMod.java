package ru.levin.synclag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncLagMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("synclag");
    public static boolean enabled = true;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                CommandManager.literal("synclag")
                    .then(CommandManager.literal("on")
                        .executes(context -> {
                            enabled = true;
                            context.getSource().sendFeedback(() -> net.minecraft.text.Text.literal("[synclag] enabled"), false);
                            return 1;
                        })
                    )
                    .then(CommandManager.literal("off")
                        .executes(context -> {
                            enabled = false;
                            context.getSource().sendFeedback(() -> net.minecraft.text.Text.literal("[synclag] disabled"), false);
                            return 1;
                        })
                    )
                    .then(CommandManager.literal("reload")
                        .executes(context -> {
                            enabled = true;
                            context.getSource().sendFeedback(() -> net.minecraft.text.Text.literal("[synclag] reloaded (default: on)"), false);
                            return 1;
                        })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(() -> net.minecraft.text.Text.literal("[synclag] status: " + (enabled ? "on" : "off") + " | usage: /synclag <on|off|reload>"), false);
                        return 1;
                    })
            );
        });

        LOGGER.info("[synclag] server-side sync lag emulation initialized");
    }
}
