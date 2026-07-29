package ru.levin.manager.commandManager.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import ru.levin.manager.ClientManager;
import ru.levin.manager.commandManager.Command;
import ru.levin.manager.commandManager.impl.args.PlayerArgumentType;
import ru.levin.playertracker.TrackerManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@SuppressWarnings("ALL")
public class PlayerTrackCommand extends Command {

    public PlayerTrackCommand() {
        super("test");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        // .test — список последних глобальных координат-аномалий.
        builder.executes(context -> {
            listRecent();
            return SINGLE_SUCCESS;
        });

        // .test <ник> — события, связанные с этим игроком.
        builder.then(arg("player", PlayerArgumentType.create())
                .executes(context -> {
                    String name = context.getArgument("player", String.class);
                    lookup(name);
                    return SINGLE_SUCCESS;
                }));
    }

    private void listRecent() {
        List<TrackerManager.TrackedEvent> events = TrackerManager.get().getRecent(15);
        if (events.isEmpty()) {
            ClientManager.message(Formatting.GRAY +
                    "Глобальных событий пока не зафиксировано. Жди гром/взрывы/порталы или действия игроков — координаты появятся здесь.");
            return;
        }
        ClientManager.message(Formatting.AQUA + "=== Последние глобальные события (в мире) ===");
        for (TrackerManager.TrackedEvent e : events) {
            ClientManager.message(format(e));
        }
    }

    private void lookup(String name) {
        List<TrackerManager.TrackedEvent> events = TrackerManager.get().getForPlayer(name);
        if (events.isEmpty()) {
            ClientManager.message(Formatting.GRAY +
                    "Нет событий, где " + name + " был в сети в момент фиксации. Показываю все недавние аномалии:");
            listRecent();
            return;
        }
        ClientManager.message(Formatting.AQUA + "=== События, где " + name + " был в сети (возможно связаны) ===");
        int limit = Math.min(15, events.size());
        for (int i = 0; i < limit; i++) {
            ClientManager.message(format(events.get(i)));
        }
    }

    private static String format(TrackerManager.TrackedEvent e) {
        String dim = TrackerManager.dimensionName(e.dimension);
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date(e.timestamp));
        String type = e.type == TrackerManager.EventType.SOUND ? "звук" : "мир";
        return Formatting.GRAY + "[" + time + "] " + Formatting.WHITE + type + ": " + e.id +
                Formatting.GRAY + " | X:" + e.x + " Y:" + e.y + " Z:" + e.z +
                " | Мир: " + Formatting.AQUA + dim;
    }
}
