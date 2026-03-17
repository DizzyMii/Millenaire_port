package org.dizzymii.millenaire2.ui;

/**
 * Stores map display info for the village minimap/overview.
 * Ported from org.millenaire.common.ui.MillMapInfo (Forge 1.12.2).
 */
public class MillMapInfo {

    /** Village center positions (x, z pairs) */
    public final java.util.List<int[]> villagePositions = new java.util.ArrayList<>();
    /** Village display names */
    public final java.util.List<String> villageNames = new java.util.ArrayList<>();
    /** Village culture keys for color mapping */
    public final java.util.List<String> villageCultures = new java.util.ArrayList<>();
    /** Whether each village is friendly to the player */
    public final java.util.List<Boolean> villageFriendly = new java.util.ArrayList<>();

    public void addVillage(int x, int z, String name, String culture, boolean friendly) {
        villagePositions.add(new int[]{x, z});
        villageNames.add(name);
        villageCultures.add(culture);
        villageFriendly.add(friendly);
    }

    public int getVillageCount() {
        return villagePositions.size();
    }

    public void clear() {
        villagePositions.clear();
        villageNames.clear();
        villageCultures.clear();
        villageFriendly.clear();
    }
}
