/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityHanging
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package org.millenaire.common.entity;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.StreamReadWrite;
import org.millenaire.common.utilities.BlockItemUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class EntityWallDecoration
extends EntityHanging
implements IEntityAdditionalSpawnData {
    public static final ResourceLocation WALL_DECORATION = new ResourceLocation("millenaire", "WallDecoration");
    public static final int NORMAN_TAPESTRY = 1;
    public static final int INDIAN_STATUE = 2;
    public static final int MAYAN_STATUE = 3;
    public static final int BYZANTINE_ICON_SMALL = 4;
    public static final int BYZANTINE_ICON_MEDIUM = 5;
    public static final int BYZANTINE_ICON_LARGE = 6;
    public static final int HIDE_HANGING = 7;
    public static final int WALL_CARPET_SMALL = 8;
    public static final int WALL_CARPET_MEDIUM = 9;
    public static final int WALL_CARPET_LARGE = 10;
    public EnumWallDecoration millArt;
    public int type;

    public static EntityWallDecoration createWallDecoration(World world, Point p, int type) {
        EnumFacing facing = EntityWallDecoration.guessOrientation(world, p);
        BlockPos blockpos = p.getBlockPos();
        blockpos = blockpos.func_177972_a(facing);
        return new EntityWallDecoration(world, p.getBlockPos(), facing, type, true);
    }

    private static EnumFacing guessOrientation(World world, Point p) {
        if (BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, p.getNorth()))) {
            return EnumFacing.SOUTH;
        }
        if (BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, p.getSouth()))) {
            return EnumFacing.NORTH;
        }
        if (BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, p.getEast()))) {
            return EnumFacing.WEST;
        }
        if (BlockItemUtilities.isBlockSolid(WorldUtilities.getBlock(world, p.getWest()))) {
            return EnumFacing.EAST;
        }
        return EnumFacing.WEST;
    }

    public EntityWallDecoration(World par1World) {
        super(par1World);
    }

    public EntityWallDecoration(World world, BlockPos pos, EnumFacing facing, int type, boolean largestPossible) {
        super(world, pos);
        this.type = type;
        ArrayList<EnumWallDecoration> arraylist = new ArrayList<EnumWallDecoration>();
        int maxSize = 0;
        for (EnumWallDecoration enumart : EnumWallDecoration.values()) {
            if (enumart.type != type) continue;
            this.millArt = enumart;
            this.func_174859_a(facing);
            if (!this.func_70518_d()) continue;
            if (!largestPossible && enumart.sizeX * enumart.sizeY > maxSize) {
                arraylist.clear();
            }
            arraylist.add(enumart);
            maxSize = enumart.sizeX * enumart.sizeY;
        }
        if (arraylist.size() > 0) {
            this.millArt = (EnumWallDecoration)MillCommonUtilities.getWeightedChoice(arraylist, null);
        }
        if (MillConfigValues.LogBuildingPlan >= 1) {
            MillLog.major((Object)this, "Creating wall decoration: " + pos + "/" + facing + "/" + type + "/" + largestPossible + ". Result: " + this.millArt.title + " picked among " + arraylist.size());
        }
        this.func_174859_a(facing);
    }

    @SideOnly(value=Side.CLIENT)
    public EntityWallDecoration(World world, int type) {
        this(world);
        this.type = type;
    }

    public Item getDropItem() {
        if (this.type == 1) {
            return MillItems.TAPESTRY;
        }
        if (this.type == 2) {
            return MillItems.INDIAN_STATUE;
        }
        if (this.type == 3) {
            return MillItems.MAYAN_STATUE;
        }
        if (this.type == 4) {
            return MillItems.BYZANTINE_ICON_SMALL;
        }
        if (this.type == 5) {
            return MillItems.BYZANTINE_ICON_MEDIUM;
        }
        if (this.type == 6) {
            return MillItems.BYZANTINE_ICON_LARGE;
        }
        if (this.type == 7) {
            return MillItems.HIDEHANGING;
        }
        if (this.type == 8) {
            return MillItems.WALLCARPETSMALL;
        }
        if (this.type == 9) {
            return MillItems.WALLCARPETMEDIUM;
        }
        if (this.type == 10) {
            return MillItems.WALLCARPETLARGE;
        }
        MillLog.error((Object)this, "Unknown walldecoration type: " + this.type);
        return null;
    }

    public int func_82330_g() {
        return this.millArt.sizeY;
    }

    public int func_82329_d() {
        return this.millArt.sizeX;
    }

    public void func_110128_b(Entity brokenEntity) {
        if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
            this.func_184185_a(SoundEvents.field_187691_dJ, 1.0f, 1.0f);
            if (brokenEntity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)brokenEntity;
                if (entityplayer.field_71075_bZ.field_75098_d) {
                    return;
                }
            }
            this.func_70099_a(new ItemStack(this.getDropItem()), 0.0f);
        }
    }

    public void func_70071_h_() {
        super.func_70071_h_();
    }

    public boolean func_70518_d() {
        return super.func_70518_d();
    }

    public void func_184523_o() {
        this.func_184185_a(SoundEvents.field_187694_dK, 1.0f, 1.0f);
    }

    public void func_70037_a(NBTTagCompound nbttagcompound) {
        this.type = nbttagcompound.func_74762_e("Type");
        String s = nbttagcompound.func_74779_i("Motive");
        for (EnumWallDecoration enumart : EnumWallDecoration.values()) {
            if (!enumart.title.equals(s)) continue;
            this.millArt = enumart;
        }
        if (this.millArt == null) {
            this.millArt = EnumWallDecoration.Griffon;
        }
        if (this.type == 0) {
            this.type = 1;
        }
        super.func_70037_a(nbttagcompound);
    }

    public void readSpawnData(ByteBuf bb) {
        PacketBuffer data = new PacketBuffer(bb);
        this.type = data.readInt();
        String title = data.func_150789_c(2048);
        for (EnumWallDecoration enumart : EnumWallDecoration.values()) {
            if (!enumart.title.equals(title)) continue;
            this.millArt = enumart;
        }
        Point p = StreamReadWrite.readNullablePoint(data);
        this.func_70107_b(p.x, p.y, p.z);
        int facingId = data.readInt();
        this.func_174859_a(EnumFacing.func_82600_a((int)facingId));
    }

    public void func_70012_b(double x, double y, double z, float yaw, float pitch) {
        this.func_70107_b(x, y, z);
    }

    @SideOnly(value=Side.CLIENT)
    public void func_180426_a(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
        BlockPos blockpos = this.field_174861_a.func_177963_a(x - this.field_70165_t, y - this.field_70163_u, z - this.field_70161_v);
        this.func_70107_b(blockpos.func_177958_n(), blockpos.func_177956_o(), blockpos.func_177952_p());
    }

    public String toString() {
        return "Tapestry (" + this.millArt.title + ") " + super.toString();
    }

    public void func_70014_b(NBTTagCompound nbttagcompound) {
        nbttagcompound.func_74768_a("Type", this.type);
        nbttagcompound.func_74778_a("Motive", this.millArt.title);
        super.func_70014_b(nbttagcompound);
    }

    public void writeSpawnData(ByteBuf bb) {
        PacketBuffer data = new PacketBuffer(bb);
        data.writeInt(this.type);
        data.func_180714_a(this.millArt.title);
        StreamReadWrite.writeNullablePoint(new Point(this.func_174857_n()), data);
        data.writeInt(this.field_174860_b.func_176745_a());
    }

    public static enum EnumWallDecoration implements MillCommonUtilities.WeightedChoice
    {
        Griffon("Griffon", 16, 16, 0, 0, 1),
        Oiseau("Oiseau", 16, 16, 16, 0, 1),
        CorbeauRenard("CorbeauRenard", 32, 16, 32, 0, 1),
        Serment("Serment", 80, 48, 0, 16, 1),
        MortHarold("MortHarold", 64, 48, 80, 16, 1),
        Drakar("Drakar", 96, 48, 144, 16, 1),
        MontStMichel("MontStMichel", 48, 32, 0, 64, 1),
        Bucherons("Bucherons", 48, 32, 48, 64, 1),
        Cuisine("Cuisine", 48, 32, 96, 64, 1),
        Flotte("Flotte", 240, 48, 0, 96, 1),
        Chasse("Chasse", 96, 48, 0, 144, 1),
        Siege("Siege", 256, 48, 0, 192, 1),
        Ganesh("Ganesh", 32, 48, 0, 0, 2),
        Kali("Kali", 32, 48, 32, 0, 2),
        Shiva("Shiva", 32, 48, 64, 0, 2),
        Osiyan("Osiyan", 32, 48, 96, 0, 2),
        Durga("Durga", 32, 48, 128, 0, 2),
        MayanTeal("MayanTeal", 32, 32, 0, 48, 3),
        MayanGold("MayanGold", 32, 32, 32, 48, 3),
        LargeJesus("LargeJesus", 32, 48, 0, 80, 6),
        LargeVirgin("LargeVirgin", 32, 48, 32, 80, 6),
        MediumVirgin1("MediumVirgin1", 32, 32, 0, 128, 5),
        MediumVirgin2("MediumVirgin2", 32, 32, 32, 128, 5),
        SmallJesus1("SmallJesus1", 16, 16, 0, 160, 4),
        SmallJesus2("SmallJesus2", 16, 16, 16, 160, 4),
        SmallSaint1("SmallSaint1", 16, 16, 32, 160, 4),
        SmallAngel1("SmallAngel1", 16, 16, 48, 160, 4),
        SmallVirgin1("SmallVirgin1", 16, 16, 64, 160, 4),
        SmallAngel2("SmallAngel2", 16, 16, 80, 160, 4),
        HideSmallCow("HideSmallCow", 16, 16, 0, 176, 7, 10),
        HideSmallRabbit("HideSmallRabbit", 16, 16, 16, 176, 7, 10),
        HideSmallSpider("HideSmallSpider", 16, 16, 32, 176, 7, 1),
        HideLargeCow("HideLargeCow", 32, 32, 0, 192, 7, 10),
        HideLargeBear("HideLargeBear", 32, 32, 32, 192, 7, 5),
        HideLargeZombie("HideLargeZombie", 32, 32, 64, 192, 7, 1),
        HideLargeWolf("HideLargeWolf", 32, 32, 96, 192, 7, 5),
        WallCarpet1("WallCarpet1", 16, 32, 0, 224, 8),
        WallCarpet2("WallCarpet2", 16, 32, 16, 224, 8),
        WallCarpet3("WallCarpet3", 16, 32, 32, 224, 8),
        WallCarpet4("WallCarpet4", 16, 32, 48, 224, 8),
        WallCarpet5("WallCarpet5", 16, 32, 64, 224, 8),
        WallCarpet6("WallCarpet6", 16, 32, 80, 224, 8),
        WallCarpet7("WallCarpet7", 16, 32, 96, 224, 8),
        WallCarpet8("WallCarpet8", 32, 48, 160, 176, 9),
        WallCarpet9("WallCarpet9", 32, 48, 192, 176, 9),
        WallCarpet10("WallCarpet10", 32, 48, 224, 176, 9),
        WallCarpet11("WallCarpet11", 48, 32, 112, 224, 10),
        WallCarpet12("WallCarpet12", 48, 32, 160, 224, 10),
        WallCarpet13("WallCarpet13", 48, 32, 208, 224, 10);

        public static final int maxArtTitleLength;
        public final String title;
        public final int sizeX;
        public final int sizeY;
        public final int offsetX;
        public final int offsetY;
        public final int type;
        private final int weight;

        private EnumWallDecoration(String title, int sizeX, int sizeY, int offsetX, int offsetY, int type) {
            this.title = title;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.type = type;
            this.weight = 1;
        }

        private EnumWallDecoration(String title, int sizeX, int sizeY, int offsetX, int offsetY, int type, int weight) {
            this.title = title;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.type = type;
            this.weight = weight;
        }

        @Override
        public int getChoiceWeight(EntityPlayer player) {
            return this.weight;
        }

        static {
            maxArtTitleLength = "SkullAndRoses".length();
        }
    }
}

