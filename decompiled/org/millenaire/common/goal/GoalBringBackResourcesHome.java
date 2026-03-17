/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;

@DocumentedElement.Documentation(value="Brings resources home from the villager's inventory. Typically used to bring back resources produced by mining, crafting etc.")
public class GoalBringBackResourcesHome
extends Goal {
    public GoalBringBackResourcesHome() {
        this.travelBookShow = false;
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 40;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) {
        return this.packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (InvItem key : villager.getInventoryKeys()) {
            for (InvItem key2 : villager.getGoodsToBringBackHome()) {
                if (!key2.equals(key)) continue;
                items.add(new ItemStack(key.getItem(), 1, key.meta));
            }
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) {
        if (villager.getGoodsToBringBackHome().size() == 0) {
            return false;
        }
        int nb = 0;
        boolean delayOver = !villager.lastGoalTime.containsKey(this) ? true : villager.field_70170_p.func_72820_D() > villager.lastGoalTime.get(this) + 2000L;
        for (InvItem key : villager.getInventoryKeys()) {
            if (villager.countInv(key) <= 0) continue;
            for (InvItem key2 : villager.getGoodsToBringBackHome()) {
                if (!key2.matches(key)) continue;
                nb += villager.countInv(key);
                if (delayOver) {
                    return true;
                }
                if (nb <= 16) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) {
        for (InvItem key : villager.getInventoryKeys()) {
            for (InvItem key2 : villager.getGoodsToBringBackHome()) {
                if (!key2.matches(key)) continue;
                villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256);
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) {
        int nbGoods = 0;
        for (InvItem key : villager.getInventoryKeys()) {
            for (InvItem key2 : villager.getGoodsToBringBackHome()) {
                if (!key2.matches(key)) continue;
                nbGoods += villager.countInv(key);
            }
        }
        return 10 + nbGoods * 3;
    }
}

