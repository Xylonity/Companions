package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.client.item.renderer.GenericAxeItemRenderer;
import dev.xylonity.companions.client.item.renderer.GenericPickaxeItemRenderer;
import dev.xylonity.companions.common.item.gecko.GeckoPickaxeItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class BloodScytheItem extends GeckoPickaxeItem {

    public BloodScytheItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    protected Supplier<Object> createGeckoRenderer() {
        return () -> new GenericPickaxeItemRenderer(resourceKey);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
        float damage = (float) pAttacker.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (pAttacker.level() instanceof ServerLevel serverLevel) {
            damage = EnchantmentHelper.modifyDamage(serverLevel, pStack, pTarget, pAttacker.damageSources().mobAttack(pAttacker), damage);
        }

        pAttacker.heal(damage * (float) CompanionsConfig.CRYSTALLIZED_BLOOD_SCYTHE_LIFE_STEAL);

        return super.hurtEnemy(pStack, pTarget, pAttacker);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull Item.TooltipContext pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
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

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

}
