package org.dizzymii.millenaire2.network.handlers;

import net.minecraft.server.level.ServerPlayer;
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
 * Handles quest-related GUI actions: accepting, completing steps, and refusing quests.
 * Dispatched from ServerPacketHandler for GUIACTION_QUEST_COMPLETESTEP and
 * GUIACTION_QUEST_REFUSE.
 */
public final class QuestHandler {

    private QuestHandler() {}

    public static void handle(int actionId, byte[] data, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;

        PacketDataHelper.Reader r = new PacketDataHelper.Reader(data);
        try {
            String questKey = r.readString();
            int villagerEntityId = r.readInt();

            MillWorldData mw = org.dizzymii.millenaire2.Millenaire2.getWorldData();
            UserProfile profile = mw.getProfile(player.getUUID());

            if (actionId == MillPacketIds.GUIACTION_QUEST_COMPLETESTEP) {
                handleCompleteStep(player, questKey, villagerEntityId, mw, profile);
            } else if (actionId == MillPacketIds.GUIACTION_QUEST_REFUSE) {
                profile.questInstances.removeIf(qi -> qi.quest != null && questKey.equals(qi.quest.key));
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                        "\u00a76[Mill\u00e9naire]\u00a7r Quest declined."));
                mw.setDirty();
            }
        } catch (Exception e) {
            MillLog.error("QuestHandler", "Error handling quest action", e);
        } finally {
            r.release();
        }
    }

    private static void handleCompleteStep(ServerPlayer player, String questKey,
                                            int villagerEntityId, MillWorldData mw,
                                            UserProfile profile) {
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
                MillLog.warn("QuestHandler", "Quest not found: " + questKey);
                return;
            }
            HashMap<String, QuestInstanceVillager> vils = new HashMap<>();
            active = new QuestInstance(mw, quest, profile, vils, System.currentTimeMillis());
            profile.questInstances.add(active);
            MillLog.minor("QuestHandler", "Quest '" + questKey + "' accepted by " + player.getName().getString());
        }

        boolean finished = active.completeStep();
        if (finished) {
            QuestStep lastStep = active.quest != null && active.currentStep > 0
                    ? active.quest.steps.get(active.currentStep - 1) : null;
            if (lastStep != null && lastStep.rewardMoney > 0) {
                profile.deniers += lastStep.rewardMoney;
            }
            profile.questInstances.remove(active);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a76[Mill\u00e9naire]\u00a7r Quest '" + questKey + "' completed!"));
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "\u00a76[Mill\u00e9naire]\u00a7r Quest step completed."));
        }
        mw.setDirty();
    }
}
