package dev.xylonity.companions.common.item.armor;

import dev.xylonity.companions.common.item.generic.GenericGeckoArmorItem;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GeckoMageArmorItem extends GenericGeckoArmorItem {

    public GeckoMageArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, String resourceKey) {
        super(armorMaterial, type, properties, resourceKey);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> pTooltipComponents, @NotNull TooltipFlag flag) {
        pTooltipComponents.add(Component.translatable("tooltip.icon.companions.star").append(Component
                .translatable("tooltip.item.companions.key.mage_set")
                .withStyle(ChatFormatting.YELLOW)));

        // pre
        pTooltipComponents.add(
                Component.literal(" ")
                        .append(Component
                                .translatable("tooltip.item.companions.key.abilities")
                                .withStyle(ChatFormatting.DARK_GRAY))
        );

        pTooltipComponents.add(
                Component.literal("  ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );

        // cat1
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_desc_1")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_desc_2", (int) ((CompanionsConfig.MAGE_SET_DAMAGE_REDUCTION * 3) * 100))
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_desc_3")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );

        // cat2
        pTooltipComponents.add(
                Component.literal("  ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_2")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_desc_4", (int) (CompanionsConfig.MAGE_SET_DAMAGE_REDUCTION * 100))
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.mage_set_desc_5", (int) (CompanionsConfig.MAGE_SET_COOLDOWN_REDUCTION * 100))
                                .withStyle(ChatFormatting.LIGHT_PURPLE)
                        )
        );

        super.appendHoverText(stack, context, pTooltipComponents, flag);
    }

}
