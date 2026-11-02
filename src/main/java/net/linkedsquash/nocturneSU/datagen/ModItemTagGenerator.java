package net.linkedsquash.nocturneSU.datagen;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.item.ModItems;
import net.linkedsquash.nocturneSU.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider, CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, NocturneSU.MOD_ID, existingFileHelper);
    }


    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(ModTags.Items.WHIPS)
                .add(ModItems.WHIP.get())
                .add((ModItems.BELMONT_WHIP.get()));
    }

}

