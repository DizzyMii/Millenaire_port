/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.MathHelper
 */
package org.millenaire.common.village.buildingmanagers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.millenaire.client.book.BookManager;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.common.buildingplan.BuildingPlan;
import org.millenaire.common.buildingplan.BuildingPlanSet;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.goal.Goal;
import org.millenaire.common.item.InvItem;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.WorldUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.BuildingLocation;
import org.millenaire.common.village.BuildingProject;
import org.millenaire.common.village.ConstructionIP;
import org.millenaire.common.village.VillagerRecord;
import org.millenaire.common.village.buildingmanagers.PanelManager;

public class PanelContentGenerator {
    private static void addProjectToList(EntityPlayer player, BuildingProject project, Building townHall, TextPage page) {
        if (project.planSet != null) {
            if (project.location == null || project.location.level < 0) {
                BuildingPlan plan = project.planSet.getRandomStartingPlan();
                page.addLine(plan.getNameNativeAndTranslated(), "\u00a71", new GuiText.GuiButtonReference(project.planSet));
                page.addLine(LanguageUtilities.string("panels.notyetbuilt") + ".");
            } else {
                boolean obsolete;
                boolean bl = obsolete = project.planSet != null && project.location.version != project.planSet.plans.get((int)project.location.getVariation())[0].version;
                if (project.location.level + 1 >= project.getLevelsNumber(project.location.getVariation())) {
                    BuildingPlan plan = project.getPlan(project.location.getVariation(), project.location.level);
                    BuildingLocation l = project.location;
                    page.addLine(plan.getNameNativeAndTranslated(), "\u00a72", new GuiText.GuiButtonReference(project.planSet));
                    page.addLine(MathHelper.func_76128_c((double)l.pos.distanceTo(townHall.getPos())) + "m " + townHall.getPos().directionToShort(l.pos) + ", " + LanguageUtilities.string("panels.finished") + ".");
                } else if (obsolete) {
                    BuildingPlan plan = project.getPlan(project.location.getVariation(), project.location.level);
                    BuildingLocation l = project.location;
                    page.addLine(plan.getNameNativeAndTranslated(), "\u00a74", new GuiText.GuiButtonReference(project.planSet));
                    page.addLine(MathHelper.func_76128_c((double)l.pos.distanceTo(townHall.getPos())) + "m " + townHall.getPos().directionToShort(l.pos) + ", " + LanguageUtilities.string("panels.obsolete") + ".");
                } else {
                    BuildingPlan plan = project.getPlan(project.location.getVariation(), project.location.level + 1);
                    BuildingLocation l = project.location;
                    page.addLine(plan.getNameNativeAndTranslated(), new GuiText.GuiButtonReference(project.planSet));
                    page.addLine(MathHelper.func_76128_c((double)l.pos.distanceTo(townHall.getPos())) + "m " + townHall.getPos().directionToShort(l.pos) + ", " + LanguageUtilities.string("panels.nbupgradesleft", "" + (project.getLevelsNumber(project.location.getVariation()) - project.location.level - 1)));
                }
            }
        }
    }

    public static TextBook generateArchives(Building townHall, long villager_id) {
        if (townHall == null) {
            return null;
        }
        VillagerRecord vr = townHall.mw.getVillagerRecordById(villager_id);
        if (vr == null) {
            return null;
        }
        TextBook text = new TextBook();
        TextPage page = new TextPage();
        page.addLine(vr.getName(), "\u00a71", new GuiText.GuiButtonReference(vr.getType()));
        page.addLine(vr.getGameOccupation());
        page.addLine("");
        if (vr.mothersName != null && vr.mothersName.length() > 0) {
            page.addLine(LanguageUtilities.string("panels.mother") + ": " + vr.mothersName);
        }
        if (vr.fathersName != null && vr.fathersName.length() > 0) {
            page.addLine(LanguageUtilities.string("panels.father") + ": " + vr.fathersName);
        }
        if (vr.spousesName != null && vr.spousesName.length() > 0) {
            page.addLine(LanguageUtilities.string("panels.spouse") + ": " + vr.spousesName);
        }
        page.addLine("");
        MillVillager villager = null;
        for (MillVillager v : townHall.getKnownVillagers()) {
            if (v.getVillagerId() != vr.getVillagerId()) continue;
            villager = v;
        }
        page.addLine("");
        if (villager == null) {
            if (vr.killed) {
                page.addLine(LanguageUtilities.string("panels.dead"), "\u00a74");
            } else if (vr.awayraiding) {
                page.addLine(LanguageUtilities.string("panels.awayraiding"));
            } else if (vr.awayhired) {
                page.addLine(LanguageUtilities.string("panels.awayhired"));
            } else if (vr.raidingVillage && townHall.world.func_72820_D() < vr.raiderSpawn + 500L) {
                page.addLine(LanguageUtilities.string("panels.invaderincoming"));
            } else {
                page.addLine(LanguageUtilities.string("panels.missing"), "\u00a74");
            }
        } else {
            String occupation = "";
            if (villager.goalKey != null && Goal.goals.containsKey(villager.goalKey)) {
                occupation = Goal.goals.get(villager.goalKey).gameName(villager);
            }
            page.addLine(LanguageUtilities.string("panels.currentoccupation") + ": " + occupation);
        }
        text.addPage(page);
        return text;
    }

