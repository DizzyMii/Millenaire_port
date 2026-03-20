package org.dizzymii.sblpoc;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.FloatToSurfaceOfFluid;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.TargetOrRetaliate;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.behaviour.DodgeProjectileBehaviour;
import org.dizzymii.sblpoc.behaviour.DrinkPotionBehaviour;
import org.dizzymii.sblpoc.behaviour.EatFoodBehaviour;
import org.dizzymii.sblpoc.behaviour.RetreatToAlliesBehaviour;
import org.dizzymii.sblpoc.behaviour.SeekCoverBehaviour;
import org.dizzymii.sblpoc.behaviour.ShieldBlockBehaviour;
import org.dizzymii.sblpoc.behaviour.SmartMeleeAttack;
import org.dizzymii.sblpoc.behaviour.SmartRangedAttack;
import org.dizzymii.sblpoc.sensor.IncomingDamageSensor;
import org.dizzymii.sblpoc.sensor.NearbyThreatSensor;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Proof-of-concept NPC demonstrating SmartBrainLib.
 * 
 * Features:
 * - Intelligent shield blocking (reactive to projectiles, melee wind-ups, being outnumbered, low HP)
 * - Eats food from inventory when damaged and safe
 * - Smart melee combat with hit-and-kite pattern
 * 
 * Spawns with: Iron Sword, Iron Axe, Shield, Bow, 32x Arrow, 8x Steak, 2x Healing Potion, 1x Strength Potion
 * Summon: /summon millenaire2:poc_npc ~ ~ ~
 */
public class PocNpc extends PathfinderMob implements SmartBrainOwner<PocNpc> {

    private final SimpleContainer inventory = new SimpleContainer(12);
    private int strategyEvalTimer = 0;

