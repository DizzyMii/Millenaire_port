package org.dizzymii.sblpoc.sensor;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor;
import net.tslat.smartbrainlib.util.BrainUtils;
import org.dizzymii.sblpoc.PocNpc;
import org.dizzymii.sblpoc.SblPocSetup;

import java.util.List;

/**
 * Fast-ticking sensor (every 5 ticks) that detects:
 * 1. Incoming projectiles heading toward the NPC
 * 2. Melee attackers within striking distance facing the NPC
 * 
 * Writes SHOULD_SHIELD (boolean) memory when danger is imminent.
 */
public class IncomingDamageSensor extends ExtendedSensor<PocNpc> {

    private static final double PROJECTILE_SCAN_RANGE = 12.0;
    private static final double MELEE_THREAT_RANGE_SQ = 16.0; // 4 blocks squared

    private static final List<MemoryModuleType<?>> MEMORIES = ObjectArrayList.of(
            SblPocSetup.SHOULD_SHIELD.get(),
            SblPocSetup.INCOMING_DAMAGE_TICKS.get()
    );

    public IncomingDamageSensor() {
        setScanRate(entity -> 5); // Fast — needs to be reactive
    }

    @Override
    public List<MemoryModuleType<?>> memoriesUsed() {
        return MEMORIES;
    }

    @Override
    public SensorType<? extends IncomingDamageSensor> type() {
        return SblPocSetup.INCOMING_DAMAGE.get();
    }

    @Override
    protected void doTick(ServerLevel level, PocNpc npc) {
        boolean shouldShield = false;

        // 1. Check for incoming projectiles
        AABB scanBox = npc.getBoundingBox().inflate(PROJECTILE_SCAN_RANGE);
        for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, scanBox,
                p -> p.isAlive() && p.getOwner() != npc)) {

            Vec3 projVel = projectile.getDeltaMovement();
            if (projVel.lengthSqr() < 0.01) continue; // stationary

            Vec3 toNpc = npc.position().subtract(projectile.position()).normalize();
            Vec3 projDir = projVel.normalize();

            // Dot product > 0.7 means projectile is heading roughly toward us
            double dot = projDir.dot(toNpc);
            if (dot > 0.7) {
                double dist = npc.distanceToSqr(projectile);
                if (dist < PROJECTILE_SCAN_RANGE * PROJECTILE_SCAN_RANGE) {
                    shouldShield = true;
                    break;
                }
            }
        }

        // 2. Check for melee attackers winding up (close + facing us)
        if (!shouldShield) {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                    npc.getBoundingBox().inflate(4.0),
                    e -> e != npc && e.isAlive() && e instanceof Monster)) {

                double distSq = npc.distanceToSqr(entity);
                if (distSq > MELEE_THREAT_RANGE_SQ) continue;

                // Check if the mob is facing toward us
                Vec3 mobLook = entity.getLookAngle();
                Vec3 toNpc = npc.position().subtract(entity.position()).normalize();
                double facingDot = mobLook.dot(toNpc);

                // Facing us (dot > 0.5) and within melee range — attack likely imminent
                if (facingDot > 0.5 && distSq < 6.25) { // 2.5 blocks squared
                    shouldShield = true;
                    break;
                }
            }
        }

        if (shouldShield) {
            BrainUtils.setMemory(npc, SblPocSetup.SHOULD_SHIELD.get(), true);
            BrainUtils.setMemory(npc, SblPocSetup.INCOMING_DAMAGE_TICKS.get(), 40);
        } else {
            BrainUtils.clearMemory(npc, SblPocSetup.SHOULD_SHIELD.get());
            BrainUtils.clearMemory(npc, SblPocSetup.INCOMING_DAMAGE_TICKS.get());
        }
    }
}
