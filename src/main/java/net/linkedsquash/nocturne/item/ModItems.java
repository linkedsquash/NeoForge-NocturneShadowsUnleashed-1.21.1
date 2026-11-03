package net.linkedsquash.nocturne.item;

import net.linkedsquash.nocturne.Nocturne;
import net.linkedsquash.nocturne.item.custom.BloodPeachItem;
import net.linkedsquash.nocturne.item.custom.FuelItem;
import net.linkedsquash.nocturne.item.custom.WhipItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class   ModItems {
    // Hold this mod's item registrations until Forge processes them during startup.
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Nocturne.MOD_ID);

    public static final DeferredItem<Item> BELMONT_WHIP = ITEMS.register("belmont_whip", () -> new WhipItem(new Item.Properties().durability(1000)));

    public static final DeferredItem<Item>  WHIP = ITEMS.register("whip", () -> new WhipItem(new Item.Properties().durability(200)));

    public static final DeferredItem<Item> BLOODSTONE = ITEMS.register("bloodstone", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BLOOD_PEACH = ITEMS.register("blood_peach", () -> new BloodPeachItem(new Item.Properties().food(ModFoods.BLOOD_PEACH)));

    public static final DeferredItem<Item> EXPLOSIVE_STONE = ITEMS.register("explosive_stone", () -> new FuelItem(new Item.Properties(), 3200));

    public static final DeferredItem<Item> RAW_TIN = ITEMS.register("raw_tin", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIN_INGOT = ITEMS.register("tin_ingot", () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> BRONZE_INGOT = ITEMS.register("bronze_ingot", () -> new Item(new Item.Properties()));

    public static final DeferredItem<SwordItem> BLOODSTONE_SWORD = ITEMS.register("bloodstone_sword",
            () -> new SwordItem(ModToolTiers.BLOOD_TIER, new Item.Properties()));

    public static final DeferredItem<ShovelItem> BLOODSTONE_SHOVEL = ITEMS.register("bloodstone_shovel",
            () -> new ShovelItem(ModToolTiers.BLOOD_TIER, new Item.Properties()));

    public static final DeferredItem<PickaxeItem> BLOODSTONE_PICKAXE = ITEMS.register("bloodstone_pickaxe",
            () -> new PickaxeItem(ModToolTiers.BLOOD_TIER, new Item.Properties()));

    public static final DeferredItem<AxeItem> BLOODSTONE_AXE = ITEMS.register("bloodstone_axe",
            () -> new AxeItem(ModToolTiers.BLOOD_TIER, new Item.Properties()));

    public static final DeferredItem<HoeItem> BLOODSTONE_HOE = ITEMS.register("bloodstone_hoe",
            () -> new HoeItem(ModToolTiers.BLOOD_TIER, new Item.Properties()));

    public static final DeferredItem<ArmorItem> BLOODSTONE_HELMET = ITEMS.register("bloodstone_helmet",
            () -> new ArmorItem(ModArmorMaterials.BLOODSTONE_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Properties().durability(ArmorItem.Type.HELMET.getDurability(19))));
    public static final DeferredItem<ArmorItem> BLOODSTONE_CHESTPLATE = ITEMS.register("bloodstone_chestplate",
            () -> new ArmorItem(ModArmorMaterials.BLOODSTONE_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().durability(ArmorItem.Type.CHESTPLATE.getDurability(19))));
    public static final DeferredItem<ArmorItem> BLOODSTONE_LEGGINGS = ITEMS.register("bloodstone_leggings",
            () -> new ArmorItem(ModArmorMaterials.BLOODSTONE_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().durability(ArmorItem.Type.LEGGINGS.getDurability(19))));
    public static final DeferredItem<ArmorItem> BLOODSTONE_BOOTS = ITEMS.register("bloodstone_boots",
            () -> new ArmorItem(ModArmorMaterials.BLOODSTONE_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Properties().durability(ArmorItem.Type.BOOTS.getDurability(19))));

    public static void register (IEventBus eventBus){
        // Attach item registration to Forge's startup event bus.
        ITEMS.register(eventBus);
    }
}