    public PocNpc(EntityType<? extends PocNpc> type, Level level) {
        super(type, level);
        setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ARMOR, 6.0);
    }

    public SimpleContainer getInventory() {
        return inventory;
    }

    // ========== SmartBrainOwner Implementation ==========

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    @Override
    public List<? extends ExtendedSensor<? extends PocNpc>> getSensors() {
        return ObjectArrayList.of(
                new NearbyThreatSensor(),     // Scans for hostiles every 10 ticks
                new IncomingDamageSensor(),    // Detects incoming projectiles/melee every 5 ticks
                new HurtBySensor<>()           // Tracks damage source every 20 ticks
        );
    }

    @Override
    public BrainActivityGroup<? extends PocNpc> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>(),
                new FloatToSurfaceOfFluid<>()
        );
    }

    @Override
    public BrainActivityGroup<? extends PocNpc> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new FirstApplicableBehaviour<PocNpc>(
                        new TargetOrRetaliate<PocNpc>()
                                .useMemory(MemoryModuleType.NEAREST_HOSTILE),
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>()
                ),
                new OneRandomBehaviour<PocNpc>(
                        new SetRandomWalkTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    @Override
    public BrainActivityGroup<? extends PocNpc> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new InvalidateAttackTarget<>(),
                new FirstApplicableBehaviour<PocNpc>(
                        new RetreatToAlliesBehaviour(), // Priority 1: flee to friends when critical HP
                        new ShieldBlockBehaviour(),     // Priority 2: reactive shield blocking / parry-riposte
                        new DodgeProjectileBehaviour(),  // Priority 3: sidestep incoming arrows
                        new SeekCoverBehaviour(),        // Priority 4: hide from ranged fire
                        new DrinkPotionBehaviour(),      // Priority 5: drink healing/strength potions
                        new EatFoodBehaviour(),          // Priority 6: eat food when safe
                        new SmartRangedAttack(),         // Priority 7: bow at range (6-24 blocks)
                        new SmartMeleeAttack()           // Priority 8: melee + weapon switch + hazard kiting
                )
        );
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);

        // Strategy adaptation — evaluate every 5 seconds
        strategyEvalTimer++;
        if (strategyEvalTimer >= 100) {
            strategyEvalTimer = 0;
            evaluateStrategy();
        }
    }

    // ========== Parry-Riposte Hook ==========

    @Override
    protected void hurtCurrentlyUsedShield(float damage) {
        super.hurtCurrentlyUsedShield(damage);
        // Signal to ShieldBlockBehaviour that we just absorbed a hit
        BrainUtils.setMemory(this, SblPocSetup.JUST_BLOCKED_HIT.get(), true);
    }

    // ========== Strategy Adaptation ==========

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (result && !this.level().isClientSide()) {
            // Track damage taken while in melee vs ranged state
            String state = BrainUtils.getMemory(this, SblPocSetup.COMBAT_STATE.get());
            if ("melee".equals(state)) {
                Integer dmg = BrainUtils.getMemory(this, SblPocSetup.DAMAGE_TAKEN_MELEE.get());
                BrainUtils.setMemory(this, SblPocSetup.DAMAGE_TAKEN_MELEE.get(),
                        (dmg != null ? dmg : 0) + (int) amount);
            } else if ("ranged".equals(state)) {
                Integer dmg = BrainUtils.getMemory(this, SblPocSetup.DAMAGE_TAKEN_RANGED.get());
                BrainUtils.setMemory(this, SblPocSetup.DAMAGE_TAKEN_RANGED.get(),
                        (dmg != null ? dmg : 0) + (int) amount);
            }
        }
        return result;
    }

    private void evaluateStrategy() {
        Integer meleeDmg = BrainUtils.getMemory(this, SblPocSetup.DAMAGE_TAKEN_MELEE.get());
        Integer rangedDmg = BrainUtils.getMemory(this, SblPocSetup.DAMAGE_TAKEN_RANGED.get());
        int m = meleeDmg != null ? meleeDmg : 0;
        int r = rangedDmg != null ? rangedDmg : 0;

        // If taking significantly more damage in melee, prefer ranged
        if (m > r + 6) {
            BrainUtils.setMemory(this, SblPocSetup.PREFERRED_STRATEGY.get(), "ranged");
        } else if (r > m + 6) {
            BrainUtils.setMemory(this, SblPocSetup.PREFERRED_STRATEGY.get(), "melee");
        }
        // else keep current — not enough data to switch

        // Reset counters for next evaluation window
        BrainUtils.setMemory(this, SblPocSetup.DAMAGE_TAKEN_MELEE.get(), 0);
        BrainUtils.setMemory(this, SblPocSetup.DAMAGE_TAKEN_RANGED.get(), 0);

        // Reset help-called flag so NPC can re-alert allies if needed
        BrainUtils.clearMemory(this, SblPocSetup.HELP_CALLED.get());
    }

    // ========== Spawn Equipment ==========

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                         MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);

        // Equip iron sword + shield
        setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));

        // Stock inventory: bow, axe, arrows, food, potions
        inventory.setItem(0, new ItemStack(Items.BOW));
        inventory.setItem(1, new ItemStack(Items.IRON_AXE));
        inventory.setItem(2, new ItemStack(Items.ARROW, 32));
        inventory.setItem(3, new ItemStack(Items.COOKED_BEEF, 8));
        inventory.setItem(4, PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        inventory.setItem(5, PotionContents.createItemStack(Items.POTION, Potions.HEALING));
        inventory.setItem(6, PotionContents.createItemStack(Items.POTION, Potions.STRENGTH));

        // Don't drop equipment on death
        setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        setDropChance(EquipmentSlot.OFFHAND, 0.0f);

        return spawnData;
    }

    // ========== NBT Persistence ==========

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        // Save inventory
        net.minecraft.core.NonNullList<ItemStack> items =
                net.minecraft.core.NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            items.set(i, inventory.getItem(i));
        }
        net.minecraft.world.ContainerHelper.saveAllItems(tag, items, this.registryAccess());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        // Load inventory
        net.minecraft.core.NonNullList<ItemStack> items =
                net.minecraft.core.NonNullList.withSize(inventory.getContainerSize(), ItemStack.EMPTY);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, items, this.registryAccess());
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }
}
