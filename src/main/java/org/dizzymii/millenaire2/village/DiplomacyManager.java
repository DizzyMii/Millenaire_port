package org.dizzymii.millenaire2.village;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.dizzymii.millenaire2.MillConfig;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Manages inter-village diplomacy: relation changes, culture affinities, and raid triggering.
 * Configuration loaded from data/millenaire2/economy/diplomacy.json.
 */
public class DiplomacyManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Relation change amounts (loaded from JSON)
    public static int initialRelation = 0;
    public static int decayPerHour = -1;
    public static int tradeRelationGain = 2;
    public static int questRelationGain = 10;
    public static int raidRelationLoss = -40;
    public static int playerKillRelationLoss = -20;
    public static int giftRelationGain = 5;

    // Raid config
    public static boolean raidsEnabled = true;
    public static int minRelationForRaid = -50;
    public static int minBuildingsToRaid = 3;
    public static int minTicksBetweenRaids = 72000;
    public static double raidChancePerCheck = 0.05;
    public static int raidCheckIntervalTicks = 6000;
    public static int raidersPerBuilding = 1;
    public static int maxRaiders = 5;
    public static int raidDurationTicks = 6000;
    public static boolean lootOnSuccess = true;

    // Culture affinities: "cultureA-cultureB" -> relation modifier
    private static final Map<String, Integer> CULTURE_AFFINITIES = new HashMap<>();

    private static boolean loaded = false;

    public static void loadFromServer(@Nullable MinecraftServer server) {
        CULTURE_AFFINITIES.clear();
        loaded = false;
        if (server == null) return;

        try {
            ResourceManager rm = server.getResourceManager();
            ResourceLocation loc = ResourceLocation.fromNamespaceAndPath("millenaire2", "economy/diplomacy.json");
            Optional<Resource> opt = rm.getResource(loc);
            if (opt.isEmpty()) {
                LOGGER.warn("No diplomacy.json found, using defaults");
                return;
            }

            try (InputStream is = opt.get().open();
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                if (root.has("relations")) {
                    JsonObject rel = root.getAsJsonObject("relations");
                    initialRelation = getInt(rel, "initial_relation", 0);
                    decayPerHour = getInt(rel, "decay_per_hour", -1);
                    tradeRelationGain = getInt(rel, "trade_relation_gain", 2);
                    questRelationGain = getInt(rel, "quest_relation_gain", 10);
                    raidRelationLoss = getInt(rel, "raid_relation_loss", -40);
                    playerKillRelationLoss = getInt(rel, "player_kill_relation_loss", -20);
                    giftRelationGain = getInt(rel, "gift_relation_gain", 5);
                }

                if (root.has("raids")) {
                    JsonObject raids = root.getAsJsonObject("raids");
                    raidsEnabled = raids.has("enabled") ? raids.get("enabled").getAsBoolean() : true;
                    minRelationForRaid = getInt(raids, "min_relation_for_raid", -50);
                    minBuildingsToRaid = getInt(raids, "min_buildings_to_raid", 3);
                    minTicksBetweenRaids = getInt(raids, "min_ticks_between_raids", 72000);
                    raidChancePerCheck = raids.has("raid_chance_per_check") ? raids.get("raid_chance_per_check").getAsDouble() : 0.05;
                    raidCheckIntervalTicks = getInt(raids, "raid_check_interval_ticks", 6000);
                    raidersPerBuilding = getInt(raids, "raiders_per_building", 1);
                    maxRaiders = getInt(raids, "max_raiders", 5);
                    raidDurationTicks = getInt(raids, "raid_duration_ticks", 6000);
                    lootOnSuccess = raids.has("loot_on_success") && raids.get("loot_on_success").getAsBoolean();
                }

                if (root.has("culture_affinities")) {
                    JsonObject affinities = root.getAsJsonObject("culture_affinities");
                    for (Map.Entry<String, JsonElement> entry : affinities.entrySet()) {
                        if (entry.getKey().startsWith("_")) continue;
                        CULTURE_AFFINITIES.put(entry.getKey(), entry.getValue().getAsInt());
                    }
                }

                loaded = true;
                LOGGER.debug("Loaded diplomacy config: "
                        + CULTURE_AFFINITIES.size() + " culture affinities");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load diplomacy.json", e);
        }
    }

    private static int getInt(JsonObject obj, String key, int def) {
        return obj.has(key) ? obj.get(key).getAsInt() : def;
    }

    /**
     * Get the base culture affinity between two cultures.
     * Order-independent: looks up both "a-b" and "b-a".
     */
    public static int getCultureAffinity(String cultureA, String cultureB) {
        if (cultureA == null || cultureB == null) return 0;
        Integer val = CULTURE_AFFINITIES.get(cultureA + "-" + cultureB);
        if (val != null) return val;
        val = CULTURE_AFFINITIES.get(cultureB + "-" + cultureA);
        return val != null ? val : 0;
    }

    /**
     * Called from Building.slowTick() on townhalls to check if a raid should be triggered.
     * Evaluates relations with known villages and rolls for raid chance.
     */
    public static void checkRaidTrigger(Building townhall, MillWorldData mw) {
        if (!raidsEnabled || !MillConfig.raidsEnabled()) return;
        if (townhall.getPos() == null) return;
        if (townhall.getLevel() == null) return;

        long gameTime = townhall.getLevel().getGameTime();
        if (townhall.lastRaidGameTime > 0
                && gameTime - townhall.lastRaidGameTime < minTicksBetweenRaids) {
            return;
        }

        // Count our village's buildings
        int ourBuildingCount = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (townhall.isSameVillage(b)) ourBuildingCount++;
        }
        if (ourBuildingCount < minBuildingsToRaid) return;

        // Check relations with known villages
        for (Point knownVillagePos : townhall.getKnownVillages()) {
            int relation = townhall.getRelation(knownVillagePos);
            if (relation > minRelationForRaid) continue;

            // Roll for raid
            if (townhall.getLevel().getRandom().nextDouble() < raidChancePerCheck) {
                startRaid(townhall, knownVillagePos, mw);
                return; // One raid per check cycle
            }
        }
    }

    /**
     * Trigger a raid from this village against a target village.
     */
    public static boolean startRaid(Building attacker, Point targetVillagePos, MillWorldData mw) {
        // Find target townhall
        Building targetTh = null;
        for (Building b : mw.getBuildingsMap().values()) {
            if (b.isTownhall && b.getPos() != null && b.getPos().equals(targetVillagePos)) {
                targetTh = b;
                break;
            }
        }
        if (targetTh == null) return false;

        if (attacker.getPos() == null) return false;
        if (attacker.raidTarget != null) return false;
        if (attacker.lastRaidGameTime > 0 && attacker.getLevel() != null
                && attacker.getLevel().getGameTime() - attacker.lastRaidGameTime < minTicksBetweenRaids) {
            return false;
        }

        // Mark attacker's village as raiding
        attacker.raidTarget = targetVillagePos;
        targetTh.underAttack = true;

        long now = attacker.getLevel() != null ? attacker.getLevel().getGameTime() : 0L;
        attacker.activeRaidStartTick = now;
        targetTh.activeRaidStartTick = now;
        attacker.lastRaidGameTime = now;
        targetTh.lastRaidGameTime = now;

        // Determine raider count
        int raiderCount = Math.min(maxRaiders, attacker.getVillagerRecords().size() / 2);
        raiderCount = Math.max(1, raiderCount);

        // Mark villagers as raiders
        int marked = 0;
        for (VillagerRecord vr : attacker.getVillagerRecords()) {
            if (vr.killed || vr.awayraiding || vr.awayhired) continue;
            vr.awayraiding = true;
            vr.raidingVillage = true;
            marked++;
            if (marked >= raiderCount) break;
        }

        // Apply relation loss
        attacker.setRelation(targetVillagePos, attacker.getRelation(targetVillagePos) + raidRelationLoss);
        targetTh.setRelation(attacker.getPos(), targetTh.getRelation(attacker.getPos()) + raidRelationLoss);

        String attackerName = attacker.getName() != null ? attacker.getName() : "Unknown";
        String targetName = targetTh.getName() != null ? targetTh.getName() : "Unknown";
        attacker.raidsPerformed.add(targetName);
        targetTh.raidsSuffered.add(attackerName);

        if (mw != null) mw.setDirty();
        LOGGER.debug("Raid triggered: " + attackerName
                + " -> " + targetName + " (" + marked + " raiders)");
        return true;
    }

    public static void updateRaidState(Building attacker, MillWorldData mw) {
        if (attacker.raidTarget == null) return;

        long now = attacker.getLevel() != null ? attacker.getLevel().getGameTime() : 0L;
        if (attacker.activeRaidStartTick == -1L) {
            attacker.activeRaidStartTick = now;
        }

        Building targetTh = mw.getBuilding(attacker.raidTarget);
        if (targetTh == null || !targetTh.isTownhall) {
            cancelRaid(attacker, mw, true);
            return;
        }

        if (now - attacker.activeRaidStartTick < raidDurationTicks) return;

        int attackStrength = getVillageRaidingStrength(attacker);
        int defendStrength = getVillageDefendingStrength(targetTh);
        boolean attackerWon = attackStrength >= defendStrength;

        if (attackerWon) {
            attacker.raidsPerformed.add(targetTh.getName() != null ? targetTh.getName() : "Unknown");
            targetTh.raidsSuffered.add(attacker.getName() != null ? attacker.getName() : "Unknown");
        }

        finishRaid(attacker, targetTh, mw);
    }

    public static void cancelRaid(Building attacker, MillWorldData mw, boolean clearTargetAttackFlag) {
        if (attacker.raidTarget == null) return;
        Building target = mw.getBuilding(attacker.raidTarget);
        if (clearTargetAttackFlag && target != null) {
            target.underAttack = false;
            target.activeRaidStartTick = -1L;
        }
        clearRaiders(attacker);
        attacker.raidTarget = null;
        attacker.activeRaidStartTick = -1L;
        attacker.lastRaidGameTime = attacker.getLevel() != null ? attacker.getLevel().getGameTime() : attacker.lastRaidGameTime;
        mw.setDirty();
    }

    public static int getVillageRaidingStrength(Building village) {
        int strength = 0;
        for (VillagerRecord vr : village.getVillagerRecords()) {
            if (vr.killed || vr.awayhired) continue;
            if (vr.awayraiding || vr.raidingVillage) {
                strength++;
            }
        }
        return Math.max(strength, 1);
    }

    public static int getVillageDefendingStrength(Building village) {
        int strength = 0;
        for (VillagerRecord vr : village.getVillagerRecords()) {
            if (!vr.killed && !vr.awayraiding) {
                strength++;
            }
        }
        return Math.max(strength, 1);
    }

    private static void finishRaid(Building attacker, Building targetTh, MillWorldData mw) {
        clearRaiders(attacker);
        attacker.raidTarget = null;
        attacker.activeRaidStartTick = -1L;
        attacker.lastRaidGameTime = attacker.getLevel() != null ? attacker.getLevel().getGameTime() : attacker.lastRaidGameTime;
        targetTh.underAttack = false;
        targetTh.activeRaidStartTick = -1L;
        targetTh.lastRaidGameTime = targetTh.getLevel() != null ? targetTh.getLevel().getGameTime() : targetTh.lastRaidGameTime;
        mw.setDirty();
    }

    private static void clearRaiders(Building village) {
        for (VillagerRecord vr : village.getVillagerRecords()) {
            if (vr.awayraiding || vr.raidingVillage) {
                vr.awayraiding = false;
                vr.raidingVillage = false;
            }
        }
    }

    /**
     * Apply relation change when a trade occurs between player-controlled villages.
     */
    public static void onTrade(Building villageA, Point villageBPos) {
        villageA.setRelation(villageBPos, villageA.getRelation(villageBPos) + tradeRelationGain);
    }

    /**
     * Apply hourly relation decay toward neutral for all known villages.
     */
    public static void tickRelationDecay(Building townhall) {
        for (Point knownPos : townhall.getKnownVillages()) {
            int current = townhall.getRelation(knownPos);
            if (current > 0) {
                townhall.setRelation(knownPos, current + decayPerHour);
            } else if (current < 0) {
                townhall.setRelation(knownPos, current - decayPerHour);
            }
        }
    }

    /**
     * Initialize relation between two villages if not already known,
     * applying culture affinity as the starting value.
     */
    public static void initRelation(Building townhallA, Building townhallB) {
        if (townhallA.getPos() == null || townhallB.getPos() == null) return;
        if (townhallA.getKnownVillages().contains(townhallB.getPos())) return;

        int base = initialRelation + getCultureAffinity(townhallA.cultureKey, townhallB.cultureKey);
        townhallA.setRelation(townhallB.getPos(), base);
        townhallB.setRelation(townhallA.getPos(), base);
    }

    public static boolean isLoaded() {
        return loaded;
    }
}
