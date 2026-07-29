package ru.levin.screens.dropdown.impl;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import ru.levin.manager.Manager;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.screens.dropdown.SettingRenderer;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.util.color.ColorUtil;
import ru.levin.util.render.RenderUtil;

import java.awt.*;
import java.util.Locale;

public class SliderSettingRenderer implements SettingRenderer<SliderSetting> {

    private static final int HEIGHT = 12;
    private static final int BAR_HEIGHT = 4;
    private static final int PADDING = 0;

    private static final int STRIPE_WIDTH = 4;
    private static final int STRIPE_GAP = 2;
    private static final int HANDLE_GRAB_SIZE = 6;

    private static final float CIRCLE_RADIUS = 6f;
    private static final float CIRCLE_SCALE_MAX = 1.2f;
    private static final float CIRCLE_SCALE_MIN = 1f;
    private static final float SCALE_STEP = 0.05f;

    @Override
    public void render(DrawContext ctx, SliderSetting setting, int x, int y, int width, int height) {
        int barWidth = width - 2 * PADDING;
        int barX = x + PADDING;
        int barY = computeBarY(y, height);
        int trackY = barY - BAR_HEIGHT / 2;

        RenderUtil.drawRoundedRect(ctx.getMatrices(), barX, trackY, barWidth, BAR_HEIGHT, 1, new Color(40, 40, 50, 200).getRGB());

        double increment = setting.getIncrement();
        double rawValue = setting.get().doubleValue();
        double roundedValue = Math.round(rawValue / increment) * increment;
        double progress = getNormalizedValue(setting, roundedValue);
        int targetProgressWidth = (int) (barWidth * progress);

        if (setting.circlePos == -1) setting.circlePos = targetProgressWidth;
        setting.circlePos += (targetProgressWidth - setting.circlePos) * 0.2;

        int themeBase = Manager.STYLE_MANAGER.getFirstColor();
        int selectedColor = ColorUtil.reAlphaInt(themeBase, 128);
        drawStripedTrack(ctx.getMatrices(), barX, trackY, barWidth, BAR_HEIGHT, (int) setting.circlePos, selectedColor);

        // поднимаем текст чуть выше, чтобы он не был на одной линии с полоской/точками
        int textY = y + 1;
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), setting.getName(), x + PADDING, textY, Color.WHITE.getRGB());
        String valueText = formatValue(roundedValue, increment);
        int valueWidth = (int) FontUtils.sf_medium[16].getWidth(valueText);
        FontUtils.sf_medium[16].drawLeftAligned(ctx.getMatrices(), valueText, x + width - valueWidth - PADDING, textY, Color.WHITE.getRGB());
        
        if (setting.dragging) {
            setting.circleScale += SCALE_STEP;
            if (setting.circleScale > CIRCLE_SCALE_MAX) setting.circleScale = CIRCLE_SCALE_MAX;
        } else {
            setting.circleScale -= SCALE_STEP;
            if (setting.circleScale < CIRCLE_SCALE_MIN) setting.circleScale = CIRCLE_SCALE_MIN;
        }

        float circleX = barX + (float) setting.circlePos;
        float circleY = trackY + BAR_HEIGHT / 2f;

        MatrixStack matrices = ctx.getMatrices();
        matrices.push();
        matrices.translate(circleX, circleY, 0);
        matrices.scale(setting.circleScale, setting.circleScale, 1f);
        matrices.translate(-circleX, -circleY, 0);

        RenderUtil.drawCircle(matrices, circleX, circleY, CIRCLE_RADIUS, Color.WHITE.getRGB());
        matrices.pop();
    }

    private int computeBarY(int y, int height) {
        // опускаем полосу ещё чуть ниже, чтобы увеличить расстояние от текста до линии
        return y + height;
    }

    private void drawStripedTrack(MatrixStack matrices, int x, int y, int width, int height, int progressWidth, int accentColor) {
        // вместо "миникружков" рисуем одну закруглённую полосу фона + полосу прогресса
        int backgroundColor = new Color(65, 65, 80, 200).getRGB();
        int filledColor = ColorUtil.applyAlpha(accentColor, 0.85f);

        // фон всей линии
        RenderUtil.drawRoundedRect(matrices, x, y, width, height, 2, backgroundColor);

        // заполненная часть по прогрессу
        int clampedProgress = MathHelper.clamp(progressWidth, 0, width);
        if (clampedProgress > 0) {
            RenderUtil.drawRoundedRect(matrices, x, y, clampedProgress, height, 2, filledColor);
        }
    }

    private double getNormalizedValue(SliderSetting setting, double roundedValue) {
        double min = setting.getMin();
        double max = setting.getMax();
        if (max - min <= 0) return 0;
        return MathHelper.clamp((roundedValue - min) / (max - min), 0, 1);
    }


    private String formatValue(double val, double increment) {
        if (increment >= 1) {
            return String.format(Locale.US, "%d", (long) val);
        } else if (increment >= 0.1) {
            return String.format(Locale.US, "%.1f", val);
        } else {
            return String.format(Locale.US, "%.2f", val);
        }
    }

    @Override
    public boolean mouseClicked(SliderSetting setting, double mouseX, double mouseY, int button, int x, int y, int width, int height) {
        // используем ЛКМ (button == 0) для управления слайдером
        if (button != 0) return false;

        int barWidth = width - 2 * PADDING;
        int barX = x + PADDING;
        int barY = computeBarY(y, height);
        int trackY = barY - BAR_HEIGHT / 2;

        double normalized = getNormalizedValue(setting, setting.get().doubleValue());
        int handleX = (int) (barX + barWidth * normalized);
        int handleY = trackY + BAR_HEIGHT / 2;

        boolean overHandle = RenderUtil.isInRegion(mouseX, mouseY,
                handleX - HANDLE_GRAB_SIZE, handleY - HANDLE_GRAB_SIZE,
                HANDLE_GRAB_SIZE * 2, HANDLE_GRAB_SIZE * 2);
        boolean overTrack = RenderUtil.isInRegion(mouseX, mouseY, barX, trackY - 3, barWidth, BAR_HEIGHT + 6);

        if (overHandle || overTrack) {
            updateValue(setting, mouseX, barX, barWidth);
            setting.dragging = true;
            return true;
        }
        return false;
    }


    public void mouseReleased(SliderSetting setting) {
        setting.dragging = false;
    }

    public void mouseDragged(SliderSetting setting, double mouseX, int x, int width) {
        if (!setting.dragging) return;

        int barWidth = width - 2 * PADDING;
        int barX = x + PADDING;

        updateValue(setting, mouseX, barX, barWidth);
    }

    private void updateValue(SliderSetting setting, double mouseX, int barX, int barWidth) {
        double relX = mouseX - barX;
        double percent = Math.min(Math.max(relX / barWidth, 0), 1);

        double newValue = setting.getMin() + percent * (setting.getMax() - setting.getMin());
        double increment = setting.getIncrement();
        newValue = Math.round(newValue / increment) * increment;
        newValue = Math.min(Math.max(newValue, setting.getMin()), setting.getMax());

        setting.set(newValue);
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }
}
