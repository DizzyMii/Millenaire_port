/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityElderGuardian
 *  net.minecraft.entity.monster.EntityGuardian
 *  net.minecraft.entity.monster.EntityPolarBear
 *  net.minecraft.entity.passive.EntitySquid
 *  net.minecraft.entity.passive.EntityWolf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.WorldServerMulti
 *  net.minecraftforge.event.entity.living.LivingDamageEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.world.WorldEvent$Load
 *  net.minecraftforge.event.world.WorldEvent$Save
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$ItemSmeltedEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientConnectedToServerEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ServerDisconnectionFromClientEvent
 */
package org.millenaire.common.forge;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityElderGuardian;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.MillItems;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;

public class MillEventController {
    @SubscribeEvent
    public void addInuitDrops(LivingDropsEvent event) {
        if (event.getEntityLiving() instanceof EntityGuardian || event.getEntityLiving() instanceof EntitySquid) {
            this.inuitDropsSeaFood(event);
        } else if (event.getEntityLiving() instanceof EntityWolf) {
            this.inuitDropsWolfMeat(event);
        } else if (event.getEntityLiving() instanceof EntityPolarBear) {
            int quantity = 1 + MillCommonUtilities.randomInt(2);
            event.getDrops().add(new EntityItem(event.getEntityLiving().field_70170_p, event.getEntityLiving().field_70165_t, event.getEntityLiving().field_70163_u, event.getEntityLiving().field_70161_v, new ItemStack((Item)MillItems.BEARMEAT_RAW, quantity)));
        }
    }

    @SubscribeEvent
    public void clientLoggedIn(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Mill.proxy.handleClientLogin();
    }

    @SubscribeEvent
    public void connectionClosed(FMLNetworkEvent.ServerDisconnectionFromClientEvent event) {
        for (MillWorldData mw : Mill.serverWorlds) {
            mw.checkConnections();
        }
    }

