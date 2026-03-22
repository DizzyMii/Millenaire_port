package org.dizzymii.millenaire2.network.handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.dizzymii.millenaire2.network.MillPacketIds;
import org.dizzymii.millenaire2.network.PacketDataHelper;
import org.dizzymii.millenaire2.quest.Quest;
import org.dizzymii.millenaire2.quest.QuestInstance;
import org.dizzymii.millenaire2.quest.QuestInstanceVillager;
import org.dizzymii.millenaire2.quest.QuestStep;
import org.dizzymii.millenaire2.util.MillLog;
import org.dizzymii.millenaire2.world.MillWorldData;
import org.dizzymii.millenaire2.world.UserProfile;

import java.util.HashMap;

/**
 * Handles quest accept/complete/refuse GUI actions from the client.
 */
public final class QuestPacketHandler {

    private QuestPacketHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String questKey = r.readString();
            int villagerEntityId = r.readInt();

            MillWorldData mw = MillWorldData.get(player.serverLevel());
            UserProfile profile = mw.getProfile(player.getUUID());

            if (actionId == MillPacketIds.GUIACTION_QUEST_COMPLETESTEP) {
                handleCompleteStep(player, profile, questKey, mw);
            } else if (actionId == MillPacketIds.GUIACTION_QUEST_REFUSE) {
                handleRefuse(player, profile, questKey, mw);
            }
        } catch (Exception e) {
            MillLog.error("QuestPacketHandler", "Error handling quest action", e);
        } finally {
            r.release();
        }
    }

    private static void handleCompleteStep(ServerPlayer player, UserProfile profile, String questKey, MillWorldData mw) {
        // Find the active quest instance for this player
        QuestInstance active = null;
        for (QuestInstance qi : profile.questInstances) {
            if (qi.quest != null && questKey.equals(qi.quest.key)) {
                active = qi;
                break;
            }
        }

        if (active == null) {
            // No active instance — this is a new quest acceptance (step 0)
            Quest quest = Quest.quests.get(questKey);
            if (quest == null) {
                MillLog.warn("QuestPacketHandler", "Quest not found: " + questKey);
                return;
            }
            // Create new quest instance
            HashMap<String, QuestInstanceVillager> vils = new HashMap<>();
            active = new QuestInstance(mw, quest, profile, vils, System.currentTimeMillis());
            profile.questInstances.add(active);
            MillLog.minor("QuestPacketHandler", "Quest '" + questKey + "' accepted by " + player.getName().getString());
        }

        boolean finished = active.completeStep();
        if (finished) {
            // Award money
            QuestStep lastStep = active.quest != null && active.currentStep > 0
                    ? active.quest.steps.get(active.currentStep - 1) : null;
            if (lastStep != null && lastStep.rewardMoney > 0) {
                profile.deniers += lastStep.rewardMoney;
            }
            profile.questInstances.remove(active);
            player.sendSystemMessage(Component.literal("\u00a76[Mill\u00e9naire]\u00a7r Quest '" + questKey + "' completed!"));
        } else {
            player.sendSystemMessage(Component.literal("\u00a76[Mill\u00e9naire]\u00a7r Quest step completed."));
        }
        mw.setDirty();
    }

    private static void handleRefuse(ServerPlayer player, UserProfile profile, String questKey, MillWorldData mw) {
        // Remove quest instance if it exists
        profile.questInstances.removeIf(qi -> qi.quest != null && questKey.equals(qi.quest.key));
        player.sendSystemMessage(Component.literal("\u00a76[Mill\u00e9naire]\u00a7r Quest declined."));
        mw.setDirty();
    }
}
