package net.linkedsquash.nocturne.item;

import net.linkedsquash.nocturne.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {

    public static final Tier BLOOD_TIER = new SimpleTier(
            ModTags.Blocks.INCORRECT_FOR_BLOODSTONE_TOOL, // Tag for blocks this tier cannot break
            500,                                      // Durability / uses
            6.0f,                                      // Mining speed
            2.5f,                                      // Attack damage bonus
            14,                                        // Enchantment value
            () -> Ingredient.of(ModItems.BLOODSTONE) // Repair ingredient supplier
    );


}

