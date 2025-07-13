package dev.xylonity.companions.common.material;

import dev.xylonity.companions.CompanionsCommon;
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

    MAGE("mage", 35, convertProtectionArrayToEnumMap(new int[]{ 3, 8, 6, 3 }), 20,
        SoundEvents.ARMOR_EQUIP_DIAMOND, 2.5f, 0.05F, () -> Ingredient.of(Items.DIAMOND)),
    HOLY_ROBE("mage", 35, convertProtectionArrayToEnumMap(new int[]{ 3, 8, 6, 3 }), 20,
            SoundEvents.ARMOR_EQUIP_DIAMOND, 2.5f, 0.05F, () -> Ingredient.of(Items.DIAMOND)),
    CRYSTALLIZED_BLOOD("crystallized_blood", 35, convertProtectionArrayToEnumMap(new int[]{ 3, 8, 6, 3 }), 20,
        SoundEvents.ARMOR_EQUIP_DIAMOND, 2.5f, 0.05F, () -> Ingredient.of(Items.DIAMOND));

    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionAmounts;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    private static EnumMap<ArmorItem.Type, Integer> convertProtectionArrayToEnumMap(int[] protectionAmounts) {
        EnumMap<ArmorItem.Type, Integer> protectionMap = new EnumMap<>(ArmorItem.Type.class);
        protectionMap.put(ArmorItem.Type.HELMET, protectionAmounts[0]);
        protectionMap.put(ArmorItem.Type.CHESTPLATE, protectionAmounts[1]);
        protectionMap.put(ArmorItem.Type.LEGGINGS, protectionAmounts[2]);
        protectionMap.put(ArmorItem.Type.BOOTS, protectionAmounts[3]);
        return protectionMap;
    }

    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), (p_266653_) -> {
        p_266653_.put(ArmorItem.Type.BOOTS, 13);
        p_266653_.put(ArmorItem.Type.LEGGINGS, 15);
        p_266653_.put(ArmorItem.Type.CHESTPLATE, 16);
        p_266653_.put(ArmorItem.Type.HELMET, 11);
    });

    ArmorMaterials(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionAmounts, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protectionAmounts = protectionAmounts;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.@NotNull Type pType) {
        return HEALTH_FUNCTION_FOR_TYPE.get(pType) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.@NotNull Type pType) {
        return this.protectionAmounts.get(pType);
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public @NotNull String getName() {
        return CompanionsCommon.MOD_ID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

}
