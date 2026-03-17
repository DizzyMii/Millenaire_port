package org.dizzymii.millenaire2.village.buildingmanagers;

import org.dizzymii.millenaire2.village.Building;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Manages marvel (wonder) building construction, donations, and completion effects.
 * Ported from org.millenaire.common.village.buildingmanagers.MarvelManager (Forge 1.12.2).
 */
public class MarvelManager {

    private static final float DONATION_RATIO = 0.5f;
    public static final String NORMAN_MARVEL_COMPLETION_TAG = "normanmarvel_helper";

    private final Building townHall;
    private CopyOnWriteArrayList<String> donationList = new CopyOnWriteArrayList<>();
    private boolean nightActionDone = false;
    private boolean marvelComplete = false;
    private boolean dawnActionDone;

    public MarvelManager(Building townHall) {
        this.townHall = townHall;
    }

    public boolean isMarvelComplete() {
        return marvelComplete;
    }

    // TODO: Implement donation tracking, night actions, marvel completion effects, NBT save/load, packet sync
}
