package ru.levin.x2demo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * УЧЕБНЫЙ пакет "клиент -> сервер" (C2S).
 *
 * Имитирует запрос на изменение количества предмета в слоте инвентаря.
 * Сервер НЕ меняет реальные предметы — только логирует запрос и отвечает подтверждением.
 *
 * Это демонстрация сетевого обмена для анализа протоколов, а НЕ настоящая дупликация.
 */
public record X2RequestC2SPacket(int slot, int count) implements CustomPayload {

    // Идентификатор канала. Namespace "lovmod" произвольный, но должен совпадать на клиенте и сервере.
    public static final CustomPayload.Id<X2RequestC2SPacket> ID =
            new CustomPayload.Id<>(Identifier.of("lovmod", "x2_request"));

    // Кодек (сериализация/десериализация байтов). Регистрируется в PayloadTypeRegistry.
    // encoder: (буфер, пакет) -> записываем поля
    // decoder: (буфер) -> читаем поля и собираем пакет
    // В 1.21.4 используется фабрика CustomPayload.codecOf (аналог StreamCodec.of).
    public static final PacketCodec<RegistryByteBuf, X2RequestC2SPacket> CODEC =
            CustomPayload.codecOf(
                    (packet, buf) -> {
                        buf.writeInt(packet.slot());
                        buf.writeInt(packet.count());
                    },
                    (buf) -> new X2RequestC2SPacket(
                            buf.readInt(),
                            buf.readInt()
                    )
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
