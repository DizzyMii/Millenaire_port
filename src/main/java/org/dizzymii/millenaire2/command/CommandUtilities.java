package org.dizzymii.millenaire2.command;

import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.world.MillWorldData;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

/**
 * Command helper utilities.
 * Ported from org.millenaire.common.commands.CommandUtilities (Forge 1.12.2).
 */
public final class CommandUtilities {

    private CommandUtilities() {}

    public static List<Building> getMatchingVillages(MillWorldData worldData, String param) {
        param = normalizeString(param);
        List<Building> villages = new ArrayList<>();
        for (Building b : worldData.allBuildings()) {
            if (b.villageName != null && normalizeString(b.villageName).contains(param)) {
                villages.add(b);
            }
        }
        return villages;
    }

    public static String normalizeString(String string) {
        string = string.replaceAll(" ", "_").toLowerCase();
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return string;
    }
}
