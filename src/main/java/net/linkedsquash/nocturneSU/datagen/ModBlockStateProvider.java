package net.linkedsquash.nocturneSU.datagen;

import net.linkedsquash.nocturneSU.NocturneSU;
import net.linkedsquash.nocturneSU.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;


public class ModBlockStateProvider extends BlockStateProvider {


    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, NocturneSU.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.BLOODSTONE_BLOCK);
        blockWithItem(ModBlocks.BLOODSTONE_ORE);
        blockWithItem(ModBlocks.TIN_ORE);
        blockWithItem(ModBlocks.TIN_BLOCK);
        blockWithItem(ModBlocks.BRONZE_BLOCK);
        blockWithItem(ModBlocks.MAGIC_BLOCK);

        stairsBlock(((StairBlock) ModBlocks.BLOODSTONE_STAIRS.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        slabBlock(((SlabBlock) ModBlocks.BLOODSTONE_SLAB.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));


        buttonBlock(((ButtonBlock) ModBlocks.BLOODSTONE_BUTTON.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        pressurePlateBlock(((PressurePlateBlock) ModBlocks.BLOODSTONE_PRESSURE_PLATE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));

        fenceBlock(((FenceBlock) ModBlocks.BLOODSTONE_FENCE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        fenceGateBlock(((FenceGateBlock) ModBlocks.BLOODSTONE_FENCE_GATE.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));
        wallBlock(((WallBlock) ModBlocks.BLOODSTONE_WALL.get()), blockTexture(ModBlocks.BLOODSTONE_BLOCK.get()));


        doorBlockWithRenderType(((DoorBlock) ModBlocks.BLOODSTONE_DOOR.get()),modLoc("block/bloodstone_door_bottom"), modLoc("block/bloodstone_door_top"), "cutout");
        trapdoorBlockWithRenderType(((TrapDoorBlock) ModBlocks.BLOODSTONE_TRAPDOOR.get()),modLoc("block/bloodstone_trapdoor"), true,"cutout");


        blockItem(ModBlocks.BLOODSTONE_STAIRS);
        blockItem(ModBlocks.BLOODSTONE_SLAB);
        blockItem(ModBlocks.BLOODSTONE_PRESSURE_PLATE);
        blockItem(ModBlocks.BLOODSTONE_FENCE_GATE);
        blockItem(ModBlocks.BLOODSTONE_TRAPDOOR, "_bottom");


    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("nocturnesu:block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile("nocturnesu:block/" + deferredBlock.getId().getPath() + appendix));
    }

}

