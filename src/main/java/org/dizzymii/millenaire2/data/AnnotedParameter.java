package org.dizzymii.millenaire2.data;

/**
 * Represents an annotated configuration parameter with metadata.
 * Ported from org.millenaire.common.annotedparameters.AnnotedParameter (Forge 1.12.2).
 */
public class AnnotedParameter {
    public String key = "";
    public String defaultValue = "";
    public String description = "";
    public String category = "";

    public AnnotedParameter() {}

    public AnnotedParameter(String key, String defaultValue, String description, String category) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
        this.category = category;
    }

    public String loadFrom(ValueIO io) {
        String val = io.readValue(key);
        return val != null ? val : defaultValue;
    }

    public void saveTo(ValueIO io, String value) {
        io.writeValue(key, value);
    }

    public boolean validate(String value) {
        return value != null && !value.isEmpty();
    }
}
