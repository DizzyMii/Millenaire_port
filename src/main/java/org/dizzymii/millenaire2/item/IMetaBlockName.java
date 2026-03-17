package org.dizzymii.millenaire2.item;

/**
 * Interface for blocks/items that provide metadata-based variant names.
 * Ported from org.millenaire.common.item.IMetaBlockName (Forge 1.12.2).
 */
public interface IMetaBlockName {
    String getSpecialName(int meta);
    // TODO: Adapt for 1.21.1 blockstate-based system (metadata removed)
}
