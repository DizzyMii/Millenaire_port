package org.dizzymii.millenaire2.config;

/**
 * Represents a documented configuration element with description and constraints.
 * Ported from org.millenaire.common.config.DocumentedElement (Forge 1.12.2).
 */
public class DocumentedElement {
    public String key = "";
    public String description = "";
    public Object defaultValue = null;

    public DocumentedElement() {}
    // TODO: Implement documentation generation and value validation
}
