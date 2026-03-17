package org.dizzymii.millenaire2.data;

/**
 * Generic value I/O interface for reading/writing parameter values.
 * Ported from org.millenaire.common.annotedparameters.ValueIO (Forge 1.12.2).
 */
public interface ValueIO {
    String readValue(String key);
    void writeValue(String key, String value);
    // TODO: Implement default value handling and type coercion
}
