package org.dizzymii.millenaire2.village.buildingmanagers;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.dizzymii.millenaire2.item.InvItem;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages marvel (wonder) building construction, donations, and completion effects.
 * Ported from org.millenaire.common.village.buildingmanagers.MarvelManager (Forge 1.12.2).
 */
public class MarvelManager {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final float DONATION_RATIO = 0.5f;
    public static final String NORMAN_MARVEL_COMPLETION_TAG = "normanmarvel_helper";
    private static final int COMPLETION_FIREWORK_COUNT = 10;

    private final Building townHall;
    private final CopyOnWriteArrayList<String> donationList = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Integer> donationAmounts = new ConcurrentHashMap<>();
    private boolean nightActionDone = false;
    private boolean marvelComplete = false;
    private boolean dawnActionDone = false;
    private int totalDonationsReceived = 0;
    private int donationsRequired = 1000;

    public MarvelManager(Building townHall) {
        this.townHall = townHall;
    }

    public boolean isMarvelComplete() {
        return marvelComplete;
    }

    public float getDonationProgress() {
        if (donationsRequired <= 0) return 1.0f;
        return Math.min(1.0f, (float) totalDonationsReceived / donationsRequired);
    }

    /**
     * Record a donation from a player towards the marvel construction.
     */
    public void recordDonation(UUID playerUUID, String playerName, InvItem item, int count) {
        int value = count;
        totalDonationsReceived += value;

        String key = playerUUID.toString();
        donationAmounts.merge(key, value, Integer::sum);
        if (!donationList.contains(playerName)) {
            donationList.add(playerName);
        }

        LOGGER.debug("Donation from " + playerName + ": " + item.key + " x" + count
                + " (total: " + totalDonationsReceived + "/" + donationsRequired + ")");

        // Check completion
        if (totalDonationsReceived >= donationsRequired && !marvelComplete) {
            completeMarvel();
        }
    }

    /**
     * Called on completion of the marvel. Triggers effects and sets flag.
     */
    private void completeMarvel() {
        marvelComplete = true;
        LOGGER.debug("Marvel complete for village at " + townHall.getPos());

        // Fire completion effects (fireworks near town hall)
        if (townHall.getLevel() instanceof ServerLevel level) {
            Point pos = townHall.getPos();
            if (pos != null) {
                spawnCompletionEffects(level, pos);
                notifyNearbyPlayers(level, pos);
            }
        }
    }

    /**
     * Spawn firework-like effects at the marvel location.
     */
    private void spawnCompletionEffects(ServerLevel level, Point pos) {
        for (int i = 0; i < COMPLETION_FIREWORK_COUNT; i++) {
            double x = pos.x + level.random.nextGaussian() * 5;
            double y = pos.y + 10 + level.random.nextDouble() * 10;
            double z = pos.z + level.random.nextGaussian() * 5;
            level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.FIREWORK,
                    x, y, z, 20,
                    0.5, 0.5, 0.5, 0.1
            );
        }
    }

    /**
     * Send a chat notification to nearby players about marvel completion.
     */
    private void notifyNearbyPlayers(ServerLevel level, Point pos) {
        AABB area = AABB.ofSize(new Vec3(pos.x, pos.y, pos.z), 256, 128, 256);
        for (ServerPlayer player : level.getPlayers(p -> area.contains(p.position()))) {
            player.sendSystemMessage(
                    net.minecraft.network.chat.Component.literal("The village marvel has been completed!")
            );
        }
    }

    /**
     * Periodic update called during building tick. Handles dawn/night actions.
     */
    public void update(ServerLevel level) {
        if (marvelComplete) return;

        long dayTime = level.getDayTime() % 24000L;

        // Dawn action: check if any new donations arrived overnight
        if (dayTime >= 0 && dayTime < 1000 && !dawnActionDone) {
            dawnActionDone = true;
            nightActionDone = false;
        }

        // Night action: log daily progress
        if (dayTime >= 13000 && dayTime < 14000 && !nightActionDone) {
            nightActionDone = true;
            dawnActionDone = false;
        }
    }

    /**
     * Get the list of player names who have donated.
     */
    public CopyOnWriteArrayList<String> getDonationList() {
        return donationList;
    }

    public int getTotalDonations() { return totalDonationsReceived; }
    public int getDonationsRequired() { return donationsRequired; }
    public void setDonationsRequired(int amount) { this.donationsRequired = amount; }

    // ========== NBT persistence ==========

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("complete", marvelComplete);
        tag.putInt("totalDonations", totalDonationsReceived);
        tag.putInt("donationsRequired", donationsRequired);

        // Save donor list
        ListTag donors = new ListTag();
        for (String name : donationList) {
            donors.add(StringTag.valueOf(name));
        }
        tag.put("donors", donors);

        // Save donation amounts
        CompoundTag amounts = new CompoundTag();
        for (Map.Entry<String, Integer> entry : donationAmounts.entrySet()) {
            amounts.putInt(entry.getKey(), entry.getValue());
        }
        tag.put("donationAmounts", amounts);

        return tag;
    }

    public void load(CompoundTag tag) {
        marvelComplete = tag.getBoolean("complete");
        totalDonationsReceived = tag.getInt("totalDonations");
        donationsRequired = tag.getInt("donationsRequired");

        donationList.clear();
        if (tag.contains("donors", Tag.TAG_LIST)) {
            ListTag donors = tag.getList("donors", Tag.TAG_STRING);
            for (int i = 0; i < donors.size(); i++) {
                donationList.add(donors.getString(i));
            }
        }

        donationAmounts.clear();
        if (tag.contains("donationAmounts", Tag.TAG_COMPOUND)) {
            CompoundTag amounts = tag.getCompound("donationAmounts");
            for (String key : amounts.getAllKeys()) {
                donationAmounts.put(key, amounts.getInt(key));
            }
        }
    }
}
