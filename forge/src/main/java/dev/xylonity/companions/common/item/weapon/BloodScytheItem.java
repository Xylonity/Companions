package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.item.generic.GeckoPickAxeItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BloodScytheItem extends GeckoPickAxeItem {

    public BloodScytheItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        float healthBefore = pTarget.getHealth();

        boolean toRet = super.hurtEnemy(pStack, pTarget, pAttacker);

        pAttacker.heal((healthBefore - pTarget.getHealth()) * 0.15f);

        return toRet;
    }

}