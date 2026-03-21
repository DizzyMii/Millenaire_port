package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.dizzymii.millenaire2.buildingplan.BuildingBlock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a construction in progress for a building.
 * Tracks which blocks remain to be placed and manages step-by-step construction.
 * Ported from org.millenaire.common.village.ConstructionIP (Forge 1.12.2).
 */
public class ConstructionIP {

    // ========== NBT key constants ==========
    private static final String NBT_DONE = "done";
    private static final String NBT_TOTAL = "total";
    private static final String NBT_ORIENTATION = "orientation";
    private static final String NBT_FIRST_PASS_COMPLETE = "firstPassComplete";
    private static final String NBT_LOC = "loc";
    private static final String NBT_FIRST_PASS = "firstPass";
    private static final String NBT_SECOND_PASS = "secondPass";
    private static final String NBT_BB_X = "x";
    private static final String NBT_BB_Y = "y";
    private static final String NBT_BB_Z = "z";
    private static final String NBT_BB_SECOND = "second";
    private static final String NBT_BB_STATE = "state";

    @Nullable public BuildingLocation location;
    public int nbBlocksDone = 0;
    public int nbBlocksTotal = 0;
    public int orientation = 0;

    // Blocks remaining to place, split into first-pass and second-pass
    public final List<BuildingBlock> firstPassBlocks = new ArrayList<>();
    public final List<BuildingBlock> secondPassBlocks = new ArrayList<>();
    public boolean firstPassComplete = false;

    public ConstructionIP() {}

    public ConstructionIP(BuildingLocation location) {
        this.location = location;
    }

    /**
     * Initialize from a list of building blocks, splitting into first/second pass.
     */
    public void setBlocks(List<BuildingBlock> allBlocks) {
        firstPassBlocks.clear();
        secondPassBlocks.clear();
        for (BuildingBlock bb : allBlocks) {
            if (bb.secondStep) {
                secondPassBlocks.add(bb);
            } else {
                firstPassBlocks.add(bb);
            }
        }
        nbBlocksTotal = allBlocks.size();
        nbBlocksDone = 0;
        firstPassComplete = false;
    }

    public float getCompletionPercent() {
        if (nbBlocksTotal <= 0) return 0f;
        return (float) nbBlocksDone / (float) nbBlocksTotal;
    }

    public boolean isComplete() {
        return firstPassBlocks.isEmpty() && secondPassBlocks.isEmpty();
    }

    /**
     * Place the next block in the construction sequence.
     * @return true if a block was placed, false if construction is done or no origin
     */
    public boolean placeNextBlock(ServerLevel level) {
        if (location == null) return false;
        BlockPos origin = location.pos != null ? location.pos.toBlockPos() : null;
        if (origin == null) return false;

        // First pass
        if (!firstPassBlocks.isEmpty()) {
            BuildingBlock bb = firstPassBlocks.remove(0);
            bb.place(level, origin, orientation);
            nbBlocksDone++;
            return true;
        }

        if (!firstPassComplete) {
            firstPassComplete = true;
        }

        // Second pass
        if (!secondPassBlocks.isEmpty()) {
            BuildingBlock bb = secondPassBlocks.remove(0);
            bb.place(level, origin, orientation);
            nbBlocksDone++;
            return true;
        }

        return false;
    }

    /**
     * Place multiple blocks at once (batch construction per tick).
     * @return number of blocks placed
     */
    public int placeBlocks(ServerLevel level, int maxPerTick) {
        int placed = 0;
        for (int i = 0; i < maxPerTick; i++) {
            if (!placeNextBlock(level)) break;
            placed++;
        }
        return placed;
    }

    // ========== Factory from BuildingPlan ==========

