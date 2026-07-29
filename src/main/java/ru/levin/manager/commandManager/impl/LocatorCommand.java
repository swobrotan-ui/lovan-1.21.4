package ru.levin.manager.commandManager.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Formatting;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.commandManager.Command;
import ru.levin.manager.commandManager.impl.args.PlayerArgumentType;
import ru.levin.manager.locator.LocatorManager;
import ru.levin.playertracker.TrackerManager;
import ru.levin.playertracker.TrackerManager.TrackedEvent;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

@SuppressWarnings("ALL")
public class LocatorCommand extends Command implements IMinecraft {

    public LocatorCommand() {
        super("locate");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(arg("player", PlayerArgumentType.create())
                .executes(context -> {
                    String name = context.getArgument("player", String.class);
                    start(name);
                    return SINGLE_SUCCESS;
                }));
    }

    private static void start(String nick) {
        if (nick == null || nick.isEmpty()) {
            ClientManager.message(Formatting.RED + "Укажи ник: .locate <ник>");
            return;
        }
        if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) {
            ClientManager.message(Formatting.RED + "Нет подключения к серверу.");
            return;
        }

        LocatorManager manager = LocatorManager.get();
        manager.startScan(nick);

        ClientManager.message(Formatting.GRAY + "Глобальный поиск игрока " + nick + "...");

        int totalSeconds = 18;
        AtomicInteger tick = new AtomicInteger(0);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "locator-scan");
            t.setDaemon(true);
            return t;
        });

        scheduler.scheduleAtFixedRate(() -> {
            int t = tick.incrementAndGet();
            int percent = Math.min(100, (int) ((double) t / totalSeconds * 100));
            int finalPercent = percent;

            mc.execute(() -> ClientManager.message(Formatting.DARK_GRAY + "Глобальный поиск... " + finalPercent + "%"));

            if (t >= totalSeconds) {
                scheduler.shutdown();
                mc.execute(LocatorCommand::finish);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static void finish() {
        LocatorManager manager = LocatorManager.get();
        String nick = manager.getTargetNick();

        FoundResult result = findPlayerCoords(nick);

        if (result != null) {
            String prefix = result.exact
                    ? Formatting.GREEN + "Игрок " + nick + " найден (" + result.source + ")"
                    : Formatting.YELLOW + "Игрок " + nick + " найден (приблизительно, " + result.source + ")";
            ClientManager.message(prefix);
            ClientManager.message(Formatting.GREEN + String.format(Locale.ROOT,
                    "X: %.1f  Y: %.1f  Z: %.1f", result.x, result.y, result.z));
        } else {
            ClientManager.message(Formatting.RED + "Не удалось найти игрока " + nick);
            ClientManager.message(Formatting.DARK_GRAY + "Игрок может быть слишком далеко или не оставлял следов");
        }
        manager.stopScan();
    }

    private static FoundResult findPlayerCoords(String nick) {
        if (mc.world == null || mc.player == null) return null;

        for (PlayerEntity p : mc.world.getPlayers()) {
            if (p != null && p.getName() != null && nick.equalsIgnoreCase(p.getName().getString())) {
                return new FoundResult(p.getX(), p.getY(), p.getZ(), "в зоне загрузки", true);
            }
        }

        java.util.List<TrackedEvent> events = TrackerManager.get().getForPlayer(nick);
        if (!events.isEmpty()) {
            TrackedEvent best = events.get(0);
            return new FoundResult(best.x + 0.5, best.y + 0.5, best.z + 0.5,
                    "глобальное событие #" + best.id + " (" + TrackerManager.dimensionName(best.dimension) + ")", false);
        }

        return null;
    }

    private static class FoundResult {
        final double x, y, z;
        final String source;
        final boolean exact;
        FoundResult(double x, double y, double z, String source, boolean exact) {
            this.x = x; this.y = y; this.z = z; this.source = source; this.exact = exact;
        }
    }
}
