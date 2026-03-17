/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 */
package org.millenaire.common.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.millenaire.client.book.TravelBookExporter;
import org.millenaire.common.buildingplan.BuildingDevUtilities;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.utilities.LanguageData;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class LanguageUtilities {
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

    public static void applyLanguage() {
        InvItem iv = InvItem.createInvItem(MillItems.SUMMONING_WAND, 1);
        MillLog.major(null, "LanguageData loaded: " + MillConfigValues.effective_language + ". Wand invitem name: " + iv.getName());
        if (MillConfigValues.generateBuildingRes && !Mill.proxy.isTrueServer()) {
            MillLog.major(null, "Generating building res file.");
            BuildingDevUtilities.generateBuildingRes();
            try {
                BuildingDevUtilities.generateWikiTable();
            }
            catch (MillLog.MillenaireException e) {
                MillLog.printException(e);
            }
            MillLog.major(null, "Generated building res file.");
        }
    }

    public static String fillInName(String s) {
        if (s == null) {
            return "";
        }
        EntityPlayer player = Mill.proxy.getTheSinglePlayer();
        if (player != null) {
            return s.replaceAll("\\$name", player.func_70005_c_());
        }
        return s;
    }

    public static List<List<String>> getHelp(int id) {
        if (MillConfigValues.mainLanguage.help.containsKey(id)) {
            return MillConfigValues.mainLanguage.help.get(id);
        }
        if (MillConfigValues.fallbackLanguage.help.containsKey(id)) {
            return MillConfigValues.fallbackLanguage.help.get(id);
        }
        return null;
    }

    public static List<String> getHoFData() {
        ArrayList<String> hofData = new ArrayList<String>();
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(new File(MillCommonUtilities.getMillenaireContentDir(), "hof.txt"));
            while ((line = reader.readLine()) != null) {
                if ((line = line.trim()).length() <= 0 || line.startsWith("//")) continue;
                hofData.add(line);
            }
        }
        catch (Exception e) {
            MillLog.printException("Error when loading HoF: ", e);
        }
        return hofData;
    }

    public static List<File> getLanguageDirs() {
        ArrayList<File> languageDirs = new ArrayList<File>();
        for (File dir : Mill.loadingDirs) {
            File languageDir = new File(dir, "languages");
            if (!languageDir.exists()) continue;
            languageDirs.add(languageDir);
        }
        languageDirs.add(new File(MillCommonUtilities.getMillenaireCustomContentDir(), "languages"));
        return languageDirs;
    }

    public static List<List<String>> getParchment(int id) {
        if (MillConfigValues.mainLanguage.texts.containsKey(id)) {
            return MillConfigValues.mainLanguage.texts.get(id);
        }
        if (MillConfigValues.fallbackLanguage.texts.containsKey(id)) {
            return MillConfigValues.fallbackLanguage.texts.get(id);
        }
        return null;
    }

    public static String getRawString(String key, boolean mustFind) {
        return LanguageUtilities.getRawString(key, mustFind, true, true);
    }

    public static String getRawString(String key, boolean mustFind, boolean main, boolean fallback) {
        if (main && MillConfigValues.mainLanguage != null && MillConfigValues.mainLanguage.strings.containsKey(key)) {
            return MillConfigValues.mainLanguage.strings.get(key);
        }
        if (main && MillConfigValues.serverMainLanguage != null && MillConfigValues.serverMainLanguage.strings.containsKey(key)) {
            return MillConfigValues.serverMainLanguage.strings.get(key);
        }
        if (fallback && MillConfigValues.fallbackLanguage != null && MillConfigValues.fallbackLanguage.strings.containsKey(key)) {
            return MillConfigValues.fallbackLanguage.strings.get(key);
        }
        if (fallback && MillConfigValues.serverFallbackLanguage != null && MillConfigValues.serverFallbackLanguage.strings.containsKey(key)) {
            return MillConfigValues.serverFallbackLanguage.strings.get(key);
        }
        if (mustFind && MillConfigValues.LogTranslation >= 1) {
            MillLog.error(null, "String not found: " + key);
        }
        if (mustFind) {
            return key;
        }
        return null;
    }

    public static String getRawStringFallbackOnly(String key, boolean mustFind) {
        return LanguageUtilities.getRawString(key, mustFind, false, true);
    }

    public static String getRawStringMainOnly(String key, boolean mustFind) {
        return LanguageUtilities.getRawString(key, mustFind, true, false);
    }

    public static boolean hasString(String key) {
        String rawResult;
        if (!LanguageUtilities.isTranslationLoaded()) {
            return false;
        }
        return !(key = key.toLowerCase()).equalsIgnoreCase(rawResult = LanguageUtilities.getRawString(key, true));
    }

    public static boolean isTranslationLoaded() {
        return MillConfigValues.mainLanguage != null;
    }

    public static void loadLanguages(String minecraftLanguage) {
        Object l;
        MillConfigValues.effective_language = !MillConfigValues.main_language.equals("") ? MillConfigValues.main_language : (minecraftLanguage != null ? minecraftLanguage : "fr");
        if (loadedLanguage != null && loadedLanguage.equals(MillConfigValues.effective_language)) {
            return;
        }
        MillLog.major(null, "Loading language: " + MillConfigValues.effective_language);
        loadedLanguage = MillConfigValues.effective_language;
        List<File> languageDirs = LanguageUtilities.getLanguageDirs();
        MillConfigValues.mainLanguage = new LanguageData(MillConfigValues.effective_language, false);
        MillConfigValues.mainLanguage.loadFromDisk(languageDirs);
        if (MillConfigValues.main_language.equals(MillConfigValues.fallback_language)) {
            MillConfigValues.fallbackLanguage = MillConfigValues.mainLanguage;
        } else {
            MillConfigValues.fallbackLanguage = new LanguageData(MillConfigValues.fallback_language, false);
            MillConfigValues.fallbackLanguage.loadFromDisk(languageDirs);
        }
        if (MillConfigValues.loadAllLanguages) {
            File mainDir = languageDirs.get(0);
            for (File lang : mainDir.listFiles()) {
                String key;
                if (!lang.isDirectory() || lang.isHidden() || MillConfigValues.loadedLanguages.containsKey(key = lang.getName().toLowerCase())) continue;
                LanguageData l2 = new LanguageData(key, false);
                l2.loadFromDisk(languageDirs);
            }
        }
        if (!MillConfigValues.loadedLanguages.containsKey("fr")) {
            l = new LanguageData("fr", false);
            ((LanguageData)l).loadFromDisk(languageDirs);
        }
        if (!MillConfigValues.loadedLanguages.containsKey("en")) {
            l = new LanguageData("en", false);
            ((LanguageData)l).loadFromDisk(languageDirs);
        }
        for (Culture culture : Culture.ListCultures) {
            culture.loadLanguages(languageDirs, MillConfigValues.effective_language, MillConfigValues.fallback_language);
        }
        LanguageUtilities.applyLanguage();
        if (MillConfigValues.DEV && MillConfigValues.loadedLanguages.containsKey("en")) {
            MillConfigValues.loadedLanguages.get("en").testTravelBookCompletion();
        }
        if (MillConfigValues.generateBuildingRes && !Mill.proxy.isTrueServer()) {
            ArrayList<LanguageData> list = new ArrayList<LanguageData>(MillConfigValues.loadedLanguages.values());
            for (LanguageData l3 : list) {
                if (!l3.language.equals("en") && !l3.language.equals("fr")) continue;
                BuildingDevUtilities.generateTranslatedHoFData(l3);
            }
        }
        if (MillConfigValues.generateTranslationGap) {
            File file;
            HashMap<String, Integer> percentageComplete = new HashMap<String, Integer>();
            ArrayList<LanguageData> arrayList = new ArrayList<LanguageData>(MillConfigValues.loadedLanguages.values());
            String refLanguage = MillConfigValues.fallback_language;
            LanguageData ref = null;
            if (MillConfigValues.loadedLanguages.containsKey(refLanguage)) {
                ref = MillConfigValues.loadedLanguages.get(refLanguage);
            } else {
                ref = new LanguageData(refLanguage, false);
                ref.loadFromDisk(languageDirs);
            }
            Map<String, String> referenceLangString = ref.loadLangFileFromDisk(languageDirs);
            LanguageData altRef = null;
            if (ref.language.equals("en")) {
                if (MillConfigValues.loadedLanguages.containsKey("fr")) {
                    altRef = MillConfigValues.loadedLanguages.get("fr");
                } else {
                    altRef = new LanguageData("fr", false);
                    altRef.loadFromDisk(languageDirs);
                }
            } else if (MillConfigValues.loadedLanguages.containsKey("en")) {
                altRef = MillConfigValues.loadedLanguages.get("en");
            } else {
                altRef = new LanguageData("en", false);
                altRef.loadFromDisk(languageDirs);
            }
            Map<String, String> altReferenceLangString = altRef.loadLangFileFromDisk(languageDirs);
            for (LanguageData l4 : arrayList) {
                if (l4.language.equals(ref.language)) {
                    l4.compareWithLanguage(languageDirs, percentageComplete, altRef, altReferenceLangString);
                    continue;
                }
                l4.compareWithLanguage(languageDirs, percentageComplete, ref, referenceLangString);
            }
            File translationGapDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Translation gaps");
            if (!translationGapDir.exists()) {
                translationGapDir.mkdirs();
            }
            if ((file = new File(translationGapDir, "Results.txt")).exists()) {
                file.delete();
            }
            try {
                BufferedWriter writer = MillCommonUtilities.getWriter(file);
                for (String key : percentageComplete.keySet()) {
                    writer.write(key + ": " + percentageComplete.get(key) + "%" + "\n");
                }
                writer.close();
            }
            catch (Exception e) {
                MillLog.printException(e);
            }
            MillLog.major(null, "Generated translation gap files for " + percentageComplete.size() + " languages.");
        }
        if (MillConfigValues.DEV) {
            MillConfigValues.writeBaseConfigFile();
        }
        if (MillConfigValues.generateTravelBookExport) {
            TravelBookExporter.exportTravelBookData();
        }
    }

    public static String questString(String key, boolean required) {
        return LanguageUtilities.questString(key, true, true, required);
    }

    public static String questString(String key, boolean main, boolean fallback, boolean required) {
        key = key.toLowerCase();
        if (main && MillConfigValues.mainLanguage != null && MillConfigValues.mainLanguage.questStrings.containsKey(key)) {
            return MillConfigValues.mainLanguage.questStrings.get(key);
        }
        if (main && MillConfigValues.serverMainLanguage != null && MillConfigValues.serverMainLanguage.questStrings.containsKey(key)) {
            return MillConfigValues.serverMainLanguage.questStrings.get(key);
        }
        if (fallback && MillConfigValues.fallbackLanguage != null && MillConfigValues.fallbackLanguage.questStrings.containsKey(key)) {
            return MillConfigValues.fallbackLanguage.questStrings.get(key);
        }
        if (fallback && MillConfigValues.serverFallbackLanguage != null && MillConfigValues.serverFallbackLanguage.questStrings.containsKey(key)) {
            return MillConfigValues.serverFallbackLanguage.questStrings.get(key);
        }
        if (required) {
            return key;
        }
        return null;
    }

    public static String removeAccent(String source) {
        return Normalizer.normalize(source, Normalizer.Form.NFD).replaceAll("[\u0300-\u036f]", "");
    }

    public static String string(String key) {
        String rawResult;
        if (!LanguageUtilities.isTranslationLoaded()) {
            return "";
        }
        if ((key = key.toLowerCase()).equalsIgnoreCase(rawResult = LanguageUtilities.getRawString(key, true)) && MillConfigValues.DEV && MillConfigValues.LogTranslation >= 1) {
            MillLog.temp(null, "Reloading because of missing key:" + key);
            LanguageUtilities.loadLanguages(null);
            rawResult = LanguageUtilities.getRawString(key, true);
        }
        return LanguageUtilities.fillInName(rawResult);
    }

    public static String string(String key, String ... values) {
        String s = LanguageUtilities.string(key);
        if (!s.equalsIgnoreCase(key)) {
            int pos = 0;
            for (String value : values) {
                s = value != null ? s.replaceAll("<" + pos + ">", LanguageUtilities.unknownString(value)) : s.replaceAll("<" + pos + ">", "");
                ++pos;
            }
        } else {
            for (String value : values) {
                s = s + ":" + value;
            }
        }
        return s;
    }

    public static String string(String[] values) {
        if (values.length == 0) {
            return "";
        }
        String s = LanguageUtilities.unknownString(values[0]);
        int pos = -1;
        for (String value : values) {
            if (pos > -1) {
                s = value != null ? s.replaceAll("<" + pos + ">", LanguageUtilities.unknownString(value)) : s.replaceAll("<" + pos + ">", "");
            }
            ++pos;
        }
        return LanguageUtilities.fillInName(s);
    }

    public static ITextComponent textComponent(String key) {
        return new TextComponentString(LanguageUtilities.string(key));
    }

    public static ITextComponent textComponent(String key, String ... values) {
        return new TextComponentString(LanguageUtilities.string(key, values));
    }

    public static String unknownString(String key) {
        String rawKey;
        int level;
        int variation;
        String buildingKey;
        BuildingPlanSet set;
        String cultureKey;
        Culture culture;
        if (key == null) {
            return "";
        }
        if (!LanguageUtilities.isTranslationLoaded()) {
            return key;
        }
        if (key.startsWith("_item:")) {
            int id = Integer.parseInt(key.split(":")[1]);
            int meta = Integer.parseInt(key.split(":")[2]);
            InvItem item = InvItem.createInvItem(MillCommonUtilities.getItemById(id), meta);
            return item.getName();
        }
        if (key.startsWith("_buildingGame:") && (culture = Culture.getCultureByName(cultureKey = key.split(":")[1])) != null && (set = culture.getBuildingPlanSet(buildingKey = key.split(":")[2])) != null && (variation = Integer.parseInt(key.split(":")[3])) < set.plans.size() && (level = Integer.parseInt(key.split(":")[4])) < set.plans.get(variation).length) {
            BuildingPlan plan = set.plans.get(variation)[level];
            return plan.getNameTranslated();
        }
        if (key.startsWith("culture:")) {
            cultureKey = key.split(":")[1];
            String stringKey = key.split(":")[2];
            Culture culture2 = Culture.getCultureByName(cultureKey);
            if (culture2 != null) {
                return culture2.getCultureString(stringKey);
            }
        }
        if ((rawKey = LanguageUtilities.getRawString(key, false)) != null) {
            return LanguageUtilities.fillInName(rawKey);
        }
        return key;
    }
}

