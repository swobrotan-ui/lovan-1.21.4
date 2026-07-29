package ru.levin.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class BuyList {
    public static Map<Item, Integer> itemsMapFt = new HashMap<>();
    static {
        itemsMapFt.put(Items.NETHERITE_HELMET, 60000);
        itemsMapFt.put(Items.NETHERITE_CHESTPLATE, 60000);
        itemsMapFt.put(Items.NETHERITE_LEGGINGS, 60000);
        itemsMapFt.put(Items.NETHERITE_BOOTS, 60000);
        itemsMapFt.put(Items.NETHERITE_AXE, 60000);
        itemsMapFt.put(Items.NETHERITE_PICKAXE, 100000);
        itemsMapFt.put(Items.NETHERITE_SWORD, 50000);
        itemsMapFt.put(Items.NETHERITE_SHOVEL, 10000);
        itemsMapFt.put(Items.NETHERITE_HOE, 10000);
        itemsMapFt.put(Items.NETHER_STAR, 10000);
        itemsMapFt.put(Items.NETHERITE_SCRAP, 20000);
        itemsMapFt.put(Items.NETHERITE_INGOT, 200000);
        itemsMapFt.put(Items.NETHERITE_BLOCK, 10000);
        itemsMapFt.put(Items.ANCIENT_DEBRIS, 25000);
        itemsMapFt.put(Items.WITHER_SKELETON_SKULL, 20000);
        itemsMapFt.put(Items.DIAMOND_BLOCK, 6000);
        itemsMapFt.put(Items.CROSSBOW, 5000);
        itemsMapFt.put(Items.GOLDEN_PICKAXE, 10000);
        itemsMapFt.put(Items.ELYTRA, 120000);
        itemsMapFt.put(Items.EMERALD_ORE, 15000);
        itemsMapFt.put(Items.EMERALD_BLOCK, 5000);
        itemsMapFt.put(Items.TOTEM_OF_UNDYING, 20000);
        itemsMapFt.put(Items.CAMPFIRE, 10000);
        itemsMapFt.put(Items.BEACON, 25000);
        itemsMapFt.put(Items.SPAWNER, 1000000);
//        itemsMapFt.put(Items.TRIPWIRE_HOOK, 10000);
        itemsMapFt.put(Items.DRAGON_HEAD, 10000);
        itemsMapFt.put(Items.ENDERMAN_SPAWN_EGG, 100000);
        itemsMapFt.put(Items.WANDERING_TRADER_SPAWN_EGG, 100000);
        itemsMapFt.put(Items.ENCHANTED_GOLDEN_APPLE, 100000);
        itemsMapFt.put(Items.VILLAGER_SPAWN_EGG, 100000);
        itemsMapFt.put(Items.ZOMBIE_VILLAGER_SPAWN_EGG, 100000);
        itemsMapFt.put(Items.CREEPER_SPAWN_EGG, 5000);
        itemsMapFt.put(Items.CREEPER_HEAD, 20000);
//        itemsMapFt.put(Items.PLAYER_HEAD, 10000);
        itemsMapFt.put(Items.SHULKER_BOX, 25000);
        itemsMapFt.put(Items.WHITE_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.ORANGE_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.MAGENTA_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.LIGHT_BLUE_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.YELLOW_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.LIME_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.PINK_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.GRAY_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.LIGHT_GRAY_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.CYAN_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.PURPLE_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.BLUE_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.BROWN_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.GREEN_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.RED_SHULKER_BOX, 25000);
        itemsMapFt.put(Items.BLACK_SHULKER_BOX, 25000);
//        itemsMapFt.put(Items.POTION, 10000);
//        itemsMapFt.put(Items.DRIED_KELP, 10000);
//        itemsMapFt.put(Items.FIRE_CHARGE, 10000);
    }

    public static Map<Item, Integer> itemsMapneed16stackft = new HashMap<>();

    static {

        itemsMapneed16stackft.put(Items.EMERALD, 15000);
        itemsMapneed16stackft.put(Items.DIAMOND_ORE, 1000);
        itemsMapneed16stackft.put(Items.DIAMOND, 40000);
        itemsMapneed16stackft.put(Items.SHULKER_SHELL, 20000);
        itemsMapneed16stackft.put(Items.GOLDEN_APPLE, 25000);
        itemsMapneed16stackft.put(Items.APPLE, 40000);
        itemsMapneed16stackft.put(Items.GUNPOWDER, 75000);
        itemsMapneed16stackft.put(Items.FIREWORK_ROCKET, 15000);
        itemsMapneed16stackft.put(Items.IRON_BLOCK, 30000);
        itemsMapneed16stackft.put(Items.IRON_INGOT, 5000);
//        itemsMapneed16stackft.put(Items.IRON_NUGGET, 5000);
        itemsMapneed16stackft.put(Items.GOLD_BLOCK, 10000);
        itemsMapneed16stackft.put(Items.ENDER_PEARL, 25000);
//        itemsMapneed16stackft.put(Items.TIPPED_ARROW, 5000);
        itemsMapneed16stackft.put(Items.COBWEB, 25000);
        itemsMapneed16stackft.put(Items.CHORUS_FLOWER, 10000);
        itemsMapneed16stackft.put(Items.COOKED_PORKCHOP, 7500);
        itemsMapneed16stackft.put(Items.DRAGON_BREATH, 5000);
    }

    public static Map<Item, Integer> itemsMapneed32stackft = new HashMap<>();

    static {

        itemsMapneed32stackft.put(Items.GLOWSTONE, 10000);
        itemsMapneed32stackft.put(Items.PUFFERFISH, 10000);
        itemsMapneed32stackft.put(Items.BLAZE_POWDER, 10000);
        itemsMapneed32stackft.put(Items.BLAZE_ROD, 10000);
        itemsMapneed32stackft.put(Items.OBSIDIAN, 20000);
        itemsMapneed32stackft.put(Items.GOLD_INGOT, 10000);
        itemsMapneed32stackft.put(Items.GOLDEN_CARROT, 10000);
        itemsMapneed32stackft.put(Items.CHORUS_FLOWER, 35000);
//        itemsMapneed32stackft.put(Items.SAND, 10000);


    }

    public static Map<Item, Integer> itemsMapDonFt = new HashMap<>();
    static {
        itemsMapDonFt.put(Items.IRON_NUGGET, 5000);
        itemsMapDonFt.put(Items.FIRE_CHARGE, 10000);
        itemsMapDonFt.put(Items.DRIED_KELP, 10000);
        itemsMapDonFt.put(Items.TRIPWIRE_HOOK, 100000);
        itemsMapDonFt.put(Items.PLAYER_HEAD, 100000);
        itemsMapDonFt.put(Items.TNT, 50000);
        itemsMapDonFt.put(Items.EXPERIENCE_BOTTLE, 40000);
        itemsMapDonFt.put(Items.ENDER_EYE, 20000);
        itemsMapDonFt.put(Items.SPLASH_POTION, 3000);
    }
}
