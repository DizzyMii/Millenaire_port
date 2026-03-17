package org.dizzymii.millenaire2.village.buildingmanagers;

import org.dizzymii.millenaire2.village.Building;
import org.dizzymii.millenaire2.village.VillagerRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates text content for village information panels / book pages.
 * Ported from org.millenaire.common.village.buildingmanagers.PanelContentGenerator (Forge 1.12.2).
 */
public class PanelContentGenerator {

    /**
     * Generate sign text lines for a standard building panel.
     */
    public static List<String> generateBuildingPanel(Building building) {
        List<String> lines = new ArrayList<>();
        String name = building.getName();
        lines.add(name != null ? name : "Building");

        // Show resident count
        int residents = 0;
        int alive = 0;
        for (VillagerRecord vr : building.getVillagerRecords()) {
            residents++;
            if (!vr.killed) alive++;
        }
        if (residents > 0) {
            lines.add("Residents: " + alive + "/" + residents);
        }

        // Show construction status
        if (building.isUnderConstruction()) {
            int pct = (int) (building.currentConstruction.getCompletionPercent() * 100);
            lines.add("Building: " + pct + "%");
        }

        // Under attack warning
        if (building.underAttack) {
            lines.add("UNDER ATTACK!");
        }

        return lines;
    }

    /**
     * Generate sign text lines for the town hall overview panel.
     */
    public static List<String> generateTownHallPanel(Building townHall) {
        List<String> lines = new ArrayList<>();
        String villageName = townHall.getName();
        lines.add(villageName != null ? villageName : "Village");

        // Population summary
        int totalAlive = 0;
        int totalKilled = 0;
        for (VillagerRecord vr : townHall.getVillagerRecords()) {
            if (vr.killed) totalKilled++;
            else totalAlive++;
        }
        lines.add("Pop: " + totalAlive);
        if (totalKilled > 0) {
            lines.add("Fallen: " + totalKilled);
        }

        // Culture
        if (townHall.cultureKey != null) {
            lines.add(townHall.cultureKey);
        }

        return lines;
    }

    /**
     * Generate a villager info text block for a single VillagerRecord.
     */
    public static List<String> generateVillagerInfo(VillagerRecord vr) {
        List<String> lines = new ArrayList<>();
        String displayName = (vr.firstName != null ? vr.firstName : "") + " " + (vr.familyName != null ? vr.familyName : "");
        lines.add(displayName.trim());

        if (vr.type != null) {
            lines.add("Role: " + vr.type);
        }
        if (vr.killed) {
            lines.add("(Deceased)");
        } else if (vr.awayraiding) {
            lines.add("(Raiding)");
        } else if (vr.awayhired) {
            lines.add("(Hired)");
        }

        return lines;
    }

    /**
     * Generate a compact trade goods summary for a building.
     */
    public static List<String> generateTradePanel(Building building) {
        List<String> lines = new ArrayList<>();
        lines.add("Trade Goods:");

        building.resManager.resources.forEach((item, count) -> {
            if (count > 0) {
                lines.add("  " + item.key + ": " + count);
            }
        });

        if (lines.size() == 1) {
            lines.add("  (none)");
        }

        return lines;
    }
}
