/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.AbstractCriterionInstance
 *  net.minecraft.util.ResourceLocation
 */
package org.millenaire.common.advancements;

import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.ResourceLocation;

public class AlwaysTrueCriterionInstance
extends AbstractCriterionInstance {
    public AlwaysTrueCriterionInstance(ResourceLocation rl) {
        super(rl);
    }

    public boolean test() {
        return true;
    }
}

