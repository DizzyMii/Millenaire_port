/*
 * Decompiled with CFR 0.152.
 */
package org.millenaire.common.buildingplan;

import java.util.List;
import org.millenaire.common.culture.Culture;

public interface IBuildingPlan {
    public Culture getCulture();

    public List<String> getFemaleResident();

    public List<String> getMaleResident();

    public String getNameTranslated();

    public String getNativeName();

    public List<String> getVisitors();
}

