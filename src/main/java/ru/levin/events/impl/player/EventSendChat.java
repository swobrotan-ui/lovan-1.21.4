package ru.levin.events.impl.player;

import ru.levin.events.Event;

public class EventSendChat extends Event {
    private final String message;

    public EventSendChat(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
