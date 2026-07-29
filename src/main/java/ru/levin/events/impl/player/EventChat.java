package ru.levin.events.impl.player;

import net.minecraft.text.Text;
import ru.levin.events.Event;

public class EventChat extends Event {
    private final Text message;
    private final boolean overlay;

    public EventChat(Text message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    public Text getMessage() {
        return message;
    }

    public boolean isOverlay() {
        return overlay;
    }
}
