package org.dizzymii.millenaire2.buildingplan;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Simple filename filter for building plan files.
 * Ported from org.millenaire.common.buildingplan.BuildingFileFiler (Forge 1.12.2).
 */
public class BuildingFileFiler implements FilenameFilter {

    private final String extension;

    public BuildingFileFiler(String extension) {
        this.extension = extension.toLowerCase();
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(extension);
    }
}
