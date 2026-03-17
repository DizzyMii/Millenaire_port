/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandBase
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.commands.CommandUtilities;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.world.WorldGenVillage;

public class CommandSpawnVillage
implements ICommand {
    private final boolean spawnLoneBuilding;

    public CommandSpawnVillage(boolean spawnLoneBuildings) {
        this.spawnLoneBuilding = spawnLoneBuildings;
    }

    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return sender.func_70003_b(this.getRequiredPermissionLevel(), this.func_71517_b());
    }

    public int compareTo(ICommand o) {
        return this.func_71517_b().compareTo(o.func_71517_b());
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.func_130014_f_();
        if (!world.field_72995_K) {
            if (args.length < 2 || args.length > 5) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            String cultureParam = args[0];
            String villageTypeParam = args[1];
            Culture culture = Culture.getCultureByName(cultureParam);
            if (culture == null) {
                throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownculture", cultureParam), new Object[0]);
            }
            VillageType villageType = this.spawnLoneBuilding ? culture.getLoneBuildingType(villageTypeParam) : culture.getVillageType(villageTypeParam);
            if (villageType == null) {
                throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownvillage", villageTypeParam), new Object[0]);
            }
            EntityPlayer player = null;
            if (sender instanceof EntityPlayer) {
                player = (EntityPlayer)sender;
            }
            int x = 0;
            int z = 0;
            float completion = 0.0f;
            if (args.length > 3) {
                x = CommandBase.func_175755_a((String)args[2]);
                z = CommandBase.func_175755_a((String)args[3]);
                if (args.length > 4) {
                    completion = (float)CommandBase.func_175755_a((String)args[4]) / 100.0f;
                }
            } else if (player != null) {
                x = (int)player.field_70165_t;
                z = (int)player.field_70161_v;
                if (args.length > 2) {
                    completion = (float)CommandBase.func_175755_a((String)args[2]) / 100.0f;
                }
            }
            MillLog.major(null, "Attempting to spawn village of type " + cultureParam + ":" + villageTypeParam + " at " + x + "/" + z + ".");
            WorldGenVillage genVillage = new WorldGenVillage();
            boolean result = genVillage.generateVillageAtPoint(world, MillCommonUtilities.random, x, 0, z, player, false, true, false, 0, villageType, null, null, completion);
            MillLog.major(null, "Result of spawn attempt: " + result);
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        if (this.spawnLoneBuilding) {
            return "millSpawnLoneBuilding";
        }
        return "millSpawnVillage";
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        Culture culture;
        if (args.length == 1) {
            ArrayList<String> possibleMatches = new ArrayList<String>();
            String partialKey = CommandUtilities.normalizeString(args[0]);
            for (Culture c : Culture.ListCultures) {
                if (!CommandUtilities.normalizeString(c.key).startsWith(partialKey)) continue;
                possibleMatches.add(CommandUtilities.normalizeString(c.key));
            }
            return possibleMatches;
        }
        if (args.length == 2 && (culture = Culture.getCultureByName(args[0])) != null) {
            ArrayList<String> possibleMatches = new ArrayList<String>();
            String partialKey = CommandUtilities.normalizeString(args[1]);
            List<VillageType> types = this.spawnLoneBuilding ? culture.listLoneBuildingTypes : culture.listVillageTypes;
            for (VillageType vtype : types) {
                if (!CommandUtilities.normalizeString(vtype.key).startsWith(partialKey)) continue;
                possibleMatches.add(CommandUtilities.normalizeString(vtype.key));
            }
            return possibleMatches;
        }
        return Collections.emptyList();
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands." + this.func_71517_b().toLowerCase() + ".usage";
    }

    public boolean func_82358_a(String[] args, int index) {
        return false;
    }
}

