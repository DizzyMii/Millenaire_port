package org.dizzymii.sblpoc.ai.goap;

import net.minecraft.world.item.Item;
import org.dizzymii.sblpoc.ai.world.BlockCategory;
import org.dizzymii.sblpoc.ai.world.InventoryModel;

import java.util.*;

/**
 * Compact snapshot of the NPC's world knowledge used for GOAP planning.
 * Immutable after construction — create a new one for each planning cycle.
 */
public class WorldState {

    private final Map<Item, Integer> inventory;
    private final Set<String> flags;
    private final Set<BlockCategory> nearbyStations;
    private final InventoryModel.ToolTier toolTier;
    private final ArmorTier armorTier;
    private final float healthPercent;
    private final int foodSupply;
    private final int dayNumber;
    private final boolean isNight;
    private final String dimension;

    private WorldState(Builder builder) {
        this.inventory = Collections.unmodifiableMap(new HashMap<>(builder.inventory));
        this.flags = Collections.unmodifiableSet(new HashSet<>(builder.flags));
        this.nearbyStations = Collections.unmodifiableSet(EnumSet.copyOf(
                builder.nearbyStations.isEmpty() ? EnumSet.noneOf(BlockCategory.class) : builder.nearbyStations));
        this.toolTier = builder.toolTier;
        this.armorTier = builder.armorTier;
        this.healthPercent = builder.healthPercent;
        this.foodSupply = builder.foodSupply;
        this.dayNumber = builder.dayNumber;
        this.isNight = builder.isNight;
        this.dimension = builder.dimension;
    }

    // ========== Queries used by GOAP preconditions/effects ==========

    public int getItemCount(Item item) {
        return inventory.getOrDefault(item, 0);
    }

    public boolean hasItem(Item item, int count) {
        return getItemCount(item) >= count;
    }

    public boolean hasFlag(String flag) {
        return flags.contains(flag);
    }

    public boolean hasNearbyStation(BlockCategory station) {
        return nearbyStations.contains(station);
    }

    public InventoryModel.ToolTier getToolTier() { return toolTier; }
    public ArmorTier getArmorTier() { return armorTier; }
    public float getHealthPercent() { return healthPercent; }
    public int getFoodSupply() { return foodSupply; }
    public int getDayNumber() { return dayNumber; }
    public boolean isNight() { return isNight; }
    public String getDimension() { return dimension; }
    public Map<Item, Integer> getInventory() { return inventory; }
    public Set<String> getFlags() { return flags; }

    // ========== Mutable copy for GOAP forward-simulation ==========

    /**
     * Create a mutable copy of this state for the planner to simulate effects.
     */
    public MutableWorldState toMutable() {
        return new MutableWorldState(this);
    }

    // ========== Builder ==========

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Item, Integer> inventory = new HashMap<>();
        private final Set<String> flags = new HashSet<>();
        private final Set<BlockCategory> nearbyStations = EnumSet.noneOf(BlockCategory.class);
        private InventoryModel.ToolTier toolTier = InventoryModel.ToolTier.NONE;
        private ArmorTier armorTier = ArmorTier.NONE;
        private float healthPercent = 1.0f;
        private int foodSupply = 0;
        private int dayNumber = 0;
        private boolean isNight = false;
        private String dimension = "overworld";

        public Builder inventory(Map<Item, Integer> inv) { this.inventory.putAll(inv); return this; }
        public Builder addItem(Item item, int count) { this.inventory.merge(item, count, Integer::sum); return this; }
        public Builder flag(String flag) { this.flags.add(flag); return this; }
        public Builder flags(Set<String> flags) { this.flags.addAll(flags); return this; }
        public Builder nearbyStation(BlockCategory station) { this.nearbyStations.add(station); return this; }
        public Builder toolTier(InventoryModel.ToolTier tier) { this.toolTier = tier; return this; }
        public Builder armorTier(ArmorTier tier) { this.armorTier = tier; return this; }
        public Builder healthPercent(float hp) { this.healthPercent = hp; return this; }
        public Builder foodSupply(int food) { this.foodSupply = food; return this; }
        public Builder dayNumber(int day) { this.dayNumber = day; return this; }
        public Builder isNight(boolean night) { this.isNight = night; return this; }
        public Builder dimension(String dim) { this.dimension = dim; return this; }

        public WorldState build() { return new WorldState(this); }
    }

    // ========== Mutable version for GOAP simulation ==========

    public static class MutableWorldState {
        public final Map<Item, Integer> inventory;
        public final Set<String> flags;
        public final Set<BlockCategory> nearbyStations;
        public InventoryModel.ToolTier toolTier;
        public ArmorTier armorTier;
        public float healthPercent;
        public int foodSupply;
        public int dayNumber;
        public boolean isNight;
        public String dimension;

        MutableWorldState(WorldState source) {
            this.inventory = new HashMap<>(source.inventory);
            this.flags = new HashSet<>(source.flags);
            this.nearbyStations = EnumSet.noneOf(BlockCategory.class);
            this.nearbyStations.addAll(source.nearbyStations);
            this.toolTier = source.toolTier;
            this.armorTier = source.armorTier;
            this.healthPercent = source.healthPercent;
            this.foodSupply = source.foodSupply;
            this.dayNumber = source.dayNumber;
            this.isNight = source.isNight;
            this.dimension = source.dimension;
        }

        public boolean hasItem(Item item, int count) {
            return inventory.getOrDefault(item, 0) >= count;
        }

        public void addItem(Item item, int count) {
            inventory.merge(item, count, Integer::sum);
        }

        public void removeItem(Item item, int count) {
            int current = inventory.getOrDefault(item, 0);
            int remaining = current - count;
            if (remaining <= 0) {
                inventory.remove(item);
            } else {
                inventory.put(item, remaining);
            }
        }

        public boolean hasFlag(String flag) {
            return flags.contains(flag);
        }

        public void setFlag(String flag) {
            flags.add(flag);
        }

        public boolean hasNearbyStation(BlockCategory station) {
            return nearbyStations.contains(station);
        }
    }

    // ========== Enums ==========

    public enum ArmorTier {
        NONE, LEATHER, CHAINMAIL, IRON, DIAMOND, NETHERITE
    }
}
