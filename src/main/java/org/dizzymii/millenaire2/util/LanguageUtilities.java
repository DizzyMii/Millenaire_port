package org.dizzymii.millenaire2.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.io.BufferedReader;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles Millénaire's custom multi-language system.
 * The mod ships its own translation files (separate from Minecraft's lang system)
 * for UI strings, quest text, culture strings, help pages, and parchments.
 *
 * Ported from org.millenaire.common.utilities.LanguageUtilities.
 */
public class LanguageUtilities {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Minecraft color formatting codes
    public static final char BLACK = '0';
    public static final char DARKBLUE = '1';
    public static final char DARKGREEN = '2';
    public static final char LIGHTBLUE = '3';
    public static final char DARKRED = '4';
    public static final char PURPLE = '5';
    public static final char ORANGE = '6';
    public static final char LIGHTGREY = '7';
    public static final char DARKGREY = '8';
    public static final char BLUE = '9';
    public static final char LIGHTGREEN = 'a';
    public static final char CYAN = 'b';
    public static final char LIGHTRED = 'c';
    public static final char PINK = 'd';
    public static final char YELLOW = 'e';
    public static final char WHITE = 'f';

    public static String loadedLanguage = null;
    private static LanguageData mainLanguage = null;
    private static LanguageData fallbackLanguage = null;
    private static final Map<String, LanguageData> loadedLanguages = new HashMap<>();

    private static String effectiveLanguage = "en";
    private static String fallbackLanguageCode = "en";

    /**
     * Load the mod's language files from disk.
     * @param minecraftLanguage the current Minecraft language code (e.g. "en_us"), or null
     */
    public static void loadLanguages(String minecraftLanguage) {
        // Determine effective language
        if (minecraftLanguage != null && !minecraftLanguage.isEmpty()) {
            // Map MC language codes to Mill codes: "en_us" -> "en", "fr_fr" -> "fr"
            effectiveLanguage = minecraftLanguage.split("_")[0].toLowerCase();
        }

        if (loadedLanguage != null && loadedLanguage.equals(effectiveLanguage)) {
            return;
        }

        LOGGER.info("Loading language: " + effectiveLanguage);
        loadedLanguage = effectiveLanguage;

        List<File> languageDirs = getLanguageDirs();

        // Load main language
        mainLanguage = new LanguageData(effectiveLanguage);
        mainLanguage.loadFromDisk(languageDirs);
        loadedLanguages.put(effectiveLanguage, mainLanguage);

        // Load fallback language
        if (effectiveLanguage.equals(fallbackLanguageCode)) {
            fallbackLanguage = mainLanguage;
        } else {
            fallbackLanguage = new LanguageData(fallbackLanguageCode);
            fallbackLanguage.loadFromDisk(languageDirs);
            loadedLanguages.put(fallbackLanguageCode, fallbackLanguage);
        }

        // Always ensure English and French are available
        if (!loadedLanguages.containsKey("en")) {
            LanguageData en = new LanguageData("en");
            en.loadFromDisk(languageDirs);
            loadedLanguages.put("en", en);
        }
        if (!loadedLanguages.containsKey("fr")) {
            LanguageData fr = new LanguageData("fr");
            fr.loadFromDisk(languageDirs);
            loadedLanguages.put("fr", fr);
        }

        LOGGER.info("Language loading complete: " + effectiveLanguage
                + " (fallback: " + fallbackLanguageCode + ")");
    }

    /**
     * Get directories that contain language files.
     */
    public static List<File> getLanguageDirs() {
        ArrayList<File> dirs = new ArrayList<>();
        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        File langDir = new File(contentDir, "languages");
        if (langDir.exists()) {
            dirs.add(langDir);
        }
        File customDir = MillCommonUtilities.getMillenaireCustomContentDir();
        File customLangDir = new File(customDir, "languages");
        if (customLangDir.exists()) {
            dirs.add(customLangDir);
        }
        return dirs;
    }

    /**
     * Look up a raw translation string by key.
     */
    public static String getRawString(String key, boolean mustFind) {
        return getRawString(key, mustFind, true, true);
    }

    public static String getRawString(String key, boolean mustFind, boolean useMain, boolean useFallback) {
        if (useMain && mainLanguage != null && mainLanguage.strings.containsKey(key)) {
            return mainLanguage.strings.get(key);
        }
        if (useFallback && fallbackLanguage != null && fallbackLanguage.strings.containsKey(key)) {
            return fallbackLanguage.strings.get(key);
        }
        if (mustFind) {
            return key;
        }
        return null;
    }

    /**
     * Get a translated string, filling in the player name.
     */
    public static String string(String key) {
        if (!isTranslationLoaded()) return "";
        key = key.toLowerCase();
        String raw = getRawString(key, true);
        return fillInName(raw);
    }

    /**
     * Get a translated string with positional replacements.
     */
    public static String string(String key, String... values) {
        String s = string(key);
        if (!s.equalsIgnoreCase(key)) {
            int pos = 0;
            for (String value : values) {
                s = value != null
                        ? s.replaceAll("<" + pos + ">", unknownString(value))
                        : s.replaceAll("<" + pos + ">", "");
                pos++;
            }
        } else {
            for (String value : values) {
                s = s + ":" + value;
            }
        }
        return s;
    }

    /**
     * Get a quest-specific translation string.
     */
    public static String questString(String key, boolean required) {
        key = key.toLowerCase();
        if (mainLanguage != null && mainLanguage.questStrings.containsKey(key)) {
            return mainLanguage.questStrings.get(key);
        }
        if (fallbackLanguage != null && fallbackLanguage.questStrings.containsKey(key)) {
            return fallbackLanguage.questStrings.get(key);
        }
        return required ? key : null;
    }

    /**
     * Try to resolve a string that might be a special key (item, building, culture string).
     */
    public static String unknownString(String key) {
        if (key == null) return "";
        if (!isTranslationLoaded()) return key;

        // Try direct translation
        String raw = getRawString(key, false);
        if (raw != null) {
            return fillInName(raw);
        }
        return key;
    }

    public static boolean hasString(String key) {
        if (!isTranslationLoaded()) return false;
        key = key.toLowerCase();
        String raw = getRawString(key, false);
        return raw != null;
    }

    public static boolean isTranslationLoaded() {
        return mainLanguage != null;
    }

    public static String fillInName(String s) {
        if (s == null) return "";
        // Player name substitution will be wired up when entity system is ported
        return s;
    }

    public static String removeAccent(String source) {
        return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036f]", "");
    }

    public static MutableComponent textComponent(String key) {
        return Component.literal(string(key));
    }

    public static MutableComponent textComponent(String key, String... values) {
        return Component.literal(string(key, values));
    }

    public static List<List<String>> getHelp(int id) {
        if (mainLanguage != null && mainLanguage.help.containsKey(id)) {
            return mainLanguage.help.get(id);
        }
        if (fallbackLanguage != null && fallbackLanguage.help.containsKey(id)) {
            return fallbackLanguage.help.get(id);
        }
        return null;
    }

    public static List<List<String>> getParchment(int id) {
        if (mainLanguage != null && mainLanguage.texts.containsKey(id)) {
            return mainLanguage.texts.get(id);
        }
        if (fallbackLanguage != null && fallbackLanguage.texts.containsKey(id)) {
            return fallbackLanguage.texts.get(id);
        }
        return null;
    }

    public static List<String> getHoFData() {
        ArrayList<String> hofData = new ArrayList<>();
        try {
            File hofFile = new File(MillCommonUtilities.getMillenaireContentDir(), "hof.txt");
            if (!hofFile.exists()) return hofData;
            BufferedReader reader = MillCommonUtilities.getReader(hofFile);
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("//")) {
                    hofData.add(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            LOGGER.error("Error loading HoF data", e);
        }
        return hofData;
    }

    public static LanguageData getMainLanguage() {
        return mainLanguage;
    }

    public static LanguageData getFallbackLanguage() {
        return fallbackLanguage;
    }

    public static LanguageData getLoadedLanguage(String code) {
        return loadedLanguages.get(code);
    }
}
