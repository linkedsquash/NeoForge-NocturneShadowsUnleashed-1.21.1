package net.linkedsquash.nocturne.item;

import net.minecraft.world.food.FoodProperties;

public class ModFoods {

    public static final FoodProperties BLOOD_PEACH = new FoodProperties.Builder().nutrition(4).fast().alwaysEdible()
            .saturationModifier(0.3f)
            .build();

}
