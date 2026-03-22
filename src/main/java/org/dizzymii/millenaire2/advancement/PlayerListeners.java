package org.dizzymii.millenaire2.advancement;

import net.minecraft.server.level.ServerPlayer;
import org.dizzymii.millenaire2.world.UserProfile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks player advancement listeners for triggering custom Millenaire advancements.
 * Checks player state (reputation, quest completion, etc.) and grants advancements
 * when conditions are met.
 * Ported from org.millenaire.common.advancements.PlayerListeners (Forge 1.12.2).
 */
public class PlayerListeners {

    private static final int REP_THRESHOLD_FOR_ADVANCEMENT = 128;
    private static final int LEADER_REP_THRESHOLD = 512;
    private static final int CRESUS_GOLD_THRESHOLD = 10000;

    private static final Map<UUID, Long> lastCheckTick = new HashMap<>();
    private static final long CHECK_INTERVAL = 200; // ticks between checks

    /**
     * Called periodically from the server tick to check and grant advancements.
     */
    public static void checkPlayerAdvancements(ServerPlayer player, UserProfile profile, long gameTick) {
        UUID uuid = player.getUUID();
        Long lastTick = lastCheckTick.get(uuid);
        if (lastTick != null && gameTick - lastTick < CHECK_INTERVAL) return;
        lastCheckTick.put(uuid, gameTick);

        // First Contact: player has any reputation
        for (String culture : MillAdvancements.ADVANCEMENT_CULTURES) {
            int rep = profile.getCultureReputation(culture);
            if (rep > 0 && !MillAdvancements.FIRST_CONTACT.isEarned(player)) {
                MillAdvancements.FIRST_CONTACT.grant(player);
            }

            // Per-culture reputation advancement
            if (rep >= REP_THRESHOLD_FOR_ADVANCEMENT) {
                MillAdvancements.grantRepAdvancement(player, culture);
            }

            // Village leader
            if (rep >= LEADER_REP_THRESHOLD) {
                MillAdvancements.grantVillageLeaderAdvancement(player, culture);
            }
        }

        // Explorer: discovered multiple cultures
        int culturesDiscovered = 0;
        for (String culture : MillAdvancements.ADVANCEMENT_CULTURES) {
            if (profile.getCultureReputation(culture) > 0) culturesDiscovered++;
        }
        if (culturesDiscovered >= 3 && !MillAdvancements.EXPLORER.isEarned(player)) {
            MillAdvancements.EXPLORER.grant(player);
        }
        if (culturesDiscovered >= 5 && !MillAdvancements.MARCO_POLO.isEarned(player)) {
            MillAdvancements.MARCO_POLO.grant(player);
        }
        if (culturesDiscovered >= MillAdvancements.ADVANCEMENT_CULTURES.length
                && !MillAdvancements.MAGELLAN.isEarned(player)) {
            MillAdvancements.MAGELLAN.grant(player);
        }
    }

    /**
     * Called when a player completes a quest.
     */
    public static void onQuestComplete(ServerPlayer player) {
        if (!MillAdvancements.THE_QUEST.isEarned(player)) {
            MillAdvancements.THE_QUEST.grant(player);
        }
    }

    /**
     * Called when a player uses the summoning wand for the first time.
     */
    public static void onSummoningWandUsed(ServerPlayer player) {
        if (!MillAdvancements.SUMMONING_WAND.isEarned(player)) {
            MillAdvancements.SUMMONING_WAND.grant(player);
        }
    }

    /**
     * Called when a player imports a custom building plan.
     */
    public static void onBuildingPlanImported(ServerPlayer player) {
        if (!MillAdvancements.AMATEUR_ARCHITECT.isEarned(player)) {
            MillAdvancements.AMATEUR_ARCHITECT.grant(player);
        }
    }

    /**
     * Called when a player kills a hostile raider near a village.
     */
    public static void onDefendVillage(ServerPlayer player) {
        if (!MillAdvancements.SELF_DEFENSE.isEarned(player)) {
            MillAdvancements.SELF_DEFENSE.grant(player);
        }
    }

    /**
     * Removes tracking data for a player who disconnected.
     */
    public static void removePlayer(UUID uuid) {
        lastCheckTick.remove(uuid);
    }
}
