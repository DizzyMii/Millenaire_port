package org.dizzymii.millenaire2.entity;

/**
 * Animation states for Millénaire villagers.
 * Synched to the client via EntityDataAccessor and used in model setupAnim.
 */
public enum VillagerAnimState {
    IDLE(0),
    WALKING(1),
    WORKING(2),
    SLEEPING(3),
    COMBAT_MELEE(4),
    COMBAT_BOW(5),
    SITTING(6),
    EATING(7);

    private final int id;

    VillagerAnimState(int id) {
        this.id = id;
    }

    public int getId() { return id; }

    public static VillagerAnimState fromId(int id) {
        for (VillagerAnimState state : values()) {
            if (state.id == id) return state;
        }
        return IDLE;
    }
}
