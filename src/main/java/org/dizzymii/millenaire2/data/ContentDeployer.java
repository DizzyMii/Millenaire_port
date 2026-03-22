package org.dizzymii.millenaire2.data;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.util.MillCommonUtilities;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * Deploys Millénaire data content from the mod JAR/resources to the game directory.
 * On first run (or version mismatch), extracts culture data, goals, quests, etc.
 * to the game directory so the mod's data loading system can read them.
 *
 * Ported from org.millenaire.common.deployer.ContentDeployer.
 */
public class ContentDeployer {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String DATA_PREFIX = "data/millenaire2/millenaire/";

    /**
     * Deploy content files from the mod's resources to the game directory.
     * Checks version.txt to avoid redundant deployments.
     */
    public static void deployContent() {
        File contentDir = MillCommonUtilities.getMillenaireContentDir();

        try {
            boolean needsDeploy = false;
            File versionFile = new File(contentDir, "version.txt");

            if (!contentDir.exists()) {
                needsDeploy = true;
                LOGGER.info("Deploying Millénaire content (directory not found).");
            } else if (!versionFile.exists()) {
                needsDeploy = true;
                LOGGER.info("Deploying Millénaire content (no version file).");
            } else {
                try (BufferedReader reader = MillCommonUtilities.getReader(versionFile)) {
                    String version = reader.readLine();
                    if (!Millenaire2.VERSION.equals(version)) {
                        needsDeploy = true;
                        LOGGER.info("Redeploying Millénaire content (version " + version + " -> " + Millenaire2.VERSION + ").");
                    } else {
                        LOGGER.info("Millénaire content already at version " + version + ", no redeployment needed.");
                    }
                }
            }

            if (needsDeploy) {
                long startTime = System.currentTimeMillis();
                deployFromResources(contentDir);

                // Write version file
                try (BufferedWriter writer = MillCommonUtilities.getWriter(versionFile)) {
                    writer.write(Millenaire2.VERSION);
                }

                LOGGER.info("Deployed Millénaire content in " + (System.currentTimeMillis() - startTime) + " ms.");
            }
        } catch (Exception e) {
            LOGGER.error("Error deploying Millénaire content", e);
        }
    }

    /**
     * Copy all files from the mod's data/millenaire2/millenaire/ resources to the content directory.
     */
    private static void deployFromResources(File contentDir) throws IOException {
        URL resourceUrl = ContentDeployer.class.getClassLoader().getResource(DATA_PREFIX);
        if (resourceUrl == null) {
            LOGGER.error("Could not find resource path: " + DATA_PREFIX);
            return;
        }

        try {
            URI uri = resourceUrl.toURI();
            Path resourcePath;

            if ("jar".equals(uri.getScheme())) {
                // Running from JAR
                FileSystem fs;
                try {
                    fs = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException e) {
                    fs = FileSystems.newFileSystem(uri, Collections.emptyMap());
                }
                resourcePath = fs.getPath(DATA_PREFIX);
            } else {
                // Running from IDE / dev environment
                resourcePath = Paths.get(uri);
            }

            try (Stream<Path> walk = Files.walk(resourcePath)) {
                walk.forEach(source -> {
                    try {
                        String relativePath = resourcePath.relativize(source).toString();
                        // Normalize path separators
                        relativePath = relativePath.replace('\\', '/');
                        File destFile = new File(contentDir, relativePath);

                        if (Files.isDirectory(source)) {
                            destFile.mkdirs();
                        } else {
                            destFile.getParentFile().mkdirs();
                            try (InputStream in = Files.newInputStream(source);
                                 OutputStream out = new FileOutputStream(destFile)) {
                                in.transferTo(out);
                            }
                        }
                    } catch (IOException e) {
                        LOGGER.error("Error copying resource: " + source, e);
                    }
                });
            }
        } catch (Exception e) {
            LOGGER.error("Error accessing mod resources for deployment", e);
        }
    }
}
