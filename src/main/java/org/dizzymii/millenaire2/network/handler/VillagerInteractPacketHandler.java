package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.entity.MillVillager;
import org.dizzymii.millenaire2.network.ServerPacketSender;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

/**
 * Handles villager right-click interaction from the client.
 */
public final class VillagerInteractPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private VillagerInteractPacketHandler() {}

    public static void handle(int entityId, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        Entity entity = player.level().getEntity(entityId);
        if (!(entity instanceof MillVillager villager)) {
            LOGGER.warn("Villager interact: entity " + entityId + " not found");
            return;
        }

        // Send full villager sync to the interacting player
        ServerPacketSender.sendVillagerSync(player, villager);

        // Resolve building trade goods and player profile
        Building building = villager.getHomeBuilding();
        if (building == null) building = villager.getTownHallBuilding();

        MillWorldData mw = MillWorldData.get(player.serverLevel());
        if (mw != null && building != null) {
            UserProfile profile = mw.getOrCreateProfile(player.getUUID(), player.getName().getString());
            Point vPos = building.getTownHallPos() != null ? building.getTownHallPos() : building.getPos();
            int rep = vPos != null ? profile.getVillageReputation(vPos) : 0;
            String vName = villager.getFirstName() + " " + villager.getFamilyName();
            ServerPacketSender.sendTradeData(player, villager.getId(),
                    building.getTradeGoods(), profile.deniers, rep, vName);
        }

        // Open trade GUI for the villager
        ServerPacketSender.sendOpenGui(player, 1, villager.getId(), villager.getTownHallPoint());
        LOGGER.debug("Player " + player.getName().getString()
                + " interacted with villager " + villager.getFirstName());
    }
}
