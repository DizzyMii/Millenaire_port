package org.dizzymii.millenaire2.goal;

/**
 * Bridge class — delegates to {@link Goal} which is the canonical base.
 * This class exists solely so that references like {@code MillGoal.GoalInformation}
 * and {@code HashMap<MillGoal, Long>} in MillVillager continue to compile
 * without renaming the 35+ Goal subclasses.
 *
 * <p>New code should reference {@link Goal} directly.</p>
 */
public abstract class MillGoal extends Goal {

    /**
     * Alias so MillVillager.GoalInformation references still resolve.
     * Delegates to the standalone {@link GoalInformation} class.
     */
    // GoalInformation is now the standalone class in this package.
    // MillVillager fields typed as MillGoal.GoalInformation are
    // replaced with the standalone GoalInformation in this feature branch.
}
