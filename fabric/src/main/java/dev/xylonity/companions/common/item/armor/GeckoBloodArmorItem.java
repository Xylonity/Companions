package dev.xylonity.companions.common.item.armor;

import dev.xylonity.companions.common.item.generic.GenericGeckoArmorItem;
import dev.xylonity.companions.config.CompanionsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GeckoBloodArmorItem extends GenericGeckoArmorItem {

    public GeckoBloodArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties, String resourceKey) {
        super(armorMaterial, type, properties, resourceKey);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, @NotNull TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("tooltip.icon.companions.star").append(Component
                .translatable("tooltip.item.companions.key.blood_set")
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
                                .translatable("tooltip.item.companions.crystallized_blood_set")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );

        // cat1
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_set_desc_1")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_set_desc_2", (int) (CompanionsConfig.CRYSTALLIZED_BLOOD_SET_MIN_HEALTH * 100))
                                .withStyle(ChatFormatting.GRAY)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_set_desc_3")
                                .withStyle(ChatFormatting.GRAY)
                        )
        );

        // cat2
        pTooltipComponents.add(
                Component.literal("  ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_set_2")
                                .withStyle(ChatFormatting.WHITE)
                        )
        );
        pTooltipComponents.add(
                Component.literal("   ")
                        .append(Component
                                .translatable("tooltip.item.companions.crystallized_blood_set_desc_4", (int) (CompanionsConfig.CRYSTALLIZED_BLOOD_SET_REDUCTION * 100))
                                .withStyle(ChatFormatting.RED)
                        )
        );

        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

}
