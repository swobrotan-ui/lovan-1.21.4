package ru.levin.modules.misc;

import net.minecraft.client.MinecraftClient;
import ru.levin.events.Event;
import ru.levin.events.impl.render.EventRender2D;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.BooleanSetting;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

@SuppressWarnings("All")
@FunctionAnnotation(name = "SensPick", desc = "Измерение DPI и чувствительности для точного ПвП", type = Type.Misc)
public class SensPick extends Function {
    private final SliderSetting dpiSetting = new SliderSetting("Мышь DPI", 800, 100, 16000, 50);
    private final BooleanSetting showEdpi = new BooleanSetting("Показывать eDPI", true);
    private final BooleanSetting showInGame = new BooleanSetting("Показывать в игре %", true);
    private final BooleanSetting showGcd = new BooleanSetting("Показывать GCD", true);
    private final BooleanSetting showAccel = new BooleanSetting("Ускорение Windows", true);
    private final BooleanSetting showTips = new BooleanSetting("PvP советы", true);
    private final ModeSetting bgMode = new ModeSetting("Фон", "Стекло", "Стекло", "Прозрачный", "Закрашенный");
    private final SliderSetting alphaSetting = new SliderSetting("Прозрачность", 220, 80, 255, 5);

    private int mouseAccelWindows = -1;
    private int mouseSpeedWindows = -1;
    private long lastAutoDetect = 0;

    public SensPick() {
        addSettings(dpiSetting, showEdpi, showInGame, showGcd, showAccel, showTips, bgMode, alphaSetting);
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventRender2D e) {
            long now = System.currentTimeMillis();
            if (now - lastAutoDetect > 30_000L) {
                autoDetect();
            }
            render(e);
        }
    }

    private void render(EventRender2D event) {
        if (mc.player == null || mc.world == null || mc.options == null) return;

        double sens = mc.options.getMouseSensitivity().getValue();
        double sensMid = sens * 0.6 + 0.2;
        double multiplier = sensMid * sensMid * sensMid * 8.0;
        int inGamePercent = (int) Math.round(sens * 200);
        int dpi = (int) Math.round(dpiSetting.get().doubleValue());
        double edpi = dpi * multiplier;
        double gcdVal = multiplier * 0.15;

        var lines = new ArrayList<String>();
        lines.add("§7Сенсы:");
        if (showInGame.get()) lines.add("§aВ игре: §f" + inGamePercent + "%");
        if (showGcd.get()) lines.add("§aGCD: §f" + String.format(Locale.ROOT, "%.4f", gcdVal));
        if (showEdpi.get()) lines.add("§aeDPI: §f" + (int) Math.round(edpi));
        if (showAccel.get() && mouseAccelWindows >= 0) {
            lines.add("§aУск. Win: §f" + (mouseAccelWindows == 1 ? "Вкл" : "Выкл"));
        }
        if (mouseSpeedWindows >= 0) {
            lines.add("§aWin Speed: §f" + mouseSpeedWindows + "/20");
        }
        if (showTips.get()) {
            if (edpi < 20) lines.add("§cОчень низкая");
            else if (edpi < 50) lines.add("§eНизкая, флекс");
            else if (edpi < 100) lines.add("§aСтабильная ПвП");
            else if (edpi < 160) lines.add("§aСредне-высокая");
            else if (edpi < 250) lines.add("§eВысокая");
            else lines.add("§cОчень высокая");
        }

        var matrices = event.getDrawContext().getMatrices();
        var font = FontUtils.sfns_display_bold[14];

        float padX = 8f;
        float padY = 6f;
        float lineGap = 1.5f;
        float lineH = font.getHeight() + lineGap;

        float maxW = 0;
        for (String line : lines) maxW = Math.max(maxW, font.getWidth(line));
        float totalW = padX * 2 + maxW;
        float topY = mc.getWindow().getScaledHeight() - 40f;
        float totalH = lines.size() * lineH + padY * 2 - lineGap;
        float x = mc.getWindow().getScaledWidth() - totalW - 10f;

        int alpha = Math.min(255, (int) Math.round(alphaSetting.get().doubleValue() * 0.7));
        int baseBg = new Color(18, 18, 24, Math.min(255, alpha)).getRGB();

        String mode = bgMode.get();
        if ("Закрашенный".equals(mode)) {
            RenderUtil.drawRoundedRect(matrices, x, topY, totalW, totalH, 7f, baseBg);
        } else if ("Стекло".equals(mode)) {
            RenderUtil.drawBlur(matrices, x, topY, totalW, totalH, new org.joml.Vector4f(7f, 7f, 7f, 7f), 12, Color.white.getRGB());
            RenderUtil.drawRoundedRect(matrices, x, topY, totalW, totalH, new org.joml.Vector4f(7f, 7f, 7f, 7f), ColorUtil.withAlpha(baseBg, 0.55f));
        }

        float y = topY + padY;
        for (String line : lines) {
            font.drawLeftAligned(matrices, line, x + padX, y, Color.white.getRGB());
            y += lineH;
        }
    }

    private void autoDetect() {
        lastAutoDetect = System.currentTimeMillis();
        int displayDpi = tryReadDisplayDpi();
        if (displayDpi > 0) {
            dpiSetting.set(Math.max(100, displayDpi));
        }
        mouseAccelWindows = readRegInt("HKEY_CURRENT_USER\\Control Panel\\Mouse", "MouseSpeed", -1);
        mouseSpeedWindows = readRegInt("HKEY_CURRENT_USER\\Control Panel\\Mouse", "MouseSensitivity", -1);
    }

    private int tryReadDisplayDpi() {
        try {
            return java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (Exception e) {
            return 0;
        }
    }

    private int readRegInt(String key, String valueName, int def) {
        if (!System.getProperty("os.name", "").toLowerCase().contains("win")) return def;
        try {
            Process p = Runtime.getRuntime().exec("reg query \"" + key + "\" /v " + valueName + " 2>nul");
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(valueName)) {
                    String[] parts = line.trim().split("\\s+");
                    if (parts.length > 0) {
                        try {
                            return Integer.parseInt(parts[parts.length - 1]);
                        } catch (NumberFormatException ex) {
                            try {
                                return (int) (long) Long.decode(parts[parts.length - 1]);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return def;
    }
}
