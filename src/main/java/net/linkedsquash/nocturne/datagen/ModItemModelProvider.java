package net.linkedsquash.nocturne.datagen;


import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.block.ModBlocks;
import net.linkedsquash.nocturne.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.LinkedHashMap;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Nocturne.MOD_ID, existingFileHelper);
    }
    private static LinkedHashMap<ResourceKey<TrimMaterial>, Float> trimMaterials = new LinkedHashMap<>();
    static {
        trimMaterials.put(TrimMaterials.QUARTZ, 0.1F);
        trimMaterials.put(TrimMaterials.IRON, 0.2F);
        trimMaterials.put(TrimMaterials.NETHERITE, 0.3F);
        trimMaterials.put(TrimMaterials.REDSTONE, 0.4F);
        trimMaterials.put(TrimMaterials.COPPER, 0.5F);
        trimMaterials.put(TrimMaterials.GOLD, 0.6F);
        trimMaterials.put(TrimMaterials.EMERALD, 0.7F);
        trimMaterials.put(TrimMaterials.DIAMOND, 0.8F);
        trimMaterials.put(TrimMaterials.LAPIS, 0.9F);
        trimMaterials.put(TrimMaterials.AMETHYST, 1.0F);
    }

    @Override
    protected void registerModels() {

        // The regular whip keeps its hand-authored 3D/2D perspective model in main resources.
        // Belmont gets a generated flat model because its texture is separate.
        whip_parent(ModItems.BELMONT_WHIP);


        basicItem(ModItems.BLOODSTONE.get());
        basicItem(ModItems.BLOOD_PEACH.get());
        basicItem(ModItems.EXPLOSIVE_STONE.get());
        basicItem(ModItems.TIN_INGOT.get());
        basicItem(ModItems.RAW_TIN.get());
        basicItem(ModItems.BRONZE_INGOT.get());

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

        trimmedArmorItem(ModItems.BLOODSTONE_HELMET);
        trimmedArmorItem(ModItems.BLOODSTONE_CHESTPLATE);
        trimmedArmorItem(ModItems.BLOODSTONE_LEGGINGS);
        trimmedArmorItem(ModItems.BLOODSTONE_BOOTS);

        // A door inventory item uses its flat item texture; the door block has separate top/bottom models.
        basicItem(ModBlocks.BLOODSTONE_DOOR.asItem());



    }
    private void trimmedArmorItem(DeferredItem<ArmorItem> itemDeferredItem) {
        final String MOD_ID = Nocturne.MOD_ID; // Change this to your mod id

        if(itemDeferredItem.get() instanceof ArmorItem armorItem) {
            trimMaterials.forEach((trimMaterial, value) -> {
                float trimValue = value;

                String armorType = switch (armorItem.getEquipmentSlot()) {
                    case HEAD -> "helmet";
                    case CHEST -> "chestplate";
                    case LEGS -> "leggings";
                    case FEET -> "boots";
                    default -> "";
                };

                String armorItemPath = armorItem.toString();
                String trimPath = "trims/items/" + armorType + "_trim_" + trimMaterial.location().getPath();
                String currentTrimName = armorItemPath + "_" + trimMaterial.location().getPath() + "_trim";
                ResourceLocation armorItemResLoc = ResourceLocation.parse(armorItemPath);
                ResourceLocation trimResLoc = ResourceLocation.parse(trimPath); // minecraft namespace
                ResourceLocation trimNameResLoc = ResourceLocation.parse(currentTrimName);

                // This is used for making the ExistingFileHelper acknowledge that this texture exist, so this will
                // avoid an IllegalArgumentException
                existingFileHelper.trackGenerated(trimResLoc, PackType.CLIENT_RESOURCES, ".png", "textures");

                // Trimmed armorItem files
                getBuilder(currentTrimName)
                        .parent(new ModelFile.UncheckedModelFile("item/generated"))
                        .texture("layer0", armorItemResLoc.getNamespace() + ":item/" + armorItemResLoc.getPath())
                        .texture("layer1", trimResLoc);

                // Non-trimmed armorItem file (normal variant)
                this.withExistingParent(itemDeferredItem.getId().getPath(),
                                mcLoc("item/generated"))
                        .override()
                        .model(new ModelFile.UncheckedModelFile(trimNameResLoc.getNamespace()  + ":item/" + trimNameResLoc.getPath()))
                        .predicate(mcLoc("trim_type"), trimValue).end()
                        .texture("layer0",
                                ResourceLocation.fromNamespaceAndPath(MOD_ID,
                                        "item/" + itemDeferredItem.getId().getPath()));
            });
        }
    }
    private ItemModelBuilder whip_parent(DeferredItem<Item> item) {
        // modLoc is required here; mcLoc would incorrectly search Minecraft's namespace for our parent model.
        return withExistingParent(item.getId().getPath(), modLoc("item/whip_parent"))
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID, "item/" + item.getId().getPath()));
    }

    public void buttonItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/button_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
