package ru.levin.events.impl.player;

import net.minecraft.entity.player.PlayerEntity;
import ru.levin.events.Event;

public class EventPlayerHurt extends Event {
    private final PlayerEntity attacker;

    public EventPlayerHurt(PlayerEntity attacker) {
        this.attacker = attacker;
    }

    public PlayerEntity getAttacker() {
        return attacker;
    }
}
