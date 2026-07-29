package ru.levin.x2demo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.levin.manager.ClientManager;
import ru.levin.x2demo.network.X2AckS2CPacket;
import ru.levin.x2demo.network.X2RequestC2SPacket;

/**
 * Точка входа мода (ModInitializer). Регистрирует учебные сетевые пакеты x2
 * и их обработчики на стороне клиента и сервера.
 *
 * Мод имеет environment "*", поэтому исполняется и на клиенте, и на сервере
 * (в том числе на встроенном сервере одиночной игры / LAN, что удобно для демо).
 */
public class X2DemoMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("x2demo");

    @Override
    public void onInitialize() {
        // 1) Регистрируем кодеки обоих пакетов НА ОБЕИХ сторонах.
        //    playC2S  — канал "клиент -> сервер"
        //    playS2C  — канал "сервер -> клиент"
        PayloadTypeRegistry.playC2S().register(X2RequestC2SPacket.ID, X2RequestC2SPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(X2AckS2CPacket.ID, X2AckS2CPacket.CODEC);

        // 2) Клиентская сторона: обработчик подтверждения (ack) от сервера.
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayNetworking.registerGlobalReceiver(X2AckS2CPacket.ID, (packet, context) -> {
                MinecraftClient client = context.client();
                // Любые действия с клиентом выполняем в основном потоке игры (render thread).
                client.execute(() -> {
                    LOGGER.info("[x2] CLIENT: получено подтверждение сервера: success={}, slot={}, count={}",
                            packet.success(), packet.slot(), packet.requestedCount());
                    ClientManager.message("[x2demo] Сервер получил запрос (демонстрация): слот " + packet.slot());
                });
            });
        }

        // 3) Серверная сторона: ловим C2S-запрос и отвечаем подтверждением.
        //    ServerLifecycleEvents.SERVER_STARTING срабатывает и на выделенном, и на встроенном сервере,
        //    поэтому демо работает даже в одиночной игре / через LAN.
        ServerLifecycleEvents.SERVER_STARTING.register((MinecraftServer server) -> {
            ServerPlayNetworking.registerGlobalReceiver(X2RequestC2SPacket.ID, (packet, context) -> {
                ServerPlayerEntity player = context.player();
                int slot = packet.slot();
                int count = packet.count();

                // УЧЕБНЫЙ ЛОГ: запрос получен, но предметы мы НЕ меняем.
                LOGGER.info("[x2] SERVER: получен запрос на 'x2' от игрока {}: slot={}, count={} " +
                                "(СИМУЛЯЦИЯ — реальных изменений инвентаря нет)",
                        player.getName().getString(), slot, count);

                // Отправляем подтверждение обратно клиенту (в потоке сервера).
                server.execute(() -> ServerPlayNetworking.send(player, new X2AckS2CPacket(true, slot, count)));
            });
        });
    }
}
