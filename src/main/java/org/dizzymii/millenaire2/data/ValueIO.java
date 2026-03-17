package org.dizzymii.millenaire2.data;

/**
 * Generic value I/O interface for reading/writing parameter values.
 * Ported from org.millenaire.common.annotedparameters.ValueIO (Forge 1.12.2).
 */
public interface ValueIO {
    String readValue(String key);
    void writeValue(String key, String value);

    default String readValue(String key, String defaultValue) {
        String val = readValue(key);
        return val != null ? val : defaultValue;
    }

    default int readInt(String key, int defaultValue) {
        String val = readValue(key);
        if (val == null) return defaultValue;
        try { return Integer.parseInt(val.trim()); } catch (NumberFormatException e) { return defaultValue; }
    }

    default boolean readBoolean(String key, boolean defaultValue) {
        String val = readValue(key);
        if (val == null) return defaultValue;
        return Boolean.parseBoolean(val.trim());
    }

    default double readDouble(String key, double defaultValue) {
        String val = readValue(key);
        if (val == null) return defaultValue;
        try { return Double.parseDouble(val.trim()); } catch (NumberFormatException e) { return defaultValue; }
    }
}
