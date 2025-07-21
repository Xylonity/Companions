package dev.xylonity.companions.common.item.blockitem;

import dev.xylonity.companions.client.blockentity.renderer.GenericBlockItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class GenericBlockItem extends BlockItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String name;

    public GenericBlockItem(Block pBlock, Properties pProperties, String name) {
        super(pBlock, pProperties);
        this.name = name;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GenericBlockItemRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = new GenericBlockItemRenderer(name);

                return this.renderer;
            }
        });

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        Component ret = super.getName(stack);

        if (ret instanceof MutableComponent c) {
            if (name.equals("shade_sword_altar")) {
                c.withStyle(ChatFormatting.RED);
            } else if (name.equals("shade_maw_altar")) {
                c.withStyle(ChatFormatting.RED);
            } else if (name.equals("frog_bonanza_block")) {
                c.withStyle(ChatFormatting.GRAY);
            } else if (name.equals("respawn_totem_block")) {
                c.withStyle(ChatFormatting.GOLD);
            }
        }

        return ret;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        components.add(Component.translatable("tooltip.block.companions."+ name).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        super.appendHoverText(stack, context, components, flag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}
