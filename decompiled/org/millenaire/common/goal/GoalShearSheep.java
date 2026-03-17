/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.util.EnumHand
 */
package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

@DocumentedElement.Documentation(value="Sheer sheeps present around the villager's house.")
public class GoalShearSheep
extends Goal {
    public GoalShearSheep() {
        this.buildingLimit.put(InvItem.createInvItem(Blocks.field_150325_L, 0), 1024);
        this.townhallLimit.put(InvItem.createInvItem(Blocks.field_150325_L, 0), 1024);
        this.icon = InvItem.createInvItem((Item)Items.field_151097_aZ);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        Point pos = villager.getPos();
        Entity closestSheep = null;
        double sheepBestDist = Double.MAX_VALUE;
        List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntitySheep.class, villager.getHouse().getPos(), 30, 10);
        for (Entity ent : sheep) {
            if (((EntitySheep)ent).func_70892_o() || ((EntitySheep)ent).func_70631_g_() || closestSheep != null && !(pos.distanceTo(ent) < sheepBestDist)) continue;
            closestSheep = ent;
            sheepBestDist = pos.distanceTo(ent);
        }
        if (closestSheep != null) {
            return this.packDest(null, villager.getHouse(), closestSheep);
        }
        return null;
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            return JPS_CONFIG_WIDE_NO_LEAVES;
        }
        return JPS_CONFIG_WIDE;
    }

    @Override
    public boolean isFightingGoal() {
        return true;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        if (!villager.getHouse().containsTags("sheeps")) {
            return false;
        }
        List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntitySheep.class, villager.getHouse().getPos(), 30, 10);
        if (sheep == null) {
            return false;
        }
        for (Entity ent : sheep) {
            EntitySheep asheep = (EntitySheep)ent;
            if (asheep.func_70631_g_() || asheep.field_70128_L || ((EntitySheep)ent).func_70892_o()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        List<Entity> sheep = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntitySheep.class, villager.getPos(), 4, 4);
        for (Entity ent : sheep) {
            EntitySheep animal;
            if (ent.field_70128_L || (animal = (EntitySheep)ent).func_70631_g_() || animal.func_70892_o()) continue;
            villager.addToInv(Blocks.field_150325_L, ((EntitySheep)ent).func_175509_cj().func_176765_a(), 3);
            ((EntitySheep)ent).func_70893_e(true);
            if (MillConfigValues.LogCattleFarmer >= 1 && villager.extraLog) {
                MillLog.major(this, "Shearing: " + ent);
            }
            villager.func_184609_a(EnumHand.MAIN_HAND);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 50;
    }

    @Override
    public int range(MillVillager villager) {
        return 5;
    }
}

