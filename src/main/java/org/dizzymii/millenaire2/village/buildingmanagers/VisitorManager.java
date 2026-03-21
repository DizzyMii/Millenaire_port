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

        // Give merchant some trade inventory from a foreign village or random goods
        populateMerchantInventory(merchant);

        level.addFreshEntity(merchant);
        building.merchantRecord = vr;
        MillLog.minor("VisitorManager", "Spawned merchant at " + pos);
    }

    /**
     * Populate a foreign merchant's inventory with goods.
     * If a neighbouring village exists (via DiplomacyManager), take surplus goods from it.
     * Otherwise, give the merchant a random selection of common trade items.
     */
    private void populateMerchantInventory(MillVillager merchant) {
        org.dizzymii.millenaire2.world.MillWorldData mw = building.mw;
        if (mw == null) return;

        // Try to find a foreign village's townhall to source goods from
        Building foreignTownhall = findForeignVillage(mw);
        if (foreignTownhall != null) {
            // Take surplus goods from the foreign village
            int itemsTaken = 0;
            for (java.util.Map.Entry<org.dizzymii.millenaire2.item.InvItem, Integer> entry :
                    new java.util.ArrayList<>(foreignTownhall.resManager.resources.entrySet())) {
                if (itemsTaken >= 5) break;
                int surplus = entry.getValue() - 16;
                if (surplus > 0) {
                    int take = Math.min(surplus, 8);
                    if (foreignTownhall.resManager.takeGoods(entry.getKey(), take)) {
                        merchant.addToInv(entry.getKey(), take);
                        itemsTaken++;
                    }
                }
            }
            if (itemsTaken > 0) {
                MillLog.minor("VisitorManager", "Merchant loaded " + itemsTaken
                        + " item types from foreign village at " + foreignTownhall.getPos());
                return;
            }
        }

        // Fallback: give random common goods
        String[] commonGoods = {"wheat", "wool", "leather", "iron_ingot", "plank"};
        for (String key : commonGoods) {
            org.dizzymii.millenaire2.item.InvItem inv = org.dizzymii.millenaire2.item.InvItem.get(key);
            if (inv != null && random.nextInt(3) == 0) {
                merchant.addToInv(inv, 4 + random.nextInt(8));
            }
        }
    }

    /**
     * Find a foreign village's townhall from known diplomacy relations.
     * Returns null if no suitable foreign village is found.
     */
    @javax.annotation.Nullable
    private Building findForeignVillage(org.dizzymii.millenaire2.world.MillWorldData mw) {
        Point myPos = building.getTownHallPos();
        if (myPos == null) myPos = building.getPos();
        if (myPos == null) return null;

        Building bestForeign = null;
        double bestDist = Double.MAX_VALUE;

        for (Building b : mw.allBuildings()) {
            if (!b.isTownhall) continue;
            if (b == building) continue;
            Point bPos = b.getPos();
            if (bPos == null) continue;
            // Check it's actually a different village (different townhall)
            if (myPos.equals(bPos)) continue;
            double dist = myPos.distanceTo(bPos);
            if (dist < bestDist && dist < 500) {
                bestDist = dist;
                bestForeign = b;
            }
        }
        return bestForeign;
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
