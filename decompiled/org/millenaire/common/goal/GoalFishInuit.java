/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 */
package org.millenaire.common.goal;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.GoalFish;
import org.millenaire.common.utilities.MillCommonUtilities;

@DocumentedElement.Documentation(value="Extension of basic fishing goal that also brings in bone block.")
public class GoalFishInuit
extends GoalFish {
    @Override
    protected void addFishResults(MillVillager villager) {
        villager.addToInv(Items.field_151115_aP, 1);
        if (MillCommonUtilities.chanceOn(4)) {
            villager.addToInv(Blocks.field_189880_di, 1);
        }
    }
}

