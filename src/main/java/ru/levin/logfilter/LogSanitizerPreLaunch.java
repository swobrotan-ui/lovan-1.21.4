package ru.levin.logfilter;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Map;

/**
 * Fabric {@code preLaunch} entrypoint.
 *
 * <p>This runs before <em>any</em> mod {@code onInitialize()} and even before
 * Minecraft's own classes are touched, which is the earliest reliable point at
 * which we can hook the Log4j2 configuration. We attach a {@link LogSanitizerFilter}
 * to:</p>
 *
 * <ol>
 *     <li>The <b>Root LoggerConfig</b> — so every logger in the process inherits it.</li>
 *     <li>Every <b>Appender</b> that writes to a file (the {@code latest.log} file
 *     appender in particular) — placed directly on the appender so the event is
 *     dropped before it is ever serialized to disk, even under async logging.</li>
 * </ol>
 *
 * <p>Attaching to the appender itself is what guarantees the message is dropped
 * <i>before</i> it is written to {@code latest.log}, regardless of which logger
 * emitted it. Because the filter is stateless and only ever returns
 * {@link Filter.Result#DENY}/{@link Filter.Result#NEUTRAL}, it cannot block or
 * deadlock the async logging thread.</p>
 */
public final class LogSanitizerPreLaunch implements PreLaunchEntrypoint {

    private static final String ROOT = LogManager.ROOT_LOGGER_NAME;

    @Override
    public void onPreLaunch() {
        try {
            install();
        } catch (Throwable ignored) {
            // Booting the game is more important than hiding logs.
        }
    }

    private static void install() {
        LoggerContext context = LoggerContext.getContext(false);
        Configuration config = context.getConfiguration();

        LogSanitizerFilter filter = LogSanitizerFilter.create();

        // 1) Global guard on the root logger config.
        LoggerConfig root = config.getLoggerConfig(ROOT);
        root.addFilter(filter);

        // 2) Drop blacklisted events directly on every appender so they never
        //    reach the disk (this is what actually keeps them out of latest.log).
        //    Appenders extend AbstractFilterable, so we cast to obtain addFilter.
        config.getAppenders().forEach((name, appender) -> {
            if (appender instanceof AbstractAppender) {
                try {
                    ((AbstractAppender) appender).addFilter(filter);
                } catch (UnsupportedOperationException ignored) {
                    // Some appenders are immutable; the root-level filter still covers them.
                }
            }
        });

        // Re-publish the configuration so the changes take effect immediately.
        context.updateLoggers();

        // Quietly confirm installation without going through the normal logger
        // (which is now filtered) — use System.out so the fact we installed the
        // sanitizer does not itself leak into the file.
        System.out.println("[LogSanitizer] Active log filtering installed.");
    }
}
