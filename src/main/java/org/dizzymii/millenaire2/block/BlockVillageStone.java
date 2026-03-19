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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof VillageStoneBlockEntity villageStone)) {
            return InteractionResult.PASS;
        }

        Point townHallPos = villageStone.getTownHallPos();
        if (townHallPos == null) {
            player.sendSystemMessage(Component.literal("This village stone is not linked to a village."));
            return InteractionResult.sidedSuccess(false);
        }

        MillWorldData worldData = MillWorldData.get(serverLevel);
        Building townHall = worldData.getBuilding(townHallPos);
        if (townHall == null) {
            player.sendSystemMessage(Component.literal("Village not found at " + townHallPos));
            return InteractionResult.sidedSuccess(false);
        }

        String villageName = townHall.getName() != null ? townHall.getName() : "Unknown Village";
        String culture = townHall.cultureKey != null ? townHall.cultureKey : "unknown";
        int buildingCount = 0;
        int villagerCount = 0;

        Point townHallVillagePos = townHall.isTownhall ? townHall.getPos() : townHall.getTownHallPos();

        for (Building building : worldData.getBuildingsMap().values()) {
            Point buildingVillagePos = building.isTownhall ? building.getPos() : building.getTownHallPos();
            if (townHallVillagePos == null || buildingVillagePos == null || !townHallVillagePos.equals(buildingVillagePos)) {
                continue;
            }

            buildingCount++;
            villagerCount += building.getVillagerRecords().size();
        }

        player.sendSystemMessage(Component.literal("=== " + villageName + " ==="));
        player.sendSystemMessage(Component.literal("Culture: " + culture));
        player.sendSystemMessage(Component.literal("Buildings: " + buildingCount));
        player.sendSystemMessage(Component.literal("Villagers: " + villagerCount));
        player.sendSystemMessage(Component.literal("Status: " + (townHall.isUnderConstruction() ? "Under construction" : "Active")));

        return InteractionResult.sidedSuccess(false);
    }
}
