package org.dizzymii.millenaire2.config;

/**
 * Represents a single Millenaire configuration parameter with type and bounds.
 * Ported from org.millenaire.common.config.MillConfigParameter (Forge 1.12.2).
 */
public class MillConfigParameter {
    public String name = "";
    public String category = "";
    public Object value = null;

    public Object minValue = null;
    public Object maxValue = null;

    public MillConfigParameter() {}

    public MillConfigParameter(String name, String category, Object value) {
        this.name = name;
        this.category = category;
        this.value = value;
    }

    public MillConfigParameter(String name, String category, Object value, Object min, Object max) {
        this(name, category, value);
        this.minValue = min;
        this.maxValue = max;
    }

    public boolean isInBounds() {
        if (value instanceof Number num && minValue instanceof Number min && maxValue instanceof Number max) {
            return num.doubleValue() >= min.doubleValue() && num.doubleValue() <= max.doubleValue();
        }
        return true;
    }

    public String serialize() {
        return category + "." + name + "=" + (value != null ? value.toString() : "");
    }

    public void deserialize(String line) {
        int eq = line.indexOf('=');
        if (eq > 0 && value instanceof Integer) {
            try { value = Integer.parseInt(line.substring(eq + 1).trim()); } catch (NumberFormatException ignored) {}
        } else if (eq > 0 && value instanceof Boolean) {
            value = Boolean.parseBoolean(line.substring(eq + 1).trim());
        } else if (eq > 0) {
            value = line.substring(eq + 1).trim();
        }
    }
}
