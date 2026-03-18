package org.dizzymii.millenaire2.world;

import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

/**
 * TreeGrower instances for Millénaire custom saplings.
 * Each grower references a configured feature key from {@link MillTreeFeatures}.
 */
public class MillTreeGrowers {

    public static final TreeGrower APPLE = new TreeGrower(
            "millenaire_apple",
            Optional.empty(),
            Optional.of(MillTreeFeatures.APPLE_TREE),
            Optional.empty()
    );

    public static final TreeGrower OLIVE = new TreeGrower(
            "millenaire_olive",
            Optional.empty(),
            Optional.of(MillTreeFeatures.OLIVE_TREE),
            Optional.empty()
    );

    public static final TreeGrower PISTACHIO = new TreeGrower(
            "millenaire_pistachio",
            Optional.empty(),
            Optional.of(MillTreeFeatures.PISTACHIO_TREE),
            Optional.empty()
    );

    public static final TreeGrower CHERRY_MILL = new TreeGrower(
            "millenaire_cherry",
            Optional.empty(),
            Optional.of(MillTreeFeatures.CHERRY_MILL_TREE),
            Optional.empty()
    );

    public static final TreeGrower SAKURA = new TreeGrower(
            "millenaire_sakura",
            Optional.empty(),
            Optional.of(MillTreeFeatures.SAKURA_TREE),
            Optional.empty()
    );
}
