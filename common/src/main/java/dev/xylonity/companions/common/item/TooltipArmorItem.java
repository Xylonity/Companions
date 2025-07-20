package dev.xylonity.companions.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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

    public TooltipArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties) {
        super(armorMaterial, type, properties);
        this.tooltipName = "";
    }

    public TooltipArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties, String tooltipName) {
        this(armorMaterial, type, properties);
        this.tooltipName = tooltipName;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!tooltipName().isEmpty() && !tooltipName().isBlank()) {
            tooltipComponents.add(Component.translatable("tooltip.item.companions." + tooltipName()).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    protected String tooltipName() {
        return tooltipName;
    }

}
