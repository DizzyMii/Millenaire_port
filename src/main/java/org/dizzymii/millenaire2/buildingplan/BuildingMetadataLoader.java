package org.dizzymii.millenaire2.buildingplan;

import org.dizzymii.millenaire2.util.MillCommonUtilities;
import org.dizzymii.millenaire2.util.MillLog;

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
            MillLog.error(null, "Failed to load building metadata: " + metaFile.getAbsolutePath());
        }
        return metadata;
    }

    // TODO: applyMetadata(BuildingCustomPlan, Map), parseResources, parseTags
}
