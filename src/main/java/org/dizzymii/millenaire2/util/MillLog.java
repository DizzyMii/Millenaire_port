package org.dizzymii.millenaire2.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Central logging utility for Millénaire.
 * Wraps SLF4J with level-gated convenience methods matching the original mod's logging patterns.
 */
public class MillLog {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void major(Object source, String message) {
        LOGGER.info("[Mill] {}", message);
    }

    public static void minor(Object source, String message) {
        LOGGER.debug("[Mill] {}", message);
    }

    public static void error(Object source, String message) {
        LOGGER.error("[Mill] {}", message);
    }

    public static void error(Object source, String message, Throwable t) {
        LOGGER.error("[Mill] {}", message, t);
    }

    public static void warn(Object source, String message) {
        LOGGER.warn("[Mill] {}", message);
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
