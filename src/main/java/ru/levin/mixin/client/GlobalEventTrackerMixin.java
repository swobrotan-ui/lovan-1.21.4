package ru.levin.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.levin.manager.IMinecraft;
import ru.levin.playertracker.TrackerManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Перехватывает входящие S2C-пакеты глобальных звуков и мировых событий.
 *
 * <p>Это и есть "эксплоит": сервер рассылает такие пакеты с точными X/Y/Z
 * источника на весь мир (и на огромный радиус), даже если игрок-инициатор
 * за тысячи блоков или в другом измерении. Сам пакет не содержит ника, поэтому
 * мы фиксируем координаты + измерение (текущий мир клиента на момент приёма) +
 * список игроков в сети, и складываем в {@link TrackerManager}.</p>
 */
@Environment(EnvType.CLIENT)
@Mixin(ClientPlayNetworkHandler.class)
public abstract class GlobalEventTrackerMixin implements IMinecraft {

    /** Глобальные звуки, выдающие координаты действий игроков. */
    @Inject(method = "onPlaySound", at = @At("HEAD"))
    private void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        String id = soundId(packet);
        if (id.isEmpty() || !isGlobalEventInterestingSound(id)) return;

        RegistryKey<World> dim = currentDimension();
        if (dim == null) return;

        TrackerManager.get().addSound(id, packet.getX(), packet.getY(), packet.getZ(), dim, onlineNames());
    }

    /** Мировые эффекты: порталы, активация ока Края, спавн Дракона и т.п. */
    @Inject(method = "onWorldEvent", at = @At("HEAD"))
    private void onWorldEvent(WorldEventS2CPacket packet, CallbackInfo ci) {
        int eventId = packet.getEventId();
        if (!isInterestingWorldEvent(eventId)) return;

        RegistryKey<World> dim = currentDimension();
        if (dim == null) return;

        var pos = packet.getPos();
        TrackerManager.get().addWorld(eventId, pos.getX(), pos.getY(), pos.getZ(), dim, onlineNames());
    }

    private static String soundId(PlaySoundS2CPacket packet) {
        var key = packet.getSound().getKey();
        return key.isPresent() ? key.get().getValue().toString().toLowerCase() : "";
    }

    private static RegistryKey<World> currentDimension() {
        return mc.world != null ? mc.world.getRegistryKey() : null;
    }

    /** Никнеймы всех игроков, находящихся сейчас в сети (для слабой привязки по времени). */
    private static Collection<String> onlineNames() {
        List<String> names = new ArrayList<>();
        if (mc.player == null) return names;
        for (PlayerListEntry entry : mc.player.networkHandler.getPlayerList()) {
            names.add(entry.getProfile().getName());
        }
        return names;
    }

    /** Звуки, по которым реально можно отследить активность игрока. */
    private static boolean isGlobalEventInterestingSound(String id) {
        return id.contains("thunder")        // молния
                || id.contains("wither")     // спавн Визера
                || id.contains("dragon")     // Дракон Края
                || id.contains("explode")    // взрывы (в т.ч. эндер-кристаллов)
                || id.contains("ender_crystal")
                || id.contains("portal")      // порталы
                || id.contains("eye")         // око Края
                || id.contains("firework");
    }

    /** WorldEvent-идентификаторы, связанные с перемещением/действиями игроков. */
    private static boolean isInterestingWorldEvent(int id) {
        return switch (id) {
            // TRAVEL_THROUGH_PORTAL, END_PORTAL_OPENED, END_PORTAL_FRAME_FILLED (око Края),
            // EYE_OF_ENDER_BREAKS, END_GATEWAY_SPAWNS, ENDER_DRAGON_RESURRECTED,
            // ENDER_DRAGON_DIES, ENDER_DRAGON_BREAKS_BLOCK, WITHER_SPAWNS
            case 1032, 1038, 1503, 2003, 3000, 3001, 1028, 2008, 1023 -> true;
            default -> false;
        };
    }
}
