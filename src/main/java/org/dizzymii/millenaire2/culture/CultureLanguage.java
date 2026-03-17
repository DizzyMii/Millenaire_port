package org.dizzymii.millenaire2.culture;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores language strings for a culture (villager names, greetings, etc.).
 * Ported from org.millenaire.common.culture.CultureLanguage (Forge 1.12.2).
 */
public class CultureLanguage {
    public String cultureKey = "";
    public Map<String, String> strings = new HashMap<>();

    public CultureLanguage() {}

    public CultureLanguage(String cultureKey) {
        this.cultureKey = cultureKey;
    }

    public void loadFromProperties(java.io.InputStream in) throws java.io.IOException {
        java.util.Properties props = new java.util.Properties();
        props.load(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
        for (String key : props.stringPropertyNames()) {
            strings.put(key, props.getProperty(key));
        }
    }

    public String getString(String key) {
        return strings.getOrDefault(key, key);
    }

    public String getString(String key, String fallback) {
        return strings.getOrDefault(key, fallback);
    }

    public boolean hasString(String key) {
        return strings.containsKey(key);
    }
}
