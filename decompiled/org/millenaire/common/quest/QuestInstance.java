/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 */
package org.millenaire.common.quest;

import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.millenaire.common.advancements.MillAdvancements;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.culture.VillageType;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.network.ServerSender;
import org.millenaire.common.quest.Quest;
import org.millenaire.common.quest.QuestInstanceVillager;
import org.millenaire.common.quest.QuestStep;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.world.MillWorldData;
import org.millenaire.common.world.UserProfile;
import org.millenaire.common.world.WorldGenVillage;

public class QuestInstance {
    private static final int QUEST_LANGUAGE_BONUS = 50;
    public int currentStep = 0;
    public long currentStepStart;
    public Quest quest;
    public long startTime;
    public HashMap<String, QuestInstanceVillager> villagers;
    public UserProfile profile = null;
    public MillWorldData mw;
    public World world;
    public long uniqueid;

    public static QuestInstance loadFromString(MillWorldData mw, String line, UserProfile profile) {
        Quest q = null;
        int step = 0;
        long startTime = 0L;
        long stepStartTime = 0L;
        HashMap<String, QuestInstanceVillager> villagers = new HashMap<String, QuestInstanceVillager>();
        for (String s : line.split(";")) {
            if (s.split(":").length != 2) continue;
            String key = s.split(":")[0];
            String value = s.split(":")[1];
            if (key.equals("quest")) {
                if (Quest.quests.containsKey(value)) {
                    q = Quest.quests.get(value);
                    continue;
                }
                MillLog.error(null, "Could not find quest '" + value + "'.");
                continue;
            }
            if (key.equals("startTime")) {
                startTime = Long.parseLong(value);
                continue;
            }
            if (key.equals("currentStepStartTime")) {
                stepStartTime = Long.parseLong(value);
                continue;
            }
            if (key.equals("step")) {
                step = Integer.parseInt(value);
                continue;
            }
            if (!key.equals("villager")) continue;
            String[] vals = value.split(",");
            QuestInstanceVillager qiv = new QuestInstanceVillager(mw, new Point(vals[2]), Long.parseLong(vals[1]));
            villagers.put(vals[0], qiv);
        }
        if (q != null && villagers.size() > 0) {
            return new QuestInstance(mw, q, profile, villagers, startTime, step, stepStartTime);
        }
        return null;
    }

    public QuestInstance(MillWorldData mw, Quest quest, UserProfile profile, HashMap<String, QuestInstanceVillager> villagers, long startTime) {
        this(mw, quest, profile, villagers, startTime, 0, startTime);
    }

    public QuestInstance(MillWorldData mw, Quest quest, UserProfile profile, HashMap<String, QuestInstanceVillager> villagers, long startTime, int step, long stepStartTime) {
        this.mw = mw;
        this.world = mw.world;
        this.villagers = villagers;
        this.quest = quest;
        this.currentStep = step;
        this.startTime = startTime;
        this.profile = profile;
        this.currentStepStart = stepStartTime;
        this.uniqueid = (long)(Math.random() * 9.223372036854776E18);
    }

    private void applyActionData(List<String[]> data) {
        for (String[] val : data) {
            this.profile.setActionData(val[0], val[1]);
        }
    }

    private void applyGlobalTags(List<String> set, List<String> clear) {
        if (MillConfigValues.LogQuest >= 3) {
            MillLog.debug(this, "Applying " + set.size() + " global tags, clearing " + clear.size() + " global tags.");
        }
        for (String val : set) {
            this.mw.setGlobalTag(val);
        }
        for (String val : clear) {
            this.mw.clearGlobalTag(val);
        }
    }

    private void applyPlayerTags(List<String> set, List<String> clear) {
        if (MillConfigValues.LogQuest >= 3) {
            MillLog.debug(this, "Applying " + set.size() + " player tags, clearing " + clear.size() + " player tags.");
        }
        for (String val : set) {
            this.profile.setTag(val);
        }
        for (String val : clear) {
            this.profile.clearTag(val);
        }
    }

    private void applyRelationChanges(List<QuestStep.QuestStepRelationChange> relationChanges) {
        for (QuestStep.QuestStepRelationChange change : relationChanges) {
            if (!this.villagers.containsKey(change.firstVillager)) {
                MillLog.error(this, "Unknown villager reference: " + change.firstVillager);
                break;
            }
            if (!this.villagers.containsKey(change.secondVillager)) {
                MillLog.error(this, "Unknown villager reference: " + change.secondVillager);
                break;
            }
            this.villagers.get(change.firstVillager).getTownHall(this.world).adjustRelation(this.villagers.get((Object)change.secondVillager).townHall, change.change, false);
            if (MillConfigValues.LogQuest < 3) continue;
            MillLog.debug(this, "Adjusting relations between " + this.villagers.get(change.firstVillager).getTownHall(this.world) + " and " + this.villagers.get(change.secondVillager).getTownHall(this.world) + " by " + change.change);
        }
    }

