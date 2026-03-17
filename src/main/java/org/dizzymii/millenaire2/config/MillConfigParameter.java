package org.dizzymii.millenaire2.config;

/**
 * Represents a single Millenaire configuration parameter with type and bounds.
 * Ported from org.millenaire.common.config.MillConfigParameter (Forge 1.12.2).
 */
public class MillConfigParameter {
    public String name = "";
    public String category = "";
    public Object value = null;

    public MillConfigParameter() {}
    // TODO: Implement typed parameter with min/max bounds and serialization
}
