/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityAgeable
 *  net.minecraft.entity.EntityList
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Items
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.common.goal.generic;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import org.millenaire.common.annotedparameters.AnnotedParameter;
import org.millenaire.common.annotedparameters.ConfigAnnotations;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.goal.generic.GoalGeneric;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.pathing.atomicstryker.AStarConfig;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;

public class GoalGenericSlaughterAnimal
extends GoalGeneric {
    public static final String GOAL_TYPE = "slaughteranimal";
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.ENTITY_ID)
    @ConfigAnnotations.FieldDocumentation(explanation="The animal to be targeted.")
    public ResourceLocation animalKey = null;
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BONUS_ITEM_ADD)
    @ConfigAnnotations.FieldDocumentation(explanation="Extra item drop the villager can get.")
    public List<AnnotedParameter.BonusItem> bonusItem = new ArrayList<AnnotedParameter.BonusItem>();
    @ConfigAnnotations.ConfigField(type=AnnotedParameter.ParameterType.BOOLEAN)
    @ConfigAnnotations.FieldDocumentation(explanation="If true, the villager will slaughter animals until only half the reference amount (the number of spawn points) is left.")
    public boolean aggressiveSlaughter = false;

    @Override
    public void applyDefaultSettings() {
        this.duration = 2;
        this.lookAtGoal = true;
        this.icon = InvItem.createInvItem(Items.field_151036_c);
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        Point pos = villager.getPos();
        Entity closest = null;
        Building destBuilding = null;
        double bestDist = Double.MAX_VALUE;
        List<Building> buildings = this.getBuildings(villager);
        for (Building dest : buildings) {
            if (!this.isDestPossible(villager, dest)) continue;
            List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityList.getClass((ResourceLocation)this.animalKey), dest.getPos(), 15, 10);
            for (Entity ent : animals) {
                if (ent.field_70128_L || this.isEntityChild(ent) || closest != null && !(pos.distanceTo(ent) < bestDist)) continue;
                closest = ent;
                destBuilding = dest;
                bestDist = pos.distanceTo(ent);
            }
        }
        if (closest == null) {
            return null;
        }
        return this.packDest(null, destBuilding, closest);
    }

    @Override
    public AStarConfig getPathingConfig(MillVillager villager) {
        if (!villager.canVillagerClearLeaves()) {
            if (this.animalKey.equals((Object)Mill.ENTITY_SQUID)) {
                return JPS_CONFIG_SLAUGHTERSQUIDS_NO_LEAVES;
            }
            return JPS_CONFIG_TIGHT_NO_LEAVES;
        }
        if (this.animalKey.equals((Object)Mill.ENTITY_SQUID)) {
            return JPS_CONFIG_SLAUGHTERSQUIDS;
        }
        return JPS_CONFIG_TIGHT;
    }

    @Override
    public String getTypeLabel() {
        return GOAL_TYPE;
    }

    @Override
    public boolean isDestPossibleSpecific(MillVillager villager, Building b) {
        List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityList.getClass((ResourceLocation)this.animalKey), b.getPos(), 25, 10);
        if (animals == null) {
            return false;
        }
        int nbanimals = 0;
        for (Entity ent : animals) {
            if (ent.field_70128_L || this.isEntityChild(ent)) continue;
            ++nbanimals;
        }
        int targetAnimals = 0;
        for (int i = 0; i < b.getResManager().spawns.size(); ++i) {
            if (!b.getResManager().spawnTypes.get(i).equals((Object)this.animalKey)) continue;
            targetAnimals = b.getResManager().spawns.get(i).size();
        }
        if (!this.aggressiveSlaughter) {
            return nbanimals > targetAnimals;
        }
        return nbanimals > targetAnimals / 2;
    }

    private boolean isEntityChild(Entity ent) {
        if (!(ent instanceof EntityAgeable)) {
            return false;
        }
        EntityAgeable animal = (EntityAgeable)ent;
        return animal.func_70631_g_();
    }

    @Override
    public boolean isFightingGoal() {
        return true;
    }

    @Override
    public boolean isPossibleGenericGoal(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        Building dest = villager.getGoalBuildingDest();
        if (dest == null) {
            return true;
        }
        List<Entity> animals = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityList.getClass((ResourceLocation)this.animalKey), villager.getPos(), 1, 5);
        for (Entity ent : animals) {
            if (ent.field_70128_L || !(ent instanceof EntityLivingBase) || this.isEntityChild(ent) || !villager.func_70685_l(ent)) continue;
            EntityLivingBase entLiving = (EntityLivingBase)ent;
            villager.func_70624_b(entLiving);
            for (AnnotedParameter.BonusItem bonusItem : this.bonusItem) {
                if (bonusItem.tag != null && !dest.containsTags(bonusItem.tag) || MillCommonUtilities.randomInt(100) > bonusItem.chance) continue;
                villager.addToInv(bonusItem.item, 1);
            }
            villager.func_184609_a(EnumHand.MAIN_HAND);
            return true;
        }
        animals = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, EntityList.getClass((ResourceLocation)this.animalKey), villager.getPos(), 2, 5);
        for (Entity ent : animals) {
            if (ent.field_70128_L || !(ent instanceof EntityLivingBase) || this.isEntityChild(ent) || !villager.func_70685_l(ent)) continue;
            EntityLivingBase entLiving = (EntityLivingBase)ent;
            villager.func_70624_b(entLiving);
            for (AnnotedParameter.BonusItem bonusItem : this.bonusItem) {
                if (bonusItem.tag != null && !dest.containsTags(bonusItem.tag) || MillCommonUtilities.randomInt(100) > bonusItem.chance) continue;
                villager.addToInv(bonusItem.item, 1);
            }
            villager.func_184609_a(EnumHand.MAIN_HAND);
            return true;
        }
        return true;
    }

    @Override
    public int range(MillVillager villager) {
        return 1;
    }

    @Override
    public boolean validateGoal() {
        if (this.animalKey == null) {
            MillLog.error(this, "The animalKey is mandatory in custom slaughter goals.");
            return false;
        }
        return true;
    }
}

