package ru.levin.playertracker;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Центральное хранилище "глобальных аномалий".
 *
 * <p>Сервер не шлёт координаты сущностей далеко находящихся игроков, но
 * транслирует глобальные звуковые и мировые события (гром, спавн Визера/Дракона,
 * взрывы эндер-кристаллов, порталы, активация ока Края) с ТОЧНЫМИ X/Y/Z источника
 * на весь мир / измерение. Миксин перехватывает эти пакеты и складывает сюда
 * координаты + измерение + время + список игроков, бывших в сети в этот момент.</p>
 *
 * <p>Прямой привязки "ник -> координаты" в пакете нет, поэтому менеджер либо
 * возвращает события, где нужный ник был в сети (сверка по времени), либо весь
 * список последних глобальных координат-аномалий.</p>
 */
public final class TrackerManager {

    private static final TrackerManager INSTANCE = new TrackerManager();

    public static TrackerManager get() {
        return INSTANCE;
    }

    /** Храним последние события; старые вытесняются. */
    private final List<TrackedEvent> events = new ArrayList<>();
    private static final int MAX_EVENTS = 300;
    private final Map<String, UUID> nameToUuid = new HashMap<>();

    private TrackerManager() {}

    public void addSound(String soundId, double x, double y, double z,
                         RegistryKey<World> dimension, Collection<String> onlineAtEvent) {
        add(EventType.SOUND, soundId, (int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z), dimension, onlineAtEvent);
    }

    public void addWorld(int eventId, int x, int y, int z,
                         RegistryKey<World> dimension, Collection<String> onlineAtEvent) {
        add(EventType.WORLD, "world_event#" + eventId, x, y, z, dimension, onlineAtEvent);
    }

    private void add(EventType type, String id, int x, int y, int z,
                     RegistryKey<World> dimension, Collection<String> onlineAtEvent) {
        synchronized (events) {
            events.add(new TrackedEvent(type, id, x, y, z, dimension, System.currentTimeMillis(), onlineAtEvent));
            while (events.size() > MAX_EVENTS) {
                events.remove(0);
            }
        }
    }

    /** Последние n событий (от новых к старым). */
    public List<TrackedEvent> getRecent(int n) {
        synchronized (events) {
            List<TrackedEvent> copy = new ArrayList<>(events);
            Collections.reverse(copy);
            return copy.subList(0, Math.min(n, copy.size()));
        }
    }

    /** События, в момент которых указанный ник был в сети (слабая, но рабочая привязка). */
    public List<TrackedEvent> getForPlayer(String nick) {
        String lower = nick.toLowerCase();
        synchronized (events) {
            List<TrackedEvent> result = new ArrayList<>();
            for (TrackedEvent e : events) {
                for (String p : e.onlinePlayers) {
                    if (p.toLowerCase().equals(lower)) {
                        result.add(e);
                        break;
                    }
                }
            }
            Collections.reverse(result);
            return result;
        }
    }

    public boolean isEmpty() {
        synchronized (events) {
            return events.isEmpty();
        }
    }

    /** Тип зафиксированного события. */
    public enum EventType { SOUND, WORLD }

    /** Одна зафиксированная глобальная "аномалия" с координатами. */
    public static final class TrackedEvent {
        public final EventType type;
        public final String id;          // звук (id) или "world_event#<id>"
        public final int x, y, z;
        public final RegistryKey<World> dimension;
        public final long timestamp;
        public final Set<String> onlinePlayers; // кто был в сети в момент события

        TrackedEvent(EventType type, String id, int x, int y, int z,
                     RegistryKey<World> dimension, long timestamp, Collection<String> onlineAtEvent) {
            this.type = type;
            this.id = id;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
            this.timestamp = timestamp;
            this.onlinePlayers = new HashSet<>(onlineAtEvent);
        }
    }

    /** Человекочитаемое название измерения. */
    public static String dimensionName(RegistryKey<World> key) {
        if (key == World.OVERWORLD) return "Обычный мир";
        if (key == World.NETHER) return "Незер (Ад)";
        if (key == World.END) return "Энд";
        String path = key.getValue().getPath().toLowerCase();
        if (path.contains("overworld") || path.contains("surface") || path.contains("normal")) return "Обычный мир";
        if (path.contains("nether") || path.contains("hell")) return "Незер (Ад)";
        if (path.contains("end") || path.contains("sky") || path.contains("dragon")) return "Энд";
        return key.getValue().toString();
    }
}
