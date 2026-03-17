/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPlanks$EnumType
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 */
package org.millenaire.common.utilities;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import org.millenaire.common.utilities.MillLog;

public class BlockStateUtilities {
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on((char)'=').limit(2);

    private static Map<IProperty<?>, Comparable<?>> getBlockStatePropertyValueMap(Block block, String values) {
        HashMap map = Maps.newHashMap();
        if ("default".equals(values)) {
            return block.func_176223_P().func_177228_b();
        }
        BlockStateContainer blockstatecontainer = block.func_176194_O();
        Iterator iterator = COMMA_SPLITTER.split((CharSequence)values).iterator();
        while (true) {
            Object comparable;
            IProperty iproperty;
            if (!iterator.hasNext()) {
                return map;
            }
            String s = (String)iterator.next();
            Iterator iterator1 = EQUAL_SPLITTER.split((CharSequence)s).iterator();
            if (!iterator1.hasNext() || (iproperty = blockstatecontainer.func_185920_a((String)iterator1.next())) == null || !iterator1.hasNext() || (comparable = BlockStateUtilities.getValueHelper(iproperty, (String)iterator1.next())) == null) break;
            map.put(iproperty, comparable);
        }
        return null;
    }

    private static <T extends Comparable<T>> IBlockState getBlockStateWithProperty(IBlockState blockState, IProperty<T> property, Comparable<?> value) {
        return blockState.func_177226_a(property, value);
    }

    public static IBlockState getBlockStateWithValues(IBlockState blockState, String values) {
        Map<IProperty<?>, Comparable<?>> properties = BlockStateUtilities.getBlockStatePropertyValueMap(blockState.func_177230_c(), values);
        if (properties == null) {
            MillLog.error(null, "Could not parse values line of " + values + " for block " + blockState.func_177230_c());
        } else {
            for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
                blockState = BlockStateUtilities.getBlockStateWithProperty(blockState, entry.getKey(), entry.getValue());
            }
        }
        return blockState;
    }

    public static BlockPlanks.EnumType getPlankVariant(IBlockState blockState) {
        Comparable rawVariant = BlockStateUtilities.getPropertyValueByName(blockState, "variant");
        if (rawVariant != null && rawVariant instanceof BlockPlanks.EnumType) {
            return (BlockPlanks.EnumType)rawVariant;
        }
        return null;
    }

    public static Comparable getPropertyValueByName(IBlockState blockState, String propertyName) {
        BlockStateContainer blockStateContainer = blockState.func_177230_c().func_176194_O();
        if (blockStateContainer.func_185920_a(propertyName) != null) {
            IProperty property = blockStateContainer.func_185920_a(propertyName);
            Comparable value = blockState.func_177229_b(property);
            return value;
        }
        return null;
    }

    public static String getStringFromBlockState(IBlockState blockState) {
        String properties = "";
        for (IProperty property : blockState.func_177227_a()) {
            if (properties.length() > 0) {
                properties = properties + ",";
            }
            properties = properties + property.func_177701_a() + "=" + ((Comparable)blockState.func_177228_b().get((Object)property)).toString();
        }
        return blockState.func_177230_c().getRegistryName().toString() + ";" + properties;
    }

    @Nullable
    private static <T extends Comparable<T>> T getValueHelper(IProperty<T> p_190792_0_, String p_190792_1_) {
        return (T)((Comparable)p_190792_0_.func_185929_b(p_190792_1_).orNull());
    }

    public static boolean hasPropertyByName(IBlockState blockState, String propertyName) {
        BlockStateContainer blockStateContainer = blockState.func_177230_c().func_176194_O();
        return blockStateContainer.func_185920_a(propertyName) != null;
    }

    public static IBlockState setPropertyValueByName(IBlockState blockState, String propertyName, Comparable value) {
        BlockStateContainer blockStateContainer = blockState.func_177230_c().func_176194_O();
        if (blockStateContainer.func_185920_a(propertyName) != null) {
            IProperty property = blockStateContainer.func_185920_a(propertyName);
            blockState = blockState.func_177226_a(property, value);
            return blockState;
        }
        return null;
    }
}

