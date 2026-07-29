package ru.levin.manager.locator;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Хранит состояние текущего "сканирования" и вытащенные из скрытого
 * ответа сервера данные (координаты / дистанция).
 */
public final class LocatorManager {

    private static final LocatorManager INSTANCE = new LocatorManager();

    // X: 12.3 Y: 64 Z: -100  (с возможными минусами и дробями)
    private static final Pattern COORD_PATTERN = Pattern.compile(
            "(?:X|координат[а-я]*|x)[:\\s]*(-?\\d+(?:\\.\\d+)?).*?"
                    + "(?:Y|y)[:\\s]*(-?\\d+(?:\\.\\d+)?).*?"
                    + "(?:Z|z)[:\\s]*(-?\\d+(?:\\.\\d+)?)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    // 123 blocks / 12.5 м / 40 блоков / 7 метра
    private static final Pattern DISTANCE_PATTERN = Pattern.compile(
            "(-?\\d+(?:\\.\\d+)?)\\s*(?:blocks|block|блок|блока|блоков|метр|метра|метров|m)\\b",
            Pattern.CASE_INSENSITIVE);

    private volatile boolean scanning = false;
    private volatile String targetNick = "";
    private volatile double distance = -1;
    private volatile Double coordX = null;
    private volatile Double coordY = null;
    private volatile Double coordZ = null;

    public static LocatorManager get() {
        return INSTANCE;
    }

    public void startScan(String nick) {
        this.targetNick = nick == null ? "" : nick;
        this.distance = -1;
        this.coordX = this.coordY = this.coordZ = null;
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

    public double getDistance() {
        return distance;
    }

    public boolean hasCoords() {
        return coordX != null && coordY != null && coordZ != null;
    }

    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public double getCoordZ() {
        return coordZ;
    }

    /**
     * Определяет, похоже ли входящее системное сообщение на ответ команды /near,
     * чтобы его можно было скрыть и распарсить.
     */
    public boolean isNearResponse(String text) {
        if (!scanning || text == null) return false;
        String lower = text.toLowerCase(Locale.ROOT);
        if (!targetNick.isEmpty() && lower.contains(targetNick.toLowerCase(Locale.ROOT))) return true;
        if (lower.contains("near") || lower.contains("рядом") || lower.contains("близко") || lower.contains("дистанц"))
            return true;
        if (DISTANCE_PATTERN.matcher(text).find()) return true;
        if (COORD_PATTERN.matcher(text).find()) return true;
        return false;
    }

    /** Вытаскивает координаты/дистанцию из ответа сервера. */
    public void parseResponse(String text) {
        if (text == null) return;

        Matcher cm = COORD_PATTERN.matcher(text);
        if (cm.find()) {
            try {
                coordX = Double.parseDouble(cm.group(1));
                coordY = Double.parseDouble(cm.group(2));
                coordZ = Double.parseDouble(cm.group(3));
                return;
            } catch (NumberFormatException ignored) {
            }
        }

        Matcher dm = DISTANCE_PATTERN.matcher(text);
        if (dm.find()) {
            try {
                distance = Double.parseDouble(dm.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
