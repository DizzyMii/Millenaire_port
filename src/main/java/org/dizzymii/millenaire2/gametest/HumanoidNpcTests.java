package org.dizzymii.millenaire2.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.behaviour.ConsumeFoodBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.ContextualToolSwapBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.InventoryManagementBehavior;
import org.dizzymii.millenaire2.entity.brain.behaviour.StrategicRetreatBehavior;
import org.dizzymii.millenaire2.entity.brain.sensor.InventoryStateSensor;
import org.dizzymii.millenaire2.entity.brain.sensor.SelfPreservationSensor;

import java.util.List;
import java.util.Optional;

/**
 * GameTest suite for the HumanoidNPC SBL (SmartBrainLib) architecture.
 *
 * <p>Covers:
 * <ul>
 *   <li>Entity spawns with a non-null Brain ({@link #testBrainIsConfiguredOnSpawn})
 *   <li>All three custom memory types are registered and readable
 *       ({@link #testCustomMemoryTypesAreRegistered})
 *   <li>WORK is the default active activity ({@link #testDefaultActivityIsWork})
 *   <li>{@link InventoryStateSensor} writes NEEDED_MATERIALS to brain memory
 *       ({@link #testInventoryStateSensorWritesNeededMaterials},
 *        {@link #testInventoryStateSensorReportsDamagedTool},
 *        {@link #testInventoryStateSensorEmptyInventoryReportsEmptyHands})
 *   <li>{@link ContextualToolSwapBehavior} swaps the correct tool for an objective
 *       ({@link #testToolSwapPickaxeForMineObjective},
 *        {@link #testToolSwapSwordForDefendObjective},
 *        {@link #testToolSwapAxeForGatherWoodObjective},
 *        {@link #testToolSwapNoSwapWhenAlreadyOptimal},
 *        {@link #testToolSwapNoOpWhenNoMatchingTool})
 *   <li>Base-location memory helpers work correctly
 *       ({@link #testSetBaseLocationStoresMemory},
 *        {@link #testClearBaseLocationErasesMemory})
 * </ul>
 */
@GameTestHolder(Millenaire2.MODID)
@PrefixGameTestTemplate(false)
public class HumanoidNpcTests {

    // ==================== Helpers ====================

    /** Spawns a {@link HumanoidNPC} at a test-relative position. */
    private static HumanoidNPC spawnNpc(GameTestHelper helper, int rx, int ry, int rz) {
        BlockPos abs = helper.absolutePos(new BlockPos(rx, ry, rz));
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = MillEntities.HUMANOID_NPC.get().create(level);
        if (npc == null) throw new IllegalStateException("Failed to create HumanoidNPC entity");
        npc.setPos(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5);
        level.addFreshEntity(npc);
        return npc;
    }

    /**
     * Creates an {@link InventoryStateSensor} that always fires on every tick
     * (scan rate = 1) so tests do not depend on the game-time being divisible
     * by the production scan rate.
     *
     * <p>{@code getScanRate} returns 1; since {@code getGameTime() % 1 == 0}
     * is unconditionally true, {@code doTick} is guaranteed to run on the
     * first call to {@code tick()}.
     */
    private static InventoryStateSensor alwaysFireSensor() {
        return new InventoryStateSensor() {
            @Override
            public int getScanRate(HumanoidNPC entity) {
                return 1;
            }
        };
    }

    private static SelfPreservationSensor alwaysFireSelfPreservationSensor() {
        return new SelfPreservationSensor() {
            @Override
            public int getScanRate(HumanoidNPC entity) {
                return 1;
            }
        };
    }

    // ==================== Brain configuration tests ====================

