package org.dizzymii.millenaire2.network.handler;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.network.payloads.QuestInstancePayload;

import javax.annotation.Nullable;

/**
 * Client-side cache and handling for quest instance packets.
 */
public final class ClientQuestPacketHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    // Client-side cache of quest data (populated by QuestInstancePayload)
    @Nullable public static QuestClientEntry cachedQuest = null;
    public static int cachedQuestVillagerEntityId = -1;

    private ClientQuestPacketHandler() {}

    public static void handle(QuestInstancePayload p) {
        cachedQuest = new QuestClientEntry(p.questKey(), p.stepIndex(), p.totalSteps(),
                p.stepDescription(), p.stepLabel(), p.rewardMoney(), p.rewardRep(), p.isOffer());
        cachedQuestVillagerEntityId = p.villagerEntityId();

        LOGGER.debug("Quest sync: " + p.questKey()
                + " step=" + p.stepIndex() + "/" + p.totalSteps() + " offer=" + p.isOffer());
    }

    public static class QuestClientEntry {
        public final String questKey;
        public final int stepIndex;
        public final int totalSteps;
        public final String stepDescription;
        public final String stepLabel;
        public final int rewardMoney;
        public final int rewardReputation;
        public final boolean isOffer;

        public QuestClientEntry(String questKey, int stepIndex, int totalSteps,
                                 String stepDescription, String stepLabel,
                                 int rewardMoney, int rewardReputation, boolean isOffer) {
            this.questKey = questKey;
            this.stepIndex = stepIndex;
            this.totalSteps = totalSteps;
            this.stepDescription = stepDescription;
            this.stepLabel = stepLabel;
            this.rewardMoney = rewardMoney;
            this.rewardReputation = rewardReputation;
            this.isOffer = isOffer;
        }
    }
}
