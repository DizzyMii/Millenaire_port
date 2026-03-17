/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.command.CommandException
 *  net.minecraft.command.ICommand
 *  net.minecraft.command.ICommandSender
 *  net.minecraft.command.WrongUsageException
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 */
package org.millenaire.common.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;

public class CommandDebugResetVillagers
implements ICommand {
    public boolean func_184882_a(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    public int compareTo(ICommand o) {
        return this.func_71517_b().compareTo(o.func_71517_b());
    }

    public void func_184881_a(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        World world = sender.func_130014_f_();
        if (!world.field_72995_K) {
            if (!(sender instanceof EntityPlayer)) {
                throw new WrongUsageException(this.func_71518_a(sender), new Object[0]);
            }
            EntityPlayer senderPlayer = (EntityPlayer)sender;
            MillWorldData worldData = Mill.getMillWorld(world);
            Building village = worldData.getClosestVillage(new Point((Entity)senderPlayer));
            if (village == null || village.getPos().distanceTo((Entity)senderPlayer) > 50.0) {
                throw new CommandException("No village within 50 blocks.", new Object[0]);
            }
            int despawnedVillagers = 0;
            int respawnedVillagers = 0;
            for (VillagerRecord vr : village.getAllVillagerRecords()) {
                ArrayList<MillVillager> matchingVillagers = new ArrayList<MillVillager>();
                for (MillVillager villager : worldData.getAllKnownVillagers()) {
                    if (villager.getVillagerId() != vr.getVillagerId()) continue;
                    matchingVillagers.add(villager);
                }
                for (int i = matchingVillagers.size() - 1; i >= 0; --i) {
                    if (!((MillVillager)matchingVillagers.get((int)i)).field_70128_L) continue;
                    ((MillVillager)matchingVillagers.get(i)).despawnVillagerSilent();
                    ++despawnedVillagers;
                    matchingVillagers.remove(i);
                }
                if (matchingVillagers.size() != 0) continue;
                village.respawnVillager(vr, vr.getHouse().location.sleepingPos);
                ++respawnedVillagers;
            }
            ServerSender.sendChat(senderPlayer, TextFormatting.DARK_GREEN, "Repeared the villager list of " + village.getVillageQualifiedName() + ". Despawned " + despawnedVillagers + " dead villager(s) and respawned " + respawnedVillagers + " villagers.");
        }
    }

    public List<String> func_71514_a() {
        return Collections.emptyList();
    }

    public String func_71517_b() {
        return "millDebugResetVillagers";
    }

    public List<String> func_184883_a(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
        return Collections.emptyList();
    }

    public String func_71518_a(ICommandSender sender) {
        return "commands." + this.func_71517_b().toLowerCase() + ".usage";
    }

    public boolean func_82358_a(String[] args, int index) {
        return false;
    }
}

