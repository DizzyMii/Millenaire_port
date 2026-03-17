/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package org.millenaire.common.utilities;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.quest.QuestStep;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;

public class LanguageData {
    private static final int PARCHMENT = 0;
    private static final int HELP = 1;
    public String language;
    public String topLevelLanguage = null;
    public boolean serverContent;
    public HashMap<String, String> strings = new HashMap();
    public HashMap<String, String> questStrings = new HashMap();
    public HashMap<Integer, List<List<String>>> texts = new HashMap();
    public HashMap<Integer, String> textsVersion = new HashMap();
    public HashMap<Integer, List<List<String>>> help = new HashMap();
    public HashMap<Integer, String> helpVersion = new HashMap();

    public static void printErrors(String languageKey, BufferedWriter writer, Set<String> errors, String message) throws IOException {
        boolean consolePrint;
        boolean bl = consolePrint = MillConfigValues.DEV && languageKey.equals("en");
        if (errors.size() > 0) {
            ArrayList errorsList = Lists.newArrayList(errors);
            Collections.sort(errorsList);
            writer.write(message + "\n" + "\n");
            if (consolePrint) {
                MillLog.writeTextRaw(message);
            }
            for (String s : errorsList) {
                writer.write(s + "\n");
                if (!consolePrint) continue;
                MillLog.writeTextRaw(s);
            }
            writer.write("\n");
            errors.clear();
        }
    }

    public LanguageData(String key, boolean serverContent) {
        this.language = key;
        if (this.language.split("_").length > 1) {
            this.topLevelLanguage = this.language.split("_")[0];
        }
        this.serverContent = serverContent;
    }

    public void compareWithLanguage(List<File> languageDirs, HashMap<String, Integer> percentages, LanguageData ref, Map<String, String> referenceLangStrings) {
        File file;
        File translationGapDir = new File(MillCommonUtilities.getMillenaireCustomContentDir(), "Translation gaps");
        if (!translationGapDir.exists()) {
            translationGapDir.mkdirs();
        }
        if ((file = new File(translationGapDir, this.language + "-" + ref.language + ".txt")).exists()) {
            file.delete();
        }
        try {
            int nbValues2;
            int nbValues;
            int translationsMissing = 0;
            int translationsDone = 0;
            BufferedWriter writer = MillCommonUtilities.getWriter(file);
            writer.write("Translation comparison between " + this.language + " and " + ref.language + ", version " + "8.1.2" + ", date: " + MillLog.now() + "\n" + "\n");
            HashSet<String> errors = new HashSet<String>();
            HashSet<String> errors2 = new HashSet<String>();
            Map<String, String> langStrings = this.loadLangFileFromDisk(languageDirs);
            ArrayList<String> keys = new ArrayList<String>(referenceLangStrings.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                if (!langStrings.containsKey(key)) {
                    errors.add(key + "=");
                    ++translationsMissing;
                    continue;
                }
                nbValues = referenceLangStrings.get(key).split("<").length - 1;
                if (nbValues != (nbValues2 = langStrings.get(key).split("<").length - 1)) {
                    errors2.add(key);
                    ++translationsMissing;
                    continue;
                }
                ++translationsDone;
            }
            this.printErrors(writer, errors, "Gap with " + ref.language + " in .lang file: ");
            this.printErrors(writer, errors2, "Mismatched number of parameters with " + ref.language + " in the .lang file: ");
            keys = new ArrayList<String>(ref.strings.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                if (!this.strings.containsKey(key)) {
                    errors.add(key + "=");
                    ++translationsMissing;
                    continue;
                }
                nbValues = ref.strings.get(key).split("<").length - 1;
                if (nbValues != (nbValues2 = this.strings.get(key).split("<").length - 1)) {
                    errors2.add(key);
                    ++translationsMissing;
                    continue;
                }
                ++translationsDone;
            }
            this.printErrors(writer, errors, "Gap with " + ref.language + " in strings.txt file: ");
            this.printErrors(writer, errors2, "Mismatched number of parameters with " + ref.language + " in the strings.txt file: ");
            keys = new ArrayList<String>(ref.questStrings.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                if (!this.questStrings.containsKey(key)) {
                    errors.add(key);
                    ++translationsMissing;
                    continue;
                }
                ++translationsDone;
            }
            this.printErrors(writer, errors, "Gap with " + ref.language + " in quest files: ");
            for (Goal goal : Goal.goals.values()) {
                if (this.strings.containsKey("goal." + goal.labelKey(null)) || ref.strings.containsKey("goal." + goal.labelKey(null))) continue;
                errors.add("goal." + goal.labelKey(null) + "=");
            }
            this.printErrors(writer, errors, "Goals with labels missing in both " + ref.language + " and " + this.language + ":");
            Iterator<Object> iterator = ref.texts.keySet().iterator();
            while (iterator.hasNext()) {
                int id = (Integer)iterator.next();
                if (!this.texts.containsKey(id)) {
                    errors.add("Parchment " + id + " is missing.");
                    translationsMissing += 10;
                    continue;
                }
                if (!this.textsVersion.get(id).equals(ref.textsVersion.get(id))) {
                    errors.add("Parchment " + id + " has a different version: it is at version " + this.textsVersion.get(id) + " while " + ref.language + " parchment is at " + ref.textsVersion.get(id));
                    translationsMissing += 5;
                    continue;
                }
                translationsDone += 10;
            }
            this.printErrors(writer, errors, "Differences in parchments with " + ref.language + ":");
            iterator = ref.help.keySet().iterator();
            while (iterator.hasNext()) {
                int id = (Integer)iterator.next();
                if (!this.help.containsKey(id)) {
                    errors.add("Help " + id + " is missing.");
                    translationsMissing += 10;
                    continue;
                }
                if (!this.helpVersion.get(id).equals(ref.helpVersion.get(id))) {
                    errors.add("Help " + id + " has a different version: it is at version " + this.helpVersion.get(id) + " while " + ref.language + " parchment is at " + ref.helpVersion.get(id));
                    translationsMissing += 5;
                    continue;
                }
                translationsDone += 10;
            }
            this.printErrors(writer, errors, "Differences in help files with " + ref.language + ":");
            for (Culture c : Culture.ListCultures) {
                int[] res = c.compareCultureLanguages(this.language, ref.language, writer);
                translationsDone += res[0];
                translationsMissing += res[1];
            }
            int percentDone = translationsDone + translationsMissing > 0 ? translationsDone * 100 / (translationsDone + translationsMissing) : 0;
            percentages.put(this.language, percentDone);
            writer.write("Traduction completness: " + percentDone + "%" + "\n");
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            MillLog.printException(e);
        }
    }

