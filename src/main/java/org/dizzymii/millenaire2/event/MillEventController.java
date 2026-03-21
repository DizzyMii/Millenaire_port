package org.dizzymii.millenaire2.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.block.MillBlocks;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;
import org.dizzymii.millenaire2.world.MillWorldData;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Central event controller for server-side game events.
 * Ported from org.millenaire.common.forge.MillEventController +
 *             org.millenaire.common.forge.ServerTickHandler (Forge 1.12.2).
 */
@EventBusSubscriber(modid = Millenaire2.MODID)
public class MillEventController {

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel serverLevel)) return;
        // Only initialise for the overworld; MillWorldData.get() handles persistence
        if (serverLevel.dimension() != ServerLevel.OVERWORLD) return;

        MillWorldData mw = MillWorldData.get(serverLevel);
        MillLog.minor("MillEventController", "Level loaded — " + mw.allBuildings().size() + " buildings tracked.");
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        LevelAccessor levelAccessor = event.getLevel();
        if (!(levelAccessor instanceof ServerLevel serverLevel)) return;
        if (serverLevel.dimension() != ServerLevel.OVERWORLD) return;

        // SavedData is automatically persisted by Minecraft when the level unloads,
        // but we mark dirty to ensure latest state is flushed.
        MillWorldData mw = MillWorldData.get(serverLevel);
        mw.setDirty();
        MillLog.minor("MillEventController", "Level unloading — marked MillWorldData dirty for save.");
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) return;
        BlockState state = event.getLevel().getBlockState(event.getPos());

        // Locked chest interaction
        if (state.is(MillBlocks.LOCKED_CHEST.get())) {
            if (!(event.getLevel() instanceof ServerLevel sl)) return;
            MillWorldData mw = MillWorldData.get(sl);

            // Find the building that owns this locked chest
            Point clickPos = new Point(event.getPos());
            Building owner = findOwnerBuilding(mw, clickPos);
            if (owner != null && owner.chestLocked) {
                // Check reputation — only allow access if player has sufficient standing
                if (event.getEntity() instanceof ServerPlayer sp) {
                    var profile = mw.getOrCreateProfile(sp.getUUID(), sp.getGameProfile().getName());
                    Point villagePos = owner.getTownHallPos();
                    if (villagePos != null) {
                        int rep = profile.getVillageReputation(villagePos);
                        if (rep < Building.MIN_REPUTATION_FOR_TRADE) {
                            sp.displayClientMessage(
                                    net.minecraft.network.chat.Component.translatable("millenaire2.chest.locked"), true);
                            event.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof MillVillager villager)) return;
        if (entity.level().isClientSide) return;

        if (!(entity.level() instanceof ServerLevel sl)) return;
        MillWorldData mw = MillWorldData.get(sl);

        long villagerId = villager.getVillagerId();

        // Mark the VillagerRecord as killed
        VillagerRecord vr = mw.getVillagerRecord(villagerId);
        if (vr != null) {
            vr.killed = true;
        }

        // Update the owning building
        Point thPoint = villager.townHallPoint;
        if (thPoint != null) {
            Building b = mw.getBuilding(thPoint);
            if (b != null) {
                VillagerRecord bvr = b.getVillagerRecord(villagerId);
                if (bvr != null) {
                    bvr.killed = true;
                }
            }
        }

        // If a player killed the villager, apply reputation penalty
        Entity killer = event.getSource().getEntity();
        if (killer instanceof ServerPlayer sp) {
            var profile = mw.getOrCreateProfile(sp.getUUID(), sp.getGameProfile().getName());
            if (thPoint != null) {
                profile.adjustVillageReputation(thPoint, -64);
            }
            String cultureKey = villager.getCultureKey();
            if (cultureKey != null && !cultureKey.isEmpty()) {
                int currentRep = profile.getCultureReputation(cultureKey);
                profile.setCultureReputation(cultureKey, currentRep - 16);
            }
            MillLog.minor("MillEventController",
                    sp.getGameProfile().getName() + " killed villager " + villager.getFirstName()
                            + " — reputation penalty applied.");
        }

        mw.setDirty();
        MillLog.minor("MillEventController", "Villager died: " + villager.getFirstName() + " " + villager.getFamilyName());
    }

    // ========== Helpers ==========

    @Nullable
    private static Building findOwnerBuilding(MillWorldData mw, Point pos) {
        double closest = Double.MAX_VALUE;
        Building owner = null;
        for (Building b : mw.allBuildings()) {
            Point bPos = b.getPos();
            if (bPos == null) continue;
            double dist = bPos.distanceTo(pos);
            if (dist < 32 && dist < closest) {
                closest = dist;
                owner = b;
            }
        }
        return owner;
    }
}
