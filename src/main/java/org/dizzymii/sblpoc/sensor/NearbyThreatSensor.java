package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.BowItem;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Scans for hostile mobs within 16 blocks every 10 ticks.
 * 
 * Threat prioritization (highest score targeted first):
 * - Creepers: +50 (immediate explosion danger)
 * - Ranged mobs (Skeleton, etc.): +15 (constant chip damage)
 * - Low HP enemies: +10 (finish them off)
 * - Very close enemies: +8 (immediate melee threat)
 * - Wither skeletons: +12 (wither effect is devastating)
 * 
 * Also detects enemy bow draws for ENEMY_DRAWING_BOW memory,
 * and counts nearby allies for ALLY_COUNT.
 */
public class NearbyThreatSensor extends ExtendedSensor<PocNpc> {

    private static final double DETECTION_RANGE = 16.0;
    private static final double ALLY_SCAN_RANGE = 20.0;

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            MemoryModuleType.NEAREST_HOSTILE,
            SblPocSetup.NEARBY_HOSTILE_COUNT.get(),
            SblPocSetup.ALLY_COUNT.get(),
            SblPocSetup.ENEMY_DRAWING_BOW.get()
    );

    public NearbyThreatSensor() {
        setScanRate(entity -> 10);
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends NearbyThreatSensor> type() {
        return SblPocSetup.NEARBY_THREAT.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        LivingEntity bestTarget = null;
        double bestScore = -1;
        int hostileCount = 0;
        boolean enemyDrawingBow = false;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                npc.getBoundingBox().inflate(DETECTION_RANGE),
                e -> e != npc && e.isAlive() && e instanceof Monster)) {

            hostileCount++;
            double score = scoreThreat(npc, entity);
            if (score > bestScore) {
                bestScore = score;
                bestTarget = entity;
            }

            // Check if this enemy is drawing a bow aimed at us
            if (!enemyDrawingBow && entity.isUsingItem()
                    && entity.getUseItem().getItem() instanceof BowItem) {
                enemyDrawingBow = true;
            }
        }

        if (bestTarget != null) {
            BrainUtils.setMemory(npc, MemoryModuleType.NEAREST_HOSTILE, bestTarget);
        } else {
            BrainUtils.clearMemory(npc, MemoryModuleType.NEAREST_HOSTILE);
        }
        BrainUtils.setMemory(npc, SblPocSetup.NEARBY_HOSTILE_COUNT.get(), hostileCount);

        // Enemy bow draw detection
        if (enemyDrawingBow) {
            BrainUtils.setMemory(npc, SblPocSetup.ENEMY_DRAWING_BOW.get(), true);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.ENEMY_DRAWING_BOW.get());
        }

        // Count nearby allies
        int allyCount = 0;
        for (PocNpc ally : level.getEntitiesOfClass(PocNpc.class,
                npc.getBoundingBox().inflate(ALLY_SCAN_RANGE),
                a -> a != npc && a.isAlive())) {
            allyCount++;
        }
        BrainUtils.setMemory(npc, SblPocSetup.ALLY_COUNT.get(), allyCount);
    }

    private double scoreThreat(PocNpc npc, LivingEntity enemy) {
        double score = 0;
        double distSq = npc.distanceToSqr(enemy);

        // Type-based priority
        if (enemy instanceof Creeper) {
            score += 50; // Explosive — kill ASAP
        } else if (enemy instanceof WitherSkeleton) {
            score += 12; // Wither effect is devastating
        } else if (enemy instanceof Skeleton) {
            score += 15; // Ranged chip damage is annoying
        }

        // Low HP — finish them off (below 30% HP)
        float hpPercent = enemy.getHealth() / enemy.getMaxHealth();
        if (hpPercent < 0.3f) {
            score += 10;
        } else if (hpPercent < 0.6f) {
            score += 4;
        }

        // Proximity bonus — closer = more immediate threat
        if (distSq < 9.0) {       // 3 blocks
            score += 8;
        } else if (distSq < 25.0) { // 5 blocks
            score += 4;
        }

        // Slight distance tiebreaker — prefer closer targets at equal score
        score += (DETECTION_RANGE * DETECTION_RANGE - distSq) * 0.01;

        return score;
    }
}
