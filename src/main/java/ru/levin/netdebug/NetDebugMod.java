package ru.levin.netdebug;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetDebugMod implements ModInitializer {
    public static final String MOD_ID = "netdebug";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[NetDebug] initialized");
    }
}
