package net.linkedsquash.nocturne.datagen;

import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.*;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;


public class ModBlockStateProvider extends BlockStateProvider {


    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Nocturne.MOD_ID, exFileHelper);
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


        // ModItemModelProvider owns these item models, avoiding duplicate output paths.
        blockItem(ModBlocks.BLOODSTONE_TRAPDOOR, "_bottom");


    }

    private void blockWithItem(DeferredBlock<?> deferredBlock) {
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock) {
        // Use the registered mod namespace so generated item models resolve under assets/<mod_id>/.
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(Nocturne.MOD_ID + ":block/" + deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String appendix) {
        // Keep item model references in the same namespace as the generated block models and textures.
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(Nocturne.MOD_ID + ":block/" + deferredBlock.getId().getPath() + appendix));
    }

}

