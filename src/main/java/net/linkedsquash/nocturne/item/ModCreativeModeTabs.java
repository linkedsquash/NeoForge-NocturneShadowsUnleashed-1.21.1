package net.linkedsquash.nocturne.item;


import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    // Create a manager for adding this mod's tabs to Minecraft's creative inventory.
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Nocturne.MOD_ID);

    // Define the tab and the content that appears inside it.
    public static final Supplier<CreativeModeTab> NOCTURNE_TAB = CREATIVE_MODE_TABS.register("nocturne_tab",
            () -> CreativeModeTab.builder()
                    // Use the BLOODSTONE item as the icon shown on the tab.
                    .icon(() -> new ItemStack(ModItems.BLOODSTONE.get()))
                    // Look up the visible tab name from the language files.
                    .title(Component.translatable("creativetab.nocturne_tab"))
                    // Add the mod's items and blocks to the tab when Minecraft fills it.
                    .displayItems((pParameter, pOutput) -> {
                        pOutput.accept(ModItems.WHIP);
                        pOutput.accept(ModItems.BELMONT_WHIP);
                        pOutput.accept(ModItems.BLOOD_PEACH);
                        pOutput.accept(ModItems.EXPLOSIVE_STONE);
                        pOutput.accept(ModItems.BRONZE_INGOT);
                        pOutput.accept(ModBlocks.BRONZE_BLOCK);
                        pOutput.accept(ModBlocks.TIN_BLOCK);
                        pOutput.accept(ModItems.TIN_INGOT);
                        pOutput.accept(ModItems.RAW_TIN);
                        pOutput.accept(ModBlocks.TIN_ORE);
                        pOutput.accept(ModBlocks.MAGIC_BLOCK);

                        pOutput.accept(ModItems.BLOODSTONE_SWORD);
                        pOutput.accept(ModItems.BLOODSTONE_PICKAXE);
                        pOutput.accept(ModItems.BLOODSTONE_AXE);
                        pOutput.accept(ModItems.BLOODSTONE_SHOVEL);
                        pOutput.accept(ModItems.BLOODSTONE_HOE);

                        pOutput.accept(ModItems.BLOODSTONE_HELMET);
                        pOutput.accept(ModItems.BLOODSTONE_CHESTPLATE);
                        pOutput.accept(ModItems.BLOODSTONE_LEGGINGS);
                        pOutput.accept(ModItems.BLOODSTONE_BOOTS);

                        pOutput.accept(ModItems.BLOODSTONE);
                        pOutput.accept(ModBlocks.BLOODSTONE_BLOCK);
                        pOutput.accept(ModBlocks.BLOODSTONE_ORE);
                        pOutput.accept(ModBlocks.BLOODSTONE_STAIRS);
                        pOutput.accept(ModBlocks.BLOODSTONE_SLAB);
                        pOutput.accept(ModBlocks.BLOODSTONE_DOOR);
                        pOutput.accept(ModBlocks.BLOODSTONE_TRAPDOOR);
                        pOutput.accept(ModBlocks.BLOODSTONE_FENCE);
                        pOutput.accept(ModBlocks.BLOODSTONE_FENCE_GATE);
                        pOutput.accept(ModBlocks.BLOODSTONE_WALL);
                        pOutput.accept(ModBlocks.BLOODSTONE_PRESSURE_PLATE);
                        pOutput.accept(ModBlocks.BLOODSTONE_BUTTON);
                    })
                    .build());

    public static void register(IEventBus eventBus){
        // Connect this tab register to Forge so it can be loaded with the mod.
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
