package ru.levin.modules.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import ru.levin.events.Event;
import ru.levin.events.impl.EventUpdate;
import ru.levin.events.impl.player.EventAttack;
import ru.levin.modules.Function;
import ru.levin.modules.FunctionAnnotation;
import ru.levin.modules.Type;
import ru.levin.modules.setting.ModeSetting;
import ru.levin.modules.setting.SliderSetting;
import ru.levin.modules.setting.TextSetting;
import ru.levin.util.player.TimerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@FunctionAnnotation(name = "AutoEZ", type = Type.Player, desc = "Пишет сообщение после убийства игрока")
public class AutoEZ extends Function {

    private final ModeSetting mode = new ModeSetting(
            "Сообщение",
            "lovan",
            "lovan",
            "Custom"
    );

    private final TextSetting customText = new TextSetting("Свой текст", "LOAN CLIENT BUST tg lovandlc",
            () -> mode.is("Custom"));

    private final SliderSetting cooldown = new SliderSetting("Задержка", 2500f, 0f, 15000f, 250f);

    private final TimerUtil delayTimer = new TimerUtil();

    private PlayerEntity lastTarget;
    private boolean waitingForDeath;

    private final Map<UUID, Long> recentKills = new HashMap<>();

    public AutoEZ() {
        addSettings(mode, customText, cooldown);
    }

    @Override
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (event instanceof EventAttack attackEvent) {
            Entity t = attackEvent.getTarget();
            if (t instanceof PlayerEntity p && p != mc.player) {
                lastTarget = p;
                waitingForDeath = true;
            }
        }

        if (event instanceof EventUpdate) {
            cleanupRecent();

            if (!waitingForDeath || lastTarget == null) return;

            boolean dead = lastTarget.isDead() || lastTarget.getHealth() <= 0.0F;
            boolean unloaded = mc.world.getEntityById(lastTarget.getId()) == null;

            if (!dead && !unloaded) return;

            UUID uuid = lastTarget.getUuid();
            long now = System.currentTimeMillis();

            long cd = cooldown.get().longValue();
            if (cd > 0 && !delayTimer.hasTimeElapsed(cd)) {
                waitingForDeath = false;
                lastTarget = null;
                return;
            }

            Long lastSent = recentKills.get(uuid);
            if (lastSent != null && now - lastSent < 5000L) {
                waitingForDeath = false;
                lastTarget = null;
                return;
            }

            String msg = buildMessage(lastTarget);
            sendMessage(msg);

            recentKills.put(uuid, now);
            delayTimer.reset();
            waitingForDeath = false;
            lastTarget = null;
        }
    }

    private void cleanupRecent() {
        long now = System.currentTimeMillis();
        recentKills.entrySet().removeIf(e -> now - e.getValue() > 30000L);
    }

    private String buildMessage(PlayerEntity target) {
        String base;
        if (mode.is("lovan")) {
            base = "LOAN CLIENT BUST tg lovandlc";
        } else {
            base = customText.getValue();
        }
        if (base == null) base = "";
        return base.replace("%target%", target != null ? target.getName().getString() : "");
    }

    private void sendMessage(String msg) {
        if (msg == null || msg.trim().isEmpty()) return;
        mc.player.networkHandler.sendChatMessage(msg);
    }
}
