/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.buildingplan;

import java.io.File;
import java.io.FilenameFilter;

public class BuildingFileFiler
implements FilenameFilter {
    String end;

    public BuildingFileFiler(String ending) {
        this.end = ending;
    }

    @Override
    public boolean accept(File file, String name) {
        if (!name.endsWith(this.end)) {
            return false;
        }
        return !name.startsWith(".");
    }
}

