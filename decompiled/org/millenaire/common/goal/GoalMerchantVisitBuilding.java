/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.village.Building;

@DocumentedElement.Documentation(value="For local merchants, pick up goods from village shops for exports and drop off goods brought from other villages.")
public class GoalMerchantVisitBuilding
extends Goal {
    public GoalMerchantVisitBuilding() {
        this.icon = InvItem.createInvItem((Block)Blocks.field_150486_ae);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        for (TradeGood good : villager.getTownHall().culture.goodsList) {
            if (good.item.meta < 0 || villager.countInv(good.item.getItem(), good.item.meta) <= 0 || villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta) <= 0) continue;
            if (MillConfigValues.LogMerchant >= 3) {
                MillLog.debug(villager, "TH needs " + villager.getTownHall().nbGoodNeeded(good.item.getItem(), good.item.meta) + " good " + good.item.getName() + ", merchant has " + villager.countInv(good.item.getItem(), good.item.meta));
            }
            return this.packDest(villager.getTownHall().getResManager().getSellingPos(), villager.getTownHall());
        }
        HashMap<TradeGood, Integer> neededGoods = villager.getTownHall().getImportsNeededbyOtherVillages();
        for (Building shop : villager.getTownHall().getShops()) {
            for (TradeGood good : villager.getTownHall().culture.goodsList) {
                if (good.item.meta < 0 || shop.isInn || shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) <= 0 || !neededGoods.containsKey(good) || neededGoods.get(good) <= villager.getHouse().countGoods(good.item.getItem(), good.item.meta) + villager.countInv(good.item.getItem(), good.item.meta)) continue;
                if (MillConfigValues.LogMerchant >= 3) {
                    MillLog.debug(villager, "Shop " + shop + " has " + shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) + " good to pick up.");
                }
                return this.packDest(shop.getResManager().getSellingPos(), shop);
            }
        }
        return null;
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (InvItem item : villager.getInventoryKeys()) {
            if (villager.countInv(item) <= 0) continue;
            items.add(new ItemStack(item.getItem(), 1, item.meta));
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        Building shop = villager.getGoalBuildingDest();
        HashMap<TradeGood, Integer> neededGoods = villager.getTownHall().getImportsNeededbyOtherVillages();
        if (shop == null || shop.isInn) {
            return true;
        }
        if (shop.isTownhall) {
            for (TradeGood good : villager.getTownHall().culture.goodsList) {
                int nb;
                int nbNeeded;
                if (good.item.meta < 0 || (nbNeeded = shop.nbGoodNeeded(good.item.getItem(), good.item.meta)) <= 0 || (nb = villager.putInBuilding(shop, good.item.getItem(), good.item.meta, nbNeeded)) <= 0 || MillConfigValues.LogMerchant < 2) continue;
                MillLog.minor(shop, villager + " delivered " + nb + " " + good.getName() + ".");
            }
        }
        for (TradeGood good : villager.getTownHall().culture.goodsList) {
            if (good.item.meta < 0 || !neededGoods.containsKey(good) || shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false) <= 0 || villager.getHouse().countGoods(good.item.getItem(), good.item.meta) + villager.countInv(good.item.getItem(), good.item.meta) >= neededGoods.get(good)) continue;
            int nb = Math.min(shop.nbGoodAvailable(good.item.getItem(), good.item.meta, false, true, false), neededGoods.get(good) - villager.getHouse().countGoods(good.item.getItem(), good.item.meta) - villager.countInv(good.item.getItem(), good.item.meta));
            nb = villager.takeFromBuilding(shop, good.item.getItem(), good.item.meta, nb);
            if (MillConfigValues.LogMerchant < 2) continue;
            MillLog.minor(shop, villager + " took " + nb + " " + good.getName() + " for trading.");
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 100;
    }
}

