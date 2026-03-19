package org.dizzymii.millenaire2.util;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Village and reputation utilities.
 * Ported from org.millenaire.common.utilities.VillageUtilities (Forge 1.12.2).
 */
public final class VillageUtilities {

    private VillageUtilities() {}

    public static String getRelationName(int relation) {
        if (relation >= 90) return "relation.excellent";
        if (relation >= 70) return "relation.verygood";
        if (relation >= 50) return "relation.good";
        if (relation >= 30) return "relation.decent";
        if (relation >= 10) return "relation.fair";
        if (relation <= -90) return "relation.openconflict";
        if (relation <= -70) return "relation.atrocious";
        if (relation <= -50) return "relation.verybad";
        if (relation <= -30) return "relation.bad";
        if (relation <= -10) return "relation.chilly";
        return "relation.neutral";
    }

    public static List<ServerPlayer> getServerPlayers(Level level) {
        List<ServerPlayer> players = new ArrayList<>();
        if (level.getServer() != null) {
            players.addAll(level.getServer().getPlayerList().getPlayers());
        }
        return players;
    }

    @javax.annotation.Nullable
    public static org.dizzymii.millenaire2.world.UserProfile getServerProfile(Level level, net.minecraft.world.entity.player.Player player) {
        if (level.getServer() == null) return null;
        net.minecraft.server.level.ServerLevel serverLevel = level.getServer().overworld();
        org.dizzymii.millenaire2.world.MillWorldData data = org.dizzymii.millenaire2.world.MillWorldData.get(serverLevel);
        return data.getProfile(player.getUUID());
    }

    public static String getVillagerSentence(String cultureKey, String sentenceKey) {
        // Try culture-specific key first: e.g. "norman.greeting"
        String culturePrefixed = cultureKey + "." + sentenceKey;
        if (LanguageUtilities.hasString(culturePrefixed)) {
            return LanguageUtilities.string(culturePrefixed);
        }
        // Fallback to generic key: e.g. "greeting"
        if (LanguageUtilities.hasString(sentenceKey)) {
            return LanguageUtilities.string(sentenceKey);
        }
        return sentenceKey;
    }
}
