/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.init.Items
 *  net.minecraft.item.ItemStack
 */
package org.millenaire.common.goal;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.WorldUtilities;

@DocumentedElement.Documentation(value="Seek out mobs around the village and attack them.")
public class GoalHuntMonster
extends Goal {
    public GoalHuntMonster() {
        this.icon = InvItem.createInvItem(Items.field_151048_u);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityMob.class, villager.getTownHall().getPos(), 50, 10);
        if (mobs == null) {
            return null;
        }
        int bestDist = Integer.MAX_VALUE;
        Entity target = null;
        for (Entity ent : mobs) {
            if (!(ent instanceof EntityMob) || ent instanceof EntityCreeper || !(villager.getPos().distanceToSquared(ent) < (double)bestDist) || !((double)villager.getTownHall().getAltitude((int)ent.field_70165_t, (int)ent.field_70161_v) < ent.field_70163_u)) continue;
            target = ent;
            bestDist = (int)villager.getPos().distanceToSquared(ent);
        }
        if (target == null) {
            return null;
        }
        return this.packDest(null, null, target);
    }

    @Override
    public ItemStack[] getHeldItemsTravelling(MillVillager villager) {
        return new ItemStack[]{villager.getWeapon()};
    }

    @Override
    public boolean isFightingGoal() {
        return true;
    }

    @Override
    public boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean isStillValidSpecific(MillVillager villager) throws Exception {
        if (villager.field_70170_p.func_72820_D() % 10L == 0L) {
            this.setVillagerDest(villager);
        }
        return villager.getGoalDestPoint() != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        List<Entity> mobs = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityMob.class, villager.getPos(), 4, 4);
        for (Entity ent : mobs) {
            if (ent.field_70128_L || !(ent instanceof EntityMob) || !villager.func_70685_l(ent)) continue;
            EntityMob mob = (EntityMob)ent;
            villager.func_70624_b((EntityLivingBase)mob);
            if (MillConfigValues.LogGeneralAI < 1) continue;
            MillLog.major(this, "Attacking entity: " + ent);
        }
        return true;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 50;
    }
}

