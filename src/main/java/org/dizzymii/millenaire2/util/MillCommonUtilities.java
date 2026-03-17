package org.dizzymii.millenaire2.util;

import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.loading.FMLPaths;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

/**
 * General-purpose utilities used across the mod.
 * Ported from org.millenaire.common.utilities.MillCommonUtilities.
 */
public class MillCommonUtilities {

    private static final Random random = new Random();

    /**
     * Returns the root Millénaire content directory (game_dir/millenaire2/).
     */
    public static File getMillenaireDir() {
        File dir = FMLPaths.GAMEDIR.get().resolve("millenaire2").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Returns the Millénaire content directory for data files (cultures, goals, etc.).
     */
    public static File getMillenaireContentDir() {
        File dir = new File(getMillenaireDir(), "millenaire");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Returns the custom content directory for user-added cultures/buildings.
     */
    public static File getMillenaireCustomDir() {
        File dir = new File(getMillenaireDir(), "custom");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Returns the custom content directory for user-provided data overrides.
     */
    public static File getMillenaireCustomContentDir() {
        File dir = new File(getMillenaireCustomDir(), "millenaire");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * Returns the save directory for Millénaire world data within a specific world.
     */
    public static File getMillenaireSaveDir(MinecraftServer server) {
        Path worldDir = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT);
        File dir = worldDir.resolve("millenaire2").toFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static int randomInt(int bound) {
        if (bound <= 0) return 0;
        return random.nextInt(bound);
    }

    public static boolean randomChance(int oneIn) {
        return random.nextInt(oneIn) == 0;
    }

    public static BufferedReader getReader(File file) throws IOException {
        return Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8);
    }

    public static BufferedWriter getWriter(File file) throws IOException {
        return Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
    }

    /**
     * Clamps value between min and max (inclusive).
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Safe string-to-int parsing with default value.
     */
    public static int safeParseInt(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safe string-to-double parsing with default value.
     */
    public static double safeParseDouble(String s, double defaultValue) {
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safe string-to-boolean parsing.
     */
    public static boolean safeParseBoolean(String s) {
        return "true".equalsIgnoreCase(s.trim()) || "1".equals(s.trim());
    }
}
