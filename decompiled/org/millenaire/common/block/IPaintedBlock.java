/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.EnumDyeColor
 */
package org.millenaire.common.block;

import net.minecraft.item.EnumDyeColor;

public interface IPaintedBlock {
    public String getBlockType();

    public EnumDyeColor getDyeColour();

    public void initModel();
}