    public void loadFromDisk(List<File> languageDirs) {
        for (File languageDir : languageDirs) {
            File stringFile;
            File effectiveLanguageDir = new File(languageDir, this.language);
            if (!effectiveLanguageDir.exists()) {
                effectiveLanguageDir = new File(languageDir, this.language.split("_")[0]);
            }
            if ((stringFile = new File(effectiveLanguageDir, "strings.txt")).exists()) {
                this.loadStrings(this.strings, stringFile);
            }
            if ((stringFile = new File(effectiveLanguageDir, "travelbook.txt")).exists()) {
                this.loadStrings(this.strings, stringFile);
            }
            if (!effectiveLanguageDir.exists()) continue;
            for (File file : effectiveLanguageDir.listFiles(new MillCommonUtilities.PrefixExtFileFilter("quests", "txt"))) {
                this.loadStrings(this.questStrings, file);
            }
        }
        for (Quest q : Quest.quests.values()) {
            for (QuestStep step : q.steps) {
                if (step.labels.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "label", step.labels.get(this.language));
                } else if (this.topLevelLanguage != null && step.labels.containsKey(this.topLevelLanguage)) {
                    this.questStrings.put(step.getStringKey() + "label", step.labels.get(this.topLevelLanguage));
                }
                if (step.descriptions.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "description", step.descriptions.get(this.language));
                } else if (this.topLevelLanguage != null && step.descriptions.containsKey(this.topLevelLanguage)) {
                    this.questStrings.put(step.getStringKey() + "description", step.descriptions.get(this.topLevelLanguage));
                }
                if (step.descriptionsSuccess.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "description_success", step.descriptionsSuccess.get(this.language));
                } else if (this.topLevelLanguage != null && step.descriptionsSuccess.containsKey(this.topLevelLanguage)) {
                    this.questStrings.put(step.getStringKey() + "description_success", step.descriptionsSuccess.get(this.topLevelLanguage));
                }
                if (step.descriptionsRefuse.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "description_refuse", step.descriptionsRefuse.get(this.language));
                } else if (this.topLevelLanguage != null && step.descriptionsRefuse.containsKey(this.topLevelLanguage)) {
                    this.questStrings.put(step.getStringKey() + "description_refuse", step.descriptionsRefuse.get(this.topLevelLanguage));
                }
                if (step.descriptionsTimeUp.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "description_timeup", step.descriptionsTimeUp.get(this.language));
                } else if (this.topLevelLanguage != null && step.descriptionsTimeUp.containsKey(this.topLevelLanguage)) {
                    this.questStrings.put(step.getStringKey() + "description_timeup", step.descriptionsTimeUp.get(this.topLevelLanguage));
                }
                if (step.listings.containsKey(this.language)) {
                    this.questStrings.put(step.getStringKey() + "listing", step.listings.get(this.language));
                    continue;
                }
                if (this.topLevelLanguage == null || !step.listings.containsKey(this.topLevelLanguage)) continue;
                this.questStrings.put(step.getStringKey() + "listing", step.listings.get(this.topLevelLanguage));
            }
        }
        this.loadTextFiles(languageDirs, 0);
        this.loadTextFiles(languageDirs, 1);
        if (!MillConfigValues.loadedLanguages.containsKey(this.language)) {
            MillConfigValues.loadedLanguages.put(this.language, this);
        }
    }

    public Map<String, String> loadLangFileFromDisk(List<File> languageDirs) {
        HashMap<String, String> values = new HashMap<String, String>();
        for (File languageDir : languageDirs) {
            File effectiveLanguageDir = new File(languageDir, this.language);
            if (!effectiveLanguageDir.exists()) {
                effectiveLanguageDir = new File(languageDir, this.language.split("_")[0]);
            }
            if (!effectiveLanguageDir.exists()) continue;
            for (File file : effectiveLanguageDir.listFiles(new MillCommonUtilities.ExtFileFilter("lang"))) {
                this.loadStrings(values, file);
            }
        }
        return values;
    }

    private void loadStrings(Map<String, String> strings, File file) {
        try {
            String line;
            BufferedReader reader = MillCommonUtilities.getReader(file);
            while ((line = reader.readLine()) != null) {
                String key;
                if ((line = line.trim()).length() <= 0 || line.startsWith("//")) continue;
                String[] temp = line.split("=");
                if (temp.length == 2) {
                    key = temp[0].trim().toLowerCase();
                    String value = temp[1].trim();
                    if (strings.containsKey(key)) {
                        MillLog.error(null, "Key " + key + " is present more than once in " + file.getAbsolutePath());
                        continue;
                    }
                    strings.put(key, value);
                    continue;
                }
                if (line.endsWith("=") && temp.length > 0) {
                    key = temp[0].toLowerCase();
                    if (strings.containsKey(key)) {
                        MillLog.error(null, "Key " + key + " is present more than once in " + file.getAbsolutePath());
                        continue;
                    }
                    strings.put(key, "");
                    continue;
                }
                if (!line.contains("====") && !line.contains("<<<<<") && !line.contains(">")) continue;
                MillLog.error(null, "Git conflict lines present in " + file.getAbsolutePath());
            }
            reader.close();
        }
        catch (Exception e) {
            MillLog.printException("Excption reading file " + file.getAbsolutePath(), e);
            return;
        }
    }

    public void loadTextFiles(List<File> languageDirs, int type) {
        String dirName = type == 0 ? "parchments" : "help";
        String filePrefix = type == 0 ? "parchment" : "help";
        for (File languageDir : languageDirs) {
            File parchmentsDir = new File(new File(languageDir, this.language), dirName);
            if (!parchmentsDir.exists()) {
                parchmentsDir = new File(new File(languageDir, this.language.split("_")[0]), dirName);
            }
            if (!parchmentsDir.exists()) {
                return;
            }
            ParchmentFileFilter filter = new ParchmentFileFilter(filePrefix);
            for (File file : parchmentsDir.listFiles(filter)) {
                String sId = file.getName().substring(filePrefix.length() + 1, file.getName().length() - 4);
                int id = 0;
                if (sId.length() > 0) {
                    try {
                        id = Integer.parseInt(sId);
                    }
                    catch (Exception e) {
                        MillLog.printException("Error when trying to read pachment id: ", e);
                    }
                } else {
                    MillLog.error(null, "Couldn't read the ID of " + file.getAbsolutePath() + ". sId: " + sId);
                }
                if (MillConfigValues.LogOther >= 1) {
                    MillLog.minor(file, "Loading " + dirName + ": " + file.getAbsolutePath());
                }
                ArrayList text = new ArrayList();
                String version = "unknown";
                try {
                    String line;
                    BufferedReader reader = MillCommonUtilities.getReader(file);
                    ArrayList<String> page = new ArrayList<String>();
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("NEW_PAGE")) {
                            text.add(page);
                            page = new ArrayList();
                            continue;
                        }
                        if (line.startsWith("version:")) {
                            version = line.split(":")[1];
                            continue;
                        }
                        page.add(line);
                    }
                    text.add(page);
                    if (type == 0) {
                        this.texts.put(id, text);
                        this.textsVersion.put(id, version);
                        continue;
                    }
                    this.help.put(id, text);
                    this.helpVersion.put(id, version);
                }
                catch (Exception e) {
                    MillLog.printException(e);
                }
            }
        }
    }

    private void printErrors(BufferedWriter writer, Set<String> errors, String message) throws IOException {
        LanguageData.printErrors(this.language, writer, errors, message);
    }

    public void testTravelBookCompletion() {
        for (Culture culture : Culture.ListCultures) {
            try {
                int nbVillagers = 0;
                int nbVillagersDesc = 0;
                int nbVillages = 0;
                int nbVillagesDesc = 0;
                int nbBuildings = 0;
                int nbBuildingsDesc = 0;
                int nbTradeGoods = 0;
                int nbTradeGoodsDesc = 0;
                for (VillagerType villagerType : culture.listVillagerTypes) {
                    if (!villagerType.travelBookDisplay) continue;
                    ++nbVillagers;
                    if (!culture.hasCultureString("travelbook.villager." + villagerType.key + ".desc")) continue;
                    ++nbVillagersDesc;
                }
                for (VillageType villageType : culture.listVillageTypes) {
                    if (!villageType.travelBookDisplay) continue;
                    ++nbVillages;
                    if (!culture.hasCultureString("travelbook.village." + villageType.key + ".desc")) continue;
                    ++nbVillagesDesc;
                }
                for (BuildingPlanSet buildingPlanSet : culture.ListPlanSets) {
                    if (!buildingPlanSet.getFirstStartingPlan().travelBookDisplay) continue;
                    ++nbBuildings;
                    if (!culture.hasCultureString("travelbook.building." + buildingPlanSet.key + ".desc")) continue;
                    ++nbBuildingsDesc;
                }
                for (TradeGood tradeGood : culture.goodsList) {
                    if (!tradeGood.travelBookDisplay) continue;
                    ++nbTradeGoods;
                    if (!culture.hasCultureString("travelbook.trade_good." + tradeGood.key + ".desc")) continue;
                    ++nbTradeGoodsDesc;
                }
                MillLog.temp(culture, "Travel book status: Villagers " + nbVillagersDesc + "/" + nbVillagers + ", village types " + nbVillagesDesc + "/" + nbVillages + ", buildings " + nbBuildingsDesc + "/" + nbBuildings + ", trade goods " + nbTradeGoodsDesc + "/" + nbTradeGoods);
            }
            catch (Exception e) {
                MillLog.printException("Error when testing Travel Book for culture " + culture.key + ":", e);
            }
        }
    }

    public String toString() {
        return this.language;
    }

    private static class ParchmentFileFilter
    implements FilenameFilter {
        private final String filePrefix;

        public ParchmentFileFilter(String filePrefix) {
            this.filePrefix = filePrefix;
        }

        @Override
        public boolean accept(File file, String name) {
            if (!name.startsWith(this.filePrefix)) {
                return false;
            }
            if (!name.endsWith(".txt")) {
                return false;
            }
            String id = name.substring(this.filePrefix.length() + 1, name.length() - 4);
            return id.length() != 0 && Integer.parseInt(id) >= 1;
        }
    }
}

