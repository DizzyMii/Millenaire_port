/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 */
package org.millenaire.common.goal.leisure;

import java.util.List;
import net.minecraft.entity.Entity;
import org.millenaire.common.config.DocumentedElement;
import org.millenaire.common.culture.CultureLanguage;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.WorldUtilities;

@DocumentedElement.Documentation(value="Make the villager meet a villager looking for someone to chat, to initiate a dialog")
public class GoalGoChat
extends Goal {
    private final char[] chatColours = new char[]{'f', '3', 'a', '7', 'c'};

    public GoalGoChat() {
        this.leasure = true;
        this.travelBookShow = false;
        this.sprint = false;
    }

    @Override
    public int actionDuration(MillVillager villager) {
        return 40;
    }

    @Override
    public Goal.GoalInformation getDestination(MillVillager villager) throws Exception {
        for (MillVillager v : villager.getTownHall().getKnownVillagers()) {
            if (v == villager || !Goal.gosocialise.key.equals(v.goalKey) || !(v.getPos().distanceToSquared((Entity)villager) < 25.0)) continue;
            return this.packDest(null, null, (Entity)v);
        }
        return null;
    }

    @Override
    protected boolean isPossibleSpecific(MillVillager villager) throws Exception {
        return this.getDestination(villager) != null;
    }

    @Override
    public boolean lookAtGoal() {
        return true;
    }

    @Override
    public void onAccept(MillVillager villager) throws Exception {
        Goal.GoalInformation info = this.getDestination(villager);
        if (info != null) {
            MillVillager target = (MillVillager)info.getTargetEnt();
            target.clearGoal();
            target.goalKey = this.key;
            target.setGoalDestEntity((Entity)villager);
            CultureLanguage.Dialogue dialog = villager.getCulture().pickNewDialogue(villager, target);
            if (dialog != null) {
                int role = dialog.validRoleFor(villager);
                villager.setGoalInformation(null);
                villager.setGoalDestEntity((Entity)target);
                int col = this.chatColours[MillCommonUtilities.randomInt(this.chatColours.length)];
                col = 102;
                if (dialog != null) {
                    List<Entity> entities = WorldUtilities.getEntitiesWithinAABB(villager.field_70170_p, MillVillager.class, villager.getPos(), 5, 5);
                    boolean dialogueChat = true;
                    for (Entity ent : entities) {
                        if (ent == villager || ent == target) continue;
                        MillVillager v = (MillVillager)ent;
                        if (!this.key.equals(v.goalKey) || !v.dialogueChat) continue;
                        dialogueChat = false;
                    }
                    villager.dialogueKey = dialog.key;
                    villager.dialogueRole = role;
                    villager.dialogueStart = villager.field_70170_p.func_72820_D();
                    villager.dialogueColour = (char)col;
                    villager.dialogueChat = dialogueChat;
                    target.dialogueKey = dialog.key;
                    target.dialogueRole = 3 - role;
                    target.dialogueStart = villager.field_70170_p.func_72820_D();
                    target.dialogueColour = (char)col;
                    target.dialogueChat = dialogueChat;
                }
            }
        }
        super.onAccept(villager);
    }

    @Override
    public boolean performAction(MillVillager villager) throws Exception {
        return villager.dialogueKey == null;
    }

    @Override
    public int priority(MillVillager villager) throws Exception {
        return 10;
    }

    @Override
    public int range(MillVillager villager) {
        return 3;
    }
}

