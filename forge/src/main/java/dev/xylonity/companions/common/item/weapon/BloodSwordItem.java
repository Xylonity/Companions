package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.entity.projectile.BloodSlashProjectile;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import dev.xylonity.companions.common.item.generic.GenericGeckoSwordItem;
import dev.xylonity.companions.common.material.ItemMaterials;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BloodSwordItem extends GenericGeckoSwordItem {

    public BloodSwordItem(Properties properties, String resourceKey, ItemMaterials material, float extraDamage, float extraSpeed) {
        super(properties, resourceKey, material, extraDamage, extraSpeed);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!pLevel.isClientSide) {
            BloodSlashProjectile slash = CompanionsEntities.BLOOD_SLASH_PROJECTILE.get().create(pLevel);
            if (slash != null) {
                Vec3 spawnPos = player.position().add(0, player.getEyeHeight(), 0);
                slash.setOwner(player);
                slash.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                slash.setDeltaMovement(player.getLookAngle().scale(HolinessStartProjectile.SPEED));
                slash.setNoGravity(true);
                pLevel.addFreshEntity(slash);
            }

            player.getCooldowns().addCooldown(this, CompanionsConfig.CRYSTALLIZED_BLOOD_SWORD_COOLDOWN);
        }

        return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide());
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
                                .translatable("tooltip.item.companions.crystallized_blood_sword")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );

        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_sword_desc_1")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_sword_desc_2")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_sword_desc_3")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );

        super.appendHoverText(stack, context, pTooltipComponents, flag);
    }

}