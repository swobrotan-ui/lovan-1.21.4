package ru.levin.netdebug;

import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Глобальный трекер аудита сетевых пакетов.
 *
 * <p>Собирает карту субъектов по их UUID.
 * Данные пополняются при перехвате {@link net.minecraft.network.packet.s2c.play.PlayerListS2CPacket}
 * (метаданные: UUID, ник, измерение) и пакетов сущностей
 * {@link net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket},
 * {@link net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket}
 * (реальные координаты игроков в зоне загрузки).
 * Ключевое назначение — предоставить быстрый look-up по UUID/никнейму
 * для формирования отчёта безопасности.</p>
 */
public final class NetworkDataLogger {

    private static final NetworkDataLogger INSTANCE = new NetworkDataLogger();

    private final Map<UUID, AuditData> auditMap = new HashMap<>();

    private NetworkDataLogger() {}

    public static NetworkDataLogger get() {
        return INSTANCE;
    }

    /**
     * Регистрирует субъекта при приёме {@code PlayerListS2CPacket}.
     *
     * <p>Пакет не содержит координат — позиция инициализируется как
     * {@link BlockPos#ORIGIN}. Реальные координаты обновляются
     * через {@link #updatePlayerPosition} при получении пакетов
     * сущностей ({@code EntitySpawnS2CPacket},
     * {@code EntityPositionSyncS2CPacket}).</p>
     */
    public void auditPlayer(String uuid,
                            String nickname,
                            RegistryKey<World> dimension) {
        UUID id = UUID.fromString(uuid);
        AuditData existing = auditMap.get(id);
        if (existing != null && !existing.uuid().equals(uuid)) {
            return;
        }
        AuditData newRecord = new AuditData(uuid, nickname, dimension, BlockPos.ORIGIN);
        auditMap.put(id, newRecord);
    }

    /**
     * Обновляет позицию субъекта по его UUID.
     *
     * <p>Вызывается при перехвате пакетов сущностей, содержащих
     * абсолютные координаты игрока ({@code EntitySpawnS2CPacket},
     * {@code EntityPositionSyncS2CPacket}).</p>
     */
    public void updatePlayerPosition(UUID uuid, BlockPos position) {
        AuditData existing = auditMap.get(uuid);
        if (existing != null) {
            AuditData updated = new AuditData(
                    existing.uuid(),
                    existing.nickname(),
                    existing.dimension(),
                    position
            );
            auditMap.put(uuid, updated);
        }
    }

    /**
     * Регистрирует координату чанка из {@code CommandSuggestionsS2CPacket}.
     *
     * <p>Пакет автодополнения команд не содержит координат напрямую,
     * но сервер иногда транслирует их в строковых предложениях.
     * При успешном извлечении координат чанка, метод дополняет запись
     * уже известного субъекта более точными данными.</p>
     */
public void auditCommandSuggestion(UUID uuid,
                                         int chunkX,
                                         int chunkZ,
                                         BlockPos parsedPos) {
          AuditData existing = auditMap.get(uuid);
          if (existing == null) {
              return;
          }
          if (parsedPos.equals(BlockPos.ORIGIN) && !existing.position().equals(BlockPos.ORIGIN)) {
              return;
          }
          BlockPos refined = BlockPos.ofFloored(chunkX * 16, existing.position().getY(), chunkZ * 16);
          AuditData updated = new AuditData(
                  existing.uuid(),
                  existing.nickname(),
                  existing.dimension(),
                  refined
          );
          auditMap.put(uuid, updated);
      }

    public Optional<AuditData> getAuditData(UUID uuid) {
        return Optional.ofNullable(auditMap.get(uuid));
    }

    /**
     * Ищет запись по никнейму (пробегает все значения).
     */
    public Optional<AuditData> getAuditDataByNickname(String nickname) {
        return auditMap.values().stream()
                .filter(data -> data.nickname().equalsIgnoreCase(nickname))
                .findFirst();
    }

    public boolean hasData() {
        return !auditMap.isEmpty();
    }

    public void clear() {
        auditMap.clear();
    }
}
