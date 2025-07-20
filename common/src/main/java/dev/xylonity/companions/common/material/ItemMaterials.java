package dev.xylonity.companions.common.material;

import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum ItemMaterials implements Tier {

    NETHERITE_DAGGER(
            "netherite_dagger",
            () -> Ingredient.of(Items.NETHERITE_SCRAP),
            CompanionsConfig.NETHERITE_DAGGER_STATS
    ),

    CRYSTALLIZED_BLOOD(
            "crystallized_blood",
            () -> Ingredient.of(Items.NETHERITE_INGOT),
            CompanionsConfig.CRYSTALLIZED_BLOOD_WEAPON_STATS
    );

    private final String name;
    private final Supplier<Ingredient> repairIngredient;
    private final String statsCsv;

    ItemMaterials(String name, Supplier<Ingredient> repairIngredient, String statsCsv) {
        this.name = name;
        this.repairIngredient = repairIngredient;
        this.statsCsv = statsCsv;
    }

    private Stats stats() {
        return new Stats(statsCsv);
    }

    @Override
    public int getUses() {
        return stats().durability;
    }

    @Override
    public float getSpeed() {
        return stats().miningSpeed;
    }

    @Override
    public float getAttackDamageBonus() {
        return stats().attackDamage;
    }

    @Override
    public int getLevel() {
        return stats().miningLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return stats().enchantability;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String toString() {
        return name;
    }

    private static class Stats {
        final int miningLevel;
        final int durability;
        final float miningSpeed;
        final float attackDamage;
        final int enchantability;

        private Stats(String csv) {
            String[] parts = csv.trim().split("\\s*,\\s*");
            miningLevel = Integer.parseInt(parts[0]);
            durability = Integer.parseInt(parts[1]);
            miningSpeed = Float.parseFloat(parts[2]);
            attackDamage = Float.parseFloat(parts[3]);
            enchantability= Integer.parseInt(parts[4]);
        }
    }

}
