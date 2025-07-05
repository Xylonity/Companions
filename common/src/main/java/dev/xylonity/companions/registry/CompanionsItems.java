package dev.xylonity.companions.registry;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.item.EternalLighter;
import dev.xylonity.companions.common.item.PuppetArm;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.material.ItemMaterials;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.function.Supplier;

public class CompanionsItems {

    public static void init() { ;; }

    public static final Supplier<Item> NETHERITE_CHAINS = registerItem("netherite_chains", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> BIG_BREAD = registerItem("big_bread", () -> new Item(new Item.Properties().stacksTo(1).food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build())));
    public static final Supplier<Item> NEEDLE = registerItem("needle", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> SOUL_GEM = registerItem("soul_gem", () -> new Item(new Item.Properties()));
    public static final Supplier<Item> CANNON_ARM = registerItem("cannon_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> WHIP_ARM = registerItem("whip_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> BLADE_ARM = registerItem("blade_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> MUTANT_ARM = registerItem("mutant_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> NETHERITE_DAGGER = registerItem("netherite_dagger", () -> new SwordItem(ItemMaterials.NETHERITE_DAGGER, 3, -2f, (new Item.Properties()).fireResistant()));
    public static final Supplier<Item> ANGEL_OF_GERTRUDE_DISC = registerItem("angel_of_gertrude_disc", () -> new RecordItem(7, CompanionsSounds.ANGEL_OF_GERTRUDE.get(), new Item.Properties().stacksTo(1), 5280));
    public static final Supplier<Item> MUTANT_FLESH = registerItem("mutant_flesh", () -> new Item(new Item.Properties().stacksTo(1).food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build())));
    public static final Supplier<Item> DEMON_FLESH = registerItem("demon_flesh", () -> new Item(new Item.Properties().stacksTo(1).food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build())));

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

    public static final Supplier<Item> ETERNAL_LIGHTER = registerItem("eternal_lighter", () -> new EternalLighter(new Item.Properties().stacksTo(1)));
    public static final Supplier<Item> WRENCH = registerSpecificItem("wrench", new Item.Properties().durability(64), ItemType.WRENCH);
    public static final Supplier<Item> HOURGLASS = registerSpecificItem("hourglass", new Item.Properties().stacksTo(1), ItemType.HOURGLASS);
    public static final Supplier<Item> SHADOW_BELL = registerSpecificItem("shadow_bell", new Item.Properties().stacksTo(1).fireResistant(), ItemType.SHADOW_BELL);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD = registerSpecificItem("crystallized_blood", new Item.Properties(), ItemType.CRYSTALLIZED_BLOOD);

    public static final Supplier<Item> MAGE_COAT = registerArmorItem("mage_coat", ArmorMaterials.MAGE, ArmorItem.Type.CHESTPLATE, false);
    public static final Supplier<Item> MAGE_HAT = registerArmorItem("mage_hat", ArmorMaterials.MAGE, ArmorItem.Type.HELMET, true);
    public static final Supplier<Item> MAGE_STAFF = registerItem("mage_staff", () -> new Item(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> CRYSTALLIZED_BLOOD_HELMET = registerArmorItem("crystallized_blood_helmet", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.HELMET, true);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_CHESTPLATE = registerArmorItem("crystallized_blood_chestplate", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.CHESTPLATE, false);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_LEGGINGS = registerArmorItem("crystallized_blood_leggings", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.LEGGINGS, false);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_BOOTS = registerArmorItem("crystallized_blood_boots", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.BOOTS, false);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_SWORD = registerSpecificItem("crystallized_blood_sword", new Item.Properties(), ItemType.BLOOD_SWORD, ItemMaterials.CRYSTALLIZED_BLOOD, 3f, -2.4F);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_SCYTHE = registerSpecificItem("crystallized_blood_scythe", new Item.Properties(), ItemType.BLOOD_PICKAXE, ItemMaterials.CRYSTALLIZED_BLOOD, 1f, -2.8F);
    public static final Supplier<Item> CRYSTALLIZED_BLOOD_AXE = registerSpecificItem("crystallized_blood_axe", new Item.Properties(), ItemType.BLOOD_AXE, ItemMaterials.CRYSTALLIZED_BLOOD, 5f, -3.0F);

    private static <T extends Item> Supplier<T> registerItem(String id, Supplier<T> item) {
        return CompanionsCommon.COMMON_PLATFORM.registerItem(id, item);
    }

    private static <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, ItemType itemType) {
        return CompanionsCommon.COMMON_PLATFORM.registerSpecificItem(id, properties, itemType);
    }

    private static <T extends Item> Supplier<T> registerSpecificItem(String id, Item.Properties properties, ItemType itemType, ItemMaterials material, float extraDamage, float extraSpeed) {
        return CompanionsCommon.COMMON_PLATFORM.registerSpecificItem(id, properties, itemType, material, extraDamage, extraSpeed);
    }

    private static <T extends Item> Supplier<T> registerArmorItem(String id, ArmorMaterial armorMaterial, ArmorItem.Type armorType, boolean isGeckoArmor) {
        return CompanionsCommon.COMMON_PLATFORM.registerArmorItem(id, armorMaterial, armorType, isGeckoArmor);
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

    public enum ItemType {
        HOURGLASS,
        WRENCH,
        SHADOW_BELL,
        CRYSTALLIZED_BLOOD,
        GENERIC, // 3D renderer
        BLOOD_SWORD, // 3D renderer
        BLOOD_PICKAXE, // 3D renderer
        BLOOD_AXE // 3D renderer
    }

}
