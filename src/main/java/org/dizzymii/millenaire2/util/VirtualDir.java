package org.dizzymii.millenaire2.util;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A virtual directory that merges content from multiple physical directories.
 * Used to overlay mod default data with user custom data.
 * For example, the mod's culture data comes from both the built-in data directory
 * and a user-defined custom directory — VirtualDir merges them.
 *
 * Ported from org.millenaire.common.utilities.virtualdir.VirtualDir.
 */
public class VirtualDir {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final List<File> sourceDirs;
    private Map<String, File> recursiveChildrenCache = null;
    private List<File> recursiveChildrenListCache = null;

    public VirtualDir(File sourceDir) {
        this.sourceDirs = new ArrayList<>();
        this.sourceDirs.add(sourceDir);
    }

    public VirtualDir(List<File> sourceDirs) throws Exception {
        if (sourceDirs == null || sourceDirs.isEmpty()) {
            throw new Exception("A virtual directory cannot be created with no source directories.");
        }
        this.sourceDirs = new ArrayList<>(sourceDirs);
    }

    public boolean exists() {
        for (File sourceDir : this.sourceDirs) {
            if (sourceDir.exists()) return true;
        }
        return false;
    }

    public List<File> getAllChildFiles(String childName) {
        ArrayList<File> childFiles = new ArrayList<>();
        for (File sourceDir : this.sourceDirs) {
            File possibleChild = new File(sourceDir, childName);
            if (possibleChild.exists()) {
                childFiles.add(possibleChild);
            }
        }
        return childFiles;
    }

    public VirtualDir getChildDirectory(String childDirectory) {
        ArrayList<File> childSourceDir = new ArrayList<>();
        for (File sourceDir : this.sourceDirs) {
            childSourceDir.add(new File(sourceDir, childDirectory));
        }
        try {
            return new VirtualDir(childSourceDir);
        } catch (Exception e) {
            LOGGER.error("Failed to create child VirtualDir: {}", childDirectory, e);
            return null;
        }
    }

    public File getChildFile(String childName) {
        File childFile = null;
        for (File sourceDir : this.sourceDirs) {
            File possibleChild = new File(sourceDir, childName);
            if (possibleChild.exists()) {
                childFile = possibleChild;
            }
        }
        return childFile;
    }

    public File getChildFileRecursive(String childName) {
        if (this.recursiveChildrenCache == null) {
            this.rebuildRecursiveCache();
        }
        return this.recursiveChildrenCache.get(childName.toLowerCase());
    }

    public String getName() {
        return this.sourceDirs.get(0).getName();
    }

    public List<File> listFiles() {
        return this.listFiles(null);
    }

    public List<File> listFiles(FilenameFilter filter) {
        HashMap<String, File> children = new HashMap<>();
        for (File sourceDir : this.sourceDirs) {
            if (!sourceDir.exists()) continue;
            File[] files = sourceDir.listFiles();
            if (files == null) continue;
            for (File file : files) {
                if (file.isDirectory()) continue;
                if (filter != null && !filter.accept(sourceDir, file.getName())) continue;
                children.put(file.getName().toLowerCase(), file);
            }
        }
        ArrayList<File> childrenList = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>(children.keySet());
        Collections.sort(names);
        for (String name : names) {
            childrenList.add(children.get(name));
        }
        return childrenList;
    }

    public List<File> listFilesRecursive() {
        return this.listFilesRecursive(null);
    }

    public List<File> listFilesRecursive(FilenameFilter filter) {
        if (this.recursiveChildrenCache == null) {
            this.rebuildRecursiveCache();
        }
        ArrayList<File> results = new ArrayList<>();
        for (File file : this.recursiveChildrenListCache) {
            if (filter != null && !filter.accept(file.getParentFile(), file.getName())) continue;
            results.add(file);
        }
        return results;
    }

    public List<VirtualDir> listSubDirs() {
        return this.listSubDirs(null);
    }

    public List<VirtualDir> listSubDirs(FilenameFilter filter) {
        HashMap<String, File> children = new HashMap<>();
        for (File sourceDir : this.sourceDirs) {
            if (!sourceDir.exists()) continue;
            File[] files = sourceDir.listFiles();
            if (files == null) continue;
            for (File file : files) {
                if (!file.isDirectory()) continue;
                if (filter != null && !filter.accept(sourceDir, file.getName())) continue;
                children.put(file.getName().toLowerCase(), file);
            }
        }
        ArrayList<VirtualDir> childrenList = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>(children.keySet());
        Collections.sort(names);
        for (String name : names) {
            childrenList.add(this.getChildDirectory(name));
        }
        return childrenList;
    }

    public void mkdirs() {
        for (File source : this.sourceDirs) {
            if (!source.exists()) {
                source.mkdirs();
            }
        }
    }

    private void rebuildRecursiveCache() {
        this.recursiveChildrenCache = new HashMap<>();
        for (File sourceDir : this.sourceDirs) {
            if (!sourceDir.exists()) continue;
            this.rebuildRecursiveCacheHandleDirectory(sourceDir, this.recursiveChildrenCache);
        }
        this.recursiveChildrenListCache = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>(this.recursiveChildrenCache.keySet());
        Collections.sort(names);
        for (String name : names) {
            this.recursiveChildrenListCache.add(this.recursiveChildrenCache.get(name));
        }
    }

    private void rebuildRecursiveCacheHandleDirectory(File directory, Map<String, File> filesFound) {
        File[] files = directory.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isFile()) {
                filesFound.put(file.getName().toLowerCase(), file);
            }
        }
        for (File file : files) {
            if (file.isDirectory()) {
                this.rebuildRecursiveCacheHandleDirectory(file, filesFound);
            }
        }
    }
}
