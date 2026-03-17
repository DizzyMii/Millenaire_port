/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;

@DocumentedElement.Documentation(value="Deliver to the villager's house goods required by the household, like food or crafting inputs. Paired with getgoodshousehold.")
public class GoalDeliverGoodsHousehold
extends Goal {
    public GoalDeliverGoodsHousehold() {
        this.icon = InvItem.createInvItem(Items.field_151025_P);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        return this.packDest(villager.getHouse().getResManager().getSellingPos(), villager.getHouse());
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) throws Exception {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            for (InvItem key : v.requiresGoods().keySet()) {
                if (villager.countInv(key.getItem(), key.meta) <= 0) continue;
                items.add(new ItemStack(key.getItem(), 1, key.meta));
            }
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return false;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        for (MillVillager v : villager.getHouse().getKnownVillagers()) {
            for (InvItem key : v.requiresGoods().keySet()) {
                villager.putInBuilding(villager.getHouse(), key.getItem(), key.meta, 256);
            }
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 100;
    }
}