    public static TextBook generateConstructions(Building townHall) {
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.constructions") + " : " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addLine("");
        for (ConstructionIP cip : townHall.getConstructionsInProgress()) {
            String loc;
            if (cip.getBuildingLocation() == null) continue;
            BuildingPlanSet buildingPlanSet = townHall.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey);
            String planName = buildingPlanSet.getNameNative();
            String status = cip.getBuildingLocation().level == 0 ? LanguageUtilities.string("ui.inconstruction") : LanguageUtilities.string("ui.upgrading", "" + cip.getBuildingLocation().level);
            if (cip.getBuildingLocation() != null) {
                int distance = MathHelper.func_76128_c((double)townHall.getPos().distanceTo(cip.getBuildingLocation().pos));
                String direction = LanguageUtilities.string(townHall.getPos().directionTo(cip.getBuildingLocation().pos));
                loc = LanguageUtilities.string("other.shortdistancedirection", "" + distance, "" + direction);
            } else {
                loc = "";
            }
            page.addLine(planName, "\u00a71", new GuiText.GuiButtonReference(buildingPlanSet));
            page.addLine(status + ", " + loc);
            page.addLine("");
        }
        page.addLine("");
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!townHall.buildingProjects.containsKey((Object)ep)) continue;
            List projectsLevel = townHall.buildingProjects.get((Object)ep);
            for (BuildingProject project : projectsLevel) {
                if (project.location == null) continue;
                String level = null;
                if (project.location.level < 0) {
                    level = LanguageUtilities.string("ui.notyetbuilt");
                }
                if (project.location.level > 0) {
                    level = LanguageUtilities.string("panels.upgrade") + " " + project.location.level;
                }
                List<String> effects = project.location.getBuildingEffects(townHall.world);
                String effect = null;
                if (effects.size() > 0) {
                    effect = "";
                    for (String s : effects) {
                        if (effect.length() > 0) {
                            effect = effect + ", ";
                        }
                        effect = effect + s;
                    }
                }
                if (project.location.isCustomBuilding) {
                    page.addLine(project.location.getNativeName(), "\u00a71");
                } else {
                    page.addLine(project.location.getNativeName(), "\u00a71", new GuiText.GuiButtonReference(project.planSet));
                }
                if (project.location.getPlan() != null) {
                    page.addLine(project.location.getPlan().getNameTranslated() + ", " + MathHelper.func_76128_c((double)project.location.pos.distanceTo(townHall.getPos())) + "m " + townHall.getPos().directionToShort(project.location.pos));
                } else {
                    page.addLine(MathHelper.func_76128_c((double)project.location.pos.distanceTo(townHall.getPos())) + "m " + townHall.getPos().directionToShort(project.location.pos));
                }
                if (level != null) {
                    page.addLine(level);
                }
                if (effect != null) {
                    page.addLine(effect);
                }
                page.addLine("");
            }
        }
        TextBook book = new TextBook();
        book.addPage(page);
        return book;
    }

    public static TextBook generateEtatCivil(Building townHall) {
        if (townHall == null) {
            return null;
        }
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        TextPage visitorsPage = new TextPage();
        page.addLine(LanguageUtilities.string("ui.population") + " " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addBlankLine();
        visitorsPage.addLine(LanguageUtilities.string("panels.visitors") + ":", "\u00a71");
        visitorsPage.addBlankLine();
        for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
            int nbFound = 0;
            boolean belongsToVillage = true;
            MillVillager foundVillager = null;
            for (MillVillager villager : townHall.getKnownVillagers()) {
                if (villager.getVillagerId() != vr.getVillagerId()) continue;
                ++nbFound;
                belongsToVillage = !villager.isVisitor();
                foundVillager = villager;
            }
            String error = "";
            if (nbFound == 0) {
                Building thServer;
                error = vr.killed ? " (" + LanguageUtilities.string("panels.dead").toLowerCase() + ")" : (vr.awayraiding ? " (" + LanguageUtilities.string("panels.awayraiding").toLowerCase() + ")" : (vr.awayhired ? " (" + LanguageUtilities.string("panels.awayhired").toLowerCase() + ")" : (vr.raidingVillage && townHall.world.func_72820_D() < vr.raiderSpawn + 500L ? " (" + LanguageUtilities.string("panels.invaderincoming").toLowerCase() + ")" : (vr.raidingVillage ? " (" + LanguageUtilities.string("panels.raider").toLowerCase() + ")" : " (" + LanguageUtilities.string("panels.missing").toLowerCase() + ")"))));
                if (MillConfigValues.LogVillagerSpawn >= 1 && Mill.serverWorlds.size() > 0 && (thServer = Mill.serverWorlds.get(0).getBuilding(townHall.getPos())) != null) {
                    int nbOnServer = 0;
                    for (MillVillager villager : thServer.getKnownVillagers()) {
                        if (villager.getVillagerId() != vr.getVillagerId()) continue;
                        ++nbOnServer;
                    }
                    error = error + " nbOnServer:" + nbOnServer;
                }
            } else if (nbFound > 1) {
                error = " (" + LanguageUtilities.string("panels.multiple", "" + nbFound).toLowerCase() + ")";
            }
            String debugLine = "Is seller: " + vr.getType().canSell;
            if (foundVillager != null) {
                debugLine = debugLine + ", isDead client: " + foundVillager.field_70128_L + ", isDead server: " + foundVillager.isDeadOnServer;
            }
            if (belongsToVillage) {
                page.addLine(vr.getName() + ", " + vr.getGameOccupation().toLowerCase() + error, new GuiText.GuiButtonReference(vr.getType()));
                if (MillConfigValues.LogVillagerSpawn >= 1) {
                    page.addLine(debugLine);
                }
                page.addBlankLine();
                continue;
            }
            visitorsPage.addLine(vr.getName() + ", " + vr.getGameOccupation().toLowerCase() + error, new GuiText.GuiButtonReference(vr.getType()));
            page.addBlankLine();
        }
        if (MillConfigValues.DEV && Mill.serverWorlds.size() > 0) {
            int nbClient = WorldUtilities.getEntitiesWithinAABB(townHall.world, MillVillager.class, townHall.getPos(), 64, 16).size();
            Building thServer = Mill.serverWorlds.get(0).getBuilding(townHall.getPos());
            int nbServer = WorldUtilities.getEntitiesWithinAABB(thServer.world, MillVillager.class, townHall.getPos(), 64, 16).size();
            page.addLine("Client: " + nbClient + ", server: " + nbServer);
        }
        book.addPage(page);
        book.addPage(visitorsPage);
        return book;
    }

    public static TextBook generateHouse(Building house) {
        TextPage page = new TextPage();
        if (house.location.isCustomBuilding) {
            page.addLine(LanguageUtilities.string("panels.house") + " : " + house.getNativeBuildingName(), "\u00a71");
        } else {
            page.addLine(LanguageUtilities.string("panels.house") + " : " + house.getNativeBuildingName(), "\u00a71", new GuiText.GuiButtonReference(house.culture.getBuildingPlanSet(house.location.planKey)));
        }
        page.addLine("");
        VillagerRecord wife = null;
        VillagerRecord husband = null;
        int nbMaleAdults = 0;
        int nbFemaleAdults = 0;
        for (VillagerRecord vr : house.getAllVillagerRecords()) {
            if (vr.gender == 2 && !vr.getType().isChild) {
                wife = vr;
                ++nbFemaleAdults;
            }
            if (vr.gender != 1 || vr.getType().isChild) continue;
            husband = vr;
            ++nbMaleAdults;
        }
        if (house.getAllVillagerRecords().size() == 0) {
            page.addLine(LanguageUtilities.string("panels.houseunoccupied"));
        } else if ((wife != null || husband != null) && nbMaleAdults < 2 && nbFemaleAdults < 2) {
            if (wife == null) {
                page.addLine(LanguageUtilities.string("panels.man") + ": " + husband.getName() + ", " + husband.getGameOccupation(), new GuiText.GuiButtonReference(husband.getType()));
                page.addLine("");
                if (house.location.getFemaleResidents().size() == 0) {
                    page.addLine(LanguageUtilities.string("panels.nofemaleresident"));
                } else {
                    page.addLine(LanguageUtilities.string("panels.bachelor"));
                }
            } else if (husband == null) {
                page.addLine(LanguageUtilities.string("panels.woman") + ": " + wife.getName() + ", " + wife.getGameOccupation(), new GuiText.GuiButtonReference(wife.getType()));
                page.addLine("");
                if (house.location.getMaleResidents() == null || house.location.getMaleResidents().size() == 0) {
                    page.addLine(LanguageUtilities.string("panels.nomaleresident"));
                } else {
                    page.addLine(LanguageUtilities.string("panels.spinster"));
                }
            } else {
                page.addLine(LanguageUtilities.string("panels.woman") + ": " + wife.getName() + ", " + wife.getGameOccupation().toLowerCase(), new GuiText.GuiButtonReference(wife.getType()));
                page.addLine(LanguageUtilities.string("panels.man") + ": " + husband.getName() + ", " + husband.getGameOccupation().toLowerCase(), new GuiText.GuiButtonReference(husband.getType()));
                if (house.getAllVillagerRecords().size() > 2) {
                    page.addLine("");
                    page.addLine(LanguageUtilities.string("panels.children") + ":");
                    page.addLine("");
                    for (VillagerRecord vr : house.getAllVillagerRecords()) {
                        if (!vr.getType().isChild) continue;
                        page.addLine(vr.getName() + ", " + vr.getGameOccupation().toLowerCase(), new GuiText.GuiButtonReference(vr.getType()));
                    }
                }
            }
        } else {
            for (VillagerRecord vr : house.getAllVillagerRecords()) {
                page.addLine(vr.getName() + ", " + vr.getGameOccupation().toLowerCase(), new GuiText.GuiButtonReference(vr.getType()));
            }
        }
        TextBook book = new TextBook();
        book.addPage(page);
        return book;
    }

    public static TextBook generateInnGoods(Building house) {
        TradeGood tradeGood;
        TextPage page = new TextPage();
        if (house.location.isCustomBuilding) {
            page.addLine(house.getNativeBuildingName(), "\u00a71");
        } else {
            page.addLine(house.getNativeBuildingName(), "\u00a71", new GuiText.GuiButtonReference(house.culture.getBuildingPlanSet(house.location.planKey)));
        }
        page.addBlankLine();
        page.addLine(LanguageUtilities.string("panels.goodstraded") + ":");
        page.addLine("");
        page.addLine(LanguageUtilities.string("panels.goodsimported") + ":");
        page.addLine("");
        for (InvItem good : house.imported.keySet()) {
            tradeGood = house.culture.getTradeGood(good);
            if (tradeGood == null) {
                page.addLine(good.getName() + ": " + house.imported.get(good), good.getItemStack(), true);
                continue;
            }
            page.addLine(good.getName() + ": " + house.imported.get(good), new GuiText.GuiButtonReference(tradeGood));
        }
        page.addLine("");
        page.addLine(LanguageUtilities.string("panels.goodsexported") + ":");
        page.addLine("");
        for (InvItem good : house.exported.keySet()) {
            tradeGood = house.culture.getTradeGood(good);
            if (tradeGood == null) {
                page.addLine(good.getName() + ": " + house.exported.get(good), good.getItemStack(), true);
                continue;
            }
            page.addLine(good.getName() + ": " + house.exported.get(good), new GuiText.GuiButtonReference(tradeGood));
        }
        TextBook text = new TextBook();
        text.addPage(page);
        return text;
    }

    public static TextBook generateInnVisitors(Building house) {
        TextPage page = new TextPage();
        if (house.location.isCustomBuilding) {
            page.addLine(LanguageUtilities.string("panels.innvisitors", house.getNativeBuildingName()) + ":", "\u00a71");
        } else {
            page.addLine(LanguageUtilities.string("panels.innvisitors", house.getNativeBuildingName()) + ":", "\u00a71", new GuiText.GuiButtonReference(house.culture.getBuildingPlanSet(house.location.planKey)));
        }
        page.addLine("");
        for (int i = house.visitorsList.size() - 1; i > -1; --i) {
            String s = house.visitorsList.get(i);
            if (s.split(";").length > 1) {
                int j;
                String taken;
                String[] v;
                if (s.startsWith("storedexports;")) {
                    v = s.split(";");
                    taken = "";
                    for (j = 2; j < v.length; ++j) {
                        if (taken.length() > 0) {
                            taken = taken + ", ";
                        }
                        taken = taken + MillCommonUtilities.parseItemString(house.culture, v[j]);
                    }
                    page.addLine(LanguageUtilities.string("panels.storedexports", v[1], taken));
                } else if (s.startsWith("broughtimport;")) {
                    v = s.split(";");
                    taken = "";
                    for (j = 2; j < v.length; ++j) {
                        if (taken.length() > 0) {
                            taken = taken + ", ";
                        }
                        taken = taken + MillCommonUtilities.parseItemString(house.culture, v[j]);
                    }
                    page.addLine(LanguageUtilities.string("panels.broughtimport", v[1], taken));
                } else {
                    page.addLine(LanguageUtilities.string(s.split(";")));
                }
            } else {
                page.addLine(s);
            }
            page.addLine("");
        }
        TextBook text = new TextBook();
        text.addPage(page);
        return text;
    }

    public static TextBook generateMilitary(Building townHall) {
        String taken;
        String[] v;
        String s;
        Item bestMelee;
        String weapon;
        String status;
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.military") + " : " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addLine("");
        int nbAttackers = 0;
        Point attackingVillagePos = null;
        if (townHall.raidTarget != null) {
            Building target = Mill.clientWorld.getBuilding(townHall.raidTarget);
            if (target != null) {
                if (townHall.raidStart > 0L) {
                    page.addLine(LanguageUtilities.string("panels.raidinprogresslong", target.getVillageQualifiedName(), "" + Math.round((townHall.world.func_72820_D() - townHall.raidStart) / 1000L)));
                } else {
                    page.addLine(LanguageUtilities.string("panels.planningraidlong", target.getVillageQualifiedName(), "" + Math.round((townHall.world.func_72820_D() - townHall.raidPlanningStart) / 1000L)));
                }
                page.addLine("");
            }
        } else {
            for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
                if (!vr.raidingVillage) continue;
                ++nbAttackers;
                attackingVillagePos = vr.originalVillagePos;
            }
            if (nbAttackers > 0) {
                Building attackingVillage = Mill.clientWorld.getBuilding(attackingVillagePos);
                String attackedBy = attackingVillage != null ? attackingVillage.getVillageQualifiedName() : LanguageUtilities.string("panels.unknownattacker");
                page.addLine(LanguageUtilities.string("panels.underattacklong", "" + nbAttackers, "" + townHall.getVillageAttackerStrength(), attackedBy));
                page.addLine("");
            }
        }
        page.addLine(LanguageUtilities.string("panels.offenselong", "" + townHall.getVillageRaidingStrength()));
        page.addLine(LanguageUtilities.string("panels.defenselong", "" + townHall.getVillageDefendingStrength()));
        book.addPage(page);
        page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.villagefighters"), "\u00a71");
        page.addLine("");
        for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
            if (!vr.getType().isRaider && !vr.getType().helpInAttacks || vr.raidingVillage) continue;
            status = "";
            if (vr.getType().helpInAttacks) {
                status = status + LanguageUtilities.string("panels.defender");
            }
            if (vr.getType().isRaider) {
                if (status.length() > 0) {
                    status = status + ", ";
                }
                status = status + LanguageUtilities.string("panels.raider");
            }
            if (vr.awayraiding) {
                status = status + ", " + LanguageUtilities.string("panels.awayraiding");
            } else if (vr.awayhired) {
                status = status + ", " + LanguageUtilities.string("panels.awayhired");
            } else if (vr.raidingVillage && townHall.world.func_72820_D() < vr.raiderSpawn + 500L) {
                status = status + ", " + LanguageUtilities.string("panels.invaderincoming");
            } else if (vr.killed) {
                status = status + ", " + LanguageUtilities.string("panels.dead");
            }
            weapon = "";
            bestMelee = vr.getBestMeleeWeapon();
            if (bestMelee != null) {
                weapon = new ItemStack(bestMelee).func_82833_r();
            }
            if (vr.getType().isArcher && vr.countInv((Item)Items.field_151031_f) > 0) {
                if (weapon.length() > 0) {
                    weapon = weapon + ", ";
                }
                weapon = weapon + new ItemStack((Item)Items.field_151031_f).func_82833_r();
            }
            page.addLine(vr.getName() + ", " + vr.getGameOccupation(), new GuiText.GuiButtonReference(vr.getType()));
            page.addLine(status);
            page.addLine(LanguageUtilities.string("panels.health") + ": " + vr.getMaxHealth() + ", " + LanguageUtilities.string("panels.armour") + ": " + vr.getTotalArmorValue() + ", " + LanguageUtilities.string("panels.weapons") + ": " + weapon + ", " + LanguageUtilities.string("panels.militarystrength") + ": " + vr.getMilitaryStrength());
            page.addLine("");
        }
        book.addPage(page);
        if (nbAttackers > 0) {
            page = new TextPage();
            page.addLine(LanguageUtilities.string("panels.attackers"), "\u00a74");
            page.addLine("");
            for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
                if (!vr.raidingVillage) continue;
                status = "";
                if (vr.killed) {
                    status = LanguageUtilities.string("panels.dead");
                }
                weapon = "";
                bestMelee = vr.getBestMeleeWeapon();
                if (bestMelee != null) {
                    weapon = new ItemStack(bestMelee).func_82833_r();
                }
                if (vr.getType().isArcher && vr.countInv((Item)Items.field_151031_f) > 0) {
                    if (weapon.length() > 0) {
                        weapon = weapon + ", ";
                    }
                    weapon = weapon + new ItemStack((Item)Items.field_151031_f).func_82833_r();
                }
                page.addLine(vr.getName() + ", " + vr.getGameOccupation(), new GuiText.GuiButtonReference(vr.getType()));
                page.addLine(status);
                page.addLine(LanguageUtilities.string("panels.health") + ": " + vr.getMaxHealth() + ", " + LanguageUtilities.string("panels.armour") + ": " + vr.getTotalArmorValue() + ", " + LanguageUtilities.string("panels.weapons") + ": " + weapon + ", " + LanguageUtilities.string("panels.militarystrength") + ": " + vr.getMilitaryStrength());
                page.addLine("");
            }
            book.addPage(page);
        }
        if (townHall.raidsPerformed.size() > 0) {
            page = new TextPage();
            page.addLine(LanguageUtilities.string("panels.raidsperformed"), "\u00a71");
            page.addLine("");
            for (int i = townHall.raidsPerformed.size() - 1; i >= 0; --i) {
                s = townHall.raidsPerformed.get(i);
                if (s.split(";").length > 1) {
                    if (s.split(";")[0].equals("failure")) {
                        page.addLine(LanguageUtilities.string("raid.historyfailure", s.split(";")[1]), "\u00a74");
                    } else {
                        v = s.split(";");
                        taken = "";
                        for (int j = 2; j < v.length; ++j) {
                            if (taken.length() > 0) {
                                taken = taken + ", ";
                            }
                            taken = taken + MillCommonUtilities.parseItemString(townHall.culture, v[j]);
                        }
                        if (taken.length() == 0) {
                            taken = LanguageUtilities.string("raid.nothing");
                        }
                        page.addLine(LanguageUtilities.string("raid.historysuccess", s.split(";")[1], taken), "\u00a72");
                    }
                } else {
                    page.addLine(townHall.raidsPerformed.get(i));
                }
                page.addLine("");
            }
            book.addPage(page);
        }
        if (townHall.raidsSuffered.size() > 0) {
            page = new TextPage();
            page.addLine(LanguageUtilities.string("panels.raidssuffered"), "\u00a74");
            page.addLine("");
            for (int i = townHall.raidsSuffered.size() - 1; i >= 0; --i) {
                s = townHall.raidsSuffered.get(i);
                if (s.split(";").length > 1) {
                    if (s.split(";")[0].equals("failure")) {
                        page.addLine(LanguageUtilities.string("raid.historydefended", s.split(";")[1]), "\u00a72");
                    } else {
                        v = s.split(";");
                        taken = "";
                        for (int j = 2; j < v.length; ++j) {
                            if (taken.length() > 0) {
                                taken = taken + ", ";
                            }
                            taken = taken + MillCommonUtilities.parseItemString(townHall.culture, v[j]);
                        }
                        if (taken.length() == 0) {
                            taken = LanguageUtilities.string("raid.nothing");
                        }
                        page.addLine(LanguageUtilities.string("raid.historyraided", s.split(";")[1], taken), "\u00a74");
                    }
                } else {
                    page.addLine(townHall.raidsSuffered.get(i));
                }
                page.addLine("");
            }
            book.addPage(page);
        }
        return book;
    }

    public static TextBook generateProjects(EntityPlayer player, Building townHall) {
        if (townHall.villageType == null) {
            return null;
        }
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.buildingprojects") + " : " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addLine("");
        for (BuildingProject.EnumProjects ep : BuildingProject.EnumProjects.values()) {
            if (!townHall.buildingProjects.containsKey((Object)ep)) continue;
            boolean addCategory = false;
            if (ep == BuildingProject.EnumProjects.WALLBUILDING) {
                addCategory = false;
            } else if (townHall.villageType.playerControlled) {
                if (ep == BuildingProject.EnumProjects.CENTRE || ep == BuildingProject.EnumProjects.START || ep == BuildingProject.EnumProjects.CORE) {
                    addCategory = true;
                }
            } else if (ep != BuildingProject.EnumProjects.CUSTOMBUILDINGS) {
                addCategory = true;
            }
            if (!addCategory) continue;
            List projectsLevel = townHall.buildingProjects.get((Object)ep);
            page.addLine(LanguageUtilities.string(ep.labelKey) + ":", "\u00a71");
            page.addLine("");
            for (BuildingProject project : projectsLevel) {
                if (!townHall.isDisplayableProject(project)) continue;
                PanelContentGenerator.addProjectToList(player, project, townHall, page);
            }
            page.addLine("");
        }
        TextBook book = new TextBook();
        book.addPage(page);
        return book;
    }

    /*
     * WARNING - void declaration
     */
    public static TextBook generateResources(Building house) {
        TextPage page = new TextPage();
        TextBook book = new TextBook();
        page.addLine(LanguageUtilities.string("panels.resources") + ": " + house.getNativeBuildingName(), "\u00a71", new GuiText.GuiButtonReference(house.villageType));
        page.addLine("");
        BuildingPlan goalPlan = house.getCurrentGoalBuildingPlan();
        ArrayList<InvItem> res = new ArrayList<InvItem>();
        HashMap<InvItem, Integer> resCost = new HashMap<InvItem, Integer>();
        HashMap<InvItem, Integer> resHas = new HashMap<InvItem, Integer>();
        if (goalPlan != null) {
            void var11_15;
            String name;
            for (InvItem key : goalPlan.resCost.keySet()) {
                res.add(key);
                resCost.put(key, goalPlan.resCost.get(key));
                int has = house.countGoods(key.getItem(), key.meta);
                for (ConstructionIP constructionIP : house.getConstructionsInProgress()) {
                    if (constructionIP.getBuilder() == null || constructionIP.getBuildingLocation() == null || !constructionIP.getBuildingLocation().planKey.equals(house.buildingGoal)) continue;
                    has += constructionIP.getBuilder().countInv(key.getItem(), key.meta);
                }
                if (has > goalPlan.resCost.get(key)) {
                    has = goalPlan.resCost.get(key);
                }
                resHas.put(key, has);
            }
            page.addLine(LanguageUtilities.string("panels.resourcesneeded") + ":");
            String gameName = goalPlan.getNameTranslated();
            if (goalPlan.nativeName != null && goalPlan.nativeName.length() > 0) {
                name = goalPlan.nativeName;
            } else if (goalPlan.getNameTranslated() != null && goalPlan.getNameTranslated().length() > 0) {
                name = goalPlan.getNameTranslated();
                gameName = "";
            } else {
                name = "";
            }
            if (gameName != null && gameName.length() > 0) {
                name = name + " (" + gameName + ")";
            }
            String status = "";
            Object projectCIP = null;
            for (ConstructionIP cip : house.getConstructionsInProgress()) {
                if (projectCIP != null || cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(house.buildingGoal)) continue;
                projectCIP = cip;
            }
            status = projectCIP != null ? (((ConstructionIP)projectCIP).getBuildingLocation().level == 0 ? LanguageUtilities.string("ui.inconstruction") : LanguageUtilities.string("ui.upgrading") + " (" + ((ConstructionIP)projectCIP).getBuildingLocation().level + ")") : LanguageUtilities.string(house.buildingGoalIssue);
            page.addLine(name + " - " + status);
            page.addLine("");
            Collections.sort(res, new MillVillager.InvItemAlphabeticalComparator());
            boolean bl = false;
            while (var11_15 < res.size()) {
                String colour = (Integer)resHas.get(res.get((int)var11_15)) >= (Integer)resCost.get(res.get((int)var11_15)) ? "\u00a72" : "\u00a74";
                TradeGood tradeGood = house.culture.getTradeGood((InvItem)res.get((int)var11_15));
                TextLine column1 = tradeGood == null ? new TextLine(resHas.get(res.get((int)var11_15)) + "/" + resCost.get(res.get((int)var11_15)), colour, ((InvItem)res.get((int)var11_15)).getItemStack(), true) : new TextLine(resHas.get(res.get((int)var11_15)) + "/" + resCost.get(res.get((int)var11_15)), colour, new GuiText.GuiButtonReference(tradeGood));
                if (var11_15 + true < res.size()) {
                    colour = (Integer)resHas.get(res.get((int)(var11_15 + true))) >= (Integer)resCost.get(res.get((int)(var11_15 + true))) ? "\u00a72" : "\u00a74";
                    tradeGood = house.culture.getTradeGood((InvItem)res.get((int)(var11_15 + true)));
                    TextLine column2 = tradeGood == null ? new TextLine(resHas.get(res.get((int)(var11_15 + true))) + "/" + resCost.get(res.get((int)(var11_15 + true))), colour, ((InvItem)res.get((int)(var11_15 + true))).getItemStack(), true) : new TextLine(resHas.get(res.get((int)(var11_15 + true))) + "/" + resCost.get(res.get((int)(var11_15 + true))), colour, new GuiText.GuiButtonReference(tradeGood));
                    page.addLineWithColumns(column1, column2);
                } else {
                    page.addLine(column1);
                }
                var11_15 += 2;
            }
            book.addPage(page);
            page = new TextPage();
        }
        page.addLine(LanguageUtilities.string("panels.resourcesavailable") + ":", "\u00a71");
        page.addLine("");
        HashMap<InvItem, Integer> contents = house.getResManager().getChestsContent();
        ArrayList<InvItem> keys = new ArrayList<InvItem>(contents.keySet());
        Collections.sort(keys, new MillVillager.InvItemAlphabeticalComparator());
        ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
        for (InvItem invItem : keys) {
            TradeGood tradeGood = house.culture.getTradeGood(invItem);
            if (tradeGood == null) {
                infoColumns.add(new TextLine("" + contents.get(invItem), invItem.getItemStack(), true));
                continue;
            }
            infoColumns.add(new TextLine("" + contents.get(invItem), new GuiText.GuiButtonReference(tradeGood)));
        }
        List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 4);
        for (TextLine l : linesWithColumns) {
            page.addLine(l);
        }
        book.addPage(page);
        return book;
    }

    /*
     * WARNING - void declaration
     */
    public static TextBook generateSummary(Building townHall) {
        TextPage page = new TextPage();
        TextBook text = new TextBook();
        page.addLine(LanguageUtilities.string("panels.villagesummary") + ": " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addLine("");
        int nbMen = 0;
        int nbFemale = 0;
        int nbGrownBoy = 0;
        int nbGrownGirl = 0;
        int nbBoy = 0;
        int nbGirl = 0;
        for (VillagerRecord vr : townHall.getAllVillagerRecords()) {
            boolean belongsToVillage = vr.getType() != null && !vr.getType().visitor && !vr.raidingVillage;
            if (!belongsToVillage) continue;
            if (!vr.getType().isChild) {
                if (vr.gender == 1) {
                    ++nbMen;
                    continue;
                }
                ++nbFemale;
                continue;
            }
            if (vr.size == 20) {
                if (vr.gender == 1) {
                    ++nbGrownBoy;
                    continue;
                }
                ++nbGrownGirl;
                continue;
            }
            if (vr.gender == 1) {
                ++nbBoy;
                continue;
            }
            ++nbGirl;
        }
        page.addLine(LanguageUtilities.string("ui.populationnumber", "" + (nbMen + nbFemale + nbGrownBoy + nbGrownGirl + nbBoy + nbGirl)));
        page.addLine(LanguageUtilities.string("ui.adults", "" + (nbMen + nbFemale), "" + nbMen, "" + nbFemale));
        page.addLine(LanguageUtilities.string("ui.teens", "" + (nbGrownBoy + nbGrownGirl), "" + nbGrownBoy, "" + nbGrownGirl));
        page.addLine(LanguageUtilities.string("ui.children", "" + (nbBoy + nbGirl), "" + nbBoy, "" + nbGirl));
        page.addLine("");
        if (townHall.buildingGoal == null) {
            page.addLine(LanguageUtilities.string("ui.goalscompleted1") + " " + LanguageUtilities.string("ui.goalscompleted2"));
        } else {
            BuildingPlan goal = townHall.getCurrentGoalBuildingPlan();
            ConstructionIP goalCIP = null;
            for (ConstructionIP cip : townHall.getConstructionsInProgress()) {
                if (cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(townHall.buildingGoal)) continue;
                goalCIP = cip;
            }
            String status = goalCIP != null ? (goalCIP.getBuildingLocation().level == 0 ? LanguageUtilities.string("ui.inconstruction") : LanguageUtilities.string("ui.upgrading", "" + goalCIP.getBuildingLocation().level)) : LanguageUtilities.string(townHall.buildingGoalIssue);
            page.addLine(LanguageUtilities.string("panels.buildingproject") + " " + goal.nativeName + " " + goal.getNameTranslated() + ": " + status);
            ArrayList<InvItem> res = new ArrayList<InvItem>();
            HashMap<InvItem, Integer> resCost = new HashMap<InvItem, Integer>();
            HashMap<InvItem, Integer> resHas = new HashMap<InvItem, Integer>();
            for (InvItem key : goal.resCost.keySet()) {
                res.add(key);
                resCost.put(key, goal.resCost.get(key));
                int has = townHall.countGoods(key.getItem(), key.meta);
                for (ConstructionIP cip : townHall.getConstructionsInProgress()) {
                    if (cip.getBuilder() == null || cip.getBuildingLocation() == null || !cip.getBuildingLocation().planKey.equals(townHall.buildingGoal)) continue;
                    has += cip.getBuilder().countInv(key.getItem(), key.meta);
                }
                if (has > goal.resCost.get(key)) {
                    has = goal.resCost.get(key);
                }
                resHas.put(key, has);
            }
            page.addLine("");
            page.addLine(LanguageUtilities.string("panels.resourcesneeded") + ":");
            page.addLine("");
            Collections.sort(res, new MillVillager.InvItemAlphabeticalComparator());
            ArrayList<TextLine> infoColumns = new ArrayList<TextLine>();
            for (int i = 0; i < res.size(); ++i) {
                void var18_31;
                TradeGood tradeGood = townHall.culture.getTradeGood((InvItem)res.get(i));
                if ((Integer)resHas.get(res.get(i)) >= (Integer)resCost.get(res.get(i))) {
                    String string = "\u00a72";
                } else {
                    String string = "\u00a74";
                }
                tradeGood = townHall.culture.getTradeGood((InvItem)res.get(i));
                TextLine line = tradeGood == null ? new TextLine(resHas.get(res.get(i)) + "/" + resCost.get(res.get(i)), (String)var18_31, ((InvItem)res.get(i)).getItemStack(), true) : new TextLine(resHas.get(res.get(i)) + "/" + resCost.get(res.get(i)), (String)var18_31, new GuiText.GuiButtonReference(tradeGood));
                infoColumns.add(line);
            }
            List<TextLine> linesWithColumns = BookManager.splitInColumns(infoColumns, 2);
            for (TextLine textLine : linesWithColumns) {
                page.addLine(textLine);
            }
        }
        page.addLine("");
        page.addLine(LanguageUtilities.string("panels.currentconstruction"));
        boolean constructionIP = false;
        for (ConstructionIP cip : townHall.getConstructionsInProgress()) {
            void var18_38;
            if (cip.getBuildingLocation() == null) continue;
            String planName = townHall.culture.getBuildingPlanSet(cip.getBuildingLocation().planKey).getNameNative();
            String status = cip.getBuildingLocation().level == 0 ? LanguageUtilities.string("ui.inconstruction") : LanguageUtilities.string("ui.upgrading", "" + cip.getBuildingLocation().level);
            int distance = MathHelper.func_76128_c((double)townHall.getPos().distanceTo(cip.getBuildingLocation().pos));
            String direction = LanguageUtilities.string(townHall.getPos().directionTo(cip.getBuildingLocation().pos));
            String loc = LanguageUtilities.string("other.shortdistancedirection", "" + distance, "" + direction);
            MillVillager builder = null;
            for (MillVillager v : townHall.getKnownVillagers()) {
                if (v.constructionJobId != cip.getId()) continue;
                builder = v;
            }
            String string = "";
            if (builder != null) {
                String string2 = " - " + builder.func_70005_c_();
            }
            page.addLine(planName + ": " + status + " - " + loc + (String)var18_38);
            page.addLine("");
        }
        text.addPage(page);
        return text;
    }

    public static TextBook generateVillageMap(Building house) {
        TextBook text = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("ui.villagemap") + ": " + house.getNativeBuildingName(), "\u00a71", new GuiText.GuiButtonReference(house.villageType));
        text.addPage(page);
        page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.mapwarning"));
        page.addLine("");
        page.addLine(LanguageUtilities.string("panels.mappurple"));
        page.addLine(LanguageUtilities.string("panels.mapblue"));
        page.addLine(LanguageUtilities.string("panels.mapgreen"));
        page.addLine(LanguageUtilities.string("panels.maplightgreen"));
        page.addLine(LanguageUtilities.string("panels.mapred"));
        page.addLine(LanguageUtilities.string("panels.mapyellow"));
        page.addLine(LanguageUtilities.string("panels.maporange"));
        page.addLine(LanguageUtilities.string("panels.maplightblue"));
        page.addLine(LanguageUtilities.string("panels.mapbrown"));
        text.addPage(page);
        return text;
    }

    public static TextBook generateVisitors(Building building, boolean isMarket) {
        if (building == null) {
            return null;
        }
        TextBook text = new TextBook();
        TextPage page = new TextPage();
        if (building.location.isCustomBuilding) {
            page.addLine(building.getNativeBuildingName(), "\u00a71");
        } else {
            page.addLine(building.getNativeBuildingName(), "\u00a71", new GuiText.GuiButtonReference(building.culture.getBuildingPlanSet(building.location.planKey)));
        }
        page.addBlankLine();
        if (isMarket) {
            page.addLine(LanguageUtilities.string("panels.merchantlist") + ": ");
            page.addLine("(" + LanguageUtilities.string("panels.capacity") + ": " + building.getResManager().stalls.size() + ")");
            page.addLine("");
        } else {
            page.addLine(LanguageUtilities.string("panels.visitorlist") + ": ");
            page.addLine("(" + LanguageUtilities.string("panels.capacity") + ": " + building.location.getVisitors().size() + ")");
            page.addLine("");
        }
        for (VillagerRecord vr : building.getAllVillagerRecords()) {
            MillVillager v = null;
            for (MillVillager av : building.getKnownVillagers()) {
                if (!vr.matches(av)) continue;
                v = av;
            }
            page.addLine(vr.getName());
            if (v == null) {
                if (vr.killed) {
                    page.addLine(LanguageUtilities.string("panels.dead"));
                    continue;
                }
                page.addLine(LanguageUtilities.string("panels.missing"));
                continue;
            }
            page.addLine(v.getNativeOccupationName());
            page.addLine(LanguageUtilities.string("panels.nbnightsin", "" + v.visitorNbNights));
            page.addLine("");
        }
        text.addPage(page);
        return text;
    }

    public static TextBook generateWalls(EntityPlayer player, Building townHall) {
        if (townHall.villageType == null) {
            return null;
        }
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.walls") + " : " + townHall.getVillageQualifiedName(), "\u00a71", new GuiText.GuiButtonReference(townHall.villageType));
        page.addLine("");
        int wallLevel = townHall.computeCurrentWallLevel();
        if (wallLevel >= 0 && wallLevel < Integer.MAX_VALUE) {
            PanelManager.WallStatusInfos wallInfos = townHall.getPanelManager().computeWallInfos(townHall.getFlatProjectList(), wallLevel);
            page.addLine(LanguageUtilities.string("panels.wallslevel", "" + wallLevel, "" + wallInfos.segmentsDone, "" + (wallInfos.segmentsDone + wallInfos.segmentsToDo)));
            page.addLine("");
            page.addLine(LanguageUtilities.string("panels.wallsres"));
            for (int i = 0; i < wallInfos.resources.size(); i += 2) {
                PanelManager.ResourceLine resLineLeft = wallInfos.resources.get(i);
                String colour = resLineLeft.has >= resLineLeft.cost ? "\u00a72" : "\u00a74";
                TradeGood tradeGood = townHall.culture.getTradeGood(resLineLeft.res);
                TextLine column1 = tradeGood == null ? new TextLine(resLineLeft.has + "/" + resLineLeft.cost, colour, resLineLeft.res.getItemStack(), true) : new TextLine(resLineLeft.has + "/" + resLineLeft.cost, colour, new GuiText.GuiButtonReference(tradeGood));
                if (i + 1 < wallInfos.resources.size()) {
                    PanelManager.ResourceLine resLineRight = wallInfos.resources.get(i + 1);
                    colour = resLineRight.has >= resLineRight.cost ? "\u00a72" : "\u00a74";
                    tradeGood = townHall.culture.getTradeGood(resLineRight.res);
                    TextLine column2 = tradeGood == null ? new TextLine(resLineRight.has + "/" + resLineRight.cost, colour, resLineRight.res.getItemStack(), true) : new TextLine(resLineRight.has + "/" + resLineRight.cost, colour, new GuiText.GuiButtonReference(tradeGood));
                    page.addLineWithColumns(column1, column2);
                    continue;
                }
                page.addLine(column1);
            }
            page.addLine("");
        }
        if (townHall.buildingProjects.containsKey((Object)BuildingProject.EnumProjects.WALLBUILDING)) {
            BuildingProject.EnumProjects ep = BuildingProject.EnumProjects.WALLBUILDING;
            List projectsLevel = townHall.buildingProjects.get((Object)ep);
            page.addLine(LanguageUtilities.string("panels.wallssegments"));
            for (BuildingProject project : projectsLevel) {
                if (!townHall.isDisplayableProject(project)) continue;
                PanelContentGenerator.addProjectToList(player, project, townHall, page);
            }
            page.addLine("");
        }
        TextBook book = new TextBook();
        book.addPage(page);
        return book;
    }
}