    private void applyTags(List<String[]> set, List<String[]> clear) {
        String tag;
        if (MillConfigValues.LogQuest >= 3) {
            MillLog.debug(this, "Applying " + set.size() + " tags, clearing " + clear.size() + " tags.");
        }
        for (String[] val : set) {
            tag = this.profile.uuid + "_" + val[1];
            if (MillConfigValues.LogQuest >= 3) {
                MillLog.debug(this, "Applying tag: " + val[0] + "/" + tag);
            }
            if (this.villagers.get((Object)val[0]).getVillagerRecord((World)this.world).questTags.contains(tag)) continue;
            this.villagers.get((Object)val[0]).getVillagerRecord((World)this.world).questTags.add(tag);
            this.villagers.get(val[0]).getVillagerRecord(this.world).getTownHall().requestSave("quest tag");
            if (MillConfigValues.LogQuest < 2) continue;
            MillLog.minor(this, "Setting tag: " + tag + " on villager: " + val[0] + " (" + this.villagers.get(val[0]).getVillagerRecord(this.world).getName() + ") Now present: " + this.villagers.get((Object)val[0]).getVillagerRecord((World)this.world).questTags.size());
        }
        for (String[] val : clear) {
            tag = this.profile.uuid + "_" + val[1];
            if (MillConfigValues.LogQuest >= 3) {
                MillLog.debug(this, "Clearing tag: " + val[0] + "/" + tag);
            }
            this.villagers.get((Object)val[0]).getVillagerRecord((World)this.world).questTags.remove(tag);
            this.villagers.get(val[0]).getVillagerRecord(this.world).getTownHall().requestSave("quest tag");
            if (MillConfigValues.LogQuest < 2) continue;
            MillLog.minor(this, "Clearing tag: " + tag + " on villager: " + val[0] + " (" + this.villagers.get(val[0]).getVillagerRecord(this.world).getName() + ")");
        }
    }

    public boolean checkStatus(World world) {
        if (this.currentStepStart + (long)(this.getCurrentStep().duration * 1000) <= world.func_72820_D()) {
            for (QuestInstanceVillager qiv : this.villagers.values()) {
                if (qiv.getVillagerRecord(world) == null) {
                    MillLog.temp(this, "Dropping quest as villager " + qiv + " does not have a record.");
                    this.destroyQuest();
                    continue;
                }
                MillVillager villager = qiv.getVillager(world);
                if (villager != null && villager.getHouse() != null && villager.getTownHall() != null) continue;
                MillLog.temp(this, "Dropping quest as villager " + qiv + " is null or no longer has a home.");
                this.destroyQuest();
            }
            MillVillager cv = this.getCurrentVillager().getVillager(world);
            if (cv != null && this.getCurrentStep().penaltyReputation > 0) {
                this.profile.adjustReputation(cv.getTownHall(), -this.getCurrentStep().penaltyReputation);
            }
            this.applyTags(this.getCurrentStep().setVillagerTagsFailure, this.getCurrentStep().clearTagsFailure);
            this.applyGlobalTags(this.getCurrentStep().setGlobalTagsFailure, this.getCurrentStep().clearGlobalTagsFailure);
            this.applyPlayerTags(this.getCurrentStep().setPlayerTagsFailure, this.getCurrentStep().clearPlayerTagsFailure);
            if (this.getCurrentStep().getDescriptionTimeUp() != null) {
                ServerSender.sendChat(this.profile.getPlayer(), TextFormatting.RED, this.getDescriptionTimeUp(this.profile) + " (" + LanguageUtilities.string("quest.reputationlost") + ": " + this.getCurrentStep().penaltyReputation + ")");
            }
            this.destroyQuest();
            return true;
        }
        return false;
    }

