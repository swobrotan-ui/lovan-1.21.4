package ru.levin.netdebug;

import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

/**
 * Запись аудита сетевого трафика. Хранит идентификатор субъекта (UUID),
 * измерение и координаты блока, извлечённые из пакета.
 *
 * <p>Сервер не обязан рассылать координаты явно, поэтому координаты
 * фиксируются только в привязке к измерению игрока на момент приёма
 * {@link PlayerListS2CPacket} — это позволяет построить гипотезу о
 * том, в каком мире субъект присутствовал при обновлении данных.</p>
 */
public record AuditData(String uuid,
                        String nickname,
                        RegistryKey<World> dimension,
                        BlockPos position) {

    public String getDimensionName() {
        if (dimension.equals(World.OVERWORLD)) return "Overworld";
        if (dimension.equals(World.NETHER)) return "Nether";
        if (dimension.equals(World.END)) return "End";
        return dimension.getValue().getPath();
    }
}