    /**
     * A freshly created {@link HumanoidNPC} must have a non-null Brain.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testBrainIsConfiguredOnSpawn(GameTestHelper helper) {
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        helper.assertFalse(npc.getBrain() == null, "Brain must not be null after spawn");
        helper.succeed();
    }

    /**
     * All custom memory module types must be registered and readable from
     * the brain without throwing.
     *
     * <p>Accessing a registered type with no value returns an empty Optional;
     * an unregistered type causes the Brain to throw.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testCustomMemoryTypesAreRegistered(GameTestHelper helper) {
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        Optional<?> baseLoc   = npc.getBrain().getMemory(ModMemoryTypes.BASE_LOCATION.get());
        Optional<?> objective = npc.getBrain().getMemory(ModMemoryTypes.MACRO_OBJECTIVE.get());
        Optional<?> materials = npc.getBrain().getMemory(ModMemoryTypes.NEEDED_MATERIALS.get());
        Optional<?> needsHealing = npc.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get());
        Optional<?> lastKnownDanger = npc.getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get());

        // getMemory returns Optional.empty() for registered-but-absent memories and
        // throws for unregistered ones; the non-null check proves registration succeeded.
        helper.assertFalse(baseLoc   == null, "BASE_LOCATION memory access must not return null");
        helper.assertFalse(objective == null, "MACRO_OBJECTIVE memory access must not return null");
        helper.assertFalse(materials == null, "NEEDED_MATERIALS memory access must not return null");
        helper.assertFalse(needsHealing == null, "NEEDS_HEALING memory access must not return null");
        helper.assertFalse(lastKnownDanger == null, "LAST_KNOWN_DANGER memory access must not return null");
        helper.succeed();
    }

    /**
     * The default active activity for a freshly spawned {@link HumanoidNPC} must
     * be {@link Activity#WORK} (the "acquisition" phase as per the architecture spec).
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testDefaultActivityIsWork(GameTestHelper helper) {
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        helper.assertTrue(npc.getBrain().isActive(Activity.WORK),
                "Default activity must be WORK, got: " + npc.getBrain().getActiveActivities());
        helper.succeed();
    }

    // ==================== InventoryStateSensor tests ====================

    /**
     * When the NPC has empty hands and no carried items, the sensor must write a
     * non-null list (containing at least the empty-hand entries) to
     * {@link ModMemoryTypes#NEEDED_MATERIALS}.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testInventoryStateSensorWritesNeededMaterials(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        alwaysFireSensor().tick(level, npc);

        Optional<List<String>> memory =
                npc.getBrain().getMemory(ModMemoryTypes.NEEDED_MATERIALS.get());
        helper.assertTrue(memory.isPresent(),
                "NEEDED_MATERIALS memory must be present after sensor tick");
        helper.succeed();
    }

    /**
     * When the NPC holds a critically damaged tool (≤ 25 % durability remaining,
     * matching {@link InventoryStateSensor}'s {@code DAMAGE_THRESHOLD}),
     * the sensor must include a {@code ":damaged:"} entry in the needs list.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testInventoryStateSensorReportsDamagedTool(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        // Damage the pickaxe to 95% of max (leaving only 5% durability — well below
        // the 25% DAMAGE_THRESHOLD in InventoryStateSensor).
        final float criticalDamageFraction = 0.95f;
        ItemStack brokenPickaxe = new ItemStack(Items.IRON_PICKAXE);
        int maxDamage = brokenPickaxe.getMaxDamage();
        brokenPickaxe.setDamageValue((int) (maxDamage * criticalDamageFraction));
        npc.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, brokenPickaxe);

        alwaysFireSensor().tick(level, npc);

        List<String> needs = npc.getBrain()
                .getMemory(ModMemoryTypes.NEEDED_MATERIALS.get())
                .orElse(List.of());
        boolean hasDamagedEntry = needs.stream().anyMatch(s -> s.contains(":damaged:"));
        helper.assertTrue(hasDamagedEntry,
                "Sensor must report damaged main-hand tool; needs=" + needs);
        helper.succeed();
    }

    /**
     * When both hand slots are empty the sensor must report at least two
     * {@code ":empty"} entries — one per hand.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testInventoryStateSensorEmptyInventoryReportsEmptyHands(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        alwaysFireSensor().tick(level, npc);

        List<String> needs = npc.getBrain()
                .getMemory(ModMemoryTypes.NEEDED_MATERIALS.get())
                .orElse(List.of());
        long emptyCount = needs.stream().filter(s -> s.endsWith(":empty")).count();
        helper.assertTrue(emptyCount >= 2,
                "Sensor must report at least 2 empty-hand entries; needs=" + needs);
        helper.succeed();
    }

    // ==================== ContextualToolSwapBehavior tests ====================

    /**
     * Given objective {@code "mine_stone"}, {@link ContextualToolSwapBehavior}
     * must swap an iron pickaxe from the inventory into the main hand.
     *
     * <p>The public {@link org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour#tryStart}
     * method is used to invoke the behaviour without breaking encapsulation.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testToolSwapPickaxeForMineObjective(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        npc.getBrain().setMemory(ModMemoryTypes.MACRO_OBJECTIVE.get(), "mine_stone");
        npc.addToCarriedInventory(new ItemStack(Items.IRON_PICKAXE));

        new ContextualToolSwapBehavior().tryStart(level, npc, level.getGameTime());

        ItemStack mainHand = npc.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        helper.assertTrue(mainHand.getItem() instanceof net.minecraft.world.item.PickaxeItem,
                "Main hand must hold a pickaxe after swap for mine_stone; got: "
                        + mainHand.getItem().getDescriptionId());
        helper.succeed();
    }

    /**
     * Given objective {@code "defend"}, the behaviour must swap a sword into
     * the main hand when the NPC's carried inventory contains one.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testToolSwapSwordForDefendObjective(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        npc.getBrain().setMemory(ModMemoryTypes.MACRO_OBJECTIVE.get(), "defend");
        npc.addToCarriedInventory(new ItemStack(Items.IRON_SWORD));

        new ContextualToolSwapBehavior().tryStart(level, npc, level.getGameTime());

        ItemStack mainHand = npc.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        helper.assertTrue(mainHand.getItem() instanceof net.minecraft.world.item.SwordItem,
                "Main hand must hold a sword after swap for defend; got: "
                        + mainHand.getItem().getDescriptionId());
        helper.succeed();
    }

    /**
     * Given objective {@code "gather_wood"}, the behaviour must swap an axe into
     * the main hand when the NPC's carried inventory contains one.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testToolSwapAxeForGatherWoodObjective(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        npc.getBrain().setMemory(ModMemoryTypes.MACRO_OBJECTIVE.get(), "gather_wood");
        npc.addToCarriedInventory(new ItemStack(Items.IRON_AXE));

        new ContextualToolSwapBehavior().tryStart(level, npc, level.getGameTime());

        ItemStack mainHand = npc.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        helper.assertTrue(mainHand.getItem() instanceof net.minecraft.world.item.AxeItem,
                "Main hand must hold an axe after swap for gather_wood; got: "
                        + mainHand.getItem().getDescriptionId());
        helper.succeed();
    }

    /**
     * When the NPC's main hand already holds an optimal tool for the active
     * objective, {@code tryStart} must return {@code false} — no swap occurs.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testToolSwapNoSwapWhenAlreadyOptimal(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        npc.getBrain().setMemory(ModMemoryTypes.MACRO_OBJECTIVE.get(), "mine_stone");
        // Place the pickaxe directly in the main hand — already optimal.
        npc.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                new ItemStack(Items.IRON_PICKAXE));

        boolean started = new ContextualToolSwapBehavior().tryStart(level, npc, level.getGameTime());
        helper.assertFalse(started,
                "tryStart must return false when main hand already holds the optimal tool");
        helper.succeed();
    }

    /**
     * When the NPC's carried inventory contains no tool matching the objective,
     * the behaviour must leave the main hand unchanged (no crash; main hand stays empty).
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testToolSwapNoOpWhenNoMatchingTool(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        npc.getBrain().setMemory(ModMemoryTypes.MACRO_OBJECTIVE.get(), "mine_stone");
        // Inventory has only a sword — no pickaxe available.
        npc.addToCarriedInventory(new ItemStack(Items.IRON_SWORD));

        new ContextualToolSwapBehavior().tryStart(level, npc, level.getGameTime());

        // Main hand must remain empty since no matching tool was found.
        ItemStack mainHand = npc.getItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND);
        helper.assertTrue(mainHand.isEmpty(),
                "Main hand must remain empty when no matching tool is in inventory; got: "
                        + mainHand.getItem().getDescriptionId());
        helper.succeed();
    }

    // ==================== Base-location memory tests ====================

    /**
     * {@link HumanoidNPC#setBaseLocation} must write a {@link net.minecraft.core.GlobalPos}
     * to the {@link ModMemoryTypes#BASE_LOCATION} memory slot.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testSetBaseLocationStoresMemory(GameTestHelper helper) {
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));
        net.minecraft.core.GlobalPos gp = net.minecraft.core.GlobalPos.of(
                helper.getLevel().dimension(), abs);

        npc.setBaseLocation(gp);

        Optional<net.minecraft.core.GlobalPos> mem =
                npc.getBrain().getMemory(ModMemoryTypes.BASE_LOCATION.get());
        helper.assertTrue(mem.isPresent(),
                "BASE_LOCATION memory must be present after setBaseLocation");
        helper.assertTrue(mem.get().pos().equals(abs),
                "BASE_LOCATION pos must match the pos that was set");
        helper.succeed();
    }

    /**
     * Passing {@code null} to {@link HumanoidNPC#setBaseLocation} must erase the
     * {@link ModMemoryTypes#BASE_LOCATION} memory.
     */
    @GameTest(template = "empty", timeoutTicks = 40)
    public static void testClearBaseLocationErasesMemory(GameTestHelper helper) {
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        BlockPos abs = helper.absolutePos(new BlockPos(1, 1, 1));
        npc.setBaseLocation(net.minecraft.core.GlobalPos.of(
                helper.getLevel().dimension(), abs));

        npc.setBaseLocation(null);

        Optional<net.minecraft.core.GlobalPos> mem =
                npc.getBrain().getMemory(ModMemoryTypes.BASE_LOCATION.get());
        helper.assertFalse(mem.isPresent(),
                "BASE_LOCATION memory must be absent after setBaseLocation(null)");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testSelfPreservationSensorWritesNeedsHealingAndDanger(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        npc.hurt(level.damageSources().generic(), 13.0f);
        npc.setNpcFoodLevel(6);

        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) throw new IllegalStateException("Failed to create zombie");
        zombie.setPos(npc.getX() + 2, npc.getY(), npc.getZ());
        level.addFreshEntity(zombie);

        alwaysFireSelfPreservationSensor().tick(level, npc);

        boolean needsHealing = npc.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false);
        Optional<BlockPos> danger = npc.getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get());
        helper.assertTrue(needsHealing, "NEEDS_HEALING should be true under 40% health");
        helper.assertTrue(danger.isPresent(), "LAST_KNOWN_DANGER should be present with nearby hostile");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testConsumeFoodBehaviorUsesBestFoodFromInventory(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        npc.setHealth(6.0f);
        npc.setNpcFoodLevel(5);
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), true);
        npc.addToCarriedInventory(new ItemStack(Items.APPLE));
        npc.addToCarriedInventory(new ItemStack(Items.COOKED_BEEF));

        new ConsumeFoodBehavior().tryStart(level, npc, level.getGameTime());

        helper.assertTrue(npc.getHealth() > 6.0f, "ConsumeFoodBehavior should heal when food is consumed");
        helper.assertTrue(npc.getNpcFoodLevel() > 5, "ConsumeFoodBehavior should increase NPC food level");
        boolean stillHasApple = npc.getCarriedInventory().stream().anyMatch(s -> s.is(Items.APPLE));
        boolean stillHasBeef = npc.getCarriedInventory().stream().anyMatch(s -> s.is(Items.COOKED_BEEF));
        helper.assertTrue(stillHasApple, "Lower-value food should remain after consuming best food first");
        helper.assertFalse(stillHasBeef, "Best food should be consumed and removed from carried inventory");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testConsumeFoodBehaviorRestoresPreviousMainHand(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        npc.setHealth(6.0f);
        npc.setNpcFoodLevel(5);
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), true);
        npc.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_SWORD));
        npc.addToCarriedInventory(new ItemStack(Items.COOKED_BEEF));

        new ConsumeFoodBehavior().tryStart(level, npc, level.getGameTime());

        ItemStack mainHand = npc.getItemInHand(InteractionHand.MAIN_HAND);
        helper.assertTrue(mainHand.is(Items.IRON_SWORD),
                "ConsumeFoodBehavior must restore pre-consumption main-hand item");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testStrategicRetreatBehaviorSprintsTowardBaseWhenThreatened(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 5, 1, 5);
        npc.setHealth(6.0f);
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), true);
        BlockPos base = helper.absolutePos(new BlockPos(1, 1, 1));
        npc.setBaseLocation(GlobalPos.of(level.dimension(), base));

        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) throw new IllegalStateException("Failed to create zombie");
        zombie.setPos(npc.getX() + 1, npc.getY(), npc.getZ() + 1);
        level.addFreshEntity(zombie);

        StrategicRetreatBehavior retreat = new StrategicRetreatBehavior();
        boolean started = retreat.tryStart(level, npc, level.getGameTime());
        helper.assertTrue(started, "StrategicRetreatBehavior should start when low health and hostiles are nearby");
        helper.assertTrue(npc.isSprinting(), "NPC should sprint while retreating");
        retreat.doStop(level, npc, level.getGameTime());
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testStrategicRetreatBehaviorKeepsRunningUnderThreat(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 5, 1, 5);
        npc.setHealth(6.0f);
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), true);

        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) throw new IllegalStateException("Failed to create zombie");
        zombie.setPos(npc.getX() + 1, npc.getY(), npc.getZ() + 1);
        level.addFreshEntity(zombie);

        StrategicRetreatBehavior retreat = new StrategicRetreatBehavior();
        helper.assertTrue(retreat.tryStart(level, npc, level.getGameTime()),
                "Retreat behavior should start when threatened");
        retreat.tickOrStop(level, npc, level.getGameTime() + 1);

        helper.assertTrue(retreat.getStatus().equals(Behavior.Status.RUNNING),
                "Retreat behavior should keep running while threat remains nearby");
        helper.assertTrue(npc.isSprinting(),
                "NPC should remain sprinting while retreat behavior is still running");
        retreat.doStop(level, npc, level.getGameTime() + 2);
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 80)
    public static void testInventoryManagementBehaviorDropsLowValueItemsWhenOverCapacity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);

        for (int i = 0; i < 30; i++) {
            npc.addToCarriedInventory(new ItemStack(Items.COBBLESTONE));
        }
        npc.addToCarriedInventory(new ItemStack(Items.DIAMOND));
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), false);
        npc.getBrain().eraseMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get());

        int beforeSize = npc.getCarriedInventory().size();
        new InventoryManagementBehavior().tryStart(level, npc, level.getGameTime());
        int afterSize = npc.getCarriedInventory().size();

        helper.assertTrue(afterSize < beforeSize, "InventoryManagementBehavior should discard low-value items");
        List<ItemEntity> dropped = level.getEntitiesOfClass(
                ItemEntity.class,
                npc.getBoundingBox().inflate(3.0D)
        );
        helper.assertFalse(dropped.isEmpty(), "Discarded low-value item should be spawned as ItemEntity");
        boolean stillHasDiamond = npc.getCarriedInventory().stream().anyMatch(s -> s.is(Items.DIAMOND));
        helper.assertTrue(stillHasDiamond, "High-value items should be retained after inventory cleanup");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 120)
    public static void testSurvivalActivityOverridesWorkWhenInDanger(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        npc.hurt(level.damageSources().generic(), 13.0f);

        Zombie zombie = EntityType.ZOMBIE.create(level);
        if (zombie == null) throw new IllegalStateException("Failed to create zombie");
        zombie.setPos(npc.getX() + 2, npc.getY(), npc.getZ() + 1);
        level.addFreshEntity(zombie);

        helper.runAtTickTime(helper.getTick() + 30, () -> {
            helper.assertTrue(
                    npc.getBrain().isActive(HumanoidNPC.SURVIVAL_ACTIVITY),
                    "SURVIVAL activity should override WORK when low health and threatened");
            helper.succeed();
        });
    }

    @GameTest(template = "empty", timeoutTicks = 120)
    public static void testLogisticsActivityDoesNotPreemptWorkWithoutLowValueItems(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        HumanoidNPC npc = spawnNpc(helper, 1, 1, 1);
        npc.setNpcFoodLevel(20);
        npc.getBrain().setMemory(ModMemoryTypes.NEEDS_HEALING.get(), false);
        npc.getBrain().eraseMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get());

        for (int i = 0; i < 30; i++) {
            npc.addToCarriedInventory(new ItemStack(Items.DIAMOND));
        }

        helper.runAtTickTime(helper.getTick() + 30, () -> {
            helper.assertTrue(npc.getBrain().isActive(Activity.WORK),
                    "WORK should remain active when no discardable logistics item exists");
            helper.assertFalse(npc.getBrain().isActive(HumanoidNPC.LOGISTICS_ACTIVITY),
                    "LOGISTICS should not activate without low-value discard candidates");
            helper.succeed();
        });
    }
}
