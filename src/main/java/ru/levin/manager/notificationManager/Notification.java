package ru.levin.manager.notificationManager;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Direction;
import ru.levin.manager.IMinecraft;
import ru.levin.util.animations.Animation;
import ru.levin.util.animations.impl.DecelerateAnimation;
import ru.levin.util.color.ColorUtil;
import ru.levin.manager.fontManager.FontUtils;
import ru.levin.util.render.RenderUtil;

import java.awt.*;

public class Notification implements IMinecraft {
    @Getter
    @Setter
    private float x, y;

    @Getter
    private String name;

    @Getter
    private String desc;

    @Getter
    private int duration;

    @Getter
    private NotificationType type;

    @Getter
    private long time = System.currentTimeMillis();

    public Animation animation = new DecelerateAnimation(500, 1, Direction.AxisDirection.POSITIVE);
    public Animation animationy = new DecelerateAnimation(500, 1, Direction.AxisDirection.POSITIVE);

    float alpha;
    int times;

    private float width;

    public Notification(NotificationType type, String name, String desc, int time) {
        this.type = type;
        this.name = name;
        this.desc = desc;
        this.duration = time;
    }

    public float draw(DrawContext context) {
        String title = name;
        String statusText;
        if (type == NotificationType.SUCCESS) {
            statusText = "Включен";
        } else if (type == NotificationType.REMOVED) {
            statusText = "Выключен";
        } else {
            statusText = desc != null ? desc : "";
        }

        float titleWidth = FontUtils.durman[13].getWidth(title);
        float statusWidth = FontUtils.durman[12].getWidth(statusText);
        float textWidth = Math.max(titleWidth, statusWidth);

        float paddingX = 8f;
        float paddingY = 4f;

        width = Math.max(paddingX * 2 + textWidth, 90f);
        float height = 26f;
        float barHeight = 2.5f;

        // позиция берётся из x,y, которые выставляет NotificationManager (draggable NotificationsHUD)
        float boxX = x;
        float boxY = y;

        // слегка фиолетовый фон, в стиле остальных HUD карточек
        int bgColor = ColorUtil.rgba(18, 12, 35, (int)(190 * alpha));

        int enabledColor = ColorUtil.rgba(90, 200, 120, (int)(220 * alpha));
        int disabledColor = ColorUtil.rgba(220, 90, 90, (int)(220 * alpha));
        int statusColor = (type == NotificationType.SUCCESS ? enabledColor : type == NotificationType.REMOVED ? disabledColor : ColorUtil.rgba(210, 210, 210, (int)(220 * alpha)));

        RenderUtil.drawRoundedRect(context.getMatrices(), boxX, boxY, width, height, 6f, bgColor);

        float textX = boxX + paddingX;
        float titleY = boxY + paddingY;
        float statusY = boxY + height - paddingY - 7f;

        FontUtils.durman[13].drawLeftAligned(context.getMatrices(), title, textX, titleY, ColorUtil.rgba(255, 255, 255, (int)(220 * alpha)));
        FontUtils.durman[12].drawLeftAligned(context.getMatrices(), statusText, textX, statusY, statusColor);

        long timePassed = System.currentTimeMillis() - time;
        long totalDuration = duration * 1000L;
        float progress = totalDuration > 0 ? Math.max(0f, 1f - (float) timePassed / (float) totalDuration) : 0f;
        float barWidth = width * progress;
        if (barWidth > 0.5f) {
            int barColor = ColorUtil.rgba(255, 255, 255, (int)(80 * alpha));
            RenderUtil.drawRoundedRect(context.getMatrices(), boxX + 1f, boxY + height - barHeight - 1f, barWidth, barHeight, 1.25f, barColor);
        }

        return height + barHeight;
    }


    public float getWidth() {
        return width;
    }
}
