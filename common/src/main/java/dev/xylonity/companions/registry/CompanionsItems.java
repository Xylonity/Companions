package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.item.EternalLighter;
import dev.xylonity.companions.common.item.PuppetArm;
import dev.xylonity.companions.common.material.ArmorMaterials;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class CompanionsItems {

    public static void init() { ;; }

    public static final Supplier<Item> COPPER_COIN = registerItem("copper_coin", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NETHER_COIN = registerItem("nether_coin", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> END_COIN = registerItem("end_coin", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> NETHERITE_CHAINS = registerItem("netherite_chains", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BIG_BREAD = registerItem("big_bread", () -> new Item(new Item.Properties().stacksTo(1).food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build())));
    public static final Supplier<Item> SOUL_GEM = registerItem("soul_gem", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CANNON_ARM = registerItem("cannon_arm", () -> new PuppetArm(new Item.Properties()));
    public static final Supplier<Item> WHIP_ARM = registerItem("whip_arm", () -> new PuppetArm(new Item.Properties()));
    public static final Supplier<Item> BLADE_ARM = registerItem("blade_arm", () -> new PuppetArm(new Item.Properties()));
    public static final Supplier<Item> MUTANT_ARM = registerItem("mutant_arm", () -> new PuppetArm(new Item.Properties()));

    public static final Supplier<Item> CROISSANT_DRAGON_ARMOR_STRAWBERRY = registerItem("croissant_dragon_strawberry_armor", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CROISSANT_DRAGON_ARMOR_VANILLA = registerItem("croissant_dragon_vanilla_armor", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CROISSANT_DRAGON_ARMOR_CHOCOLATE = registerItem("croissant_dragon_chocolate_armor", () -> new Item(new Item.Properties()));

    public static final Supplier<Item> BOOK_ICE_SHARD = registerMagicBook("book_ice_shard", new Item.Properties().stacksTo(1), MagicType.ICE_SHARD);
    public static final Supplier<Item> BOOK_ICE_TORNADO = registerMagicBook("book_ice_tornado", new Item.Properties().stacksTo(1), MagicType.ICE_TORNADO);
    public static final Supplier<Item> BOOK_FIRE_MARK = registerMagicBook("book_fire_mark", new Item.Properties().stacksTo(1), MagicType.FIRE_MARK);
    public static final Supplier<Item> BOOK_HEAL_RING = registerMagicBook("book_heal_ring", new Item.Properties().stacksTo(1), MagicType.HEAL_RING);
    public static final Supplier<Item> BOOK_STONE_SPIKES = registerMagicBook("book_stone_spikes", new Item.Properties().stacksTo(1), MagicType.STONE_SPIKES);
    public static final Supplier<Item> BOOK_BRACE = registerMagicBook("book_brace", new Item.Properties().stacksTo(1), MagicType.BRACE);
    public static final Supplier<Item> BOOK_MAGIC_RAY = registerMagicBook("book_magic_ray", new Item.Properties().stacksTo(1), MagicType.MAGIC_RAY);
    public static final Supplier<Item> BOOK_BLACK_HOLE = registerMagicBook("book_black_hole", new Item.Properties().stacksTo(1), MagicType.BLACK_HOLE);

    public static final Supplier<Item> ETERNAL_LIGHTER = registerItem("eternal_lighter", () -> new EternalLighter(new Item.Properties()));
    public static final Supplier<Item> WRENCH = registerWrenchItem("wrench", new Item.Properties().durability(64));

    public static final Supplier<Item> MAGE_COAT = registerArmorItem("mage_coat", ArmorMaterials.MAGE, ArmorItem.Type.CHESTPLATE, false);
    public static final Supplier<Item> MAGE_HAT = registerArmorItem("mage_hat", ArmorMaterials.MAGE, ArmorItem.Type.HELMET, true);
    public static final Supplier<Item> MAGE_STAFF = registerItem("mage_staff", () -> new Item(new Item.Properties()));

    private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return CompanionsCommon.COMMON_PLATFORM.registerItem(id, item);
    }

    private static <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterial armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor) {
        return CompanionsCommon.COMMON_PLATFORM.registerArmorItem(id, armorMaterial, armorType, isGeckoArmor);
    }

    private static <T extends Item> Supplier<T> registerWrenchItem(String id, Item.Properties properties) {
        return CompanionsCommon.COMMON_PLATFORM.registerWrenchItem(id, properties);
    }

    private static <T extends Item> Supplier<T> registerMagicBook(String id, Item.Properties properties, MagicType magicType) {
        return CompanionsCommon.COMMON_PLATFORM.registerMagicBook(id, properties, magicType);
    }

    public enum MagicType {
        ICE_SHARD,
        ICE_TORNADO,
        FIRE_MARK,
        HEAL_RING,
        STONE_SPIKES,
        BRACE,
        BLACK_HOLE,
        MAGIC_RAY
    }

}
