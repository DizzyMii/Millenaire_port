package org.dizzymii.millenaire2.village.buildingmanagers;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import org.dizzymii.millenaire2.entity.MillEntities;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;

import java.util.Random;

/**
 * Manages visitor and foreign merchant spawning for markets and inns.
 * Ported from org.millenaire.common.village.buildingmanagers.VisitorManager (Forge 1.12.2).
 */
public class VisitorManager {

    private static final int MERCHANT_SPAWN_CHANCE = 200;
    private static final int VISITOR_SPAWN_CHANCE = 150;
    private static final int MAX_VISITORS = 3;
    private final Building building;
    private boolean nightActionPerformed = false;
    private int visitorCount = 0;
    private final Random random = new Random();

    public VisitorManager(Building building) {
        this.building = building;
    }

    public void update(boolean forceAttempt) {
        if (this.building.isMarket) {
            this.updateMarket(forceAttempt);
        } else {
            this.updateVisitors(forceAttempt);
        }
    }

    /**
     * Attempt to spawn a foreign merchant at the market building.
     * Merchants appear during daytime and leave at night.
     */
    private void updateMarket(boolean forceAttempt) {
        if (building.world == null || building.world.isClientSide) return;
        if (!(building.world instanceof ServerLevel serverLevel)) return;
        Point pos = building.getPos();
        if (pos == null) return;

        long dayTime = serverLevel.getDayTime() % 24000L;
        boolean isDaytime = dayTime >= 0 && dayTime < 12000;

        // Night action: despawn merchants
        if (!isDaytime && !nightActionPerformed) {
            nightActionPerformed = true;
            if (building.merchantRecord != null) {
                despawnMerchant(serverLevel, building.merchantRecord);
                building.merchantRecord = null;
            }
            return;
        }

        // Reset night flag in the morning
        if (isDaytime) {
            nightActionPerformed = false;
        }

        // Only try spawning during daytime
        if (!isDaytime || building.merchantRecord != null) return;

        if (forceAttempt || random.nextInt(MERCHANT_SPAWN_CHANCE) == 0) {
            spawnMerchant(serverLevel, pos);
        }
    }

    /**
     * Attempt to spawn visitors at inn/tavern buildings.
     */
    private void updateVisitors(boolean forceAttempt) {
        if (building.world == null || building.world.isClientSide) return;
        if (!(building.world instanceof ServerLevel serverLevel)) return;
        Point pos = building.getPos();
        if (pos == null) return;

        if (visitorCount >= MAX_VISITORS) return;

        long dayTime = serverLevel.getDayTime() % 24000L;
        boolean isEvening = dayTime >= 12000 && dayTime < 18000;

        // Night cleanup
        if (dayTime >= 22000 && !nightActionPerformed) {
            nightActionPerformed = true;
            visitorCount = 0;
            return;
        }
        if (dayTime < 12000) nightActionPerformed = false;

        // Visitors arrive in the evening
        if (!isEvening && !forceAttempt) return;

        if (forceAttempt || random.nextInt(VISITOR_SPAWN_CHANCE) == 0) {
            spawnVisitor(serverLevel, pos);
        }
    }

    private void spawnMerchant(ServerLevel level, Point pos) {
        MillVillager merchant = MillEntities.GENERIC_MALE.get().create(level);
        if (merchant == null) return;

        merchant.setPos(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5);
        merchant.setFirstName("Merchant");
        merchant.setFamilyName("");
        merchant.setGender(MillVillager.MALE);

        VillagerRecord vr = new VillagerRecord();
        vr.setVillagerId(random.nextLong());
        vr.firstName = "Merchant";
        vr.gender = MillVillager.MALE;
        merchant.setVillagerId(vr.getVillagerId());

        level.addFreshEntity(merchant);
        building.merchantRecord = vr;
        MillLog.minor("VisitorManager", "Spawned merchant at " + pos);
    }

    private void spawnVisitor(ServerLevel level, Point pos) {
        EntityType<? extends MillVillager> type = random.nextBoolean()
                ? MillEntities.GENERIC_MALE.get()
                : MillEntities.GENERIC_SYMM_FEMALE.get();
        MillVillager visitor = type.create(level);
        if (visitor == null) return;

        visitor.setPos(pos.x + 0.5 + random.nextGaussian(), pos.y + 1.0, pos.z + 0.5 + random.nextGaussian());
        visitor.setFirstName("Visitor");
        visitor.setGender(random.nextBoolean() ? MillVillager.MALE : MillVillager.FEMALE);

        level.addFreshEntity(visitor);
        visitorCount++;
        MillLog.minor("VisitorManager", "Spawned visitor at " + pos + " (total: " + visitorCount + ")");
    }

    private void despawnMerchant(ServerLevel level, VillagerRecord vr) {
        level.getEntitiesOfClass(MillVillager.class,
                net.minecraft.world.phys.AABB.ofSize(
                        new net.minecraft.world.phys.Vec3(
                                building.getPos().x, building.getPos().y, building.getPos().z),
                        64, 32, 64),
                v -> v.getVillagerId() == vr.getVillagerId()
        ).forEach(MillVillager::discard);
    }

    public int getVisitorCount() { return visitorCount; }
}
