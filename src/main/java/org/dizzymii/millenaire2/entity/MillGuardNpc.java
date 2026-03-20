package org.dizzymii.millenaire2.entity;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.tslat.smartbrainlib.api.SmartBrainOwner;
import net.tslat.smartbrainlib.api.core.BrainActivityGroup;
import net.tslat.smartbrainlib.api.core.SmartBrainProvider;
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.look.LookAtTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle;
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import org.dizzymii.millenaire2.entity.ai.behavior.GuardFleeWhenLow;
import org.dizzymii.millenaire2.entity.ai.behavior.GuardMeleeAttack;
import org.dizzymii.millenaire2.entity.ai.behavior.TargetNearestHostileBehaviour;
import org.dizzymii.millenaire2.entity.ai.sensor.HostileMobSensor;

import java.util.List;

/**
 * A combat-capable NPC guard entity powered by SmartBrainLib.
 *
 * <h2>AI Architecture</h2>
 *
 * <h3>Sensors</h3>
 * <ul>
 *   <li>{@link HostileMobSensor} — Scans every 10 ticks for the nearest hostile
 *       mob ({@link net.minecraft.world.entity.monster.Monster}) within 16 blocks.
 *       Writes to {@code NEAREST_HOSTILE} memory.</li>
 * </ul>
 *
 * <h3>Memory Modules (all vanilla)</h3>
 * <ul>
 *   <li>{@code NEAREST_HOSTILE} — set by sensor, consumed by targeting behaviour</li>
 *   <li>{@code ATTACK_TARGET} — set by targeting behaviour; its presence triggers
 *       the transition from IDLE → FIGHT activity</li>
 *   <li>{@code HURT_BY / HURT_BY_ENTITY} — vanilla damage tracking; used for retaliation</li>
 *   <li>{@code WALK_TARGET / LOOK_TARGET} — navigation and head tracking</li>
 * </ul>
 *
 * <h3>Activity Groups</h3>
 * <pre>
 * CORE  (always active)
 *   ├─ LookAtTarget          — face whatever LOOK_TARGET points at
 *   └─ MoveToWalkTarget      — walk toward WALK_TARGET (vanilla navigation)
 *
 * IDLE  (default, no ATTACK_TARGET)
 *   ├─ TargetNearestHostile  — promotes NEAREST_HOSTILE → ATTACK_TARGET
 *   │                          (also retaliates against HURT_BY_ENTITY)
 *   └─ OneRandomBehaviour
 *       ├─ SetPlayerLookTarget — look at nearby players
 *       ├─ SetRandomLookTarget — look around randomly
 *       └─ Idle                — brief pause (30-60 ticks)
 *
 * FIGHT (active when ATTACK_TARGET is present)
 *   ├─ GuardFleeWhenLow      — HP &lt; 20%: clear target, flee away (HIGHEST priority)
 *   └─ GuardMeleeAttack      — walk to target, swing when in 2-block range
 * </pre>
 *
 * <h3>Idle → Fight Transition</h3>
 * SmartBrainLib automatically switches to the FIGHT activity when
 * {@code ATTACK_TARGET} memory becomes present (set by {@link TargetNearestHostileBehaviour}).
 * When the target dies or the memory is erased (e.g. by {@link GuardFleeWhenLow}),
 * the brain falls back to IDLE.
 */
public class MillGuardNpc extends PathfinderMob implements SmartBrainOwner<MillGuardNpc> {

    public MillGuardNpc(EntityType<? extends MillGuardNpc> type, Level level) {
        super(type, level);
    }

    // ========== Attributes ==========

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)     // 15 hearts
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 5.0)    // 2.5 hearts per hit
                .add(Attributes.ATTACK_SPEED, 1.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.ARMOR, 4.0);           // light armor
    }

    // ========== SmartBrainOwner Implementation ==========

    @Override
    protected Brain.Provider<?> brainProvider() {
        return new SmartBrainProvider<>(this);
    }

    /**
     * Sensors run on a timer and populate brain memories.
     * The HostileMobSensor scans for Monster entities every 10 ticks
     * and writes the nearest one to NEAREST_HOSTILE.
     */
    @Override
    public List<? extends ExtendedSensor<? extends MillGuardNpc>> getSensors() {
        return ObjectArrayList.of(
                new HostileMobSensor()
        );
    }

    /**
     * Core tasks run unconditionally every tick regardless of activity.
     * LookAtTarget faces the LOOK_TARGET memory, MoveToWalkTarget
     * drives vanilla pathfinding toward WALK_TARGET.
     */
    @Override
    public BrainActivityGroup<? extends MillGuardNpc> getCoreTasks() {
        return BrainActivityGroup.coreTasks(
                new LookAtTarget<>(),
                new MoveToWalkTarget<>()
        );
    }

    /**
     * Idle tasks run when the FIGHT activity is not active
     * (i.e. ATTACK_TARGET memory is absent).
     *
     * TargetNearestHostileBehaviour checks for NEAREST_HOSTILE or
     * HURT_BY_ENTITY and promotes them to ATTACK_TARGET, which
     * triggers the automatic transition to FIGHT activity.
     */
    @Override
    public BrainActivityGroup<? extends MillGuardNpc> getIdleTasks() {
        return BrainActivityGroup.idleTasks(
                new TargetNearestHostileBehaviour(),
                new OneRandomBehaviour<>(
                        new SetPlayerLookTarget<>(),
                        new SetRandomLookTarget<>(),
                        new Idle<>().runFor(entity -> entity.getRandom().nextInt(30, 60))
                )
        );
    }

    /**
     * Fight tasks run when ATTACK_TARGET memory is present.
     * SmartBrainLib uses {@link FirstApplicableBehaviour} semantics:
     * the first behaviour whose start conditions pass will run.
     *
     * <ol>
     *   <li><b>GuardFleeWhenLow</b> — checked first. If HP &lt; 20%,
     *       clears ATTACK_TARGET and sets a flee WALK_TARGET in the
     *       opposite direction. This effectively exits FIGHT activity.</li>
     *   <li><b>GuardMeleeAttack</b> — walks toward target at 1.2x speed,
     *       swings main hand every 20 ticks when within 2 blocks.</li>
     * </ol>
     */
    @Override
    public BrainActivityGroup<? extends MillGuardNpc> getFightTasks() {
        return BrainActivityGroup.fightTasks(
                new FirstApplicableBehaviour<MillGuardNpc>(
                        new GuardFleeWhenLow(),  // highest priority: flee if dying
                        new GuardMeleeAttack()   // otherwise: fight
                )
        );
    }

    @Override
    protected void customServerAiStep() {
        tickBrain(this);
    }
}
