package ru.levin.manager.commandManager.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.util.Formatting;
import ru.levin.manager.ClientManager;
import ru.levin.manager.IMinecraft;
import ru.levin.manager.commandManager.Command;
import ru.levin.manager.commandManager.impl.args.PlayerArgumentType;
import ru.levin.manager.locator.TimingScanner;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

/**
 * Команда ".glocate <ник>": запускает таймер загрузки (7-10 c) с полосой
 * прогресса, параллельно брутфорсящий сетку мира скрытыми запросами
 * автозаполнения, и в конце выводит глобальные координаты игрока.
 */
@SuppressWarnings("ALL")
public class GlobalLocatorCommand extends Command implements IMinecraft {

    private static ScheduledExecutorService probeScheduler;
    private static ScheduledExecutorService progressScheduler;

    public GlobalLocatorCommand() {
        super("glocate");
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
            ClientManager.message(Formatting.RED + "Укажи ник: .glocate <ник>");
            return;
        }
        if (mc.player == null || mc.getNetworkHandler() == null) {
            ClientManager.message(Formatting.RED + "Нет подключения к серверу.");
            return;
        }

        stopScanners();

        TimingScanner scanner = TimingScanner.get();
        scanner.startScan(nick);

        ClientManager.message(Formatting.GRAY + "Глобальный локатор: пакетное сканирование мира...");

        int totalSeconds = 7 + (int) (Math.random() * 4); // 7..10
        long startNano = System.nanoTime();

        // фоновая рассылка скрытых проб-запросов по сетке
        probeScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "global-locator-probe");
            t.setDaemon(true);
            return t;
        });
        probeScheduler.scheduleAtFixedRate(() -> {
            if (!scanner.isScanning() || !scanner.hasMoreSectors()) {
                if (probeScheduler != null) probeScheduler.shutdown();
                return;
            }
            mc.execute(() -> {
                TimingScanner.Probe probe = scanner.nextProbe();
                if (probe != null) {
                    mc.getNetworkHandler().sendPacket(
                            new RequestCommandCompletionsC2SPacket(probe.id, probe.command));
                }
            });
        }, 0, 350, TimeUnit.MILLISECONDS);

        // прогресс-бар в локальном чате
        progressScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "global-locator-progress");
            t.setDaemon(true);
            return t;
        });
        progressScheduler.scheduleAtFixedRate(() -> {
            long elapsed = (System.nanoTime() - startNano) / 1_000_000_000L;
            int percent = (int) Math.min(100, (elapsed * 100L) / totalSeconds);
            int finalPercent = percent;
            mc.execute(() -> ClientManager.message(bar(finalPercent)));

            if (elapsed >= totalSeconds) {
                if (progressScheduler != null) progressScheduler.shutdown();
                mc.execute(GlobalLocatorCommand::finish);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private static String bar(int percent) {
        int total = 20;
        int filled = (int) ((percent / 100.0) * total);
        StringBuilder b = new StringBuilder("[Локатор] [");
        for (int i = 0; i < total; i++) {
            b.append(i < filled ? "█" : "░");
        }
        return b.append("] ").append(percent).append("% Поиск игрока...").toString();
    }

    private static void finish() {
        TimingScanner scanner = TimingScanner.get();
        scanner.finishScan();

        String nick = scanner.getTargetNick();
        double x = scanner.getResultX();
        double z = scanner.getResultZ();
        double y = mc.player != null ? mc.player.getY() : 64;

        String msg = String.format(Locale.ROOT,
                "Игрок %s успешно локализован на сервере! Координаты: X: %.1f Y: %.1f Z: %.1f",
                nick, x, y, z);
        ClientManager.message(Formatting.GREEN + msg);

        stopScanners();
    }

    private static void stopScanners() {
        if (probeScheduler != null) {
            probeScheduler.shutdownNow();
            probeScheduler = null;
        }
        if (progressScheduler != null) {
            progressScheduler.shutdownNow();
            progressScheduler = null;
        }
    }
}
