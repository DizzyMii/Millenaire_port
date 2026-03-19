package org.dizzymii.millenaire2.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.dizzymii.millenaire2.entity.blockentity.VillageStoneBlockEntity;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;

/**
 * The Village Stone block is placed at the centre of each Millénaire village.
 * Right-clicking opens the village information panel.
 * The block entity stores the village reference (townhall position).
 *
 * Ported from the original Millenaire's VillageStone.
 */
public class BlockVillageStone extends BaseEntityBlock {

    public static final MapCodec<BlockVillageStone> CODEC = simpleCodec(BlockVillageStone::new);

    public BlockVillageStone(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VillageStoneBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                Player player, BlockHitResult hitResult) {
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof VillageStoneBlockEntity vsbe)) {
            return InteractionResult.PASS;
        }

        Point townhallPos = vsbe.getTownHallPos();
        if (townhallPos == null) {
            player.sendSystemMessage(Component.literal("This village stone is not linked to a village."));
            return InteractionResult.SUCCESS;
        }

        // Find the building
        MillWorldData mw = MillWorldData.get(serverLevel);
        if (mw == null) {
            player.sendSystemMessage(Component.literal("World data not loaded."));
            return InteractionResult.SUCCESS;
        }

        Building townhall = mw.getBuilding(townhallPos);
        if (townhall == null) {
            player.sendSystemMessage(Component.literal("Village not found at " + townhallPos));
            return InteractionResult.SUCCESS;
        }

        // Send village info to the player
        String villageName = townhall.getName() != null ? townhall.getName() : "Unknown Village";
        String culture = townhall.cultureKey != null ? townhall.cultureKey : "unknown";
        int buildingCount = 0;
        int villagerCount = 0;
        for (Building b : mw.getBuildingsMap().values()) {
            if (townhall.isSameVillage(b)) {
                buildingCount++;
                villagerCount += b.getVillagerRecords().size();
            }
        }

        player.sendSystemMessage(Component.literal("=== " + villageName + " ==="));
        player.sendSystemMessage(Component.literal("Culture: " + culture));
        player.sendSystemMessage(Component.literal("Buildings: " + buildingCount));
        player.sendSystemMessage(Component.literal("Villagers: " + villagerCount));

        if (townhall.isUnderConstruction()) {
            player.sendSystemMessage(Component.literal("Status: Under construction"));
        } else {
            player.sendSystemMessage(Component.literal("Status: Active"));
        }

        return InteractionResult.SUCCESS;
    }
}
