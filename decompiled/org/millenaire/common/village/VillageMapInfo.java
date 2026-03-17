/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLog
 *  net.minecraft.init.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 */
package org.millenaire.common.village;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.ThreadSafeUtilities;
import org.millenaire.common.village.BuildingLocation;

public class VillageMapInfo
implements Cloneable {
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
    private List<BuildingLocation> buildingLocations = new ArrayList<BuildingLocation>();
    public World world;
    public int lastUpdatedX;
    public int lastUpdatedZ;
    private int updateCounter;

    public static BuildingLocation[][] blArrayDeepClone(BuildingLocation[][] source) {
        BuildingLocation[][] target = new BuildingLocation[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = (BuildingLocation[])source[i].clone();
        }
        return target;
    }

    public static boolean[][] booleanArrayDeepClone(boolean[][] source) {
        boolean[][] target = new boolean[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = (boolean[])source[i].clone();
        }
        return target;
    }

    public static short[][] shortArrayDeepClone(short[][] source) {
        short[][] target = new short[source.length][];
        for (int i = 0; i < source.length; ++i) {
            target[i] = (short[])source[i].clone();
        }
        return target;
    }

    public void addBuildingLocationToMap(BuildingLocation bl) {
        if (MillConfigValues.LogWorldInfo >= 1) {
            MillLog.major(this, "Registering building location: " + bl);
        }
        this.buildingLocations.add(bl);
        int sx = Math.max(bl.minxMargin - this.mapStartX, 0);
        int sz = Math.max(bl.minzMargin - this.mapStartZ, 0);
        int ex = Math.min(bl.maxxMargin - this.mapStartX, this.length);
        int ez = Math.min(bl.maxzMargin - this.mapStartZ, this.width);
        for (int i = sx; i < ex; ++i) {
            for (int j = sz; j < ez; ++j) {
                this.buildingLocRef[i][j] = bl;
            }
        }
    }

    public VillageMapInfo clone() throws CloneNotSupportedException {
        VillageMapInfo o = (VillageMapInfo)super.clone();
        o.topGround = VillageMapInfo.shortArrayDeepClone(this.topGround);
        o.spaceAbove = VillageMapInfo.shortArrayDeepClone(this.spaceAbove);
        o.danger = VillageMapInfo.booleanArrayDeepClone(this.danger);
        o.buildingLocRef = VillageMapInfo.blArrayDeepClone(this.buildingLocRef);
        o.canBuild = VillageMapInfo.booleanArrayDeepClone(this.canBuild);
        o.buildingForbidden = VillageMapInfo.booleanArrayDeepClone(this.buildingForbidden);
        o.water = VillageMapInfo.booleanArrayDeepClone(this.water);
        o.tree = VillageMapInfo.booleanArrayDeepClone(this.tree);
        o.path = VillageMapInfo.booleanArrayDeepClone(this.path);
        o.buildingLocations = new ArrayList<BuildingLocation>();
        o.buildingLocations.addAll(this.buildingLocations);
        return o;
    }

    private void createWorldInfo(List<BuildingLocation> locations, int pstartX, int pstartZ, int endX, int endZ) throws MillLog.MillenaireException {
        if (MillConfigValues.LogWorldInfo >= 2) {
            MillLog.minor(this, "Creating world info: " + pstartX + "/" + pstartZ + "/" + endX + "/" + endZ);
        }
        this.chunkStartX = pstartX >> 4;
        this.chunkStartZ = pstartZ >> 4;
        this.mapStartX = this.chunkStartX << 4;
        this.mapStartZ = this.chunkStartZ << 4;
        this.length = ((endX >> 4) + 1 << 4) - this.mapStartX;
        this.width = ((endZ >> 4) + 1 << 4) - this.mapStartZ;
        this.frequency = (int)Math.max(1000.0 / (double)(this.length * this.width / 256), 10.0);
        if (this.frequency == 0) {
            throw new MillLog.MillenaireException("Null frequency in createWorldInfo.");
        }
        if (MillConfigValues.LogWorldInfo >= 1) {
            MillLog.major(this, "Creating world info: " + this.mapStartX + "/" + this.mapStartZ + "/" + this.length + "/" + this.width);
        }
        this.topGround = new short[this.length][this.width];
        this.spaceAbove = new short[this.length][this.width];
        this.danger = new boolean[this.length][this.width];
        this.buildingLocRef = new BuildingLocation[this.length][this.width];
        this.buildingForbidden = new boolean[this.length][this.width];
        this.canBuild = new boolean[this.length][this.width];
        this.buildTested = new boolean[this.length][this.width];
        this.water = new boolean[this.length][this.width];
        this.tree = new boolean[this.length][this.width];
        this.path = new boolean[this.length][this.width];
        this.topAdjusted = new boolean[this.length][this.width];
        this.buildingLocations = new ArrayList<BuildingLocation>();
        for (int i = 0; i < this.length; ++i) {
            for (int j = 0; j < this.width; ++j) {
                this.buildingLocRef[i][j] = null;
                this.canBuild[i][j] = false;
            }
        }
        for (BuildingLocation location : locations) {
            this.addBuildingLocationToMap(location);
        }
        for (int i = 0; i < this.length; i += 16) {
            for (int j = 0; j < this.width; j += 16) {
                try {
                    this.updateChunk(i, j);
                    continue;
                }
                catch (ThreadSafeUtilities.ChunkAccessException e) {
                    MillLog.error(this, "Chunk access violation while updating map info.");
                }
            }
        }
        this.lastUpdatedX = 0;
        this.lastUpdatedZ = 0;
    }

    public List<BuildingLocation> getBuildingLocations() {
        return this.buildingLocations;
    }

    public boolean isConstructionForbiddenHere(Point p) {
        if (p.getiX() < this.mapStartX || p.getiZ() < this.mapStartZ || p.getiX() >= this.mapStartX + this.length || p.getiZ() >= this.mapStartZ + this.width) {
            return false;
        }
        return this.buildingForbidden[p.getiX() - this.mapStartX][p.getiZ() - this.mapStartZ];
    }

    public void removeBuildingLocation(BuildingLocation bl) {
        for (BuildingLocation l : this.buildingLocations) {
            if (!l.isLocationSamePlace(bl)) continue;
            this.buildingLocations.remove(l);
            break;
        }
        int sx = Math.max(bl.minxMargin - this.mapStartX, 0);
        int sz = Math.max(bl.minzMargin - this.mapStartZ, 0);
        int ex = Math.min(bl.maxxMargin - this.mapStartX, this.length);
        int ez = Math.min(bl.maxzMargin - this.mapStartZ, this.width);
        for (int i = sx; i < ex; ++i) {
            for (int j = sz; j < ez; ++j) {
                this.buildingLocRef[i][j] = null;
            }
        }
    }

    public boolean update(World world, List<BuildingLocation> locations, Point centre, int radius) throws MillLog.MillenaireException {
        this.world = world;
        this.yBaseline = centre.getiY();
        if (this.buildingLocations != null && this.buildingLocations.size() > 0 && this.buildingLocations.size() == locations.size()) {
            this.buildingLocations = new ArrayList<BuildingLocation>(locations);
            this.updateNextChunk();
            return false;
        }
        int startX = centre.getiX();
        int startZ = centre.getiZ();
        int endX = centre.getiX();
        int endZ = centre.getiZ();
        BuildingLocation blStartX = null;
        BuildingLocation blStartZ = null;
        BuildingLocation blEndX = null;
        BuildingLocation blEndZ = null;
        for (BuildingLocation location : locations) {
            if (location == null) continue;
            if (location.pos.getiX() - location.length / 2 < startX) {
                startX = location.pos.getiX() - location.length / 2;
                blStartX = location;
            }
            if (location.pos.getiX() + location.length / 2 > endX) {
                endX = location.pos.getiX() + location.length / 2;
                blEndX = location;
            }
            if (location.pos.getiZ() - location.width / 2 < startZ) {
                startZ = location.pos.getiZ() - location.width / 2;
                blStartZ = location;
            }
            if (location.pos.getiZ() + location.width / 2 <= endZ) continue;
            endZ = location.pos.getiZ() + location.width / 2;
            blEndZ = location;
        }
        if (MillConfigValues.LogWorldInfo >= 1) {
            MillLog.major(this, "WorldInfo Centre: " + centre);
            if (startX - 5 < centre.getiX() - radius - 5) {
                MillLog.major(this, "Pushing startX down by " + (startX - 5 - (centre.getiX() - radius - 5)) + " due to " + blStartX);
            } else {
                MillLog.major(this, "Using default value of " + (centre.getiX() - radius - 5) + " for startX");
            }
            if (startZ - 5 < centre.getiZ() - radius - 5) {
                MillLog.major(this, "Pushing startZ down by " + (startZ - 5 - (centre.getiZ() - radius - 5)) + " due to " + blStartZ);
            } else {
                MillLog.major(this, "Using default value of " + (centre.getiZ() - radius - 5) + " for startZ");
            }
            if (endX + 5 > centre.getiX() + radius + 5) {
                MillLog.major(this, "Pushing endX up by " + (endX + 5 - (centre.getiX() + radius + 5)) + " due to " + blEndX);
            } else {
                MillLog.major(this, "Using default value of " + (centre.getiX() + radius + 5) + " for endX");
            }
            if (endZ + 5 > centre.getiZ() + radius + 5) {
                MillLog.major(this, "Pushing endZ up by " + (endZ + 5 - (centre.getiZ() + radius + 5)) + " due to " + blEndZ);
            } else {
                MillLog.major(this, "Using default value of " + (centre.getiZ() + radius + 5) + " for endZ");
            }
        }
        startX = Math.min(startX - 5, centre.getiX() - radius - 5);
        startZ = Math.min(startZ - 5, centre.getiZ() - radius - 5);
        endX = Math.max(endX + 5, centre.getiX() + radius + 5);
        endZ = Math.max(endZ + 5, centre.getiZ() + radius + 5);
        if (MillConfigValues.LogWorldInfo >= 1) {
            MillLog.major(this, "Values: " + startX + "/" + startZ + "/" + endX + "/" + endZ);
        }
        int chunkStartXTemp = startX >> 4;
        int chunkStartZTemp = startZ >> 4;
        int mapStartXTemp = chunkStartXTemp << 4;
        int mapStartZTemp = chunkStartZTemp << 4;
        int lengthTemp = ((endX >> 4) + 1 << 4) - mapStartXTemp;
        int widthTemp = ((endZ >> 4) + 1 << 4) - mapStartZTemp;
        if (MillConfigValues.LogWorldInfo >= 1) {
            MillLog.major(this, "Values after chunks: " + mapStartXTemp + "/" + mapStartZTemp + "/" + (mapStartXTemp + lengthTemp) + "/" + (mapStartZTemp + widthTemp));
        }
        if (lengthTemp != this.length || widthTemp != this.width) {
            this.createWorldInfo(locations, startX, startZ, endX, endZ);
            return true;
        }
        this.buildingLocations = new ArrayList<BuildingLocation>();
        for (BuildingLocation location : locations) {
            this.addBuildingLocationToMap(location);
        }
        this.updateNextChunk();
        return false;
    }

    private void updateChunk(int startX, int startZ) throws ThreadSafeUtilities.ChunkAccessException {
        int j;
        int i;
        for (int deltaX = -1; deltaX < 2; ++deltaX) {
            for (int deltaZ = -1; deltaZ < 2; ++deltaZ) {
                if (ThreadSafeUtilities.isChunkAtLoaded(this.world, startX + this.mapStartX + deltaX * 16, startZ + this.mapStartZ + deltaZ * 16)) continue;
                if (MillConfigValues.LogWorldInfo >= 3) {
                    MillLog.debug(this, "Chunk is not loaded at: " + (startX + this.mapStartX + deltaX * 16) + "/" + (startZ + this.mapStartZ + deltaZ * 16));
                }
                return;
            }
        }
        Chunk chunk = this.world.func_175726_f(new BlockPos(startX + this.mapStartX, 0, startZ + this.mapStartZ));
        if (MillConfigValues.LogWorldInfo >= 3) {
            MillLog.debug(this, "Updating chunk: " + startX + "/" + startZ + "/" + this.yBaseline + "/" + chunk.field_76635_g + "/" + chunk.field_76647_h);
        }
        for (i = 0; i < 16; ++i) {
            for (j = 0; j < 16; ++j) {
                short y;
                short miny = (short)Math.max(this.yBaseline - 25, 1);
                short maxy = (short)Math.min(this.yBaseline + 25, 255);
                int mx = i + startX;
                int mz = j + startZ;
                this.canBuild[mx][mz] = false;
                this.buildingForbidden[mx][mz] = false;
                this.water[mx][mz] = false;
                this.topAdjusted[mx][mz] = false;
                Block tblock = chunk.func_186032_a(i, (int)y, j).func_177230_c();
                for (y = maxy; y >= miny && !BlockItemUtilities.isBlockGround(tblock); y = (short)(y - 1)) {
                    if (BlockItemUtilities.isBlockForbidden(tblock)) {
                        this.buildingForbidden[mx][mz] = true;
                    }
                    tblock = chunk.func_186032_a(i, (int)y, j).func_177230_c();
                }
                Block block = y <= maxy && y > 1 ? chunk.func_186032_a(i, (int)y, j).func_177230_c() : null;
                boolean onground = true;
                short lastLiquid = -1;
                while (block != null && (BlockItemUtilities.isBlockSolid(block) || BlockItemUtilities.isBlockLiquid(block) || !onground)) {
                    if (BlockItemUtilities.isBlockForbidden(block)) {
                        this.buildingForbidden[mx][mz] = true;
                    }
                    if (BlockItemUtilities.isBlockLiquid(block)) {
                        onground = false;
                        lastLiquid = y;
                    } else if (BlockItemUtilities.isBlockSolid(block)) {
                        onground = true;
                    }
                    y = (short)(y + 1);
                    if (y <= maxy && y > 1) {
                        block = chunk.func_186032_a(i, (int)y, j).func_177230_c();
                        continue;
                    }
                    block = null;
                }
                if (!onground) {
                    y = lastLiquid;
                }
                while (y <= maxy && y > 1 && (BlockItemUtilities.isBlockSolid(chunk.func_186032_a(i, (int)y, j).func_177230_c()) || BlockItemUtilities.isBlockSolid(chunk.func_186032_a(i, y + 1, j).func_177230_c()))) {
                    y = (short)(y + 1);
                }
                this.topGround[mx][mz] = y = (short)((short)Math.max(1, y));
                this.spaceAbove[mx][mz] = 0;
                Block soilBlock = chunk.func_186032_a(i, y - 1, j).func_177230_c();
                block = chunk.func_186032_a(i, (int)y, j).func_177230_c();
                if (BlockItemUtilities.isBlockWater(block)) {
                    this.water[mx][mz] = true;
                }
                this.tree[mx][mz] = soilBlock instanceof BlockLog;
                this.path[mx][mz] = BlockItemUtilities.isPath(soilBlock);
                boolean blocked = false;
                if (!BlockItemUtilities.isFence(soilBlock) && !BlockItemUtilities.isBlockSolid(block) && block != Blocks.field_150358_i && soilBlock != Blocks.field_150355_j) {
                    this.spaceAbove[mx][mz] = 1;
                } else {
                    blocked = true;
                }
                if (BlockItemUtilities.isBlockDangerous(block)) {
                    if (MillConfigValues.LogWorldInfo >= 3) {
                        MillLog.debug(this, "Found danger: " + block);
                    }
                    this.danger[mx][mz] = true;
                } else {
                    this.danger[mx][mz] = false;
                    for (Block forbiddenBlock : MillConfigValues.forbiddenBlocks) {
                        if (forbiddenBlock == block) {
                            this.danger[mx][mz] = true;
                        }
                        if (soilBlock != block) continue;
                        this.danger[mx][mz] = true;
                    }
                }
                if (!this.danger[mx][mz] && this.buildingLocRef[mx][mz] == null && this.topGround[mx][mz] > this.yBaseline - 10 && this.topGround[mx][mz] < this.yBaseline + 10) {
                    this.canBuild[mx][mz] = true;
                }
                if (BlockItemUtilities.isBlockForbidden(block)) {
                    this.buildingForbidden[mx][mz] = true;
                }
                for (y = (short)((short)(y + 1)); y < maxy && y > 0; y = (short)((short)(y + 1))) {
                    block = chunk.func_186032_a(i, (int)y, j).func_177230_c();
                    if (!blocked && this.spaceAbove[mx][mz] < 3 && !BlockItemUtilities.isBlockSolid(block)) {
                        short[] sArray = this.spaceAbove[mx];
                        int n = mz;
                        sArray[n] = (short)(sArray[n] + 1);
                    } else {
                        blocked = true;
                    }
                    if (!BlockItemUtilities.isBlockForbidden(block)) continue;
                    this.buildingForbidden[mx][mz] = true;
                }
                if (this.buildingForbidden[mx][mz]) {
                    this.canBuild[mx][mz] = false;
                }
                if (this.buildingLocRef[mx][mz] == null) continue;
                this.topGround[mx][mz] = (short)this.buildingLocRef[mx][mz].pos.y;
                this.spaceAbove[mx][mz] = 3;
            }
        }
        for (i = 0; i < 16; ++i) {
            for (j = 0; j < 16; ++j) {
                int mx = i + startX;
                int mz = j + startZ;
                if (!this.danger[mx][mz]) continue;
                for (int k = -2; k < 3; ++k) {
                    for (int l = -2; l < 3; ++l) {
                        if (k < 0 || l < 0 || k >= this.length || l >= this.width) continue;
                        this.spaceAbove[mx][mz] = 0;
                    }
                }
            }
        }
    }

    public void updateNextChunk() {
        this.updateCounter = (this.updateCounter + 1) % this.frequency;
        if (this.updateCounter != 0) {
            return;
        }
        ++this.lastUpdatedX;
        if (this.lastUpdatedX * 16 >= this.length) {
            this.lastUpdatedX = 0;
            ++this.lastUpdatedZ;
        }
        if (this.lastUpdatedZ * 16 >= this.width) {
            this.lastUpdatedZ = 0;
        }
        UpdateThread thread = new UpdateThread();
        thread.setPriority(1);
        thread.x = this.lastUpdatedX << 4;
        thread.z = this.lastUpdatedZ << 4;
        thread.start();
    }

    public class UpdateThread
    extends Thread {
        int x;
        int z;

        @Override
        public void run() {
            block2: {
                try {
                    VillageMapInfo.this.updateChunk(this.x, this.z);
                }
                catch (ThreadSafeUtilities.ChunkAccessException e) {
                    if (MillConfigValues.LogChunkLoader < 2) break block2;
                    MillLog.minor(this, "Chunk access violation while attempting to update village map in thread at: " + (this.x + VillageMapInfo.this.mapStartX >> 4) + "/" + (this.z + VillageMapInfo.this.mapStartZ >> 4) + ", error at " + e.x + "/" + e.z + ", chunk at " + (e.x >> 4) + "/" + (e.z >> 4));
                }
            }
        }
    }
}

