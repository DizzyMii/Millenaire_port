package org.dizzymii.millenaire2.util;

import com.mojang.logging.LogUtils;
import org.dizzymii.millenaire2.MillConfig;
import org.slf4j.Logger;

/**
 * Central logging utility for Millénaire.
 * Wraps SLF4J with level-gated convenience methods matching the original mod's logging patterns.
 * The {@code source} parameter is used to tag log messages for easier debugging.
 */
public class MillLog {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static String tag(Object source) {
        if (source instanceof String s) return s;
        if (source == null) return "Mill";
        return source.getClass().getSimpleName();
    }

    public static void major(Object source, String message) {
        if (MillConfig.logGeneral() >= 1) {
            LOGGER.info("[Mill/{}] {}", tag(source), message);
        }
    }

    public static void minor(Object source, String message) {
        if (MillConfig.logGeneral() >= 2) {
            LOGGER.debug("[Mill/{}] {}", tag(source), message);
        }
    }

    public static void error(Object source, String message) {
        LOGGER.error("[Mill/{}] {}", tag(source), message);
    }

    public static void error(Object source, String message, Throwable t) {
        LOGGER.error("[Mill/{}] {}", tag(source), message, t);
    }

    public static void warn(Object source, String message) {
        LOGGER.warn("[Mill/{}] {}", tag(source), message);
    }

    public static void printException(MillenaireException e) {
        LOGGER.error("[Mill] Exception: {}", e.getMessage(), e);
    }

    /**
     * Custom exception type for Millénaire-specific errors.
     */
    public static class MillenaireException extends Exception {
        public MillenaireException(String message) {
            super(message);
        }

        public MillenaireException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
