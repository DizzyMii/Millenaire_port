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
import java.util.stream.Collectors;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.millenaire.common.buildingplan.BuildingImportExport;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.commands.CommandUtilities;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;

public class CommandImportCulture
implements ICommand {
    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return sender.func_70003_b(this.getRequiredPermissionLevel(), this.func_71517_b());
    }

    public int compareTo(ICommand o) {
        return this.func_71517_b().compareTo(o.func_71517_b());
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.func_130014_f_();
        if (!world.field_72995_K) {
            int z;
            int x;
            EntityPlayer player = null;
            if (sender instanceof EntityPlayer) {
                player = (EntityPlayer)sender;
            }
            if (args.length != 1 && args.length != 3) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            String cultureParam = args[0];
            Culture culture = Culture.getCultureByName(cultureParam);
            if (culture == null) {
                throw new CommandException(LanguageUtilities.string("command.spawnvillage_unknownculture", cultureParam), new Object[0]);
            }
            if (args.length > 1) {
                x = CommandBase.func_175755_a((String)args[1]);
                z = CommandBase.func_175755_a((String)args[2]);
            } else {
                x = (int)player.field_70165_t;
                z = (int)player.field_70161_v;
            }
            int y = WorldUtilities.findTopSoilBlock(world, x, z);
            Point startPoint = new Point(x, y, z);
            Point adjustedStartPoint = new Point(x, y, z);
            List<BuildingPlanSet> planSets = new ArrayList<BuildingPlanSet>(culture.ListPlanSets);
            planSets = planSets.stream().sorted((p1, p2) -> p1.mainFile.compareTo(p2.mainFile)).collect(Collectors.toList());
            for (BuildingPlanSet planSet : planSets) {
                if (planSet.getFirstStartingPlan().isSubBuilding) continue;
                ServerSender.sendTranslatedSentence(player, 'f', "command.importculture_importingbuilding", planSet.getNameTranslated());
                int xDelta = BuildingImportExport.importTableHandleImportRequest(player, adjustedStartPoint, culture.key, planSet.key, true, 0, 0, 0, true);
                adjustedStartPoint = new Point(adjustedStartPoint.x + (double)xDelta + 5.0, startPoint.y, startPoint.z);
            }
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        return "millImportCulture";
    }

    public int getRequiredPermissionLevel() {
        return 3;
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        if (args.length == 1) {
            ArrayList<String> possibleMatches = new ArrayList<String>();
            String partialKey = CommandUtilities.normalizeString(args[0]);
            for (Culture c : Culture.ListCultures) {
                if (!CommandUtilities.normalizeString(c.key).startsWith(partialKey)) continue;
                possibleMatches.add(CommandUtilities.normalizeString(c.key));
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

