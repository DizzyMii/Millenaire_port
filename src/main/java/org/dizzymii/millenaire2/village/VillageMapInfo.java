package org.dizzymii.millenaire2.village;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Maintains a 2D map of the village area for building placement, pathfinding, and terrain analysis.
 * Ported from org.millenaire.common.village.VillageMapInfo (Forge 1.12.2).
 */
public class VillageMapInfo implements Cloneable {

    private static final int MAP_MARGIN = 5;
    private static final int BUILDING_MARGIN = 5;
    private static final int VALID_HEIGHT_DIFF = 10;
    public static final int UPDATE_FREQUENCY = 1000;

    public int length = 0;
    public int width = 0;
    public int chunkStartX = 0;
    public int chunkStartZ = 0;
    public int mapStartX = 0;
    public int mapStartZ = 0;
    public int yBaseline = 0;

    public short[][] topGround;
    public short[][] spaceAbove;
    public boolean[][] danger;
    public BuildingLocation[][] buildingLocRef;
    public boolean[][] canBuild;
    public boolean[][] buildingForbidden;
    public boolean[][] water;
    public boolean[][] tree;
    public boolean[][] buildTested = null;
    public boolean[][] topAdjusted;
    public boolean[][] path;

    public int frequency = 10;
    private List<BuildingLocation> buildingLocations = new ArrayList<>();
    public Level world;
    public int lastUpdatedX;
    public int lastUpdatedZ;
    private int updateCounter;

    public static BuildingLocation[][] blArrayDeepClone(BuildingLocation[][] source) {
        BuildingLocation[][] target = new BuildingLocation[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    public static boolean[][] booleanArrayDeepClone(boolean[][] source) {
        boolean[][] target = new boolean[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    public static short[][] shortArrayDeepClone(short[][] source) {
        short[][] target = new short[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = source[i].clone();
        }
        return target;
    }

    // TODO: Implement map update, building placement checks, terrain analysis
}
