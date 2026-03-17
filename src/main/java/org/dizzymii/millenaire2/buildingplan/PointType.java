package org.dizzymii.millenaire2.buildingplan;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.dizzymii.millenaire2.item.InvItem;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * Represents a colour-coded point in a building plan PNG.
 * Maps a colour to a block/special type, with optional cost.
 * Ported from org.millenaire.common.buildingplan.PointType (Forge 1.12.2).
 */
public class PointType {

    public static final String SUBTYPE_SIGN = "sign";
    public static final String SUBTYPE_MAINCHEST = "mainchest";
    public static final String SUBTYPE_LOCKEDCHEST = "lockedchest";
    public static final String SUBTYPE_VILLAGEBANNERWALL = "villageBannerWall";
    public static final String SUBTYPE_VILLAGEBANNERSTANDING = "villageBannerStanding";
    public static final String SUBTYPE_CULTUREBANNERWALL = "cultureBannerWall";
    public static final String SUBTYPE_CULTUREBANNERSTANDING = "cultureBannerStanding";

    public static HashMap<Integer, PointType> colourPoints = new HashMap<>();

    final int colour;
    @Nullable final String specialType;
    final String label;
    @Nullable private final Block block;
    @Nullable private final BlockState blockState;
    @Nullable private InvItem costItem = null;
    @Nullable private BlockState costBlockState = null;
    private int costQuantity = 1;
    boolean secondStep = false;

    public PointType(int colour, String name) {
        this.specialType = name;
        this.colour = colour;
        this.block = null;
        this.label = name;
        this.blockState = null;
    }

    public PointType(int colour, String label, Block block, BlockState blockState, boolean secondStep) {
        this.colour = colour;
        this.block = block;
        this.blockState = blockState;
        this.secondStep = secondStep;
        this.specialType = null;
        this.label = label;
    }

    @Nullable public Block getBlock() { return block; }
    @Nullable public BlockState getBlockState() { return blockState; }
    @Nullable public String getSpecialType() { return specialType; }
    public int getCostQuantity() { return costQuantity; }

    public boolean isSubType(String type) {
        return specialType != null && specialType.startsWith(type);
    }

    public boolean isType(String type) {
        return type.equalsIgnoreCase(specialType);
    }

    public void setCost(BlockState blockState, int quantity) {
        this.costBlockState = blockState;
        this.costQuantity = quantity;
        this.costItem = null;
    }

    public void setCost(InvItem item, int quantity) {
        this.costBlockState = null;
        this.costQuantity = quantity;
        this.costItem = item;
    }

    public void setCostToFree() {
        this.costBlockState = null;
        this.costQuantity = 0;
        this.costItem = null;
    }

    @Override
    public String toString() {
        return label + "/" + specialType + "/" + colour + "/" + block + "/" + blockState;
    }

    // TODO: readColourPoint(String), getCostInvItem() — need block registry lookups
}
