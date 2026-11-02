package net.linkedsquash.nocturneSU.datagen;

import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.block.ModBlocks;
import net.linkedsquash.nocturneSU.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, NocturneSU.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.BLOODSTONE_ORE.get())
                .add(ModBlocks.TIN_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.MAGIC_BLOCK.get()
                        ,ModBlocks.BLOODSTONE_BLOCK.get()
                        ,ModBlocks.BLOODSTONE_ORE.get()
                        ,ModBlocks.TIN_ORE.get()
                        ,ModBlocks.TIN_BLOCK.get()
                        ,ModBlocks.BRONZE_BLOCK.get());

        this.tag(BlockTags.NEEDS_STONE_TOOL);

        this.tag(ModTags.Blocks.NEEDS_BLOODSTONE_TOOL)
                .add(ModBlocks.MAGIC_BLOCK.get());


        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.MAGIC_BLOCK.get())
                .add(ModBlocks.BLOODSTONE_ORE.get())
                .add(ModBlocks.TIN_ORE.get())
                .add(ModBlocks.TIN_BLOCK.get())
                .add(ModBlocks.BRONZE_BLOCK.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL);

        this.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL);

        this.tag(BlockTags.FENCES)
                .add(ModBlocks.BLOODSTONE_FENCE.get());
        this.tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.BLOODSTONE_FENCE.get());
        this.tag(BlockTags.WALLS)
                .add(ModBlocks.BLOODSTONE_WALL.get());

    }
}
