package org.dizzymii.millenaire2.config;

/**
 * Represents a documented configuration element with description and constraints.
 * Ported from org.millenaire.common.config.DocumentedElement (Forge 1.12.2).
 */
public class DocumentedElement {
    public String key = "";
    public String description = "";
    public Object defaultValue = null;

    public DocumentedElement() {}

    public DocumentedElement(String key, String description, Object defaultValue) {
        this.key = key;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public boolean validate(Object value) {
        if (defaultValue == null) return true;
        return defaultValue.getClass().isInstance(value);
    }

    public String toDocString() {
        return "# " + description + "\n" + key + "=" + (defaultValue != null ? defaultValue.toString() : "");
    }
}
