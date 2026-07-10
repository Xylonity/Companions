package dev.xylonity.companions.registry;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.*;
import dev.xylonity.companions.common.item.armor.BloodArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoBloodArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoHolyRobeArmorItem;
import dev.xylonity.companions.common.item.armor.GeckoMageArmorItem;
import dev.xylonity.companions.common.item.book.books.*;
import dev.xylonity.companions.common.item.weapon.BloodAxeItem;
import dev.xylonity.companions.common.item.weapon.BloodScytheItem;
import dev.xylonity.companions.common.item.weapon.BloodSwordItem;
import dev.xylonity.companions.common.material.ArmorMaterials;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.knightlib.api.registrar.ResourceDispatcher;
import dev.xylonity.knightlib.api.registrar.ResourceEntry;
import dev.xylonity.knightlib.api.registrar.ResourceRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

public class CompanionsItems {

    public static final ResourceRegistry<Item> ITEMS = ResourceDispatcher.create(BuiltInRegistries.ITEM, Companions.MOD_ID);

    public static final ResourceEntry<Item> NETHERITE_CHAINS = ITEMS.register("netherite_chains", () -> new TooltipItem(new Item.Properties(), "netherite_chains"));
    public static final ResourceEntry<Item> BIG_BREAD = ITEMS.register("big_bread", () -> new TooltipItem(new Item.Properties().stacksTo(16).food((new FoodProperties.Builder()).nutrition(10).saturationModifier(0.6F).build()), "big_bread"));
    public static final ResourceEntry<Item> SOUL_GEM = ITEMS.register("soul_gem", () -> new TooltipItem(new Item.Properties(), "soul_gem"));
    public static final ResourceEntry<Item> CANNON_ARM = ITEMS.register("cannon_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "cannon_arm"));
    public static final ResourceEntry<Item> WHIP_ARM = ITEMS.register("whip_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "whip_arm"));
    public static final ResourceEntry<Item> BLADE_ARM = ITEMS.register("blade_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "blade_arm"));
    public static final ResourceEntry<Item> MUTANT_ARM = ITEMS.register("mutant_arm", () -> new PuppetArm(new Item.Properties().stacksTo(1), "mutant_arm"));
    public static final ResourceEntry<Item> NETHERITE_DAGGER = ITEMS.register("netherite_dagger", () -> new SwordItem(ItemMaterials.NETHERITE_DAGGER, (new Item.Properties()).fireResistant().attributes(SwordItem.createAttributes(ItemMaterials.NETHERITE_DAGGER, 2, -2f))));
    public static final ResourceEntry<Item> SAINT_KLIMT_MUSIC_DISC = ITEMS.register("saint_klimt_music_disc", () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.JUKEBOX_SONG, Companions.of("saint_klimt")))));
    public static final ResourceEntry<Item> MUTANT_FLESH = ITEMS.register("mutant_flesh", () -> new MutantFlesh(new Item.Properties().food((new FoodProperties.Builder()).nutrition(5).saturationModifier(0.3F).build()), "mutant_flesh"));
    public static final ResourceEntry<Item> ANTLION_FUR = ITEMS.register("antlion_fur", () -> new AntlionFur(new Item.Properties().food((new FoodProperties.Builder()).nutrition(4).saturationModifier(0.4F).build()), "antlion_fur"));
    public static final ResourceEntry<Item> DEMON_FLESH = ITEMS.register("demon_flesh", () -> new TooltipItem(new Item.Properties().food((new FoodProperties.Builder()).nutrition(10).saturationModifier(0.6F).build()), "demon_flesh"));
    public static final ResourceEntry<Item> OLD_CLOTH = ITEMS.register("old_cloth", () -> new TooltipItem(new Item.Properties().fireResistant(), "old_cloth"));
    public static final ResourceEntry<Item> RELIC_GOLD = ITEMS.register("relic_gold", () -> new TooltipItem(new Item.Properties().fireResistant(), "relic_gold"));

    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_STRAWBERRY = ITEMS.register("croissant_dragon_strawberry_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_strawberry_armor"));
    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_VANILLA = ITEMS.register("croissant_dragon_vanilla_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_vanilla_armor"));
    public static final ResourceEntry<Item> CROISSANT_DRAGON_ARMOR_CHOCOLATE = ITEMS.register("croissant_dragon_chocolate_armor", () -> new CroissantDragonArmor(new Item.Properties(), "croissant_dragon_chocolate_armor"));

    public static final ResourceEntry<Item> BOOK_ICE_SHARD = ITEMS.register("book_ice_shard", () -> new IceShardBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_ICE_TORNADO = ITEMS.register("book_ice_tornado", () -> new IceTornadoBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_FIRE_MARK = ITEMS.register("book_fire_mark", () -> new FireMarkBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_HEAL_RING = ITEMS.register("book_heal_ring", () -> new HealRingBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_STONE_SPIKES = ITEMS.register("book_stone_spikes", () -> new StoneSpikesBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_BRACE = ITEMS.register("book_brace", () -> new BraceBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_MAGIC_RAY = ITEMS.register("book_magic_ray", () -> new MagicRayBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_BLACK_HOLE = ITEMS.register("book_black_hole", () -> new BlackHoleBook(new Item.Properties().stacksTo(1)));
    public static final ResourceEntry<Item> BOOK_NAGINATA = ITEMS.register("book_naginata", () -> new NaginataBook(new Item.Properties().stacksTo(1)));

    public static final ResourceEntry<Item> ETERNAL_LIGHTER = ITEMS.register("eternal_lighter", () -> new EternalLighter(new Item.Properties().stacksTo(1).durability(256)));
    public static final ResourceEntry<Item> WRENCH = ITEMS.register("wrench", () -> new WrenchItem(new Item.Properties().durability(128)));
    public static final ResourceEntry<Item> HOURGLASS = ITEMS.register("hourglass", () -> new HourglassItem(new Item.Properties().stacksTo(1).durability(12)));
    public static final ResourceEntry<Item> SHADOW_BELL = ITEMS.register("shadow_bell", () -> new ShadowBellItem(new Item.Properties().stacksTo(1).fireResistant()));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD = ITEMS.register("crystallized_blood", () -> new CrystallizedBloodItem(new Item.Properties()));
    public static final ResourceEntry<Item> NEEDLE = ITEMS.register("needle", () -> new NeedleItem(new Item.Properties()));

    public static final ResourceEntry<Item> MAGE_HAT = ITEMS.register("mage_hat", () -> new GeckoMageArmorItem(ArmorMaterials.MAGE, ArmorItem.Type.HELMET, new Item.Properties().durability(ArmorMaterials.MAGE_STATS.getDurabilityForType(ArmorItem.Type.HELMET)), "mage_hat"));
    public static final ResourceEntry<Item> MAGE_COAT = ITEMS.register("mage_coat", () -> new GeckoMageArmorItem(ArmorMaterials.MAGE, ArmorItem.Type.CHESTPLATE, new Item.Properties().durability(ArmorMaterials.MAGE_STATS.getDurabilityForType(ArmorItem.Type.CHESTPLATE)), "mage_coat"));
    public static final ResourceEntry<Item> MAGE_LEGGINGS = ITEMS.register("mage_leggings", () -> new GeckoMageArmorItem(ArmorMaterials.MAGE, ArmorItem.Type.LEGGINGS, new Item.Properties().durability(ArmorMaterials.MAGE_STATS.getDurabilityForType(ArmorItem.Type.LEGGINGS)), "mage_leggings"));
    public static final ResourceEntry<Item> MAGE_STAFF = ITEMS.register("mage_staff", () -> new GenericGeckoItem(new Item.Properties().stacksTo(1), "mage_staff"));

    public static final ResourceEntry<Item> HOLY_ROBE_MASK = ITEMS.register("holy_robe_mask", () -> new GeckoHolyRobeArmorItem(ArmorMaterials.HOLY_ROBE, ArmorItem.Type.HELMET, new Item.Properties().stacksTo(1).durability(ArmorMaterials.HOLY_ROBE_STATS.getDurabilityForType(ArmorItem.Type.HELMET)), "holy_robe_mask"));
    public static final ResourceEntry<Item> HOLY_ROBE_COAT = ITEMS.register("holy_robe_coat", () -> new GeckoHolyRobeArmorItem(ArmorMaterials.HOLY_ROBE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1).durability(ArmorMaterials.HOLY_ROBE_STATS.getDurabilityForType(ArmorItem.Type.CHESTPLATE)), "holy_robe_coat"));
    public static final ResourceEntry<Item> HOLY_ROBE_LEGGINGS = ITEMS.register("holy_robe_leggings", () -> new GeckoHolyRobeArmorItem(ArmorMaterials.HOLY_ROBE, ArmorItem.Type.LEGGINGS, new Item.Properties().stacksTo(1).durability(ArmorMaterials.HOLY_ROBE_STATS.getDurabilityForType(ArmorItem.Type.LEGGINGS)), "holy_robe_leggings"));

    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_HELMET = ITEMS.register("crystallized_blood_helmet", () -> new GeckoBloodArmorItem(ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.HELMET, new Item.Properties().fireResistant().durability(ArmorMaterials.CRYSTALLIZED_BLOOD_STATS.getDurabilityForType(ArmorItem.Type.HELMET)), "crystallized_blood_helmet"));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_CHESTPLATE = ITEMS.register("crystallized_blood_chestplate", () -> new BloodArmorItem(ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant().durability(ArmorMaterials.CRYSTALLIZED_BLOOD_STATS.getDurabilityForType(ArmorItem.Type.CHESTPLATE))));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_LEGGINGS = ITEMS.register("crystallized_blood_leggings", () -> new BloodArmorItem(ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.LEGGINGS, new Item.Properties().fireResistant().durability(ArmorMaterials.CRYSTALLIZED_BLOOD_STATS.getDurabilityForType(ArmorItem.Type.LEGGINGS))));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_BOOTS = ITEMS.register("crystallized_blood_boots", () -> new BloodArmorItem(ArmorMaterials.CRYSTALLIZED_BLOOD, ArmorItem.Type.BOOTS, new Item.Properties().fireResistant().durability(ArmorMaterials.CRYSTALLIZED_BLOOD_STATS.getDurabilityForType(ArmorItem.Type.BOOTS))));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_SWORD = ITEMS.register("crystallized_blood_sword", () -> new BloodSwordItem(new Item.Properties().fireResistant(), "crystallized_blood_sword", ItemMaterials.CRYSTALLIZED_BLOOD, 3f, -2.4F));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_SCYTHE = ITEMS.register("crystallized_blood_scythe", () -> new BloodScytheItem(new Item.Properties().fireResistant(), "crystallized_blood_scythe", ItemMaterials.CRYSTALLIZED_BLOOD, 1f, -2.8F));
    public static final ResourceEntry<Item> CRYSTALLIZED_BLOOD_AXE = ITEMS.register("crystallized_blood_axe", () -> new BloodAxeItem(new Item.Properties().fireResistant(), "crystallized_blood_axe", ItemMaterials.CRYSTALLIZED_BLOOD, 5f, -3.0F));

    public static final ResourceEntry<Item> CORNELIUS_SPAWN_EGG = ITEMS.registerSpawnEgg("cornelius_spawn_egg", CompanionsEntities.CORNELIUS, 0x92b475, 0x57565c, new Item.Properties());
    public static final ResourceEntry<Item> TEDDY_SPAWN_EGG = ITEMS.registerSpawnEgg("teddy_spawn_egg", CompanionsEntities.TEDDY, 0x765b47, 0xa475b1, new Item.Properties());
    public static final ResourceEntry<Item> WILD_ANTLION_SPAWN_EGG = ITEMS.registerSpawnEgg("wild_antlion_spawn_egg", CompanionsEntities.WILD_ANTLION, 0xb5ae86, 0x66563f, new Item.Properties());
    public static final ResourceEntry<Item> BROKEN_DINAMO_SPAWN_EGG = ITEMS.registerSpawnEgg("broken_dinamo_spawn_egg", CompanionsEntities.BROKEN_DINAMO, 0x8d7441, 0xafafaf, new Item.Properties());
    public static final ResourceEntry<Item> HOSTILE_IMP_SPAWN_EGG = ITEMS.registerSpawnEgg("hostile_imp_spawn_egg", CompanionsEntities.HOSTILE_IMP, 0x47353a, 0x87496e, new Item.Properties());
    public static final ResourceEntry<Item> GOLDEN_ALLAY_SPAWN_EGG = ITEMS.registerSpawnEgg("golden_allay_spawn_egg", CompanionsEntities.GOLDEN_ALLAY, 0xa070d8, 0xf2db6a, new Item.Properties());
    public static final ResourceEntry<Item> SACRED_PONTIFF_SPAWN_EGG = ITEMS.registerSpawnEgg("sacred_pontiff_spawn_egg", CompanionsEntities.SACRED_PONTIFF, 0x4c604f, 0x8b6f51, new Item.Properties());
    public static final ResourceEntry<Item> LIVING_CANDLE_SPAWN_EGG = ITEMS.registerSpawnEgg("living_candle_spawn_egg", CompanionsEntities.LIVING_CANDLE, 0xfff67c, 0xfde4ab, new Item.Properties());
    public static final ResourceEntry<Item> ILLAGER_GOLEM_SPAWN_EGG = ITEMS.registerSpawnEgg("illager_golem_spawn_egg", CompanionsEntities.ILLAGER_GOLEM, 0x8d7441, 0xafafaf, new Item.Properties());
    public static final ResourceEntry<Item> HOSTILE_PUPPET_GLOVE_SPAWN_EGG = ITEMS.registerSpawnEgg("hostile_puppet_glove_spawn_egg", CompanionsEntities.HOSTILE_PUPPET_GLOVE, 0xe7e7e7, 0x1a1a1a, new Item.Properties());

}
