package org.dizzymii.sblpoc.ai.world;

import net.minecraft.world.level.Level;

/**
 * Utility for time-of-day queries. Minecraft day is 24000 ticks:
 * 0-12000 = day, 12000-13000 = dusk, 13000-23000 = night, 23000-24000 = dawn
 */
public class GameClock {

    private static final long DAY_LENGTH = 24000L;
    private static final long DUSK_START = 12000L;
    private static final long NIGHT_START = 13000L;
    private static final long DAWN_START = 23000L;

    private final Level level;

    public GameClock(Level level) {
        this.level = level;
    }

    public long getTimeOfDay() {
        return level.getDayTime() % DAY_LENGTH;
    }

    public boolean isDaytime() {
        long time = getTimeOfDay();
        return time < DUSK_START || time >= DAWN_START;
    }

    public boolean isNight() {
        long time = getTimeOfDay();
        return time >= NIGHT_START && time < DAWN_START;
    }

    public boolean isNightfallApproaching() {
        long time = getTimeOfDay();
        return time >= 10000L && time < NIGHT_START;
    }

    public long ticksUntilNight() {
        long time = getTimeOfDay();
        if (time >= NIGHT_START && time < DAWN_START) return 0;
        if (time < NIGHT_START) return NIGHT_START - time;
        return DAY_LENGTH - time + NIGHT_START;
    }

    public int getDayNumber() {
        return (int) (level.getDayTime() / DAY_LENGTH);
    }

    public long getGameTime() {
        return level.getGameTime();
    }
}
