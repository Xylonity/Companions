package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.*;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.function.ResourceEntry;

public class CompanionsItems {

    public static final ResourceRegistry<Item> ITEMS = ResourceDispatcher.create(BuiltInRegistries.ITEM, Companions.MOD_ID);

    public static final ResourceEntry<Item> NETHERITE_CHAINS = ITEMS.register("netherite_chains", () -> new TooltipItem(new Item.Properties(), "netherite_chains"));
    public static final ResourceEntry<Item> BIG_BREAD = ITEMS.register("big_bread", () -> new TooltipItem(new Item.Properties().stacksTo(16).food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build()), "big_bread"));
    public static final ResourceEntry<Item> SOUL_GEM = ITEMS.register("soul_gem", () -> new TooltipItem(new Item.Properties(), "soul_gem"));
    public static final ResourceEntry<Item> CANNON_ARM = ITEMS.register("cannon_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "cannon_arm"));
    public static final ResourceEntry<Item> WHIP_ARM = ITEMS.register("whip_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "whip_arm"));
    public static final ResourceEntry<Item> BLADE_ARM = ITEMS.register("blade_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "blade_arm"));
    public static final ResourceEntry<Item> MUTANT_ARM = ITEMS.register("mutant_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "mutant_arm"));
    public static final ResourceEntry<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger", () -> new SwordItem(ItemMaterials.NETHERITE_DAGGER, 2, -2f, (new Item.Properties()).fireResistant()));
    public static final ResourceEntry<Item> SAINT_KLIMT_MUSIC_DISC = ITEMS.register("saint_klimt_music_disc", () -> new RecordItem(7, CompanionsSounds.SAINT_KLIMT.get(), new Item.Properties().stacksTo(1), 5800));
    public static final ResourceEntry<Item> MUTANT_FLESH = ITEMS.register("mutant_flesh", () -> new MutantFlesh(new Item.Properties().food((new FoodProperties.Builder()).nutrition(5).saturationMod(0.3F).meat().build()), "mutant_flesh"));
    public static final ResourceEntry<Item> ANTLION_FUR = ITEMS.register("antlion_fur", () -> new AntlionFur(new Item.Properties().food((new FoodProperties.Builder()).nutrition(4).saturationMod(0.4F).meat().build()), "antlion_fur"));
    public static final ResourceEntry<Item> DEMON_FLESH = ITEMS.register("demon_flesh", () -> new TooltipItem(new Item.Properties().food((new FoodProperties.Builder()).nutrition(10).saturationMod(0.6F).meat().build()), "demon_flesh"));
    public static final ResourceEntry<Item> OLD_CLOTH = ITEMS.register("old_cloth", () -> new TooltipItem(new Item.Properties().fireResistant(), "old_cloth"));
    public static final ResourceEntry<Item> RELIC_GOLD = ITEMS.register("relic_gold", () -> new TooltipItem(new Item.Properties().fireResistant(), "relic_gold"));

    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_STRAWBERRY = ITEMS.register("croissant_dragon_strawberry_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_strawberry_armor"));
    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_VANILLA = ITEMS.register("croissant_dragon_vanilla_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_vanilla_armor"));
    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_CHOCOLATE = ITEMS.register("croissant_dragon_chocolate_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_chocolate_armor"));

    public static final ResourceEntry<Item> BOOK_ICE_SHARD = ITEMS.register("book_ice_shard", new Item.Properties().stacksTo(1), MagicType.ICE_SHARD);
    public static final ResourceEntry<Item> BOOK_ICE_TORNADO = ITEMS.register("book_ice_tornado", new Item.Properties().stacksTo(1), MagicType.ICE_TORNADO);
    public static final ResourceEntry<Item> BOOK_FIRE_MARK = ITEMS.register("book_fire_mark", new Item.Properties().stacksTo(1), MagicType.FIRE_MARK);
    public static final ResourceEntry<Item> BOOK_HEAL_RING = ITEMS.register("book_heal_ring", new Item.Properties().stacksTo(1), MagicType.HEAL_RING);
    public static final ResourceEntry<Item> BOOK_STONE_SPIKES = ITEMS.register("book_stone_spikes", new Item.Properties().stacksTo(1), MagicType.STONE_SPIKES);
    public static final ResourceEntry<Item> BOOK_BRACE = ITEMS.register("book_brace", new Item.Properties().stacksTo(1), MagicType.BRACE);
    public static final ResourceEntry<Item> BOOK_MAGIC_RAY = ITEMS.register("book_magic_ray", new Item.Properties().stacksTo(1), MagicType.MAGIC_RAY);
    public static final ResourceEntry<Item> BOOK_BLACK_HOLE = ITEMS.register("book_black_hole", new Item.Properties().stacksTo(1), MagicType.BLACK_HOLE);
    public static final ResourceEntry<Item> BOOK_NAGINATA = ITEMS.register("book_naginata", new Item.Properties().stacksTo(1), MagicType.NAGINATA);

    public static final ResourceEntry<Item> ETERNAL_LIGHTER = ITEMS.register("eternal_lighter", () -> new EternalLighter(new Item.Properties().stacksTo(1).durability(256)));
    public static final ResourceEntry<Item> WRENCH = ITEMS.register("wrench", new Item.Properties().durability(128), ItemType.WRENCH);
    public static final ResourceEntry<Item> HOURGLASS = ITEMS.register("hourglass", new Item.Properties().stacksTo(1).durability(12), ItemType.HOURGLASS);
    public static final ResourceEntry<Item> SHADOW_BELL = ITEMS.register("shadow_bell", new Item.Properties().stacksTo(1).fireResistant(), ItemType.SHADOW_BELL);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD = ITEMS.register("crystallized_blood", new Item.Properties(), ItemType.CRYSTALLIZED_BLOOD);
    public static final ResourceEntry<Item> NEEDLE = ITEMS.register("needle", new Item.Properties(), ItemType.NEEDLE);

    public static final ResourceEntry<Item> MAGE_HAT = ITEMS.register("mage_hat", ArmorMaterials.MAGE, ArmorItem.Type.HELMET, true);
    public static final ResourceEntry<Item> MAGE_COAT = ITEMS.register("mage_coat", ArmorMaterials.MAGE, ArmorItem.Type.CHESTPLATE, true);
    public static final ResourceEntry<Item> MAGE_LEGGINGS = ITEMS.register("mage_leggings", ArmorMaterials.MAGE, ArmorItem.Type.LEGGINGS, true);
    public static final ResourceEntry<Item> MAGE_STAFF = ITEMS.register("mage_staff", new Item.Properties().stacksTo(1), ItemType.GENERIC);

    public static final ResourceEntry<Item> HOLY_ROBE_MASK = ITEMS.register("holy_robe_mask", ArmorMaterials.HOLY_ROBE, ArmorItem.Type.HELMET, true);
    public static final ResourceEntry<Item> HOLY_ROBE_COAT = ITEMS.register("holy_robe_coat", ArmorMaterials.HOLY_ROBE, ArmorItem.Type.CHESTPLATE, true);
    public static final ResourceEntry<Item> HOLY_ROBE_LEGGINGS = ITEMS.register("holy_robe_leggings", ArmorMaterials.HOLY_ROBE, ArmorItem.Type.LEGGINGS, true);

    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_HELMET = ITEMS.register("crystallized_blood_helmet", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.HELMET, true);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_CHESTPLATE = ITEMS.register("crystallized_blood_chestplate", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.CHESTPLATE, false);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_LEGGINGS = ITEMS.register("crystallized_blood_leggings", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.LEGGINGS, false);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_BOOTS = ITEMS.register("crystallized_blood_boots", ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.BOOTS, false);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_SWORD = ITEMS.register("crystallized_blood_sword", new Item.Properties().fireResistant(), ItemType.BLOOD_SWORD, ItemMaterials.CRYSTALLIZED_BLOOD, 3f, -2.4F);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_SCYTHE = ITEMS.register("crystallized_blood_scythe", new Item.Properties().fireResistant(), ItemType.BLOOD_PICKAXE, ItemMaterials.CRYSTALLIZED_BLOOD, 1f, -2.8F);
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_AXE = ITEMS.register("crystallized_blood_axe", new Item.Properties().fireResistant(), ItemType.BLOOD_AXE, ItemMaterials.CRYSTALLIZED_BLOOD, 5f, -3.0F);

}
