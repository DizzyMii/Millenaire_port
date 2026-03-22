package org.dizzymii.millenaire2.buildingplan;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.util.MillCommonUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads building plan metadata from text config files.
 * Ported from org.millenaire.common.buildingplan.BuildingMetadataLoader (Forge 1.12.2).
 */
public final class BuildingMetadataLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    private BuildingMetadataLoader() {}

    public static Map<String, String> loadMetadata(File metaFile) {
        Map<String, String> metadata = new HashMap<>();
        try (BufferedReader reader = MillCommonUtilities.getReader(metaFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq > 0) {
                    metadata.put(line.substring(0, eq).trim(), line.substring(eq + 1).trim());
                }
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load building metadata: " + metaFile.getAbsolutePath());
        }
        return metadata;
    }

    /**
     * Apply loaded metadata key-value pairs to a BuildingCustomPlan.
     */
    public static void applyMetadata(BuildingCustomPlan plan, Map<String, String> meta) {
        for (Map.Entry<String, String> entry : meta.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue();
            try {
                switch (key) {
                    case "name" -> plan.nativeName = value;
                    case "gamename" -> plan.gameNameKey = value;
                    case "shop" -> plan.shop = value;
                    case "radius" -> plan.radius = Integer.parseInt(value);
                    case "heightradius" -> plan.heightRadius = Integer.parseInt(value);
                    case "prioritymovein" -> plan.priorityMoveIn = Integer.parseInt(value);
                    case "male" -> {
                        for (String s : value.split(",")) {
                            String t = s.trim();
                            if (!t.isEmpty()) plan.maleResident.add(t);
                        }
                    }
                    case "female" -> {
                        for (String s : value.split(",")) {
                            String t = s.trim();
                            if (!t.isEmpty()) plan.femaleResident.add(t);
                        }
                    }
                    case "visitor" -> {
                        for (String s : value.split(",")) {
                            String t = s.trim();
                            if (!t.isEmpty()) plan.visitors.add(t);
                        }
                    }
                    case "tag" -> {
                        for (String s : value.split(",")) {
                            String t = s.trim();
                            if (!t.isEmpty()) plan.tags.add(t);
                        }
                    }
                    default -> {
                        // Store in generic names map for culture-specific lookups
                        if (key.startsWith("name_")) {
                            plan.names.put(key.substring(5), value);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                LOGGER.warn("BuildingMetadataLoader: bad number for " + key + "=" + value);
            }
        }
    }
}
