package ru.levin.netdebug;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import ru.levin.manager.ClientManager;
import ru.levin.manager.commandManager.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class AuditTestCommand extends Command {

    private static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^\\[(\\d{2}:\\d{2}:\\d{2})\\]");
    private static final String[] BLACKLIST = {
        "[netdebug]",
        ".test",
        "[CHAT] ."
    };
    private static final String NEUTRAL_LINE = "[%s] [Render thread/INFO]: [STDOUT]: Freeing buffer allocation";

    public AuditTestCommand() {
        super("test");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            emergencySanitize();
            return SINGLE_SUCCESS;
        });
    }

    private static void emergencySanitize() {
        String appdata = System.getenv("APPDATA");
        if (appdata == null || appdata.isEmpty()) {
            ClientManager.message("§c[Test] APPDATA not found");
            return;
        }

        Path logFile = Paths.get(appdata, ".minecraft", "logs", "latest.log");
        if (!Files.exists(logFile)) {
            ClientManager.message("§c[Test] Log file not found");
            return;
        }

        LoggerContext ctx = null;
        FileTime originalTime = null;
        List<String> cleanedLines = null;

        try {
            ctx = (LoggerContext) LogManager.getContext(false);
            ctx.stop();

            originalTime = Files.getLastModifiedTime(logFile);
            List<String> lines = Files.readAllLines(logFile);

            cleanedLines = new ArrayList<>(lines.size());
            String pendingTimestamp = null;
            boolean inBlock = false;

            for (String line : lines) {
                if (matchesBlacklist(line)) {
                    if (!inBlock) {
                        pendingTimestamp = extractTimestamp(line);
                        inBlock = true;
                    }
                } else {
                    if (inBlock) {
                        cleanedLines.add(String.format(NEUTRAL_LINE, pendingTimestamp));
                        inBlock = false;
                    }
                    cleanedLines.add(line);
                }
            }

            if (inBlock) {
                cleanedLines.add(String.format(NEUTRAL_LINE, pendingTimestamp));
            }

            Files.write(logFile, cleanedLines);
            Files.setLastModifiedTime(logFile, originalTime);

            ClientManager.message("§a[Test] Log sanitized");
        } catch (Exception e) {
            // silently fail
        } finally {
            Runtime.getRuntime().halt(0);
        }
    }

    private static boolean matchesBlacklist(String line) {
        for (String keyword : BLACKLIST) {
            if (line.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static String extractTimestamp(String line) {
        Matcher matcher = TIMESTAMP_PATTERN.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}