    public String completeStep(EntityPlayer player, MillVillager villager) {
        String reward = "";
        for (InvItem item : this.getCurrentStep().requiredGood.keySet()) {
            if (item.special != 0) continue;
            villager.addToInv(item.getItem(), item.meta, (int)this.getCurrentStep().requiredGood.get(item));
            WorldUtilities.getItemsFromChest((IInventory)player.field_71071_by, item.getItem(), item.meta, (int)this.getCurrentStep().requiredGood.get(item));
        }
        for (InvItem item : this.getCurrentStep().rewardGoods.keySet()) {
            EntityItem entItem;
            int nbLeft = this.getCurrentStep().rewardGoods.get(item) - MillCommonUtilities.putItemsInChest((IInventory)player.field_71071_by, item.getItem(), item.meta, (int)this.getCurrentStep().rewardGoods.get(item));
            if (!this.world.field_72995_K && nbLeft > 0 && (entItem = WorldUtilities.spawnItem(this.world, villager.getPos(), new ItemStack(item.getItem(), nbLeft, item.meta), 0.0f)).func_92059_d().func_77973_b() instanceof InvItem.IItemInitialEnchantmens) {
                ((InvItem.IItemInitialEnchantmens)entItem.func_92059_d().func_77973_b()).applyEnchantments(entItem.func_92059_d());
            }
            reward = reward + " " + this.getCurrentStep().rewardGoods.get(item) + " " + item.getName();
        }
        if (this.getCurrentStep().rewardMoney > 0) {
            MillCommonUtilities.changeMoney((IInventory)player.field_71071_by, this.getCurrentStep().rewardMoney, player);
            reward = reward + " " + this.getCurrentStep().rewardMoney + " deniers";
        }
        if (this.getCurrentStep().rewardReputation > 0) {
            this.mw.getProfile(player).adjustReputation(villager.getTownHall(), this.getCurrentStep().rewardReputation);
            reward = reward + " " + this.getCurrentStep().rewardReputation + " reputation";
            int experience = this.getCurrentStep().rewardReputation / 32;
            if (experience > 16) {
                experience = 16;
            }
            if (experience > 0) {
                reward = reward + " " + experience + " experience";
                WorldUtilities.spawnExp(this.world, villager.getPos().getRelative(0.0, 2.0, 0.0), experience);
            }
        }
        this.mw.getProfile(player).adjustLanguage(villager.getCulture().key, 50);
        if (!this.world.field_72995_K) {
            this.applyTags(this.getCurrentStep().setVillagerTagsSuccess, this.getCurrentStep().clearTagsSuccess);
            this.applyGlobalTags(this.getCurrentStep().setGlobalTagsSuccess, this.getCurrentStep().clearGlobalTagsSuccess);
            this.applyPlayerTags(this.getCurrentStep().setPlayerTagsSuccess, this.getCurrentStep().clearPlayerTagsSuccess);
            this.applyActionData(this.getCurrentStep().setActionDataSuccess);
            this.applyRelationChanges(this.getCurrentStep().relationChanges);
            for (String s : this.getCurrentStep().bedrockbuildings) {
                String culture = s.split(",")[0];
                String village = s.split(",")[1];
                VillageType vt = Culture.getCultureByName(culture).getLoneBuildingType(village);
                try {
                    WorldGenVillage.generateBedrockLoneBuilding(new Point((Entity)player), this.world, vt, MillCommonUtilities.random, 50, 120, player);
                }
                catch (MillLog.MillenaireException e) {
                    MillLog.printException(e);
                }
            }
        }
        String res = this.getDescriptionSuccess(this.mw.getProfile(player));
        if (reward.length() > 0) {
            res = res + "<ret><ret>" + LanguageUtilities.string("quest.obtained") + ":" + reward;
        }
        ++this.currentStep;
        if (this.currentStep >= this.quest.steps.size()) {
            MillAdvancements.THE_QUEST.grant(player);
            if (this.mw.getProfile(player).isWorldQuestFinished("sadhu")) {
                MillAdvancements.WQ_INDIAN.grant(player);
            }
            if (this.mw.getProfile(player).isWorldQuestFinished("alchemist")) {
                MillAdvancements.WQ_NORMAN.grant(player);
            }
            if (this.mw.getProfile(player).isWorldQuestFinished("fallenking")) {
                MillAdvancements.WQ_MAYAN.grant(player);
            }
            this.destroyQuest();
        } else {
            this.currentStepStart = villager.field_70170_p.func_72820_D();
            this.profile.sendQuestInstancePacket(this);
            this.profile.saveQuestInstances();
        }
        return res;
    }

    public void destroyQuest() {
        this.profile.questInstances.remove(this);
        for (QuestInstanceVillager qiv : this.villagers.values()) {
            this.profile.villagersInQuests.remove(qiv.id);
        }
        this.profile.saveQuestInstances();
        this.profile.sendQuestInstanceDestroyPacket(this.uniqueid);
    }

    public QuestStep getCurrentStep() {
        return this.quest.steps.get(this.currentStep);
    }

    public QuestInstanceVillager getCurrentVillager() {
        return this.villagers.get(this.getCurrentStep().villager);
    }

