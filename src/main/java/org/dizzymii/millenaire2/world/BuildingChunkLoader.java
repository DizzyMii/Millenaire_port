package org.dizzymii.millenaire2.world;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Manages chunk loading for active village buildings to keep them ticking.
 * Uses NeoForge's forceChunk API to keep building chunks loaded.
 * Ported from org.millenaire.common.forge.BuildingChunkLoader (Forge 1.12.2).
 */
public class BuildingChunkLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int BUILDING_CHUNK_RADIUS = 1;
    private static final Map<Point, Set<ChunkPos>> loadedChunks = new HashMap<>();

    /**
     * Forces chunks around the building's position to stay loaded.
     */
    public static void forceLoadBuilding(ServerLevel level, Building building) {
        Point pos = building.getPos();
        if (pos == null) return;

        Set<ChunkPos> chunks = getChunksAround(pos);
        for (ChunkPos cp : chunks) {
            level.setChunkForced(cp.x, cp.z, true);
        }
        loadedChunks.put(pos, chunks);
        LOGGER.debug("Force-loaded " + chunks.size() + " chunks for building at " + pos);
    }

    /**
     * Releases forced chunks for a building.
     */
    public static void releaseBuilding(ServerLevel level, Building building) {
        Point pos = building.getPos();
        if (pos == null) return;

        Set<ChunkPos> chunks = loadedChunks.remove(pos);
        if (chunks != null) {
            for (ChunkPos cp : chunks) {
                level.setChunkForced(cp.x, cp.z, false);
            }
            LOGGER.debug("Released " + chunks.size() + " chunks for building at " + pos);
        }
    }

    /**
     * Releases all force-loaded chunks managed by Millenaire.
     */
    public static void releaseAll(ServerLevel level) {
        for (Set<ChunkPos> chunks : loadedChunks.values()) {
            for (ChunkPos cp : chunks) {
                level.setChunkForced(cp.x, cp.z, false);
            }
        }
        loadedChunks.clear();
        LOGGER.debug("Released all Millenaire force-loaded chunks.");
    }

    /**
     * Updates chunk loading — loads chunks for active buildings, releases inactive ones.
     */
    public static void updateLoadedChunks(ServerLevel level, MillWorldData worldData) {
        Set<Point> activePositions = new HashSet<>();
        for (Building b : worldData.allBuildings()) {
            if (b.isActive && b.getPos() != null) {
                activePositions.add(b.getPos());
                if (!loadedChunks.containsKey(b.getPos())) {
                    forceLoadBuilding(level, b);
                }
            }
        }

        // Release chunks for buildings that are no longer active
        Set<Point> toRemove = new HashSet<>();
        for (Point p : loadedChunks.keySet()) {
            if (!activePositions.contains(p)) {
                toRemove.add(p);
            }
        }
        for (Point p : toRemove) {
            Set<ChunkPos> chunks = loadedChunks.remove(p);
            if (chunks != null) {
                for (ChunkPos cp : chunks) {
                    level.setChunkForced(cp.x, cp.z, false);
                }
            }
        }
    }

    private static Set<ChunkPos> getChunksAround(Point pos) {
        Set<ChunkPos> chunks = new HashSet<>();
        int centerChunkX = pos.x >> 4;
        int centerChunkZ = pos.z >> 4;
        for (int dx = -BUILDING_CHUNK_RADIUS; dx <= BUILDING_CHUNK_RADIUS; dx++) {
            for (int dz = -BUILDING_CHUNK_RADIUS; dz <= BUILDING_CHUNK_RADIUS; dz++) {
                chunks.add(new ChunkPos(centerChunkX + dx, centerChunkZ + dz));
            }
        }
        return chunks;
    }
}
