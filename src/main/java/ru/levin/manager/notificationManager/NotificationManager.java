package ru.levin.manager.notificationManager;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.dragManager.DragManager;
import ru.levin.manager.dragManager.Dragging;
import ru.levin.util.math.MathUtil;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager implements IMinecraft {

    private final List<Notification> notifications = new ArrayList<>();

    public void add(NotificationType type, String name, String desc, int time) {
        synchronized (notifications) {
            notifications.add(new Notification(type, name, desc, time));
        }
    }

    public List<Notification> getNotifications() {
        synchronized (notifications) {
            return new ArrayList<>(notifications);
        }
    }

    public void draw(DrawContext context) {
        float yoffset = 0;

        Dragging drag = DragManager.draggables.get("NotificationsHUD");
        if (drag != null) {
            drag.setWidth(150f);
            drag.setHeight(30f);
        }

        List<Notification> snapshot;
        synchronized (notifications) {
            snapshot = new ArrayList<>(notifications);
        }

        for (Notification notification : snapshot) {
            long timePassed = System.currentTimeMillis() - notification.getTime();
            long totalDuration = notification.getDuration() * 1000L;

            if (timePassed > totalDuration - 222) {
                notification.animation.setDirection(Direction.AxisDirection.NEGATIVE);
            }

            notification.alpha = (float) notification.animation.getOutput();

            if (timePassed > totalDuration) {
                notification.animationy.setDirection(Direction.AxisDirection.NEGATIVE);
            }
            if (notification.animationy.finished(Direction.AxisDirection.NEGATIVE)) {
                synchronized (notifications) {
                    notifications.remove(notification);
                }
                continue;
            }

            float fixedRightPadding = 8f;
            float width = notification.getWidth();
            if (width <= 0) width = 90f;

            float baseX;
            float baseY;

            if (drag != null) {
                baseX = drag.getX();
                baseY = drag.getY();
            } else {
                baseX = mc.getWindow().getScaledWidth() - width - fixedRightPadding;
                baseY = mc.getWindow().getScaledHeight() - 30;
            }

            float notifHeight = 28.5f;

            float y;
            if (notification.animationy.getDirection() == Direction.AxisDirection.NEGATIVE) {
                y = baseY - notifHeight * (float) notification.animationy.getOutput();
            } else {
                y = baseY - notifHeight * yoffset * 1.1f;
            }

            float x = baseX;
            if (notification.animation.getDirection() == Direction.AxisDirection.NEGATIVE) {
                double output = notification.animation.getOutput();
                x = baseX + (float) (width * (1.0 - output));
            }

            notification.setX(x);
            if (y <= notification.getY()) {
                notification.setY(y);
            } else {
                notification.setY(MathUtil.fast(notification.getY(), y, 10));
            }

            notification.draw(context);

            yoffset++;
        }
    }
}
