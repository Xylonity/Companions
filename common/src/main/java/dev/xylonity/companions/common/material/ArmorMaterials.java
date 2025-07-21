package dev.xylonity.companions.common.material;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.EnumMap;
import java.util.List;

public final class ArmorMaterials {

    public static void init() { ;; }

    public static final Holder<ArmorMaterial> MAGE = register("mage", CompanionsConfig.MAGE_SET_STATS, Items.DIAMOND, SoundEvents.ARMOR_EQUIP_DIAMOND);
    public static final Holder<ArmorMaterial> HOLY_ROBE = register("holy_robe", CompanionsConfig.HOLY_ROBE_SET_STATS, Items.DIAMOND, SoundEvents.ARMOR_EQUIP_DIAMOND);
    public static final Holder<ArmorMaterial> CRYSTALLIZED_BLOOD = register("crystallized_blood", CompanionsConfig.CRYSTALLIZED_BLOOD_SET_STATS, Items.DIAMOND, SoundEvents.ARMOR_EQUIP_DIAMOND);

    private static Holder<ArmorMaterial> register(String name, String statsCsv, ItemLike repairItem, Holder<SoundEvent> equipSound) {
        String[] parts = statsCsv.trim().split("\\s*,\\s*");

        if (parts.length != 7) {
            throw new IllegalArgumentException("[Companions!] Invalid armor stats: " + statsCsv);
        }

        EnumMap<ArmorItem.Type, Integer> defense = new EnumMap<>(ArmorItem.Type.class);
        defense.put(ArmorItem.Type.HELMET, Integer.parseInt(parts[0]));
        defense.put(ArmorItem.Type.CHESTPLATE, Integer.parseInt(parts[1]));
        defense.put(ArmorItem.Type.LEGGINGS, Integer.parseInt(parts[2]));
        defense.put(ArmorItem.Type.BOOTS, Integer.parseInt(parts[3]));

        float toughness = Float.parseFloat(parts[4]);
        float knockback = Float.parseFloat(parts[5]);

        return CompanionsCommon.COMMON_PLATFORM.registerArmorMaterial(name,
                () -> new ArmorMaterial(
                        defense, 20, equipSound, () -> Ingredient.of(repairItem),
                        List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, name))),
                        toughness, knockback
                )
        );
    }

}
