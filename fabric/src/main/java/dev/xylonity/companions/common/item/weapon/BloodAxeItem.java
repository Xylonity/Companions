package dev.xylonity.companions.common.item.weapon;

import dev.xylonity.companions.common.entity.projectile.BloodTornadoProjectile;
import dev.xylonity.companions.common.item.generic.GenericGeckoAxeItem;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BloodAxeItem extends GenericGeckoAxeItem {

    public BloodAxeItem(Properties properties, String resourceKey, ItemMaterials material) {
        super(properties, resourceKey, material);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!pLevel.isClientSide) {
            for (int i = 0; i < 4; i++) {
                double angleRad = Math.toRadians(player.getYRot() + i * 90);

                BloodTornadoProjectile tornado = CompanionsEntities.BLOOD_TORNADO_PROJECTILE.create(pLevel);
                if (tornado != null) {
                    tornado.setPos(player.getX(), player.getY() + player.getBbHeight() * 0.4f, player.getZ());

                    tornado.setOwner(player);
                    tornado.setAlpha((float) angleRad);

                    tornado.shoot(-Math.sin(angleRad), 0.0, Math.cos(angleRad), 1.2525f, 0.0f);

                    pLevel.addFreshEntity(tornado);
                }
            }

            player.getCooldowns().addCooldown(this, CompanionsConfig.CRYSTALLIZED_BLOOD_AXE_COOLDOWN);
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
                                .translatable("tooltip.item.companions.crystallized_blood_axe")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );

        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_axe_desc_1")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_axe_desc_2")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_axe_desc_3")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );

        super.appendHoverText(stack, context, pTooltipComponents, flag);
    }

}
