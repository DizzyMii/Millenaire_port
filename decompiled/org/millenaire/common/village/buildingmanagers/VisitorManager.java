/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package org.millenaire.common.village.buildingmanagers;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.VillagerType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;

public class VisitorManager {
    private final Building building;
    private boolean nightActionPerformed = false;

    public VisitorManager(Building building) {
        this.building = building;
    }

    public void update(boolean forceAttempt) throws MillLog.MillenaireException {
        if (this.building.isMarket) {
            this.updateMarket(forceAttempt);
        } else {
            this.updateVisitors(forceAttempt);
        }
    }

    private void updateMarket(boolean forceAttempt) throws MillLog.MillenaireException {
        if (this.building.world.func_72935_r() && !forceAttempt) {
            this.nightActionPerformed = false;
        } else if (!this.nightActionPerformed || forceAttempt) {
            int maxMerchants = this.building.getResManager().stalls.size();
            if (this.building.getAllVillagerRecords().size() < maxMerchants) {
                if (MillConfigValues.LogMerchant >= 1) {
                    MillLog.major(this, "Attempting to create a foreign merchant.");
                }
                ArrayList<VillagerType> merchantTypesOtherVillages = new ArrayList<VillagerType>();
                for (Point p : this.building.getTownHall().getRelations().keySet()) {
                    Building distantVillage;
                    if (this.building.getTownHall().getRelations().get(p) <= 70 || (distantVillage = this.building.mw.getBuilding(p)) == null || distantVillage.culture == this.building.getTownHall().culture || distantVillage.getBuildingsWithTag("market").size() <= 0) continue;
                    merchantTypesOtherVillages.add(distantVillage.culture.getRandomForeignMerchant());
                }
                int foreignChance = Math.min(1 + merchantTypesOtherVillages.size(), 5);
                VillagerType type = merchantTypesOtherVillages.size() > 0 && MillCommonUtilities.randomInt(11) < foreignChance ? (merchantTypesOtherVillages.size() == 0 ? this.building.culture.getRandomForeignMerchant() : (VillagerType)merchantTypesOtherVillages.get(MillCommonUtilities.randomInt(merchantTypesOtherVillages.size()))) : this.building.culture.getRandomForeignMerchant();
                VillagerRecord merchantRecord = VillagerRecord.createVillagerRecord(type.culture, type.key, this.building.mw, this.building.getPos(), this.building.getTownHall().getPos(), null, null, -1L, false);
                MillVillager merchant = MillVillager.createVillager(merchantRecord, this.building.world, this.building.getResManager().getSleepingPos(), false);
                this.building.world.func_72838_d((Entity)merchant);
                for (InvItem iv : merchant.vtype.foreignMerchantStock.keySet()) {
                    this.building.storeGoods(iv.getItem(), iv.meta, (int)merchant.vtype.foreignMerchantStock.get(iv));
                }
                if (MillConfigValues.LogMerchant >= 1) {
                    MillLog.major(this, "Created foreign merchant: " + merchantRecord);
                }
            }
            this.nightActionPerformed = true;
        }
    }

    private void updateVisitors(boolean forceAttempt) throws MillLog.MillenaireException {
        if (this.building.world.func_72935_r() && !forceAttempt) {
            this.nightActionPerformed = false;
        } else if (!this.nightActionPerformed || forceAttempt) {
            HashMap<String, Integer> targetCount = new HashMap<String, Integer>();
            for (String visitorType : this.building.location.getVisitors()) {
                if (targetCount.containsKey(visitorType)) {
                    targetCount.put(visitorType, (Integer)targetCount.get(visitorType) + 1);
                    continue;
                }
                targetCount.put(visitorType, 1);
            }
            for (String visitorType : targetCount.keySet()) {
                int currentCount = 0;
                for (VillagerRecord vr : this.building.getAllVillagerRecords()) {
                    if (!vr.type.equals(visitorType)) continue;
                    ++currentCount;
                }
                VillagerType type = this.building.culture.getVillagerType(visitorType);
                for (int i = currentCount; i < (Integer)targetCount.get(visitorType); ++i) {
                    if (!MillCommonUtilities.chanceOn(2)) continue;
                    VillagerRecord visitorRecord = VillagerRecord.createVillagerRecord(type.culture, type.key, this.building.mw, this.building.getPos(), this.building.getTownHall().getPos(), null, null, -1L, false);
                    MillVillager visitor = MillVillager.createVillager(visitorRecord, this.building.world, this.building.getResManager().getSleepingPos(), false);
                    this.building.world.func_72838_d((Entity)visitor);
                    if (MillConfigValues.LogMerchant < 1) continue;
                    MillLog.major(this, "Created visitor: " + visitorRecord);
                }
            }
            this.nightActionPerformed = true;
        }
    }
}

