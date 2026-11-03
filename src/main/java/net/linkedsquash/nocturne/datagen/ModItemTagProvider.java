package net.linkedsquash.nocturne.datagen;


import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.item.ModItems;
import net.linkedsquash.nocturne.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, Nocturne.MOD_ID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(ItemTags.SWORDS)
                .add(ModItems.BLOODSTONE_SWORD.asItem());
        tag(ItemTags.PICKAXES)
                .add(ModItems.BLOODSTONE_PICKAXE.asItem());
        tag(ItemTags.AXES)
                .add(ModItems.BLOODSTONE_AXE.asItem());
        tag(ItemTags.SHOVELS)
                .add(ModItems.BLOODSTONE_SHOVEL.asItem());
        tag(ItemTags.HOES)
                .add(ModItems.BLOODSTONE_HOE.asItem());

        tag(ModTags.Items.WHIPS)
                .add(ModItems.WHIP.get())
                .add((ModItems.BELMONT_WHIP.get()));

        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(ModItems.BLOODSTONE_HELMET.get())
                .add(ModItems.BLOODSTONE_CHESTPLATE.get())
                .add(ModItems.BLOODSTONE_LEGGINGS.get())
                .add(ModItems.BLOODSTONE_BOOTS.get());
    }

}

