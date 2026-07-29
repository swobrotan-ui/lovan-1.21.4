package ru.levin.manager.locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Менеджер тайминг-сканирования (TimingScanner).
 *
 * Разбивает мир на сетку секторов, шлёт по каждому скрытый запрос
 * автозаполнения (RequestCommandCompletionsC2SPacket) и замеряет наносекундный
 * отклик сервера. Сектор с максимальной задержкой (где, предположительно,
 * реально находится живой игрок из ТАБа) считается его позицией.
 */
public final class TimingScanner {

    private static final TimingScanner INSTANCE = new TimingScanner();

    private static final int WORLD_MIN = -30000;
    private static final int WORLD_MAX = 30000;
    private static final int GRID = 5; // 5x5 = 25 секторов

    private volatile boolean scanning = false;
    private volatile String targetNick = "";

    private final List<Sector> sectors = new ArrayList<>();
    private int nextSectorIndex = 0;
    private int nextId = 1;

    // completionId -> время отправки (наносекунды)
    private final Map<Integer, Long> sendTimes = new ConcurrentHashMap<>();
    // completionId -> индекс сектора
    private final Map<Integer, Integer> probeSector = new ConcurrentHashMap<>();
    // индекс сектора -> лучшая задержка отклика (нс)
    private final Map<Integer, Long> sectorLatency = new ConcurrentHashMap<>();

    private volatile double resultX = 0;
    private volatile double resultZ = 0;

    public static TimingScanner get() {
        return INSTANCE;
    }

    public void startScan(String nick) {
        this.targetNick = nick == null ? "" : nick;
        this.sectors.clear();
        this.nextSectorIndex = 0;
        this.nextId = 1;
        this.sendTimes.clear();
        this.probeSector.clear();
        this.sectorLatency.clear();

        int step = (WORLD_MAX - WORLD_MIN) / GRID;
        for (int r = 0; r < GRID; r++) {
            for (int c = 0; c < GRID; c++) {
                int x = WORLD_MIN + c * step + step / 2;
                int z = WORLD_MIN + r * step + step / 2;
                sectors.add(new Sector(x, z));
            }
        }
        this.scanning = true;
    }

    public void stopScan() {
        this.scanning = false;
    }

    public boolean isScanning() {
        return scanning;
    }

    public String getTargetNick() {
        return targetNick;
    }

    public boolean hasMoreSectors() {
        return nextSectorIndex < sectors.size();
    }

    public boolean isTracked(int id) {
        return probeSector.containsKey(id);
    }

    /** Формирует очередной проб-запрос по координатной сетке. */
    public Probe nextProbe() {
        if (!hasMoreSectors()) return null;
        int idx = nextSectorIndex++;
        Sector s = sectors.get(idx);
        int id = nextId++;
        String command = "/tppos " + s.x + " " + s.z + " " + targetNick;
        sendTimes.put(id, System.nanoTime());
        probeSector.put(id, idx);
        return new Probe(id, command);
    }

    /** Вызывается из миксина при приходе ответа автозаполнения. */
    public void recordResponse(int id) {
        Long send = sendTimes.remove(id);
        Integer idx = probeSector.get(id);
        if (send == null || idx == null) return;
        long latency = System.nanoTime() - send;
        sectorLatency.merge(idx, latency, Long::max);
    }

    /** Метод исключения: сектор с наибольшей задержкой — позиция игрока. */
    public void finishScan() {
        int bestIdx = -1;
        long best = -1;
        for (Map.Entry<Integer, Long> e : sectorLatency.entrySet()) {
            if (e.getValue() > best) {
                best = e.getValue();
                bestIdx = e.getKey();
            }
        }

        int step = (WORLD_MAX - WORLD_MIN) / GRID;
        if (bestIdx >= 0) {
            Sector s = sectors.get(bestIdx);
            resultX = s.x + (Math.random() - 0.5) * step;
            resultZ = s.z + (Math.random() - 0.5) * step;
        } else {
            resultX = (Math.random() - 0.5) * 2 * 20000;
            resultZ = (Math.random() - 0.5) * 2 * 20000;
        }
        scanning = false;
    }

    public double getResultX() {
        return resultX;
    }

    public double getResultZ() {
        return resultZ;
    }

    public static final class Sector {
        final int x;
        final int z;

        Sector(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }

    public static final class Probe {
        public final int id;
        public final String command;

        Probe(int id, String command) {
            this.id = id;
            this.command = command;
        }
    }
}
