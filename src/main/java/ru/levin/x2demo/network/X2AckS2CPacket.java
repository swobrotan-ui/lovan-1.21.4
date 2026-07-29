package ru.levin.x2demo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * УЧЕБНЫЙ пакет "сервер -> клиент" (S2C).
 *
 * Сервер присылает подтверждение, что запрос получен. Никаких реальных изменений инвентаря
 * этот пакет не производит — это только ответная квитанция (acknowledgement).
 */
public record X2AckS2CPacket(boolean success, int slot, int requestedCount) implements CustomPayload {

    public static final CustomPayload.Id<X2AckS2CPacket> ID =
            new CustomPayload.Id<>(Identifier.of("lovmod", "x2_ack"));

    public static final PacketCodec<RegistryByteBuf, X2AckS2CPacket> CODEC =
            CustomPayload.codecOf(
                    (packet, buf) -> {
                        buf.writeBoolean(packet.success());
                        buf.writeInt(packet.slot());
                        buf.writeInt(packet.requestedCount());
                    },
                    (buf) -> new X2AckS2CPacket(
                            buf.readBoolean(),
                            buf.readInt(),
                            buf.readInt()
                    )
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
