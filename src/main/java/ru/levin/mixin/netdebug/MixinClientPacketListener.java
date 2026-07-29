package ru.levin.mixin.netdebug;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.netdebug.NetworkDataLogger;
import ru.levin.manager.IMinecraft;

import java.util.List;

/**
 * Перехватывает входящие и исходящие сетевые пакеты для
 * глобального аудита утечек данных о местоположении игроков.
 *
 * <p>Цель: обнаружение косвенных каналов утечки координат через
 * стандартные S2C-пакеты:</p>
 * <ul>
 *   <li>{@link PlayerListS2CPacket} — фиксирует UUID и ник при обновлении списка игроков,
 *       привязанные к измерению и положению локального игрока.</li>
 *   <li>{@link CommandSuggestionsS2CPacket} — ответ сервера на запрос автодополнения команд;
 *       извлекает метаданные координат чанка из текстовых предложений.</li>
 * </ul>
 *
 * <p>Также имитирует периодическую отправку {@link RequestCommandCompletionsC2SPacket}
 * для проверки валидности инкапсуляции координат сервером.</p>
 */
@Mixin(ClientPlayNetworkHandler.class)
public class MixinClientPacketListener implements IMinecraft {

    private int tickCounter;

    /**
     * При обновлении списка игроков извлекаем UUID, никнейм
     * и привязываем к измерению + приблизительным координатам
     * локального игрока.
     *
     * <p>Этот пакет сервер рассылает при входе игрока, смене измерения
     * и других событиях. Он является первичным источником информации
     * о присутствии субъектов в определённом мире.</p>
     */
    @Inject(method = "onPlayerList(Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket;)V",
            at = @At("HEAD"))
    private void onPlayerList(PlayerListS2CPacket packet, CallbackInfo ci) {
        if (mc.world == null || mc.player == null) return;

RegistryKey<World> dimension = mc.world.getRegistryKey();

        packet.getEntries().forEach(entry -> {
            String uuid = entry.profileId() != null ? entry.profileId().toString() : null;
            String name = entry.profile() != null ? entry.profile().getName() : null;

            if (uuid == null || name == null || name.isEmpty()) return;

            NetworkDataLogger.get().auditPlayer(uuid, name, dimension);
        });
    }

/**
       * Перехватывает ответ сервера на запрос автодополнения команд.
       *
       * <p>Сервер может включать в текстовые предложения координаты
       * чанков (например, в дебаг-режиме или через кастомные плагины).
       * Поддерживаются форматы: {@code X: ... Y: ... Z: ...},
       * {@code world x y z}, {@code x, y, z}, {@code (x y z)},
       * а также {@code [x y z]}. При успешном извлечении координат,
       * запись аудита обновляется более точными данными.</p>
       */
@Inject(method = "onCommandSuggestions(Lnet/minecraft/network/packet/s2c/play/CommandSuggestionsS2CPacket;)V",
              at = @At("HEAD"))
      private void onCommandSuggestions(CommandSuggestionsS2CPacket packet, CallbackInfo ci) {
          if (mc.world == null || mc.player == null) return;

          com.mojang.brigadier.suggestion.Suggestions suggestions = packet.getSuggestions();
          if (suggestions == null || suggestions.getList() == null) return;

          System.out.println("[netdebug] command suggestions received: " + suggestions.getList().size());

          List<PlayerListEntry> onlinePlayers = mc.player.networkHandler.getPlayerList().stream().toList();
          if (onlinePlayers.isEmpty()) return;

          for (com.mojang.brigadier.suggestion.Suggestion suggestion : suggestions.getList()) {
              String raw = suggestion.getText();
              if (raw == null) continue;

              System.out.println("[netdebug] suggestion: " + raw);

              int[] coords = parseCoordinates(raw);
              if (coords == null) continue;

              int x = coords[0];
              int y = coords[1];
              int z = coords[2];

              BlockPos parsedPos = BlockPos.ofFloored(x, y, z);

              PlayerListEntry closest = findClosestPlayer(onlinePlayers);
              if (closest != null) {
                  NetworkDataLogger.get().auditCommandSuggestion(closest.getProfile().getId(), x >> 4, z >> 4, parsedPos);
              }
          }
      }

      private static int[] parseCoordinates(String raw) {
          java.util.regex.Matcher matcher;

          matcher = java.util.regex.Pattern.compile("X:\\s*(-?\\d+)\\s*Y:\\s*(-?\\d+)\\s*Z:\\s*(-?\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE).matcher(raw);
          if (matcher.find()) {
              return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))};
          }

          matcher = java.util.regex.Pattern.compile("world\\s*[,:]?\\s*(-?\\d+)\\s*[,:]?\\s*(-?\\d+)\\s*[,:]?\\s*(-?\\d+)").matcher(raw);
          if (matcher.find()) {
              return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))};
          }

          matcher = java.util.regex.Pattern.compile("(?<![\\w.])(-?\\d+)\\s*,\\s*(-?\\d+)\\s*,\\s*(-?\\d+)(?![\\w.])").matcher(raw);
          if (matcher.find()) {
              return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))};
          }

          matcher = java.util.regex.Pattern.compile("[\\[({]\\s*(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)\\s*[\\])}]").matcher(raw);
          if (matcher.find()) {
              return new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3))};
          }

          return null;
      }

    /**
     * Каждые 200 тиков (≈10 сек) отправляет {@link RequestCommandCompletionsC2SPacket}
     * для инициации ответа сервера с автодополнением команд.
     *
     * <p>Это粟μляет поведение клиента при вводе команды для проверки
     * валидности инкапсуляции координат в ответе сервера.</p>
     */
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        tickCounter++;
        if (tickCounter < 200 || mc.player == null || mc.getNetworkHandler() == null) return;

        tickCounter = 0;
        ClientPlayNetworkHandler handler = (ClientPlayNetworkHandler)(Object) this;
        handler.getConnection().send(
                new RequestCommandCompletionsC2SPacket(0, "/tp ")
        );
    }

    private static PlayerListEntry findClosestPlayer(List<PlayerListEntry> players) {
        PlayerEntity local = mc.player;
        if (local == null || mc.world == null) return null;

        PlayerListEntry closest = null;
        double minDist = Double.MAX_VALUE;

        for (PlayerListEntry entry : players) {
            PlayerEntity entity = null;
            for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
                if (player.getUuid().equals(entry.getProfile().getId())) {
                    entity = player;
                    break;
                }
            }
            if (entity == null || entity == local) continue;

            double dist = entity.squaredDistanceTo(local);
            if (dist < minDist) {
                minDist = dist;
                closest = entry;
            }
        }
        return closest;
    }
}
