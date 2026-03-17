/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.block.MillBlocks;
import org.millenaire.common.utilities.WorldUtilities;

public class BlockAlchemistExplosive
extends Block {
    private static final int EXPLOSION_RADIUS = 32;

    public BlockAlchemistExplosive(String blockName) {
        super(Material.field_151576_e);
        this.setHarvestLevel("pickaxe", 0);
        this.func_149711_c(1.5f);
        this.func_149752_b(10.0f);
        this.func_149663_c("millenaire." + blockName);
        this.setRegistryName(blockName);
        this.func_149647_a(MillBlocks.tabMillenaire);
    }

    private void alchemistExplosion(World world, BlockPos pos) {
        int centreX = pos.func_177958_n();
        int centreY = pos.func_177956_o();
        int centreZ = pos.func_177952_p();
        WorldUtilities.setBlockAndMetadata(world, centreX, centreY, centreZ, Blocks.field_150350_a, 0, true, false);
        for (int dy = 32; dy >= -32; --dy) {
            if (dy + centreY < 0 || dy + centreY >= 128) continue;
            for (int dx = -32; dx <= 32; ++dx) {
                for (int dz = -32; dz <= 32; ++dz) {
                    Block block;
                    if (dx * dx + dy * dy + dz * dz > 1024 || (block = WorldUtilities.getBlock(world, centreX + dx, centreY + dy, centreZ + dz)) == Blocks.field_150350_a) continue;
                    WorldUtilities.setBlockAndMetadata(world, centreX + dx, centreY + dy, centreZ + dz, Blocks.field_150350_a, 0, true, false);
                }
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation((Item)Item.func_150898_a((Block)this), (int)0, (ModelResourceLocation)new ModelResourceLocation(this.getRegistryName(), ""));
    }

    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        this.alchemistExplosion(world, pos);
    }
}

