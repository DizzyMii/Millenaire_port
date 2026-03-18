package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
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
        tag.putInt("done", nbBlocksDone);
        tag.putInt("total", nbBlocksTotal);
        tag.putInt("orientation", orientation);
        tag.putBoolean("firstPassComplete", firstPassComplete);

        if (location != null) {
            location.save(tag, "loc");
        }

        // Save remaining first-pass blocks with block state
        ListTag firstList = new ListTag();
        for (BuildingBlock bb : firstPassBlocks) {
            firstList.add(saveBuildingBlock(bb));
        }
        tag.put("firstPass", firstList);

        // Save remaining second-pass blocks with block state
        ListTag secondList = new ListTag();
        for (BuildingBlock bb : secondPassBlocks) {
            secondList.add(saveBuildingBlock(bb));
        }
        tag.put("secondPass", secondList);

        return tag;
    }

    public static ConstructionIP load(CompoundTag tag) {
        ConstructionIP cip = new ConstructionIP();
        cip.nbBlocksDone = tag.getInt("done");
        cip.nbBlocksTotal = tag.getInt("total");
        cip.orientation = tag.getInt("orientation");
        cip.firstPassComplete = tag.getBoolean("firstPassComplete");

        cip.location = BuildingLocation.read(tag, "loc");

        if (tag.contains("firstPass", Tag.TAG_LIST)) {
            ListTag firstList = tag.getList("firstPass", Tag.TAG_COMPOUND);
            for (int i = 0; i < firstList.size(); i++) {
                cip.firstPassBlocks.add(loadBuildingBlock(firstList.getCompound(i), false));
            }
        }

        if (tag.contains("secondPass", Tag.TAG_LIST)) {
            ListTag secondList = tag.getList("secondPass", Tag.TAG_COMPOUND);
            for (int i = 0; i < secondList.size(); i++) {
                cip.secondPassBlocks.add(loadBuildingBlock(secondList.getCompound(i), true));
            }
        }

        return cip;
    }

    // ========== Block NBT helpers ==========

    private static CompoundTag saveBuildingBlock(BuildingBlock bb) {
        CompoundTag bt = new CompoundTag();
        bt.putInt("x", bb.x);
        bt.putInt("y", bb.y);
        bt.putInt("z", bb.z);
        bt.putBoolean("second", bb.secondStep);
        if (bb.blockState != null) {
            bt.put("state", NbtUtils.writeBlockState(bb.blockState));
        }
        return bt;
    }

    private static BuildingBlock loadBuildingBlock(CompoundTag bt, boolean defaultSecond) {
        BuildingBlock bb = new BuildingBlock();
        bb.x = bt.getInt("x");
        bb.y = bt.getInt("y");
        bb.z = bt.getInt("z");
        bb.secondStep = bt.contains("second") ? bt.getBoolean("second") : defaultSecond;
        if (bt.contains("state", Tag.TAG_COMPOUND)) {
            bb.blockState = NbtUtils.readBlockState(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(),
                    bt.getCompound("state"));
        }
        return bb;
    }
}
