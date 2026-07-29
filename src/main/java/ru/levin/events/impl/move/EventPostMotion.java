package ru.levin.events.impl.move;

import ru.levin.events.Event;

public class EventPostMotion extends Event {
    private final float yaw;
    private final float pitch;

    public EventPostMotion(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
