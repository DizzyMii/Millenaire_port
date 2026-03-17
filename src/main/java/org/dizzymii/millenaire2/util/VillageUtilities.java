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

    // TODO: getServerProfile — needs Mill singleton or MillWorldData accessor
    // TODO: getVillagerSentence — depends on culture dialogue system
}
