package dev.xylonity.companions.common.item.blockitem;

import dev.xylonity.companions.client.blockentity.renderer.GenericBlockItemRenderer;
import dev.xylonity.companions.common.item.gecko.GeckoBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

public class GenericBlockItem extends GeckoBlockItem {

    public GenericBlockItem(Block pBlock, Properties pProperties, String resourceKey) {
        super(pBlock, pProperties, resourceKey);
    }

    @Override
    protected Supplier<Object> createGeckoRenderer() {
        return () -> new GenericBlockItemRenderer(resourceKey);
    }

    @Override
    public Component getName(ItemStack stack) {
        final Component stackName = super.getName(stack);
        if (stackName instanceof MutableComponent component) {
            switch (resourceKey) {
                case "shade_sword_altar", "shade_maw_altar", "shade_bat_altar" -> component.withStyle(ChatFormatting.RED);
                case "frog_bonanza_block" -> component.withStyle(ChatFormatting.GRAY);
                case "respawn_totem_block" -> component.withStyle(ChatFormatting.GOLD);
            }

        }

        return stackName;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack pStack, @NotNull Item.TooltipContext pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.block.companions."+ resourceKey).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

}
