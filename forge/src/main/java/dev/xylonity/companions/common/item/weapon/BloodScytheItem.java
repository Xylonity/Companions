package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.item.generic.GenericGeckoPickaxeItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodScytheItem extends GenericGeckoPickaxeItem {

    public BloodScytheItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        float healthBefore = pTarget.getHealth();

        boolean toRet = super.hurtEnemy(pStack, pTarget, pAttacker);

        pAttacker.heal((healthBefore - pTarget.getHealth()) * (float) CompanionsConfig.CRYSTALLIZED_BLOOD_SCYTHE_LIFE_STEAL);

        return toRet;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag flag) {
        pTooltipComponents.add(Component.translatable("tooltip.icon.companions.star").append(Component
                .translatable("tooltip.item.companions.key.blood_weapon")
                .withStyle(ChatFormatting.YELLOW)));

        pTooltipComponents.add(
                Component.literal(" ")
                        .append(Component
                                .translatable("tooltip.item.companions.key.abilities")
                                .withStyle(ChatFormatting.DARK_GRAY))
        );

        pTooltipComponents.add(
                Component.literal("  ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_scythe")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );

        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_scythe_desc", (int) (CompanionsConfig.CRYSTALLIZED_BLOOD_SCYTHE_LIFE_STEAL * 100))
                                .withStyle(ChatFormatting.RED)
                        )
        );

        super.appendHoverText(stack, context, pTooltipComponents, flag);
    }

}