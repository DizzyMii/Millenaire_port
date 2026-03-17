/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  org.lwjgl.input.Keyboard
 */
package org.millenaire.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;
import org.millenaire.client.book.TextBook;
import org.millenaire.client.book.TextLine;
import org.millenaire.client.book.TextPage;
import org.millenaire.client.forge.ClientProxy;
import org.millenaire.client.gui.DisplayActions;
import org.millenaire.client.gui.text.GuiPanelParchment;
import org.millenaire.client.gui.text.GuiText;
import org.millenaire.client.network.ClientSender;
import org.millenaire.common.config.MillConfigValues;
import org.millenaire.common.culture.Culture;
import org.millenaire.common.entity.MillVillager;
import org.millenaire.common.entity.TileEntityPanel;
import org.millenaire.common.forge.Mill;
import org.millenaire.common.item.TradeGood;
import org.millenaire.common.quest.QuestInstance;
import org.millenaire.common.utilities.DevModUtilities;
import org.millenaire.common.utilities.LanguageUtilities;
import org.millenaire.common.utilities.MillCommonUtilities;
import org.millenaire.common.utilities.MillLog;
import org.millenaire.common.utilities.Point;
import org.millenaire.common.utilities.VillageUtilities;
import org.millenaire.common.village.Building;
import org.millenaire.common.village.buildingmanagers.PanelContentGenerator;
import org.millenaire.common.world.UserProfile;

public class MillClientUtilities {
    private static final int VILLAGE_RADIUS_WARNING_LEVEL = 120;
    private static long lastPing = 0L;
    private static long lastFreeRes = 0L;

