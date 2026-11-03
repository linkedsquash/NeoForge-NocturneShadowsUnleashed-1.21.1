package net.linkedsquash.nocturne.util;

import net.linkedsquash.nocturne.Nocturne;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        public static final TagKey<Block> NEEDS_BLOODSTONE_TOOL = createTag("needs-bloodstone_tool");
        public static final TagKey<Block> INCORRECT_FOR_BLOODSTONE_TOOL = createTag("inncorrect-for-bloodstone_tool");


        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID, name));
        };
    }
    public static class Items {

        public static final TagKey<Item> WHIPS = createTag("whips");
        };

        public static final TagKey<Item> TIN = createTag("tin");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(Nocturne.MOD_ID, name));
        };
    }
