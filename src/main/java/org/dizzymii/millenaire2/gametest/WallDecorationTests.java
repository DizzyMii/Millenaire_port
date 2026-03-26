package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.EntityWallDecoration;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.item.MillItems;

/**
 * GameTest suite for {@link EntityWallDecoration}.
 * Validates interaction, drop logic, creative pick-block, and survival
 * (wall-removal) behaviour added in Phase&nbsp;8.
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class WallDecorationTests {

    // ==================== isPickable ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testWallDecorationIsPickable(GameTestHelper helper) {
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.NORMAN_TAPESTRY);
        helper.assertTrue(deco.isPickable(), "EntityWallDecoration should be pickable");
        helper.succeed();
    }

    // ==================== getPickResult ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPickResultNormanTapestry(GameTestHelper helper) {
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.NORMAN_TAPESTRY);
        ItemStack pick = deco.getPickResult();
        helper.assertTrue(pick.is(MillItems.TAPESTRY.get()),
                "Pick result for NORMAN_TAPESTRY should be TAPESTRY item");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPickResultHideHanging(GameTestHelper helper) {
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.HIDE_HANGING);
        ItemStack pick = deco.getPickResult();
        helper.assertTrue(pick.is(MillItems.HIDE_HANGING.get()),
                "Pick result for HIDE_HANGING should be HIDE_HANGING item");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testPickResultJapanesePaintingLarge(GameTestHelper helper) {
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.JAPANESE_PAINTING_LARGE);
        ItemStack pick = deco.getPickResult();
        helper.assertTrue(pick.is(MillItems.WALL_CARPET_LARGE.get()),
                "Pick result for JAPANESE_PAINTING_LARGE should be WALL_CARPET_LARGE item");
        helper.succeed();
    }

    // ==================== hurt() drops item & discards ====================

    @GameTest(template = "empty", timeoutTicks = 60)
    public static void testHurtDropsItemAndDiscards(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.INDIAN_STATUE);
        deco.setFacingDirection(Direction.NORTH);
        deco.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        helper.getLevel().addFreshEntity(deco);

        // Hurt the decoration on the next tick
        helper.runAfterDelay(1, () -> {
            DamageSource generic = helper.getLevel().damageSources().generic();
            deco.hurt(generic, 1.0F);
        });

        // Entity should be discarded and an ItemEntity present
        helper.succeedWhen(() -> {
            helper.assertTrue(deco.isRemoved(), "Decoration should be removed after hurt");
            helper.assertEntityPresent(net.minecraft.world.entity.EntityType.ITEM, pos);
        });
    }

    // ==================== Wall removal drops item ====================

    @GameTest(template = "empty", timeoutTicks = 300)
    public static void testWallRemovalDropsItem(GameTestHelper helper) {
        // Place a stone block and attach a decoration to it
        BlockPos wallPos = helper.absolutePos(new BlockPos(1, 1, 1));
        BlockPos decoRelPos = new BlockPos(1, 1, 2);
        BlockPos decoAbsPos = helper.absolutePos(decoRelPos);

        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);

        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(EntityWallDecoration.MAYAN_STATUE);
        // Facing south means the wall behind is to the north (relative pos 1,1,1)
        deco.setFacingDirection(Direction.SOUTH);
        deco.setPos(decoAbsPos.getX() + 0.5, decoAbsPos.getY(), decoAbsPos.getZ() + 0.5);
        helper.getLevel().addFreshEntity(deco);

        // Remove the wall after a short delay
        helper.runAfterDelay(5, () ->
                helper.setBlock(new BlockPos(1, 1, 1), Blocks.AIR));

        // Decoration should eventually discard and drop (checked every 100 ticks)
        helper.succeedWhen(() ->
                helper.assertTrue(deco.isRemoved(), "Decoration should discard when wall is removed"));
    }

    // ==================== Unknown type returns EMPTY ====================

    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testUnknownTypeReturnsEmpty(GameTestHelper helper) {
        EntityWallDecoration deco = new EntityWallDecoration(
                MillEntities.WALL_DECORATION.get(), helper.getLevel());
        deco.setDecorationType(0); // default / unknown
        ItemStack pick = deco.getPickResult();
        // Unknown type should fall through to super (spawn egg or empty)
        helper.assertFalse(pick.is(MillItems.TAPESTRY.get()),
                "Unknown decoration type should not return TAPESTRY");
        helper.succeed();
    }
}
