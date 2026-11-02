package net.linkedsquash.nocturneSU.datagen;


import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.block.ModBlocks;
import net.linkedsquash.nocturneSU.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, NocturneSU.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

//        whip_parent(ModItems.BELMONT_WHIP);
//        //whip_parent(ModItems.WHIP);


        basicItem(ModItems.BLOODSTONE.get());
        basicItem(ModItems.BLOOD_PEACH.get());
        basicItem(ModItems.EXPLOSIVE_STONE.get());
        basicItem(ModItems.TIN_INGOT.get());
        basicItem(ModItems.RAW_TIN.get());
        basicItem(ModItems.BRONZE_INGOT.get());
        simpleBlockItem(ModBlocks.BLOODSTONE_DOOR.get());

        simpleBlockItem(ModBlocks.BLOODSTONE_STAIRS.get());
        simpleBlockItem(ModBlocks.BLOODSTONE_SLAB.get());
        simpleBlockItem(ModBlocks.BLOODSTONE_PRESSURE_PLATE.get());
        simpleBlockItem(ModBlocks.BLOODSTONE_FENCE_GATE.get());

        fenceItem(ModBlocks.BLOODSTONE_FENCE, ModBlocks.BLOODSTONE_BLOCK);
        buttonItem(ModBlocks.BLOODSTONE_BUTTON, ModBlocks.BLOODSTONE_BLOCK);
        wallItem(ModBlocks.BLOODSTONE_WALL, ModBlocks.BLOODSTONE_BLOCK);

        handheldItem(ModItems.BLOODSTONE_SWORD.asItem());
        handheldItem(ModItems.BLOODSTONE_PICKAXE.asItem());
        handheldItem(ModItems.BLOODSTONE_AXE.asItem());
        handheldItem(ModItems.BLOODSTONE_SHOVEL.asItem());
        handheldItem(ModItems.BLOODSTONE_HOE.asItem());

        basicItem(ModBlocks.BLOODSTONE_DOOR.asItem());



    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(NocturneSU.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(NocturneSU.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(NocturneSU.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
