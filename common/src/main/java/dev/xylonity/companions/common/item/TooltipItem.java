package dev.xylonity.companions.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TooltipItem extends Item {

    private String tooltipName;

    public TooltipItem(Properties properties) {
        super(properties);
        this.tooltipName = "";
    }

    public TooltipItem(Properties properties, String tooltipName) {
        this(properties);
        this.tooltipName = tooltipName;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component name = super.getName(stack);

        if (name instanceof MutableComponent c) {
            if (tooltipName.equals("relic_gold")) {
                c.withStyle(ChatFormatting.GOLD);
            } else if (tooltipName.equals("old_cloth")) {
                c.withStyle(ChatFormatting.DARK_GREEN);
            }
        }

        return name;
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
