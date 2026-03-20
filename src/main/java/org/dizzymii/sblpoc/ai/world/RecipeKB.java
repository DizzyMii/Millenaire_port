package org.dizzymii.sblpoc.ai.world;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Knowledge base of crafting/smelting recipes, loaded once from RecipeManager.
 * Provides reverse-index lookup: given a desired output, find recipes that produce it.
 */
public class RecipeKB {

    private final Map<Item, List<SimpleRecipe>> recipesByOutput = new HashMap<>();
    private boolean loaded = false;

    /**
     * Load all recipes from the RecipeManager. Call once when the NPC first ticks server-side.
     */
    public void loadFrom(RecipeManager recipeManager) {
        if (loaded) return;
        loaded = true;

        // Crafting recipes
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            Recipe<?> recipe = holder.value();
            ItemStack result = recipe.getResultItem(null);
            if (result.isEmpty()) continue;

            List<ItemStack> inputs = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                ItemStack[] items = ingredient.getItems();
                if (items.length > 0) {
                    inputs.add(items[0].copy());
                }
            }

            SimpleRecipe sr = new SimpleRecipe(
                    holder.id().toString(),
                    RecipeCategory.CRAFTING,
                    inputs,
                    result.copy(),
                    null, // no station needed for 2x2; 3x3 needs crafting table
                    10    // ~0.5 sec
            );

            recipesByOutput.computeIfAbsent(result.getItem(), k -> new ArrayList<>()).add(sr);
        }

        // Smelting recipes
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
            Recipe<?> recipe = holder.value();
            ItemStack result = recipe.getResultItem(null);
            if (result.isEmpty()) continue;

            List<ItemStack> inputs = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                ItemStack[] items = ingredient.getItems();
                if (items.length > 0) {
                    inputs.add(items[0].copy());
                }
            }

            SimpleRecipe sr = new SimpleRecipe(
                    holder.id().toString(),
                    RecipeCategory.SMELTING,
                    inputs,
                    result.copy(),
                    BlockCategory.FURNACE,
                    200 // 10 seconds smelting time
            );

            recipesByOutput.computeIfAbsent(result.getItem(), k -> new ArrayList<>()).add(sr);
        }

        // Smithing recipes
        for (RecipeHolder<?> holder : recipeManager.getAllRecipesFor(RecipeType.SMITHING)) {
            Recipe<?> recipe = holder.value();
            ItemStack result = recipe.getResultItem(null);
            if (result.isEmpty()) continue;

            List<ItemStack> inputs = new ArrayList<>();
            for (Ingredient ingredient : recipe.getIngredients()) {
                ItemStack[] items = ingredient.getItems();
                if (items.length > 0) {
                    inputs.add(items[0].copy());
                }
            }

            SimpleRecipe sr = new SimpleRecipe(
                    holder.id().toString(),
                    RecipeCategory.SMITHING,
                    inputs,
                    result.copy(),
                    BlockCategory.ANVIL,
                    20
            );

            recipesByOutput.computeIfAbsent(result.getItem(), k -> new ArrayList<>()).add(sr);
        }
    }

    /**
     * Find all recipes that produce the given item.
     */
    public List<SimpleRecipe> getRecipesFor(Item output) {
        return recipesByOutput.getOrDefault(output, Collections.emptyList());
    }

    /**
     * Find the cheapest recipe (by tick cost) for a given output.
     */
    @Nullable
    public SimpleRecipe getCheapestRecipe(Item output) {
        List<SimpleRecipe> recipes = getRecipesFor(output);
        if (recipes.isEmpty()) return null;
        return recipes.stream().min(Comparator.comparingInt(r -> r.tickCost)).orElse(null);
    }

    /**
     * Check if any recipe exists for the given output.
     */
    public boolean hasRecipeFor(Item output) {
        return recipesByOutput.containsKey(output) && !recipesByOutput.get(output).isEmpty();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public int getRecipeCount() {
        return recipesByOutput.values().stream().mapToInt(List::size).sum();
    }

    // ========== Inner Types ==========

    public enum RecipeCategory {
        CRAFTING, SMELTING, SMITHING, BREWING
    }

    public static class SimpleRecipe {
        public final String id;
        public final RecipeCategory category;
        public final List<ItemStack> inputs;
        public final ItemStack output;
        @Nullable
        public final BlockCategory requiredStation;
        public final int tickCost;

        public SimpleRecipe(String id, RecipeCategory category, List<ItemStack> inputs,
                            ItemStack output, @Nullable BlockCategory requiredStation, int tickCost) {
            this.id = id;
            this.category = category;
            this.inputs = Collections.unmodifiableList(inputs);
            this.output = output;
            this.requiredStation = requiredStation;
            this.tickCost = tickCost;
        }

        /**
         * Check if the given inventory has all required inputs.
         */
        public boolean canCraftWith(InventoryModel inventory) {
            Map<Item, Integer> needed = new HashMap<>();
            for (ItemStack stack : inputs) {
                needed.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
            for (Map.Entry<Item, Integer> entry : needed.entrySet()) {
                if (inventory.countItem(entry.getKey()) < entry.getValue()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            return "Recipe[" + id + " -> " + output.getItem() + " x" + output.getCount() + "]";
        }
    }
}
