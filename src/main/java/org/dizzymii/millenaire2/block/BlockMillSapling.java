package org.dizzymii.millenaire2.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Custom Millenaire sapling that grows into a custom tree.
 * Uses a STAGE property (0-1); when it reaches stage 1 and gets another growth tick, it grows.
 * Tree generation is delegated to the world-gen tree generators.
 */
public class BlockMillSapling extends Block implements BonemealableBlock {

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    public BlockMillSapling(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            advanceTree(state, level, pos, random);
        }
    }

    private void advanceTree(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(STAGE) == 0) {
            level.setBlock(pos, state.setValue(STAGE, 1), 4);
        } else {
            growTree(level, pos, random);
        }
    }

    private void growTree(ServerLevel level, BlockPos pos, RandomSource random) {
        // Replace sapling with oak log + leaves as fallback.
        // Custom tree generators (WorldGenTree subclasses) will be wired in Phase 10.
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState(), 3);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 3; dy <= 5; dy++) {
                    BlockPos leafPos = pos.offset(dx, dy, dz);
                    if (level.isEmptyBlock(leafPos)) {
                        level.setBlock(leafPos, net.minecraft.world.level.block.Blocks.OAK_LEAVES.defaultBlockState(), 3);
                    }
                }
            }
        }
        // Place trunk
        for (int y = 1; y <= 3; y++) {
            BlockPos logPos = pos.above(y);
            if (level.isEmptyBlock(logPos)) {
                level.setBlock(logPos, net.minecraft.world.level.block.Blocks.OAK_LOG.defaultBlockState(), 3);
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return level.random.nextFloat() < 0.45F;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        advanceTree(state, level, pos, random);
    }
}
