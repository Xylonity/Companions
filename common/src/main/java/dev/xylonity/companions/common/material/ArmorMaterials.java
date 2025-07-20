package dev.xylonity.companions.common.material;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Supplier;

public enum ArmorMaterials implements ArmorMaterial {
    MAGE(
            "mage",
            () -> Ingredient.of(Items.DIAMOND),
            () -> new ArmorStats(CompanionsConfig.MAGE_SET_STATS)
    ),
    HOLY_ROBE(
            "holy_robe",
            () -> Ingredient.of(Items.DIAMOND),
            () -> new ArmorStats(CompanionsConfig.HOLY_ROBE_SET_STATS)
    ),
    CRYSTALLIZED_BLOOD(
            "crystallized_blood",
            () -> Ingredient.of(Items.DIAMOND),
            () -> new ArmorStats(CompanionsConfig.CRYSTALLIZED_BLOOD_SET_STATS)
    );

    private final String name;
    private final Supplier<Ingredient> repairIngredient;
    private final Supplier<ArmorStats> statsSupplier;

    ArmorMaterials(String name, Supplier<Ingredient> repairIngredient, Supplier<ArmorStats> sup) {
        this.name = name;
        this.repairIngredient = repairIngredient;
        this.statsSupplier = sup;
    }

    private ArmorStats stats() {
        return statsSupplier.get();
    }

    @Override
    public int getDurabilityForType(ArmorItem.@NotNull Type type) {
        return HEALTH_FUNCTION_FOR_TYPE.get(type) * (int) stats().durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type type) {
        return toProtectionMap(stats().prot).get(type);
    }

    @Override
    public int getEnchantmentValue() {
        return 20;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public @NotNull String getName() {
        return CompanionsCommon.MOD_ID + ":" + name;
    }

    @Override
    public float getToughness() {
        return (float) stats().toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return (float) stats().knockbackResistance;
    }

    private static EnumMap<ArmorItem.Type, Integer> toProtectionMap(int[] prot) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.HELMET, prot[0]);
        map.put(ArmorItem.Type.CHESTPLATE, prot[1]);
        map.put(ArmorItem.Type.LEGGINGS, prot[2]);
        map.put(ArmorItem.Type.BOOTS, prot[3]);
        return map;
    }

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(
        new EnumMap<>(ArmorItem.Type.class), m -> {
            m.put(ArmorItem.Type.BOOTS, 13);
            m.put(ArmorItem.Type.LEGGINGS, 15);
            m.put(ArmorItem.Type.CHESTPLATE, 16);
            m.put(ArmorItem.Type.HELMET, 11);
        }
    );

    private static class ArmorStats {
        final int[] prot;
        final double toughness;
        final double knockbackResistance;
        final double durabilityMultiplier;

        ArmorStats(String s) {
            String[] parts = s.trim().split("\\s*,\\s*");
            if (parts.length != 7) {
                throw new IllegalArgumentException("[Companions!] Invalid armor stats format! Expected 7 values for " + s);
            }

            prot = new int[]{parse(parts[0]), parse(parts[1]), parse(parts[2]), parse(parts[3])};
            toughness = Double.parseDouble(parts[4]);
            knockbackResistance = Double.parseDouble(parts[5]);
            durabilityMultiplier = Double.parseDouble(parts[6]);
        }

        private static int parse(String s) {
            return Integer.parseInt(s.trim());
        }

    }

}
