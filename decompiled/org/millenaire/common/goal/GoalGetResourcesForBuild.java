/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.goal;

import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.village.ConstructionIP;

@DocumentedElement.Documentation(value="Pick up the resources needed for a construction from the TH. Builders should have this.")
public class GoalGetResourcesForBuild
extends Goal {
    public GoalGetResourcesForBuild() {
        this.tags.add("tag_construction");
        this.travelBookShow = false;
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 40;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getTownHall().getResManager().getSellingPos(), villager.getTownHall());
    }

    private ConstructionIP getDoableConstructionIP(MillVillager villager) {
        for (ConstructionIP cip : villager.getTownHall().getConstructionsInProgress()) {
            boolean possible = true;
            if (cip.getBuilder() != null || cip.getBuildingLocation() == null || cip.getBblocks() == null) {
                possible = false;
            }
            if (possible) {
                for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
                    if (!Goal.getResourcesForBuild.key.equals(v.goalKey) && !Goal.construction.key.equals(v.goalKey) || v.constructionJobId != cip.getId()) continue;
                    possible = false;
                }
            }
            if (!possible) continue;
            return cip;
        }
        return null;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        return this.getDoableConstructionIP(villager) != null;
    }

    @Override
    public boolean isStillValidSpecific(MillVillager villager) throws Exception {
        ConstructionIP cip = villager.getCurrentConstruction();
        return cip != null;
    }

    @Override
    public String nextGoal(MillVillager villager) {
        return Goal.construction.key;
    }

    @Override
    public void onAccept(MillVillager villager) {
        ConstructionIP cip = this.getDoableConstructionIP(villager);
        if (cip == null) {
            return;
        }
        cip.setBuilder(villager);
        villager.constructionJobId = cip.getId();
    }

    @Override
    public boolean performAction(MillVillager villager) {
        ConstructionIP cip = villager.getCurrentConstruction();
        if (cip == null) {
            return true;
        }
        if (villager.getTownHall().getBuildingPlanForConstruction(cip) == null) {
            return true;
        }
        BuildingPlan plan = villager.getTownHall().getBuildingPlanForConstruction(cip);
        for (InvItem key : plan.resCost.keySet()) {
            int quantityRequired = plan.resCost.get(key) - villager.countInv(key);
            if (quantityRequired <= 0) continue;
            villager.takeFromBuilding(villager.getTownHall(), key.getItem(), key.meta, quantityRequired);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 1000;
    }
}

