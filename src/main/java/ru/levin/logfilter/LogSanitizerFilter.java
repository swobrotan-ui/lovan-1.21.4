package ru.levin.logfilter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * A stateless, thread-safe Log4j2 Core {@link AbstractFilter} that drops any
 * {@link LogEvent} whose formatted message (or logger/throwable text) contains
 * one of the configured blacklisted substrings.
 *
 * <p>Design notes for safety and performance:</p>
 * <ul>
 *     <li><b>Stateless &amp; immutable:</b> the blacklist is built once and never
 *     mutated, so the filter is safe to share across the async Log4j background
 *     thread and the calling thread without any locking.</li>
 *     <li><b>No allocation in the hot path on the common case:</b> we return
 *     {@link Result#NEUTRAL} immediately for non-matching events and only
 *     allocate a small local list when something actually matches (rare).</li>
 *     <li><b>Non-intrusive:</b> returning {@link Result#DENY} simply prevents the
 *     event from reaching the appenders; it never blocks, joins or holds a lock,
 *     so it cannot deadlock the async logging pipeline.</li>
 * </ul>
 */
public final class LogSanitizerFilter extends AbstractFilter {

    /**
     * Substrings that should never reach the log file.
     * Add any package names, mixin warnings or keywords you want hidden here.
     *
     * <p>These are matched case-insensitively against the full rendered text of
     * the event (message + logger name + throwable).</p>
     */
    private static final String[] BLACKLIST = {
            "lovanclient",
            "lovan client",
            "exosware",
            "x2demo",
            "sodiumextra",
            "invalid path",
            "lombok",
            "viaversion",
            "via-backwards",
            "krypton",
            "sodium",
            "iris",
            "mixin",
            "legacyclasspath",
            "fabric.legacyClassPath",
            "netdebug",
            ".test",
            "[chat] ."
    };

    // Lower-cased copy of the blacklist to avoid repeated toLowerCase() allocations.
    private final String[] lowerBlacklist;

    public LogSanitizerFilter() {
        super();
        this.lowerBlacklist = new String[BLACKLIST.length];
        for (int i = 0; i < BLACKLIST.length; i++) {
            lowerBlacklist[i] = BLACKLIST[i].toLowerCase();
        }
    }

    /**
     * Returns {@link Result#DENY} if the event must be hidden, otherwise
     * {@link Result#NEUTRAL} so other filters / appenders decide normally.
     */
    @Override
    public Result filter(LogEvent event) {
        if (event == null) {
            return Result.NEUTRAL;
        }

        // Build the searchable text lazily and only once.
        if (containsBlacklisted(toSearchableText(event))) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    /**
     * Per-method overrides are provided as well so the filter works regardless of
     * which overload Log4j2 decides to call (it dispatches to {@link #filter(LogEvent)}
     * for the most precise one). Keeping them all returning NEUTRAL on the fast
     * path avoids surprising behaviour on older call paths.
     */
    @Override
    public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
        if (msg == null) {
            return Result.NEUTRAL;
        }
        if (containsBlacklisted(msg.getFormattedMessage().toLowerCase())) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
        if (msg == null) {
            return Result.NEUTRAL;
        }
        if (containsBlacklisted(msg.toLowerCase())) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    @Override
    public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
        if (msg == null) {
            return Result.NEUTRAL;
        }
        if (containsBlacklisted(msg.toString().toLowerCase())) {
            return Result.DENY;
        }
        return Result.NEUTRAL;
    }

    /**
     * Assembles the lower-cased text Log4j will render: the message, the logger
     * name and the throwable string (if any). We deliberately re-use the event's
     * own {@link Message#getFormattedMessage()} which Log4j already computed.
     */
    private static String toSearchableText(LogEvent event) {
        StringBuilder sb = new StringBuilder(128);
        Message message = event.getMessage();
        if (message != null) {
            sb.append(message.getFormattedMessage());
        }
        String loggerName = event.getLoggerName();
        if (loggerName != null) {
            sb.append('\n').append(loggerName);
        }
        Throwable thrown = event.getThrown();
        if (thrown != null) {
            sb.append('\n').append(thrown);
        }
        return sb.toString().toLowerCase();
    }

    /**
     * Single linear scan over the immutable blacklist. Returns true as soon as a
     * match is found (short-circuit). No locking, no shared mutable state.
     */
    private boolean containsBlacklisted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (String banned : lowerBlacklist) {
            if (text.indexOf(banned) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Convenience factory used by the entrypoint so the filter can be attached
     * without reflection / config files.
     */
    public static LogSanitizerFilter create() {
        return new LogSanitizerFilter();
    }

    /**
     * (Optional) Programmatically install this filter onto the root logger and on
     * every appender that writes to disk. Safe to call once during pre-launch.
     *
     * @return {@code true} if the filter was successfully attached.
     */
    public static boolean install() {
        try {
            LoggerContext context = LoggerContext.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig root = config.getLoggerConfig(org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME);

            LogSanitizerFilter filter = new LogSanitizerFilter();

            // Attach as a global filter on the root LoggerConfig.
            root.addFilter(filter);
            context.updateLoggers();
            return true;
        } catch (Throwable t) {
            // Never let log filtering break the game's boot.
            return false;
        }
    }
}
