package org.dizzymii.millenaire2.data;

/**
 * Handles reading and writing culture-specific parameter values.
 * Ported from org.millenaire.common.annotedparameters.CultureValueIO (Forge 1.12.2).
 */
public class CultureValueIO implements ValueIO {

    private final java.util.Map<String, String> values = new java.util.HashMap<>();
    private String cultureKey = "";

    public CultureValueIO() {}

    public CultureValueIO(String cultureKey) {
        this.cultureKey = cultureKey;
    }

    public String getCultureKey() { return cultureKey; }

    public void loadFromProperties(java.util.Properties props) {
        for (String key : props.stringPropertyNames()) {
            values.put(key, props.getProperty(key));
        }
    }

    @Override
    public String readValue(String key) {
        return values.get(key);
    }

    @Override
    public void writeValue(String key, String value) {
        values.put(key, value);
    }

    public java.util.Properties toProperties() {
        java.util.Properties props = new java.util.Properties();
        props.putAll(values);
        return props;
    }
}
