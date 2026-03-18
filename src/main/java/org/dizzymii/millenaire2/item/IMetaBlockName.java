package org.dizzymii.millenaire2.item;

/**
 * Interface for blocks/items that provide metadata-based variant names.
 * Ported from org.millenaire.common.item.IMetaBlockName (Forge 1.12.2).
 */
public interface IMetaBlockName {
    String getSpecialName(int meta);
    // In 1.21.1 metadata is removed; this interface is retained for compatibility.
    // Callers should migrate to blockstate properties for variant naming.
}
