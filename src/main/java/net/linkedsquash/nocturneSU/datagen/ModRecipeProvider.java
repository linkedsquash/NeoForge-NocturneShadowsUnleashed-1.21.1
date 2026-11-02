package net.linkedsquash.nocturneSU.datagen;

import net.linkedsquash.nocturneSU.block.ModBlocks;
import net.linkedsquash.nocturneSU.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements net.neoforged.neoforge.common.conditions.IConditionBuilder {

    private static final List<ItemLike> TIN_SMELTABLES = List.of(ModItems.RAW_TIN
            ,ModBlocks.TIN_ORE);

    private static final List<ItemLike> BLOODSTONE_SMELTABLES = List.of(ModItems.BLOODSTONE
            ,ModBlocks.BLOODSTONE_ORE);

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }


    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        oreSmelting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT.get(), 0.25f, 200, "tin");
        oreBlasting(recipeOutput, TIN_SMELTABLES, RecipeCategory.MISC, ModItems.TIN_INGOT.get(), 0.25f, 100, "tin");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BRONZE_INGOT.get())
                .pattern("ccc")
                .pattern("cct")
                .pattern("ttt")
                .define('c', Tags.Items.INGOTS_COPPER)
                .define('t', ModItems.TIN_INGOT.get())
                .unlockedBy(getHasName(ModItems.TIN_INGOT.get()), has(ModItems.TIN_INGOT.get()))
                .save(recipeOutput, "nocturne:bronze_ingot_from_copper_tin");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BRONZE_INGOT.get(), 9)
                .requires(ModBlocks.BRONZE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.BRONZE_BLOCK.get()), has(ModBlocks.BRONZE_BLOCK.get()))
                .save(recipeOutput, "nocturne:bronze_ingot_from_bronze_block");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BRONZE_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.BRONZE_INGOT.get())
                .unlockedBy(getHasName(ModItems.BRONZE_INGOT.get()), has(ModItems.BRONZE_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BRONZE_INGOT.get(), 9)
                .requires(ModBlocks.BRONZE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.BRONZE_BLOCK.get()), has(ModBlocks.BRONZE_BLOCK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TIN_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.TIN_INGOT.get())
                .unlockedBy(getHasName(ModItems.TIN_INGOT.get()), has(ModItems.TIN_INGOT.get()))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.TIN_INGOT.get(), 9)
                .requires(ModBlocks.TIN_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.TIN_BLOCK.get()), has(ModBlocks.TIN_BLOCK.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOODSTONE_BLOCK.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.BLOODSTONE.get())
                .unlockedBy(getHasName(ModItems.BLOODSTONE.get()), has(ModItems.BLOODSTONE.get()))
                .save(recipeOutput);


        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BLOODSTONE.get(), 9)
                .requires(ModBlocks.BLOODSTONE_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.BLOODSTONE_BLOCK.get()), has(ModBlocks.BLOODSTONE_BLOCK.get()))
                .save(recipeOutput);


    }

}
