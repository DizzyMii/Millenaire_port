package org.dizzymii.millenaire2.data;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.dizzymii.millenaire2.Millenaire2;
import org.dizzymii.millenaire2.advancement.GenericAdvancement;
import org.dizzymii.millenaire2.advancement.MillAdvancements;

import java.util.function.Consumer;

/**
 * Generates advancement JSON files for all Millénaire advancements.
 * Each advancement uses an impossible trigger criterion so they can only
 * be granted programmatically via GenericAdvancement.grant().
 */
public class MillAdvancementProvider implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver,
                         ExistingFileHelper existingFileHelper) {
        // Root advancement — Millénaire tab
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.EMERALD,
                        Component.literal("Millénaire"),
                        Component.literal("Discover a Millénaire village"),
                        ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, "textures/gui/parchment.png"),
                        AdvancementType.TASK,
                        false, false, false
                )
                .addCriterion("trigger", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()))
                .save(saver, rl("root"), existingFileHelper);

        // First Contact
        AdvancementHolder firstContact = child(root, MillAdvancements.FIRST_CONTACT,
                "First Contact", "Discover a Millénaire village", AdvancementType.TASK)
                .save(saver, rl("firstcontact"), existingFileHelper);

        // Exploration chain
        AdvancementHolder explorer = child(firstContact, MillAdvancements.EXPLORER,
                "Explorer", "Discover 3 different cultures", AdvancementType.TASK)
                .save(saver, rl("explorer"), existingFileHelper);
        AdvancementHolder marcoPolo = child(explorer, MillAdvancements.MARCO_POLO,
                "Marco Polo", "Discover 5 different cultures", AdvancementType.GOAL)
                .save(saver, rl("marcopolo"), existingFileHelper);
        child(marcoPolo, MillAdvancements.MAGELLAN,
                "Magellan", "Discover all cultures", AdvancementType.CHALLENGE)
                .save(saver, rl("magellan"), existingFileHelper);

        // Economy chain
        child(firstContact, MillAdvancements.CRESUS,
                "Crésus", "Accumulate 10,000 deniers", AdvancementType.CHALLENGE)
                .save(saver, rl("cresus"), existingFileHelper);

        // Wand chain
        child(firstContact, MillAdvancements.SUMMONING_WAND,
                "Summoning Wand", "Use the Wand of Summoning", AdvancementType.TASK)
                .save(saver, rl("summoningwand"), existingFileHelper);

        // Building
        child(firstContact, MillAdvancements.AMATEUR_ARCHITECT,
                "Amateur Architect", "Import a custom building plan", AdvancementType.TASK)
                .save(saver, rl("amateurarchitect"), existingFileHelper);

        // Medieval Metropolis
        child(firstContact, MillAdvancements.MEDIEVAL_METROPOLIS,
                "Medieval Metropolis", "Grow a village to 100 villagers", AdvancementType.CHALLENGE)
                .save(saver, rl("medievalmetropolis"), existingFileHelper);

        // Quest
        AdvancementHolder theQuest = child(firstContact, MillAdvancements.THE_QUEST,
                "The Quest", "Complete your first quest", AdvancementType.TASK)
                .save(saver, rl("thequest"), existingFileHelper);
        child(theQuest, MillAdvancements.MAITRE_A_PENSER,
                "Maître à Penser", "Complete a world quest line", AdvancementType.CHALLENGE)
                .save(saver, rl("maitreapenser"), existingFileHelper);

        // Combat
        AdvancementHolder selfDefense = child(firstContact, MillAdvancements.SELF_DEFENSE,
                "Self Defense", "Defend a village from raiders", AdvancementType.TASK)
                .save(saver, rl("selfdefense"), existingFileHelper);
        child(selfDefense, MillAdvancements.SCIPIO,
                "Scipio", "Lead a successful village defense", AdvancementType.GOAL)
                .save(saver, rl("scipio"), existingFileHelper);
        child(selfDefense, MillAdvancements.DARK_SIDE,
                "Dark Side", "Participate in a village raid", AdvancementType.TASK)
                .save(saver, rl("darkside"), existingFileHelper);
        child(selfDefense, MillAdvancements.ATTILA,
                "Attila", "Destroy a village", AdvancementType.CHALLENGE)
                .save(saver, rl("attila"), existingFileHelper);
        child(selfDefense, MillAdvancements.VIKING,
                "Viking", "Raid 3 different villages", AdvancementType.GOAL)
                .save(saver, rl("viking"), existingFileHelper);

        // Social
        child(firstContact, MillAdvancements.CHEERS,
                "Cheers!", "Buy a drink in a tavern", AdvancementType.TASK)
                .save(saver, rl("cheers"), existingFileHelper);
        child(firstContact, MillAdvancements.HIRED,
                "Hired!", "Hire a mercenary", AdvancementType.TASK)
                .save(saver, rl("hired"), existingFileHelper);
        child(firstContact, MillAdvancements.A_FRIEND_INDEED,
                "A Friend Indeed", "Reach max reputation with a village", AdvancementType.CHALLENGE)
                .save(saver, rl("friendindeed"), existingFileHelper);
        child(firstContact, MillAdvancements.PANTHEON,
                "Pantheon", "Visit a temple in every culture", AdvancementType.CHALLENGE)
                .save(saver, rl("pantheon"), existingFileHelper);

        // Farming / hunting
        child(firstContact, MillAdvancements.MASTER_FARMER,
                "Master Farmer", "Help a village farm produce 1000 crops", AdvancementType.GOAL)
                .save(saver, rl("masterfarmer"), existingFileHelper);
        child(firstContact, MillAdvancements.GREAT_HUNTER,
                "Great Hunter", "Kill 50 hostile mobs near villages", AdvancementType.GOAL)
                .save(saver, rl("greathunter"), existingFileHelper);

        // Special
        child(firstContact, MillAdvancements.RAINBOW,
                "Rainbow", "Discover all wool colors in a weaver", AdvancementType.TASK)
                .save(saver, rl("rainbow"), existingFileHelper);

        // World quests
        child(theQuest, MillAdvancements.WQ_INDIAN,
                "Sadhu's Enlightenment", "Complete the Indian world quest", AdvancementType.CHALLENGE)
                .save(saver, rl("wq_indian"), existingFileHelper);
        child(theQuest, MillAdvancements.WQ_NORMAN,
                "Alchemist's Secret", "Complete the Norman world quest", AdvancementType.CHALLENGE)
                .save(saver, rl("wq_norman"), existingFileHelper);
        child(theQuest, MillAdvancements.WQ_MAYAN,
                "Fallen King", "Complete the Mayan world quest", AdvancementType.CHALLENGE)
                .save(saver, rl("wq_mayan"), existingFileHelper);

        // Culture-specific advancements
        for (String culture : MillAdvancements.ADVANCEMENT_CULTURES) {
            String displayCulture = culture.substring(0, 1).toUpperCase() + culture.substring(1);

            GenericAdvancement repAdv = MillAdvancements.REP_ADVANCEMENTS.get(culture);
            GenericAdvancement completeAdv = MillAdvancements.COMPLETE_ADVANCEMENTS.get(culture);
            GenericAdvancement leaderAdv = MillAdvancements.VILLAGE_LEADER_ADVANCEMENTS.get(culture);

            if (repAdv != null) {
                AdvancementHolder repH = child(firstContact, repAdv,
                        displayCulture + " Reputation", "Earn reputation with the " + displayCulture, AdvancementType.TASK)
                        .save(saver, rl(culture + "_reputation"), existingFileHelper);

                if (leaderAdv != null) {
                    child(repH, leaderAdv,
                            displayCulture + " Leader", "Become leader of a " + displayCulture + " village", AdvancementType.CHALLENGE)
                            .save(saver, rl(culture + "_villageleader"), existingFileHelper);
                }
                if (completeAdv != null) {
                    child(repH, completeAdv,
                            displayCulture + " Complete", "Fully develop a " + displayCulture + " village", AdvancementType.CHALLENGE)
                            .save(saver, rl(culture + "_complete"), existingFileHelper);
                }
            }
        }

        // Culture-specific special advancements
        child(firstContact, MillAdvancements.PUJA,
                "Puja", "Attend an Indian ceremony", AdvancementType.TASK)
                .save(saver, rl("puja"), existingFileHelper);
        child(firstContact, MillAdvancements.SACRIFICE,
                "Sacrifice", "Witness a Mayan sacrifice", AdvancementType.TASK)
                .save(saver, rl("sacrifice"), existingFileHelper);
        child(firstContact, MillAdvancements.MARVEL_NORMAN,
                "Norman Marvel", "Build a Norman cathedral", AdvancementType.CHALLENGE)
                .save(saver, rl("marvel_norman"), existingFileHelper);
        child(firstContact, MillAdvancements.ISTANBUL,
                "Istanbul", "Build a Seljuk grand mosque", AdvancementType.CHALLENGE)
                .save(saver, rl("seljuk_istanbul"), existingFileHelper);
        child(firstContact, MillAdvancements.NOTTODAY,
                "Not Today!", "Repel a Byzantine siege", AdvancementType.GOAL)
                .save(saver, rl("byzantines_nottoday"), existingFileHelper);
    }

    private static Advancement.Builder child(AdvancementHolder parent, GenericAdvancement adv,
                                              String title, String description, AdvancementType type) {
        return Advancement.Builder.advancement()
                .parent(parent)
                .display(
                        Items.PAPER,
                        Component.literal(title),
                        Component.literal(description),
                        null,
                        type,
                        true, type == AdvancementType.CHALLENGE, false
                )
                .addCriterion("trigger", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(Millenaire2.MODID, path);
    }
}
