package org.dizzymii.millenaire2.data;

/**
 * Represents an annotated configuration parameter with metadata.
 * Ported from org.millenaire.common.annotedparameters.AnnotedParameter (Forge 1.12.2).
 */
public class AnnotedParameter {
    public String key = "";
    public String defaultValue = "";
    public String description = "";
    public String category = "";

    public AnnotedParameter() {}
    // TODO: Implement annotation-based parameter loading and validation
}
