package org.dizzymii.millenaire2.entity.brain.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.millenaire2.entity.HumanoidNPC;
import org.dizzymii.millenaire2.entity.brain.ModMemoryTypes;
import org.dizzymii.millenaire2.entity.brain.smartbrain.ExtendedBehaviour;

import java.util.List;

/**
 * Survival behaviour that retreats when the NPC is low-health and threatened.
 */
public class StrategicRetreatBehavior extends ExtendedBehaviour<HumanoidNPC> {

    private static final double THREAT_RADIUS = 15.0D;
    private static final double RETREAT_DISTANCE = 12.0D;
    private static final double RETREAT_SPEED = 1.3D;

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, HumanoidNPC entity) {
        if (!entity.getBrain().getMemory(ModMemoryTypes.NEEDS_HEALING.get()).orElse(false)) {
            return false;
        }
        return !nearbyHostiles(level, entity).isEmpty();
    }

    @Override
    protected void start(ServerLevel level, HumanoidNPC entity) {
        List<Monster> hostiles = nearbyHostiles(level, entity);
        if (hostiles.isEmpty()) return;

        Vec3 retreatTarget = computeRetreatTarget(entity, hostiles);
        entity.setSprinting(true);
        entity.getNavigation().moveTo(retreatTarget.x, retreatTarget.y, retreatTarget.z, RETREAT_SPEED);
    }

    @Override
    protected void stop(ServerLevel level, HumanoidNPC entity) {
        entity.setSprinting(false);
    }

    private List<Monster> nearbyHostiles(ServerLevel level, HumanoidNPC entity) {
        return level.getEntitiesOfClass(
                Monster.class,
                entity.getBoundingBox().inflate(THREAT_RADIUS),
                hostile -> hostile.isAlive() && hostile.distanceToSqr(entity) <= THREAT_RADIUS * THREAT_RADIUS
        );
    }

    private Vec3 computeRetreatTarget(HumanoidNPC entity, List<Monster> hostiles) {
        GlobalPos base = entity.getBrain().getMemory(ModMemoryTypes.BASE_LOCATION.get()).orElse(null);
        if (base != null && base.dimension().equals(entity.level().dimension())) {
            return Vec3.atBottomCenterOf(base.pos());
        }

        Vec3 awayVector = Vec3.ZERO;
        Vec3 entityPos = entity.position();
        for (Monster hostile : hostiles) {
            Vec3 fromHostile = entityPos.subtract(hostile.position());
            if (fromHostile.lengthSqr() > 0.0001D) {
                awayVector = awayVector.add(fromHostile.normalize());
            }
        }

        if (awayVector.lengthSqr() < 0.0001D) {
            BlockPos danger = entity.getBrain().getMemory(ModMemoryTypes.LAST_KNOWN_DANGER.get()).orElse(entity.blockPosition());
            awayVector = entityPos.subtract(Vec3.atBottomCenterOf(danger));
            if (awayVector.lengthSqr() < 0.0001D) {
                awayVector = new Vec3(1, 0, 0);
            }
        }

        Vec3 direction = awayVector.normalize();
        Vec3 retreatPos = entityPos.add(direction.scale(RETREAT_DISTANCE));
        double y = entity.level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlockPos.containing(retreatPos.x, entityPos.y, retreatPos.z)).getY();
        return new Vec3(retreatPos.x, y, retreatPos.z);
    }
}
