/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.properties.PropertyBool
 */
package org.millenaire.common.block;

import net.minecraft.block.properties.PropertyBool;
import org.millenaire.common.block.BlockPath;
import org.millenaire.common.block.BlockPathSlab;

public interface IBlockPath {
    public static final PropertyBool STABLE = PropertyBool.func_177716_a((String)"stable");

    public BlockPath getDoubleSlab();

    public BlockPathSlab getSingleSlab();

    public boolean isFullPath();
}