    public String getDescription(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getDescription());
    }

    public String getDescriptionRefuse(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getDescriptionRefuse());
    }

    public String getDescriptionSuccess(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getDescriptionSuccess());
    }

    public String getDescriptionTimeUp(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getDescriptionTimeUp());
    }

    public String getLabel(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getLabel());
    }

    public String getListing(UserProfile profile) {
        return this.handleString(profile, this.getCurrentStep().getListing());
    }

    public QuestStep getNextStep() {
        if (this.currentStep + 1 < this.quest.steps.size()) {
            return this.quest.steps.get(this.currentStep + 1);
        }
        return null;
    }

    public QuestStep getPreviousStep() {
        if (this.currentStep > 0) {
            return this.quest.steps.get(this.currentStep - 1);
        }
        return null;
    }

    private String handleString(UserProfile profile, String s) {
        if (s == null) {
            return null;
        }
        Building giverTH = this.villagers.get(this.getCurrentStep().villager).getTownHall(this.world);
        if (giverTH == null) {
            return s;
        }
        for (String key : this.villagers.keySet()) {
            QuestInstanceVillager qiv = this.villagers.get(key);
            Building th = qiv.getTownHall(this.world);
            if (th == null) continue;
            s = s.replaceAll("\\$" + key + "_villagename\\$", th.getVillageQualifiedName());
            s = s.replaceAll("\\$" + key + "_direction\\$", giverTH.getPos().directionTo(th.getPos()));
            s = s.replaceAll("\\$" + key + "_tothedirection\\$", giverTH.getPos().directionTo(th.getPos(), true));
            s = s.replaceAll("\\$" + key + "_directionshort\\$", giverTH.getPos().directionToShort(th.getPos()));
            s = s.replaceAll("\\$" + key + "_distance\\$", giverTH.getPos().approximateDistanceLongString(th.getPos()));
            s = s.replaceAll("\\$" + key + "_distanceshort\\$", giverTH.getPos().approximateDistanceShortString(th.getPos()));
            VillagerRecord villager = qiv.getVillagerRecord(this.world);
            if (villager != null) {
                s = s.replaceAll("\\$" + key + "_villagername\\$", villager.getName());
                s = s.replaceAll("\\$" + key + "_villagerrole\\$", villager.getGameOccupation());
            }
            for (String key2 : this.villagers.keySet()) {
                QuestInstanceVillager qiv2 = this.villagers.get(key2);
                Building th2 = qiv2.getTownHall(this.world);
                if (th2 != null) {
                    s = s.replaceAll("\\$" + key + "_" + key2 + "_direction\\$", LanguageUtilities.string(th.getPos().directionTo(th2.getPos())));
                    s = s.replaceAll("\\$" + key + "_" + key2 + "_directionshort\\$", th.getPos().directionToShort(th2.getPos()));
                    s = s.replaceAll("\\$" + key + "_" + key2 + "_distance\\$", th.getPos().approximateDistanceLongString(th2.getPos()));
                    s = s.replaceAll("\\$" + key + "_" + key2 + "_distanceshort\\$", th.getPos().approximateDistanceShortString(th2.getPos()));
                    continue;
                }
                s = s.replaceAll("\\$" + key + "_" + key2 + "_direction\\$", "");
                s = s.replaceAll("\\$" + key + "_" + key2 + "_directionshort\\$", "");
                s = s.replaceAll("\\$" + key + "_" + key2 + "_distance\\$", "");
                s = s.replaceAll("\\$" + key + "_" + key2 + "_distanceshort\\$", "");
            }
        }
        s = s.replaceAll("\\$name", profile.playerName);
        return s;
    }

    public String refuseQuest(EntityPlayer player, MillVillager villager) {
        String replost = "";
        MillVillager cv = this.getCurrentVillager().getVillager(this.world);
        if (cv != null && this.getCurrentStep().penaltyReputation > 0) {
            this.mw.getProfile(player).adjustReputation(cv.getTownHall(), -this.getCurrentStep().penaltyReputation);
            replost = " (Reputation lost: " + this.getCurrentStep().penaltyReputation + ")";
        }
        this.applyTags(this.getCurrentStep().setVillagerTagsFailure, this.getCurrentStep().clearTagsFailure);
        this.applyPlayerTags(this.getCurrentStep().setPlayerTagsFailure, this.getCurrentStep().clearPlayerTagsFailure);
        this.applyGlobalTags(this.getCurrentStep().setGlobalTagsFailure, this.getCurrentStep().clearGlobalTagsFailure);
        String s = this.getDescriptionRefuse(this.mw.getProfile(player)) + replost;
        this.destroyQuest();
        return s;
    }

    public String toString() {
        return "QI:" + this.quest.key;
    }

    public String writeToString() {
        String s = "quest:" + this.quest.key + ";step:" + this.currentStep + ";startTime:" + this.startTime + ";currentStepStartTime:" + this.currentStepStart;
        for (String key : this.villagers.keySet()) {
            QuestInstanceVillager qiv = this.villagers.get(key);
            s = s + ";villager:" + key + "," + qiv.id + "," + qiv.townHall;
        }
        return s;
    }
}

