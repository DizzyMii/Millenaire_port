package org.dizzymii.millenaire2.util;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores translation data for a single language.
 * Loaded from .txt files in the languages/ directories.
 * Each language has: strings (key=value), quest strings, help pages, and parchment texts.
 *
 * Ported from org.millenaire.common.utilities.LanguageData.
 */
public class LanguageData {

    public final String language;
    public final String topLevelLanguage;

    public final HashMap<String, String> strings = new HashMap<>();
    public final HashMap<String, String> questStrings = new HashMap<>();
    public final HashMap<Integer, List<List<String>>> texts = new HashMap<>();
    public final HashMap<Integer, List<List<String>>> help = new HashMap<>();

    public LanguageData(String language) {
        this.language = language;
        if (language.contains("_")) {
            this.topLevelLanguage = language.split("_")[0];
        } else {
            this.topLevelLanguage = null;
        }
    }

    /**
     * Load language data from all language directories.
     * Searches for a subdirectory matching the language code in each dir.
     */
    public void loadFromDisk(List<File> languageDirs) {
        for (File langDir : languageDirs) {
            File specificDir = new File(langDir, language);
            if (!specificDir.exists() && topLevelLanguage != null) {
                specificDir = new File(langDir, topLevelLanguage);
            }
            if (!specificDir.exists()) continue;

            loadStringsFromDir(specificDir);
            loadQuestStringsFromDir(specificDir);
            loadTextsFromDir(specificDir, "texts", texts);
            loadTextsFromDir(specificDir, "help", help);
        }

        MillLog.minor(this, "Loaded language '" + language + "': "
                + strings.size() + " strings, "
                + questStrings.size() + " quest strings");
    }

    private void loadStringsFromDir(File dir) {
        File stringsFile = new File(dir, "strings.txt");
        if (!stringsFile.exists()) return;

        try (BufferedReader reader = MillCommonUtilities.getReader(stringsFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;
                int eqPos = line.indexOf('=');
                if (eqPos < 0) continue;
                String key = line.substring(0, eqPos).trim().toLowerCase();
                String value = line.substring(eqPos + 1).trim();
                strings.put(key, value);
            }
        } catch (Exception e) {
            MillLog.error(this, "Error loading strings for " + language, e);
        }

        // Also load any .txt files in the directory that aren't special files
        File[] files = dir.listFiles((d, name) ->
                name.endsWith(".txt") && !name.equals("strings.txt")
                        && !name.startsWith("quest_") && !name.startsWith("text_")
                        && !name.startsWith("help_"));
        if (files == null) return;

        for (File file : files) {
            try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("//")) continue;
                    int eqPos = line.indexOf('=');
                    if (eqPos < 0) continue;
                    String key = line.substring(0, eqPos).trim().toLowerCase();
                    String value = line.substring(eqPos + 1).trim();
                    strings.putIfAbsent(key, value);
                }
            } catch (Exception e) {
                MillLog.error(this, "Error loading strings from " + file.getName(), e);
            }
        }
    }

    private void loadQuestStringsFromDir(File dir) {
        File[] questFiles = dir.listFiles((d, name) -> name.startsWith("quest_") && name.endsWith(".txt"));
        if (questFiles == null) return;

        for (File file : questFiles) {
            try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("//")) continue;
                    int eqPos = line.indexOf('=');
                    if (eqPos < 0) continue;
                    String key = line.substring(0, eqPos).trim().toLowerCase();
                    String value = line.substring(eqPos + 1).trim();
                    questStrings.put(key, value);
                }
            } catch (Exception e) {
                MillLog.error(this, "Error loading quest strings from " + file.getName(), e);
            }
        }
    }

    /**
     * Load multi-page text content (parchments or help pages).
     * Files are named text_X.txt or help_X.txt where X is the numeric ID.
     * Pages within a file are separated by "---".
     */
    private void loadTextsFromDir(File dir, String prefix, HashMap<Integer, List<List<String>>> target) {
        File[] textFiles = dir.listFiles((d, name) -> name.startsWith(prefix + "_") && name.endsWith(".txt"));
        if (textFiles == null) return;

        for (File file : textFiles) {
            try {
                String nameWithoutExt = file.getName().replace(".txt", "");
                String idStr = nameWithoutExt.substring(prefix.length() + 1);
                int id = Integer.parseInt(idStr);

                List<List<String>> pages = new ArrayList<>();
                List<String> currentPage = new ArrayList<>();

                try (BufferedReader reader = MillCommonUtilities.getReader(file)) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals("---")) {
                            if (!currentPage.isEmpty()) {
                                pages.add(currentPage);
                                currentPage = new ArrayList<>();
                            }
                        } else {
                            currentPage.add(line);
                        }
                    }
                }
                if (!currentPage.isEmpty()) {
                    pages.add(currentPage);
                }
                if (!pages.isEmpty()) {
                    target.put(id, pages);
                }
            } catch (NumberFormatException e) {
                // Skip files that don't have numeric IDs
            } catch (Exception e) {
                MillLog.error(this, "Error loading " + prefix + " from " + file.getName(), e);
            }
        }
    }

    @Override
    public String toString() {
        return "LanguageData[" + language + "]";
    }
}
