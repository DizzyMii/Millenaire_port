/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 */
package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@DocumentedElement.Documentation(value="Gather items around the villager, if they are of a type declared for that villager. For example, saplings for lumbermen.")
public class GoalGatherGoods
extends Goal {
    public GoalGatherGoods() {
        this.travelBookShow = false;
    }

    @Override
    public int actionDuration(MillVillager villager) throws Exception {
        return 40;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
        if (item == null) {
            return null;
        }
        return this.packDest(new Point((Entity)item));
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            return JPS_CONFIG_WIDE_NO_LEAVES;
        }
        return JPS_CONFIG_WIDE;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        if (villager.getGoodsToCollect().size() == 0) {
            return false;
        }
        EntityItem item = villager.getClosestItemVertical(villager.getGoodsToCollect(), villager.getGatheringRange(), 10);
        return item != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        return 500;
    }

    @Override
    public int range(MillVillager villager) {
        return 5;
    }

    @Override
    public boolean stuckAction(MillVillager villager) {
        EntityItem item;
        List<InvItem> goods = villager.getGoodsToCollect();
        if (goods != null && (item = WorldUtilities.getClosestItemVertical(villager.field_70170_p, villager.getGoalDestPoint(), goods, 3, 20)) != null) {
            item.func_70106_y();
            villager.addToInv(item.func_92059_d().func_77973_b(), item.func_92059_d().func_77952_i(), 1);
            return true;
        }
        return false;
    }
}

