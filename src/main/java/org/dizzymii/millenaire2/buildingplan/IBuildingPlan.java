package org.dizzymii.millenaire2.buildingplan;

import org.dizzymii.millenaire2.culture.Culture;

import java.util.List;

/**
 * Interface for building plans (both standard and custom).
 * Ported from org.millenaire.common.buildingplan.IBuildingPlan (Forge 1.12.2).
 */
public interface IBuildingPlan {
    Culture getCulture();
    List<String> getFemaleResident();
    List<String> getMaleResident();
    String getNameTranslated();
    String getNativeName();
    List<String> getVisitors();
}