    /**
     * Create a ConstructionIP from a BuildingPlan at the given origin position.
     * Uses the plan's parsed color data to generate BuildingBlocks via PointType lookup.
     */
    @Nullable
    public static ConstructionIP fromBuildingPlan(
            org.dizzymii.millenaire2.culture.BuildingPlan plan,
            org.dizzymii.millenaire2.util.Point origin,
            ServerLevel level) {
        if (plan == null || !plan.hasImage() || origin == null) return null;

        List<BuildingBlock> allBlocks = new ArrayList<>();
        int w = plan.width;
        int l = plan.length;
        int floors = plan.nbFloors;

        for (int floor = 0; floor < floors; floor++) {
            int y = floor + plan.altitudeOffset;
            for (int x = 0; x < w; x++) {
                for (int z = 0; z < l; z++) {
                    int rgb = plan.getBlockColor(x, z, floor);
                    if (rgb < 0 || rgb == 0xFFFFFF) continue;

                    org.dizzymii.millenaire2.buildingplan.PointType pt =
                            org.dizzymii.millenaire2.buildingplan.PointType.colourPoints.get(rgb);
                    if (pt == null || pt.getBlock() == null) continue;

                    allBlocks.add(new BuildingBlock(pt, x, y, z));
                }
            }
        }

        if (allBlocks.isEmpty()) return null;

        BuildingLocation loc = new BuildingLocation();
        loc.pos = origin;
        ConstructionIP cip = new ConstructionIP(loc);
        cip.setBlocks(allBlocks);
        return cip;
    }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(NBT_DONE, nbBlocksDone);
        tag.putInt(NBT_TOTAL, nbBlocksTotal);
        tag.putInt(NBT_ORIENTATION, orientation);
        tag.putBoolean(NBT_FIRST_PASS_COMPLETE, firstPassComplete);

        if (location != null) {
            location.save(tag, NBT_LOC);
        }

        // Save remaining first-pass blocks with full BlockState
        ListTag firstList = new ListTag();
        for (BuildingBlock bb : firstPassBlocks) {
            firstList.add(saveBuildingBlock(bb));
        }
        tag.put(NBT_FIRST_PASS, firstList);

        // Save remaining second-pass blocks with full BlockState
        ListTag secondList = new ListTag();
        for (BuildingBlock bb : secondPassBlocks) {
            secondList.add(saveBuildingBlock(bb));
        }
        tag.put(NBT_SECOND_PASS, secondList);

        return tag;
    }

    public static ConstructionIP load(CompoundTag tag) {
        ConstructionIP cip = new ConstructionIP();
        cip.nbBlocksDone = tag.getInt(NBT_DONE);
        cip.nbBlocksTotal = tag.getInt(NBT_TOTAL);
        cip.orientation = tag.getInt(NBT_ORIENTATION);
        cip.firstPassComplete = tag.getBoolean(NBT_FIRST_PASS_COMPLETE);

        cip.location = BuildingLocation.read(tag, NBT_LOC);

        if (tag.contains(NBT_FIRST_PASS, Tag.TAG_LIST)) {
            ListTag firstList = tag.getList(NBT_FIRST_PASS, Tag.TAG_COMPOUND);
            for (int i = 0; i < firstList.size(); i++) {
                cip.firstPassBlocks.add(loadBuildingBlock(firstList.getCompound(i), false));
            }
        }

        if (tag.contains(NBT_SECOND_PASS, Tag.TAG_LIST)) {
            ListTag secondList = tag.getList(NBT_SECOND_PASS, Tag.TAG_COMPOUND);
            for (int i = 0; i < secondList.size(); i++) {
                cip.secondPassBlocks.add(loadBuildingBlock(secondList.getCompound(i), true));
            }
        }

        return cip;
    }

    private static CompoundTag saveBuildingBlock(BuildingBlock bb) {
        CompoundTag bt = new CompoundTag();
        bt.putInt(NBT_BB_X, bb.x);
        bt.putInt(NBT_BB_Y, bb.y);
        bt.putInt(NBT_BB_Z, bb.z);
        bt.putBoolean(NBT_BB_SECOND, bb.secondStep);
        if (bb.blockState != null) {
            bt.put(NBT_BB_STATE, NbtUtils.writeBlockState(bb.blockState));
        }
        return bt;
    }

    private static BuildingBlock loadBuildingBlock(CompoundTag bt, boolean defaultSecond) {
        BuildingBlock bb = new BuildingBlock();
        bb.x = bt.getInt(NBT_BB_X);
        bb.y = bt.getInt(NBT_BB_Y);
        bb.z = bt.getInt(NBT_BB_Z);
        bb.secondStep = bt.contains(NBT_BB_SECOND) ? bt.getBoolean(NBT_BB_SECOND) : defaultSecond;
        if (bt.contains(NBT_BB_STATE, Tag.TAG_COMPOUND)) {
            bb.blockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), bt.getCompound(NBT_BB_STATE));
        }
        return bb;
    }
}
