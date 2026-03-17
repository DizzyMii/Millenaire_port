package org.dizzymii.millenaire2.village.buildingmanagers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.network.chat.Component;
import org.dizzymii.millenaire2.util.Point;
import org.dizzymii.millenaire2.village.Building;
import java.util.List;

/**
 * Manages village information panels (signs) displaying building status, villager info, etc.
 * Ported from org.millenaire.common.village.buildingmanagers.PanelManager (Forge 1.12.2).
 */
public class PanelManager {

    public static final int MAX_LINE_NB = 4;
    private static final long UPDATE_INTERVAL = 6000L;

    public long lastSignUpdate = 0L;
    private final Building building;
    private final Building townHall;

    public PanelManager(Building building, Building townHall) {
        this.building = building;
        this.townHall = townHall;
    }

    /**
     * Update all sign panels for this building if enough time has passed.
     */
    public void updatePanels(ServerLevel level) {
        long gameTime = level.getGameTime();
        if (gameTime - lastSignUpdate < UPDATE_INTERVAL) return;
        lastSignUpdate = gameTime;

        if (building.location == null || building.location.pos == null) return;

        // Generate content for the building's sign
        List<String> lines = PanelContentGenerator.generateBuildingPanel(building);

        // Find and update sign block entities near the building
        updateSignsNearBuilding(level, building.location.pos, lines);
    }

    /**
     * Find sign block entities within the building's area and write panel content.
     */
    private void updateSignsNearBuilding(ServerLevel level, Point buildingPos, List<String> lines) {
        int searchRadius = 3;
        BlockPos center = buildingPos.toBlockPos();

        for (int dx = -searchRadius; dx <= searchRadius; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -searchRadius; dz <= searchRadius; dz++) {
                    BlockPos pos = center.offset(dx, dy, dz);
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof SignBlockEntity sign) {
                        writeToSign(sign, lines);
                    }
                }
            }
        }
    }

    /**
     * Write text lines to a sign block entity (front side only).
     */
    private void writeToSign(SignBlockEntity sign, List<String> lines) {
        SignText text = sign.getFrontText();
        for (int i = 0; i < MAX_LINE_NB && i < lines.size(); i++) {
            text = text.setMessage(i, Component.literal(lines.get(i)));
        }
        // Clear remaining lines
        for (int i = lines.size(); i < MAX_LINE_NB; i++) {
            text = text.setMessage(i, Component.empty());
        }
        sign.setText(text, true);
        sign.setChanged();
    }

    /**
     * Generate the town hall overview panel (village name, population, etc.).
     */
    public List<String> getTownHallPanelContent() {
        return PanelContentGenerator.generateTownHallPanel(townHall);
    }
}