    @SubscribeEvent
    public void damageOnPlayer(LivingDamageEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityLivingBase source = null;
            if (event.getSource().func_76364_f() != null && event.getSource().func_76364_f() instanceof EntityLivingBase) {
                source = (EntityLivingBase)event.getSource().func_76364_f();
            } else if (event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof EntityLivingBase) {
                source = (EntityLivingBase)event.getSource().func_76346_g();
            }
            if (source != null) {
                MillWorldData mw = Mill.getMillWorld(event.getEntityLiving().field_70170_p);
                EntityPlayer player = (EntityPlayer)event.getEntityLiving();
                String playerName = player.func_70005_c_();
                for (MillVillager villager : mw.getAllKnownVillagers()) {
                    if (!playerName.equals(villager.hiredBy)) continue;
                    villager.func_70624_b((EntityLivingBase)event.getSource().func_76346_g());
                }
            }
        }
    }

    @SubscribeEvent
    public void handleFurnaceWithdrawals(PlayerEvent.ItemSmeltedEvent event) {
        Building building;
        BuildingLocation location;
        Point playerPos;
        if (event.smelting.func_190916_E() == 0) {
            return;
        }
        EntityPlayer player = event.player;
        MillWorldData mwd = Mill.getMillWorld(player.field_70170_p);
        Building closestVillageTH = mwd.getClosestVillage(playerPos = new Point((Entity)player));
        if (closestVillageTH != null && !closestVillageTH.controlledBy(player) && (location = closestVillageTH.getLocationAtCoordWithTolerance(playerPos, 4)) != null && (building = location.getBuilding(player.field_70170_p)) != null) {
            UserProfile serverProfile;
            boolean isBuildingPlayerOwned;
            boolean bl = isBuildingPlayerOwned = building.location.getPlan() != null && (building.location.getPlan().price > 0 || building.location.getPlan().isgift);
            if (!isBuildingPlayerOwned && !building.getResManager().furnaces.isEmpty() && (serverProfile = VillageUtilities.getServerProfile(player.field_70170_p, player)) != null) {
                int reputationLost = event.smelting.func_190916_E() * 100;
                serverProfile.adjustReputation(closestVillageTH, -reputationLost);
                ServerSender.sendTranslatedSentence(player, '6', "ui.stealingsmelteditems", "" + reputationLost);
            }
        }
    }

    private void inuitDropsSeaFood(LivingDropsEvent event) {
        if (event.getSource() != null && event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getSource().func_76346_g();
            UserProfile profile = Mill.getMillWorld(event.getEntity().field_70170_p).getProfile(player);
            if (profile.isTagSet("huntingdrop_" + MillItems.SEAFOOD_RAW.getRegistryName().func_110623_a())) {
                int quantity = 0;
                if (event.getEntityLiving() instanceof EntitySquid) {
                    if (MillCommonUtilities.chanceOn(10)) {
                        quantity = 1;
                    }
                } else if (event.getEntityLiving() instanceof EntityElderGuardian) {
                    quantity = 5 + MillCommonUtilities.randomInt(5);
                } else if (event.getEntityLiving() instanceof EntityGuardian) {
                    quantity = 2 + MillCommonUtilities.randomInt(2);
                }
                if (quantity > 0) {
                    event.getDrops().add(new EntityItem(event.getEntityLiving().field_70170_p, event.getEntityLiving().field_70165_t, event.getEntityLiving().field_70163_u, event.getEntityLiving().field_70161_v, new ItemStack((Item)MillItems.SEAFOOD_RAW, quantity)));
                    MillAdvancements.GREAT_HUNTER.grant(player);
                }
            }
        }
    }

    private void inuitDropsWolfMeat(LivingDropsEvent event) {
        if (event.getSource() != null && event.getSource().func_76346_g() != null && event.getSource().func_76346_g() instanceof EntityPlayer) {
            int quantity;
            EntityPlayer player = (EntityPlayer)event.getSource().func_76346_g();
            UserProfile profile = Mill.getMillWorld(event.getEntity().field_70170_p).getProfile(player);
            if (profile.isTagSet("huntingdrop_" + MillItems.WOLFMEAT_RAW.getRegistryName().func_110623_a()) && (quantity = MillCommonUtilities.randomInt(3)) > 0) {
                event.getDrops().add(new EntityItem(event.getEntityLiving().field_70170_p, event.getEntityLiving().field_70165_t, event.getEntityLiving().field_70163_u, event.getEntityLiving().field_70161_v, new ItemStack((Item)MillItems.WOLFMEAT_RAW, quantity)));
                MillAdvancements.GREAT_HUNTER.grant(player);
            }
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            UserProfile profile = VillageUtilities.getServerProfile(event.player.field_70170_p, event.player);
            if (profile != null && !event.player.func_70005_c_().equals(profile.playerName)) {
                MillLog.major(null, "Name of player with UUID '" + profile.uuid + "' changed from '" + profile.playerName + "' to '" + event.player.func_70005_c_() + "'.");
                profile.playerName = event.player.func_70005_c_();
                profile.saveProfile();
            }
            if (profile != null) {
                profile.connectUser();
            } else {
                MillLog.error(this, "Could not get profile on login for user: " + event.player.func_70005_c_());
            }
        }
        catch (Exception e) {
            MillLog.printException("Error in ConnectionHandler.playerLoggedIn:", e);
        }
    }

    @SubscribeEvent
    public void worldLoaded(WorldEvent.Load event) {
        Mill.proxy.loadLanguagesIfNeeded();
        if (Mill.displayMillenaireLocationError && !Mill.proxy.isTrueServer()) {
            Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '4', "ERREUR: Impossible de trouver le fichier de configuration " + Mill.proxy.getConfigFile().getAbsolutePath() + ". V\u00e9rifiez que le dossier millenaire est bien dans minecraft/mods/");
            Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), '4', "ERROR: Could not find the config file at " + Mill.proxy.getConfigFile().getAbsolutePath() + ". Check that the millenaire directory is in minecraft/mods/");
            return;
        }
        if (!(event.getWorld() instanceof WorldServer)) {
            Mill.clientWorld = new MillWorldData(event.getWorld());
        } else if (!(event.getWorld() instanceof WorldServerMulti)) {
            MillWorldData newWorld = new MillWorldData(event.getWorld());
            Mill.serverWorlds.add(newWorld);
            newWorld.loadData();
        }
    }

    @SubscribeEvent
    public void worldSaved(WorldEvent.Save event) {
        if (Mill.startupError) {
            return;
        }
        if (event.getWorld().field_73011_w.getDimension() != 0) {
            return;
        }
        if (!(event.getWorld() instanceof WorldServer)) {
            Mill.clientWorld.saveEverything();
        } else {
            for (MillWorldData mw : Mill.serverWorlds) {
                if (mw.world != event.getWorld()) continue;
                mw.saveEverything();
            }
        }
    }

    @SubscribeEvent
    public void worldUnloaded(WorldEvent.Unload event) {
        if (Mill.startupError) {
            return;
        }
        if (event.getWorld().field_73011_w.getDimension() != 0) {
            return;
        }
        if (!(event.getWorld() instanceof WorldServer)) {
            if (Mill.clientWorld.world == event.getWorld()) {
                Mill.clientWorld = null;
            }
        } else {
            ArrayList<MillWorldData> toDelete = new ArrayList<MillWorldData>();
            for (MillWorldData mw : Mill.serverWorlds) {
                if (mw.world != event.getWorld()) continue;
                toDelete.add(mw);
            }
            for (MillWorldData mw : toDelete) {
                Mill.serverWorlds.remove(mw);
            }
        }
    }
}

