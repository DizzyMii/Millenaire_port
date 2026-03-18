package org.dizzymii.millenaire2.util;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * Extracts bundled culture data (building .txt/.png files, quests, goals, etc.)
 * from the mod JAR into the game directory so that the file-based loaders
 * ({@link VirtualDir}, {@link org.dizzymii.millenaire2.culture.Culture}, etc.)
 * can find them at runtime.
 *
 * Files are only copied when the destination does not already exist,
 * preserving any user customisations.
 */
public class DataExtractor {

    private static final String MOD_ID = "millenaire2";
    private static final String JAR_DATA_ROOT = "data/" + MOD_ID + "/millenaire";

    /**
     * Ensure all bundled data files exist on disk under
     * {@code game_dir/millenaire2/millenaire/}.
     */
    public static void extractIfNeeded() {
        Path destRoot = MillCommonUtilities.getMillenaireContentDir().toPath();

        try {
            IModFile modFile = ModList.get().getModFileById(MOD_ID).getFile();
            Path jarRoot = modFile.getSecureJar().getRootPath();
            Path jarDataDir = jarRoot.resolve(JAR_DATA_ROOT);

            if (!Files.isDirectory(jarDataDir)) {
                MillLog.warn("DataExtractor",
                        "Bundled data directory not found in mod resources: " + JAR_DATA_ROOT);
                return;
            }

            int[] counts = {0, 0}; // [copied, skipped]

            try (Stream<Path> walker = Files.walk(jarDataDir)) {
                walker.forEach(src -> {
                    try {
                        Path relative = jarDataDir.relativize(src);
                        Path dest = destRoot.resolve(relative.toString());

                        if (Files.isDirectory(src)) {
                            Files.createDirectories(dest);
                        } else if (!Files.exists(dest)) {
                            Files.createDirectories(dest.getParent());
                            try (InputStream in = Files.newInputStream(src)) {
                                Files.copy(in, dest);
                            }
                            counts[0]++;
                        } else {
                            counts[1]++;
                        }
                    } catch (IOException e) {
                        MillLog.error("DataExtractor",
                                "Failed to extract: " + src, e);
                    }
                });
            }

            MillLog.major("DataExtractor",
                    "Extraction complete: " + counts[0] + " files copied, "
                            + counts[1] + " already present in " + destRoot);

        } catch (Exception e) {
            MillLog.error("DataExtractor",
                    "Failed to extract bundled data files", e);
        }
    }
}
