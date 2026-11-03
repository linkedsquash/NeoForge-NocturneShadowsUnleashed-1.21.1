package net.linkedsquash.nocturne.datagen;


import net.linkedsquash.nocturne.block.ModBlocks;
import net.linkedsquash.nocturne.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {


    protected ModBlockLootTableProvider( HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.MAGIC_BLOCK.get());
        this.dropSelf(ModBlocks.BLOODSTONE_BLOCK.get());
        this.dropSelf(ModBlocks.TIN_BLOCK.get());
        this.dropSelf(ModBlocks.BRONZE_BLOCK.get());

        this.dropSelf(ModBlocks.BLOODSTONE_STAIRS.get());
        this.dropSelf(ModBlocks.BLOODSTONE_BUTTON.get());
        this.dropSelf(ModBlocks.BLOODSTONE_FENCE_GATE.get());
        this.dropSelf(ModBlocks.BLOODSTONE_FENCE.get());
        this.dropSelf(ModBlocks.BLOODSTONE_WALL.get());
        this.dropSelf(ModBlocks.BLOODSTONE_TRAPDOOR.get());
        this.dropSelf(ModBlocks.BLOODSTONE_PRESSURE_PLATE.get());

        this.add(ModBlocks.BLOODSTONE_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.BLOODSTONE_SLAB.get()));
        this.add(ModBlocks.BLOODSTONE_DOOR.get(),
                block -> createDoorTable(ModBlocks.BLOODSTONE_DOOR.get()));

        this.add(ModBlocks.BLOODSTONE_ORE.get(),
                block -> createOreDrop(ModBlocks.BLOODSTONE_ORE.get(), ModItems.BLOODSTONE.get()));

        this.add(ModBlocks.TIN_ORE.get(),
                block -> createMultipleOreDrops(ModBlocks.TIN_ORE.get(), ModItems.RAW_TIN.get(), 2, 5));

    }

    protected LootTable.Builder createMultipleOreDrops(Block pBlock, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock, LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