    public static void displayChunkPanel(World world, EntityPlayer player) {
        ArrayList<List<String>> pages = new ArrayList<List<String>>();
        ArrayList<String> page = new ArrayList<String>();
        page.add(LanguageUtilities.string("chunk.chunkmap"));
        pages.add(page);
        page = new ArrayList();
        page.add(LanguageUtilities.string("chunk.caption"));
        page.add(LanguageUtilities.string(""));
        page.add(LanguageUtilities.string("chunk.captiongeneral"));
        page.add(LanguageUtilities.string("chunk.captiongreen"));
        page.add(LanguageUtilities.string("chunk.captionblue"));
        page.add(LanguageUtilities.string("chunk.captionpurple"));
        page.add(LanguageUtilities.string("chunk.captionwhite"));
        page.add(LanguageUtilities.string(""));
        page.add(LanguageUtilities.string("chunk.playerposition", (int)player.field_70165_t + "/" + (int)player.field_70161_v));
        page.add(LanguageUtilities.string(""));
        page.add(LanguageUtilities.string("chunk.settings", "" + MillConfigValues.KeepActiveRadius, "" + ForgeChunkManager.getMaxTicketLengthFor((String)"millenaire")));
        page.add(LanguageUtilities.string(""));
        page.add(LanguageUtilities.string("chunk.explanations"));
        pages.add(page);
        TextBook book = TextBook.convertStringsToBook(pages);
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiPanelParchment(player, book, null, 2, true));
    }

    public static void displayInfoPanel(World world, EntityPlayer player) {
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.helpbutton"), 2000, new ItemStack(Items.field_151155_ap, 1, 0))));
        page.addLine(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.travelbookbutton"), 5000, new ItemStack(Items.field_151099_bA, 1, 0))));
        page.addLine(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.configbutton"), 4000, new ItemStack(Items.field_151137_ax, 1, 0))));
        if (!Mill.serverWorlds.isEmpty()) {
            page.addLine(new TextLine(new GuiText.MillGuiButton(LanguageUtilities.string("ui.chunkbutton"), 3000, new ItemStack((Item)Items.field_151148_bJ, 1, 0))));
        }
        page.addLine(LanguageUtilities.string("info.culturetitle"), "\u00a71");
        page.addBlankLine();
        for (Culture culture : Culture.ListCultures) {
            page.addLine(LanguageUtilities.string("info.culture", culture.getAdjectiveTranslated()));
            String colour = "";
            if (culture.getLocalPlayerReputation() > 0) {
                colour = "\u00a72";
            } else if (culture.getLocalPlayerReputation() < 0) {
                colour = "\u00a74";
            }
            page.addLine(LanguageUtilities.string("info.culturereputation", culture.getLocalPlayerReputationString()), colour);
            if (MillConfigValues.languageLearning) {
                page.addLine(LanguageUtilities.string("info.culturelanguage", culture.getLanguageLevelString()));
            }
            page.addBlankLine();
        }
        book.addPage(page);
        page = new TextPage();
        page.addLine(LanguageUtilities.string("quest.creationqueststatus"), "\u00a71");
        page.addBlankLine();
        for (String s : Mill.proxy.getClientProfile().getWorldQuestStatus()) {
            page.addLine(s);
        }
        page.addBlankLine();
        page.addLine(LanguageUtilities.string("quest.questlist"));
        page.addBlankLine();
        boolean questShown = false;
        UserProfile profile = Mill.proxy.getClientProfile();
        for (QuestInstance qi : profile.questInstances) {
            String s = qi.getListing(profile);
            if (s == null) continue;
            questShown = true;
            page.addLine(s);
            long timeLeft = qi.currentStepStart + (long)(qi.getCurrentStep().duration * 1000) - world.func_72820_D();
            timeLeft = Math.round(timeLeft / 1000L);
            if (timeLeft == 0L) {
                page.addLine(LanguageUtilities.string("quest.lessthananhourleft"), "\u00a74");
                continue;
            }
            page.addLine(LanguageUtilities.string("quest.timeremaining") + ": " + timeLeft + " " + LanguageUtilities.string("quest.hours"));
        }
        if (!questShown) {
            page.addLine(LanguageUtilities.string("quest.noquestsvisible"));
        }
        book.addPage(page);
        Minecraft.func_71410_x().func_147108_a((GuiScreen)new GuiPanelParchment(player, book, null, 0, true));
    }

    public static void displayPanel(World world, EntityPlayer player, Point p) {
        TileEntityPanel panel = p.getPanel(world);
        if (panel == null || panel.buildingPos == null) {
            return;
        }
        Building building = Mill.clientWorld.getBuilding(panel.buildingPos);
        if (building == null) {
            return;
        }
        TextBook book = panel.getFullText(player);
        if (book != null) {
            DisplayActions.displayParchmentPanelGUI(player, book, building, panel.getMapType(), false);
        }
    }

    public static void displayStartupText(boolean error) {
        if (error) {
            Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.loadproblem", "Mill\u00e9naire 8.1.2"));
            Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.checkload"));
            MillLog.error(null, "There was an error when trying to load Mill\u00e9naire 8.1.2.");
        } else {
            if (MillConfigValues.displayStart) {
                String bonus = "";
                if (MillConfigValues.bonusEnabled) {
                    bonus = " " + LanguageUtilities.string("startup.bonus");
                }
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.millenaireloaded", "Mill\u00e9naire 8.1.2", Keyboard.getKeyName((int)ClientProxy.KB_VILLAGES.func_151463_i())));
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.bonus", "Mill\u00e9naire 8.1.2", bonus), TextFormatting.BLUE);
            }
            if (MillConfigValues.DEV) {
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.devmode1"), TextFormatting.RED);
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.devmode2"), TextFormatting.RED);
            }
            if (MillConfigValues.VillageRadius > 120) {
                Mill.proxy.sendChatAdmin(LanguageUtilities.string("startup.radiuswarning", "100"));
            }
        }
    }

    public static void displayTradeHelp(Building shop, EntityPlayer player, GuiScreen callingGui) {
        ArrayList<String> vprices;
        int p;
        Iterator iterator;
        ArrayList<Integer> prices;
        ArrayList<Object> stacks;
        String lastDesc;
        ArrayList<List<TextLine>> pages = new ArrayList<List<TextLine>>();
        ArrayList<TextLine> page = new ArrayList<TextLine>();
        page.add(new TextLine("<darkblue>" + LanguageUtilities.string("tradehelp.title", shop.getNativeBuildingName())));
        page.add(new TextLine(""));
        page.add(new TextLine("<darkblue>" + LanguageUtilities.string("tradehelp.goodssold")));
        page.add(new TextLine(""));
        List<TradeGood> tradeGood = shop.calculateSellingGoods(null);
        if (tradeGood != null) {
            lastDesc = null;
            stacks = new ArrayList<ItemStack>();
            prices = new ArrayList<Integer>();
            for (TradeGood iterator2 : tradeGood) {
                if (lastDesc != null && !lastDesc.equals(iterator2.travelBookCategory)) {
                    ArrayList<String> vprices2 = new ArrayList<String>();
                    iterator = prices.iterator();
                    while (iterator.hasNext()) {
                        p = (Integer)iterator.next();
                        vprices2.add(LanguageUtilities.string("tradehelp.sellingprice") + " " + MillCommonUtilities.getShortPrice(p));
                    }
                    page.add(new TextLine(stacks, vprices2, LanguageUtilities.string(lastDesc), 72));
                    page.add(new TextLine());
                    stacks = new ArrayList();
                    prices = new ArrayList();
                }
                stacks.add(new ItemStack(iterator2.item.item, 1, iterator2.item.meta));
                prices.add(iterator2.getBasicSellingPrice(shop));
                if (iterator2.travelBookCategory != null) {
                    lastDesc = iterator2.travelBookCategory;
                    continue;
                }
                lastDesc = "";
            }
            if (lastDesc != null) {
                vprices = new ArrayList<String>();
                Iterator iterator2 = prices.iterator();
                while (iterator2.hasNext()) {
                    int p2 = (Integer)iterator2.next();
                    vprices.add(LanguageUtilities.string("tradehelp.sellingprice") + " " + MillCommonUtilities.getShortPrice(p2));
                }
                page.add(new TextLine(stacks, vprices, LanguageUtilities.string(lastDesc), 72));
                page.add(new TextLine());
            }
        }
        page.add(new TextLine("<darkblue>" + LanguageUtilities.string("tradehelp.goodsbought")));
        page.add(new TextLine(""));
        tradeGood = shop.calculateBuyingGoods(null);
        if (tradeGood != null) {
            lastDesc = null;
            stacks = new ArrayList();
            prices = new ArrayList();
            for (TradeGood tradeGood2 : tradeGood) {
                if (lastDesc != null && !lastDesc.equals(tradeGood2.travelBookCategory)) {
                    ArrayList<String> vprices2 = new ArrayList<String>();
                    iterator = prices.iterator();
                    while (iterator.hasNext()) {
                        p = (Integer)iterator.next();
                        vprices2.add(LanguageUtilities.string("tradehelp.buyingprice") + " " + MillCommonUtilities.getShortPrice(p));
                    }
                    page.add(new TextLine(stacks, vprices2, LanguageUtilities.string(lastDesc), 72));
                    page.add(new TextLine());
                    stacks = new ArrayList();
                    prices = new ArrayList();
                }
                stacks.add(new ItemStack(tradeGood2.item.item, 1, tradeGood2.item.meta));
                prices.add(tradeGood2.getBasicBuyingPrice(shop));
                if (tradeGood2.travelBookCategory != null) {
                    lastDesc = tradeGood2.travelBookCategory;
                    continue;
                }
                lastDesc = "";
            }
            if (lastDesc != null) {
                vprices = new ArrayList<String>();
                Iterator iterator3 = prices.iterator();
                while (iterator3.hasNext()) {
                    int p3 = (Integer)iterator3.next();
                    vprices.add(LanguageUtilities.string("tradehelp.buyingprice") + " " + MillCommonUtilities.getShortPrice(p3));
                }
                page.add(new TextLine(stacks, vprices, LanguageUtilities.string(lastDesc), 72));
                page.add(new TextLine());
            }
        }
        pages.add(page);
        page = new ArrayList();
        page.add(new TextLine("<darkblue>" + LanguageUtilities.string("tradehelp.helptitle")));
        page.add(new TextLine());
        page.add(new TextLine(LanguageUtilities.string("tradehelp.helptext")));
        pages.add(page);
        TextBook book = TextBook.convertLinesToBook(pages);
        GuiPanelParchment helpGui = new GuiPanelParchment(player, null, book, 0, true);
        helpGui.setCallingScreen(callingGui);
        Minecraft.func_71410_x().func_147108_a((GuiScreen)helpGui);
    }

    public static void displayVillageBook(World world, EntityPlayer player, Point p) {
        Building townHall = Mill.clientWorld.getBuilding(p);
        if (townHall == null) {
            return;
        }
        TextBook book = new TextBook();
        TextPage page = new TextPage();
        page.addLine(LanguageUtilities.string("panels.villagescroll") + ": " + townHall.getVillageQualifiedName());
        page.addLine("");
        book.addPage(page);
        TextBook newBook = PanelContentGenerator.generateSummary(townHall);
        book.addBook(newBook);
        newBook = PanelContentGenerator.generateEtatCivil(townHall);
        book.addBook(newBook);
        newBook = PanelContentGenerator.generateConstructions(townHall);
        book.addBook(newBook);
        newBook = PanelContentGenerator.generateProjects(player, townHall);
        book.addBook(newBook);
        newBook = PanelContentGenerator.generateResources(townHall);
        book.addBook(newBook);
        newBook = PanelContentGenerator.generateInnGoods(townHall);
        book.addBook(newBook);
        DisplayActions.displayParchmentPanelGUI(player, book, townHall, 1, true);
    }

    public static void handleKeyPress(World world) {
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        if (minecraft.field_71462_r != null) {
            return;
        }
        EntityPlayerSP player = minecraft.field_71439_g;
        if (System.currentTimeMillis() - lastPing > 2000L) {
            try {
                if (player.field_71093_bK == 0) {
                    if (Keyboard.isKeyDown((int)ClientProxy.KB_VILLAGES.func_151463_i())) {
                        ClientSender.displayVillageList(false);
                        lastPing = System.currentTimeMillis();
                    }
                    if (Keyboard.isKeyDown((int)ClientProxy.KB_ESCORTS.func_151463_i())) {
                        boolean stance = !Keyboard.isKeyDown((int)42);
                        ClientSender.hireToggleStance((EntityPlayer)player, stance);
                        lastPing = System.currentTimeMillis();
                    }
                    if (Keyboard.isKeyDown((int)ClientProxy.KB_MENU.func_151463_i())) {
                        MillClientUtilities.displayInfoPanel(world, (EntityPlayer)player);
                        lastPing = System.currentTimeMillis();
                    }
                    if (MillConfigValues.DEV) {
                        if (Keyboard.isKeyDown((int)42) && Keyboard.isKeyDown((int)19) && System.currentTimeMillis() - lastFreeRes > 5000L) {
                            DevModUtilities.fillInFreeGoods((EntityPlayer)player);
                            lastFreeRes = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)42) && Keyboard.isKeyDown((int)203)) {
                            player.func_70107_b(player.field_70165_t + 10000.0, player.field_70163_u + 10.0, player.field_70161_v);
                            lastPing = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)42) && Keyboard.isKeyDown((int)205)) {
                            player.func_70107_b(player.field_70165_t - 10000.0, player.field_70163_u + 10.0, player.field_70161_v);
                            lastPing = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)38)) {
                            ClientSender.displayVillageList(true);
                            lastPing = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)50) && Keyboard.isKeyDown((int)42)) {
                            ClientSender.devCommand(1);
                            lastPing = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)21) && Keyboard.isKeyDown((int)29)) {
                            Mill.proxy.sendChatAdmin("Sending test path request.");
                            ClientSender.devCommand(2);
                            lastPing = System.currentTimeMillis();
                        }
                        if (Keyboard.isKeyDown((int)20)) {
                            Mill.clientWorld.displayTagActionData((EntityPlayer)player);
                            lastPing = System.currentTimeMillis();
                        }
                    }
                }
            }
            catch (Exception e) {
                MillLog.printException("Exception while handling key presses:", e);
            }
        }
    }

    public static void putVillagerSentenceInChat(MillVillager v) {
        if (v.dialogueTargetFirstName != null && !v.dialogueChat) {
            return;
        }
        int radius = 0;
        radius = Mill.isDistantClient() ? MillConfigValues.VillagersSentenceInChatDistanceClient : MillConfigValues.VillagersSentenceInChatDistanceSP;
        EntityPlayer player = Mill.proxy.getTheSinglePlayer();
        if (v.getPos().distanceTo((Entity)player) > (double)radius) {
            return;
        }
        String gameSpeech = VillageUtilities.getVillagerSentence(v, player.func_70005_c_(), false);
        String nativeSpeech = VillageUtilities.getVillagerSentence(v, player.func_70005_c_(), true);
        if (nativeSpeech != null || gameSpeech != null) {
            String s = v.dialogueTargetFirstName != null ? LanguageUtilities.string("other.chattosomeone", v.func_70005_c_(), v.dialogueTargetFirstName + " " + v.dialogueTargetLastName) + ": " : v.func_70005_c_() + ": ";
            if (nativeSpeech != null) {
                s = s + "\u00a79" + nativeSpeech;
            }
            if (nativeSpeech != null && gameSpeech != null) {
                s = s + " ";
            }
            if (gameSpeech != null) {
                s = s + "\u00a74" + gameSpeech;
            }
            Mill.proxy.sendLocalChat(Mill.proxy.getTheSinglePlayer(), v.dialogueColour, s);
        }
    }
}

