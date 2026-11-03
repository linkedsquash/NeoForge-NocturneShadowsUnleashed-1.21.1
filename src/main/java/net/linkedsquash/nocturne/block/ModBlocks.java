package net.linkedsquash.nocturne.block;

import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.item.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Nocturne.MOD_ID);

        // Register a block that copies the strength and other basic properties of a diamond block.
        public static final DeferredBlock<Block> BLOODSTONE_BLOCK = registerBlock("bloodstone_block",
                () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops()));

        //slab and stairs
     public static final DeferredBlock<StairBlock> BLOODSTONE_STAIRS = registerBlock("bloodstone_stairs",
            () -> new StairBlock(ModBlocks.BLOODSTONE_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.of()));
    public static final DeferredBlock<SlabBlock> BLOODSTONE_SLAB = registerBlock("bloodstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()));

    //redstone blocks
    public static final DeferredBlock<ButtonBlock> BLOODSTONE_BUTTON = registerBlock("bloodstone_button",
            () -> new ButtonBlock( BlockSetType.OAK, 20,BlockBehaviour.Properties.of().noCollission()));
    public static final DeferredBlock<PressurePlateBlock> BLOODSTONE_PRESSURE_PLATE = registerBlock("bloodstone_pressure_plate",
            () -> new PressurePlateBlock(BlockSetType.IRON, BlockBehaviour.Properties.of().requiresCorrectToolForDrops()));

    // wall and fences
    public static final DeferredBlock<FenceBlock> BLOODSTONE_FENCE = registerBlock("bloodstone_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of()));
    public static final DeferredBlock<FenceGateBlock> BLOODSTONE_FENCE_GATE = registerBlock("bloodstone_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.of(), SoundEvents.CHAIN_PLACE, SoundEvents.CHAIN_BREAK));
    public static final DeferredBlock<WallBlock> BLOODSTONE_WALL = registerBlock("bloodstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()));


    public static final DeferredBlock<DoorBlock> BLOODSTONE_DOOR = registerBlock("bloodstone_door",
            () -> new DoorBlock(BlockSetType.OAK,BlockBehaviour.Properties.of().noOcclusion()));
    public static final DeferredBlock<TrapDoorBlock> BLOODSTONE_TRAPDOOR = registerBlock("bloodstone_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.OAK,BlockBehaviour.Properties.of().noOcclusion()));

        public static  final DeferredBlock<DropExperienceBlock> BLOODSTONE_ORE = registerBlock("bloodstone_ore",
                () -> new DropExperienceBlock(UniformInt.of(2,5), BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

    public static  final DeferredBlock<DropExperienceBlock> TIN_ORE = registerBlock("tin_ore",
            () -> new DropExperienceBlock(UniformInt.of(2,5), BlockBehaviour.Properties.of().strength(2f).requiresCorrectToolForDrops()));

        public static  final DeferredBlock<Block> MAGIC_BLOCK = registerBlock("magic_block",
                () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops()));

    public static  final DeferredBlock<Block> TIN_BLOCK = registerBlock("tin_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops()));

    public static  final DeferredBlock<Block> BRONZE_BLOCK = registerBlock("bronze_block",
            () -> new Block(BlockBehaviour.Properties.of().strength(2.0F).requiresCorrectToolForDrops()));

        private static <T extends  Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
            DeferredBlock<T> toReturn = BLOCKS.register(name, block);
            registerBlockItem(name, toReturn);
            return  toReturn;
        }

        private static <T extends Block> DeferredItem<BlockItem> registerBlockItem(String name, DeferredBlock<T> block){
                return ModItems.ITEMS.register(name,  () -> new BlockItem(block.get(), new Item.Properties()));
        }
        public static void  register(IEventBus eventBus){
            BLOCKS.register(eventBus);
        }

}
