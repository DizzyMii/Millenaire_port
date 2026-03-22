package org.dizzymii.millenaire2.parity;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.MillConfig;
import org.dizzymii.millenaire2.culture.Culture;
import org.dizzymii.millenaire2.goal.Goal;
import org.dizzymii.millenaire2.quest.Quest;
import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.village.DiplomacyManager;
import org.dizzymii.millenaire2.village.VillageEconomyLoader;
import org.dizzymii.millenaire2.world.BiomeCultureMapper;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Startup parity contract evaluator for rewrite baseline guarantees.
 */
public final class ParityContracts {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static volatile List<ParityContractResult> lastStartupReport = List.of();

    private ParityContracts() {
    }

    public static List<ParityContractResult> evaluateStartupReport(@Nullable MillWorldData worldData) {
        List<ParityContractResult> results = new ArrayList<>();

        File contentDir = MillCommonUtilities.getMillenaireContentDir();
        results.add(check(
                ParityContract.CONTENT_DEPLOYED,
                contentDir.exists() && contentDir.isDirectory(),
                "contentDir=" + contentDir.getAbsolutePath()
        ));

        boolean worldAvailable = worldData != null;
        results.add(check(
                ParityContract.WORLD_DATA_AVAILABLE,
                worldAvailable,
                worldAvailable ? "savedDataReady" : "worldData=null"
        ));

        boolean generationEnabled = worldAvailable && worldData.generateVillages && MillConfig.generateVillages();
        results.add(check(
                ParityContract.VILLAGE_GENERATION_ENABLED,
                generationEnabled,
                worldAvailable
                        ? "worldData.generateVillages=" + worldData.generateVillages + ", config=" + MillConfig.generateVillages()
                        : "worldDataMissing"
        ));

        results.add(check(
                ParityContract.CULTURES_LOADED,
                !Culture.LIST_CULTURES.isEmpty(),
                "cultures=" + Culture.LIST_CULTURES.size()
        ));

        int goalCount = Goal.registeredCount();
        results.add(check(
                ParityContract.GOALS_INITIALIZED,
                goalCount > 0,
                "goals=" + goalCount
        ));

        results.add(check(
                ParityContract.BIOME_MAPPING_LOADED,
                BiomeCultureMapper.isLoaded(),
                "loaded=" + BiomeCultureMapper.isLoaded()
        ));

        results.add(check(
                ParityContract.ECONOMY_CONFIG_LOADED,
                VillageEconomyLoader.isLoaded(),
                "loaded=" + VillageEconomyLoader.isLoaded()
        ));

        results.add(check(
                ParityContract.DIPLOMACY_CONFIG_LOADED,
                DiplomacyManager.isLoaded(),
                "loaded=" + DiplomacyManager.isLoaded()
        ));

        int questCount = Quest.quests != null ? Quest.quests.size() : 0;
        results.add(check(
                ParityContract.QUEST_DATA_LOADED,
                questCount > 0,
                "quests=" + questCount
        ));

        List<ParityContractResult> immutable = List.copyOf(results);
        lastStartupReport = immutable;
        return immutable;
    }

    public static List<ParityContractResult> getLastStartupReport() {
        return lastStartupReport;
    }

    public static boolean hasCriticalFailures(List<ParityContractResult> report) {
        for (ParityContractResult result : report) {
            if (result.contract().isCritical() && !result.passed()) {
                return true;
            }
        }
        return false;
    }

    public static void logStartupReport(List<ParityContractResult> report) {
        int passed = 0;
        int failed = 0;

        for (ParityContractResult result : report) {
            if (result.passed()) {
                passed++;
                LOGGER.debug("[contract/pass] " + result.contract().getId() + " :: " + result.details());
            } else {
                failed++;
                if (result.contract().isCritical()) {
                    LOGGER.error("[contract/fail-critical] " + result.contract().getId() + " :: " + result.details());
                } else {
                    LOGGER.warn("[contract/fail] " + result.contract().getId() + " :: " + result.details());
                }
            }
        }

        LOGGER.info("Startup parity contracts: " + passed + " passed, " + failed + " failed.");
    }

    private static ParityContractResult check(ParityContract contract, boolean ok, String details) {
        return new ParityContractResult(contract, ok, details);
    }
}
