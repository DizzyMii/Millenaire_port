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

    public enum TreeType { APPLE, OLIVE, PISTACHIO, CHERRY, SAKURA }

    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 1);
    private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

    private final TreeType treeType;

    public BlockMillSapling(BlockBehaviour.Properties props, TreeType treeType) {
        super(props);
        this.treeType = treeType;
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    public BlockMillSapling(BlockBehaviour.Properties props) {
        this(props, TreeType.APPLE);
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
        net.minecraft.world.level.block.grower.TreeGrower grower = switch (treeType) {
            case APPLE -> org.dizzymii.millenaire2.world.MillTreeGrowers.APPLE;
            case OLIVE -> org.dizzymii.millenaire2.world.MillTreeGrowers.OLIVE;
            case PISTACHIO -> org.dizzymii.millenaire2.world.MillTreeGrowers.PISTACHIO;
            case CHERRY -> org.dizzymii.millenaire2.world.MillTreeGrowers.CHERRY_MILL;
            case SAKURA -> org.dizzymii.millenaire2.world.MillTreeGrowers.SAKURA;
        };
        BlockState currentState = level.getBlockState(pos);
        if (!grower.growTree(level, level.getChunkSource().getGenerator(), pos, currentState, random)) {
            level.setBlock(pos, this.defaultBlockState().setValue(STAGE, 1), 4);
        }
    }

    @Override
    public boolean isValidBonemealTarget(net.minecraft.world.level.LevelReader level, BlockPos pos, BlockState state) {
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
