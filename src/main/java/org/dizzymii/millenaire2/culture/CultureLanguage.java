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
    // TODO: Implement language file loading and string lookup
}
