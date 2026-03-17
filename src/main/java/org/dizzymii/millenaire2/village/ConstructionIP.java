package org.dizzymii.millenaire2.village;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

        // Save remaining first-pass block positions
        ListTag firstList = new ListTag();
        for (BuildingBlock bb : firstPassBlocks) {
            CompoundTag bt = new CompoundTag();
            bt.putInt("x", bb.x);
            bt.putInt("y", bb.y);
            bt.putInt("z", bb.z);
            firstList.add(bt);
        }
        tag.put("firstPass", firstList);

        // Save remaining second-pass block positions
        ListTag secondList = new ListTag();
        for (BuildingBlock bb : secondPassBlocks) {
            CompoundTag bt = new CompoundTag();
            bt.putInt("x", bb.x);
            bt.putInt("y", bb.y);
            bt.putInt("z", bb.z);
            secondList.add(bt);
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

        // Note: block data (blockState) cannot be fully restored from just x/y/z.
        // A full implementation would re-derive block data from the plan.
        // For now we track positions so progress is preserved.
        if (tag.contains("firstPass", Tag.TAG_LIST)) {
            ListTag firstList = tag.getList("firstPass", Tag.TAG_COMPOUND);
            for (int i = 0; i < firstList.size(); i++) {
                CompoundTag bt = firstList.getCompound(i);
                BuildingBlock bb = new BuildingBlock();
                bb.x = bt.getInt("x");
                bb.y = bt.getInt("y");
                bb.z = bt.getInt("z");
                cip.firstPassBlocks.add(bb);
            }
        }

        if (tag.contains("secondPass", Tag.TAG_LIST)) {
            ListTag secondList = tag.getList("secondPass", Tag.TAG_COMPOUND);
            for (int i = 0; i < secondList.size(); i++) {
                CompoundTag bt = secondList.getCompound(i);
                BuildingBlock bb = new BuildingBlock();
                bb.x = bt.getInt("x");
                bb.y = bt.getInt("y");
                bb.z = bt.getInt("z");
                bb.secondStep = true;
                cip.secondPassBlocks.add(bb);
            }
        }

        return cip;
    }
}
