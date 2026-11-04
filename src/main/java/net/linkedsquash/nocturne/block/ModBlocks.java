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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Nocturne.MOD_ID);
    private static final BlockSetType IRON_SOUNDS_HAND_OPEN =
            BlockSetType.register(new BlockSetType(
                    "nocturne_iron_sounds_hand_open",
                    true, // canOpenByHand
                    BlockSetType.IRON.canOpenByWindCharge(),
                    BlockSetType.IRON.canButtonBeActivatedByArrows(),
                    BlockSetType.IRON.pressurePlateSensitivity(),
                    BlockSetType.IRON.soundType(),
                    BlockSetType.IRON.doorClose(),
                    BlockSetType.IRON.doorOpen(),
                    BlockSetType.IRON.trapdoorClose(),
                    BlockSetType.IRON.trapdoorOpen(),
                    BlockSetType.IRON.pressurePlateClickOff(),
                    BlockSetType.IRON.pressurePlateClickOn(),
                    BlockSetType.IRON.buttonClickOff(),
                    BlockSetType.IRON.buttonClickOn()
            ));

        // Register a block that copies the strength and other basic properties of a diamond block.
        public static final DeferredBlock<Block> BLOODSTONE_BLOCK = registerBlock("bloodstone_block",
                () -> new Block(BlockBehaviour.Properties.of()
                        .strength(5.0F)
                        .sound(SoundType.METAL)
                        .requiresCorrectToolForDrops()));

        //slab and stairs
     public static final DeferredBlock<StairBlock> BLOODSTONE_STAIRS = registerBlock("bloodstone_stairs",
            () -> new StairBlock(ModBlocks.BLOODSTONE_BLOCK.get().defaultBlockState(),BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .strength(3.0F)));
    public static final DeferredBlock<SlabBlock> BLOODSTONE_SLAB = registerBlock("bloodstone_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .strength(3.0F)));

    //redstone blocks
    public static final DeferredBlock<ButtonBlock> BLOODSTONE_BUTTON = registerBlock("bloodstone_button",
            () -> new ButtonBlock( IRON_SOUNDS_HAND_OPEN, 20,BlockBehaviour.Properties.of()
                    .noCollission()
                    .sound(SoundType.METAL)
                    .strength(0.5F)));
    public static final DeferredBlock<PressurePlateBlock> BLOODSTONE_PRESSURE_PLATE = registerBlock("bloodstone_pressure_plate",
            () -> new PressurePlateBlock(IRON_SOUNDS_HAND_OPEN, BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(0.5F)
                    .sound(SoundType.METAL)
            ));

    // wall and fences
    public static final DeferredBlock<FenceBlock> BLOODSTONE_FENCE = registerBlock("bloodstone_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .strength(1.0F)
                    .sound(SoundType.METAL)));
    public static final DeferredBlock<FenceGateBlock> BLOODSTONE_FENCE_GATE = registerBlock("bloodstone_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.of()
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .strength(1.0F), SoundEvents.CHAIN_PLACE, SoundEvents.CHAIN_BREAK));

    public static final DeferredBlock<WallBlock> BLOODSTONE_WALL = registerBlock("bloodstone_wall",
            () -> new WallBlock(BlockBehaviour.Properties.of()
                    .strength(3.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<DoorBlock> BLOODSTONE_DOOR = registerBlock("bloodstone_door",
            () -> new DoorBlock(BlockSetType.IRON,BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(1.0f)
                    .sound(SoundType.METAL)));

    public static final DeferredBlock<TrapDoorBlock> BLOODSTONE_TRAPDOOR = registerBlock("bloodstone_trapdoor",
            () -> new TrapDoorBlock(BlockSetType.IRON,BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .strength(1.0F)
                    .sound(SoundType.METAL)));

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
