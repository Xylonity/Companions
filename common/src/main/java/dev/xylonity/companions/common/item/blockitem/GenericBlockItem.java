package dev.xylonity.companions.common.item.blockitem;

import dev.xylonity.companions.client.blockentity.renderer.GenericBlockItemRenderer;
import dev.xylonity.companions.common.item.gecko.GeckoBlockItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Supplier;

public class GenericBlockItem extends GeckoBlockItem {

    public GenericBlockItem(Block pBlock, Properties pProperties, String resourceKey) {
        super(pBlock, pProperties, resourceKey);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
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
                case "shade_sword_altar", "shade_maw_altar" -> component.withStyle(ChatFormatting.RED);
                case "frog_bonanza_block" -> component.withStyle(ChatFormatting.GRAY);
                case "respawn_totem_block" -> component.withStyle(ChatFormatting.GOLD);
            }

        }

        return stackName;
    }

    @Override
    public void appendHoverText(ItemStack pStack, Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.block.companions."+ resourceKey).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

}
