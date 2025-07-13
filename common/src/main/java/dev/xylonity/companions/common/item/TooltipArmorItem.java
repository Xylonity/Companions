package dev.xylonity.companions.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipArmorItem extends ArmorItem {

    private String tooltipName;

    public TooltipArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties) {
        super(armorMaterial, type, properties);
        this.tooltipName = "";
    }

    public TooltipArmorItem(ArmorMaterial armorMaterial, Type type, Properties properties, String tooltipName) {
        this(armorMaterial, type, properties);
        this.tooltipName = tooltipName;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack itemStack, @Nullable Level level, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        if (!tooltipName().isEmpty() && !tooltipName().isBlank()) {
            components.add(Component.translatable("tooltip.item.companions." + tooltipName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(itemStack, level, components, flag);
    }

    protected String tooltipName() {
        return tooltipName;
    }

}
