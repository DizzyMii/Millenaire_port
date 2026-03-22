package org.dizzymii.millenaire2.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.dizzymii.millenaire2.init.ModBlocks;
import org.dizzymii.millenaire2.init.ModItems;

import java.util.concurrent.CompletableFuture;

public class MillRecipeProvider extends RecipeProvider {

    public MillRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        // Cooked brick from mud brick via smelting
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModBlocks.MUD_BRICK), RecipeCategory.BUILDING_BLOCKS,
                        ModBlocks.COOKED_BRICK, 0.1f, 200)
                .unlockedBy("has_mud_brick", has(ModBlocks.MUD_BRICK))
                .save(output);

        // Thatch from wheat
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.THATCH, 4)
                .pattern("WW")
                .pattern("WW")
                .define('W', Items.WHEAT)
                .unlockedBy("has_wheat", has(Items.WHEAT))
                .save(output);

        // Timber frame from sticks + planks
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TIMBER_FRAME_PLAIN, 4)
                .pattern("SP")
                .pattern("PS")
                .define('S', Items.STICK)
                .define('P', Items.OAK_PLANKS)
                .unlockedBy("has_planks", has(Items.OAK_PLANKS))
                .save(output);

        // Timber frame cross variant
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.TIMBER_FRAME_CROSS, 4)
                .pattern("PS")
                .pattern("SP")
                .define('S', Items.STICK)
                .define('P', Items.OAK_PLANKS)
                .unlockedBy("has_planks", has(Items.OAK_PLANKS))
                .save(output);

        // Stone decoration from stone
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STONE_DECORATION, 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', Items.STONE)
                .unlockedBy("has_stone", has(Items.STONE))
                .save(output);

        // Denier from gold nugget
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DENIER, 9)
                .requires(ModItems.DENIER_ARGENT)
                .unlockedBy("has_denier_argent", has(ModItems.DENIER_ARGENT))
                .save(output);

        // Denier argent from deniers
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DENIER_ARGENT)
                .pattern("DDD")
                .pattern("DDD")
                .pattern("DDD")
                .define('D', ModItems.DENIER)
                .unlockedBy("has_denier", has(ModItems.DENIER))
                .save(output);

        // Denier or from denier argent
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DENIER_OR)
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.DENIER_ARGENT)
                .unlockedBy("has_denier_argent", has(ModItems.DENIER_ARGENT))
                .save(output);

        // Denier argent from denier or
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.DENIER_ARGENT, 9)
                .requires(ModItems.DENIER_OR)
                .unlockedBy("has_denier_or", has(ModItems.DENIER_OR))
                .save(output, "millenaire2:denier_argent_from_or");

        // Cooked meats
        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.BEARMEAT_RAW), RecipeCategory.FOOD,
                        ModItems.BEARMEAT_COOKED, 0.35f, 200)
                .unlockedBy("has_raw_bear", has(ModItems.BEARMEAT_RAW))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.WOLFMEAT_RAW), RecipeCategory.FOOD,
                        ModItems.WOLFMEAT_COOKED, 0.35f, 200)
                .unlockedBy("has_raw_wolf", has(ModItems.WOLFMEAT_RAW))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.SEAFOOD_RAW), RecipeCategory.FOOD,
                        ModItems.SEAFOOD_COOKED, 0.35f, 200)
                .unlockedBy("has_raw_seafood", has(ModItems.SEAFOOD_RAW))
                .save(output);
    }
}

