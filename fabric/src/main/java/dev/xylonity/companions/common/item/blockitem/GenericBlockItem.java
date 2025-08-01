package dev.xylonity.companions.common.item.blockitem;

import dev.xylonity.companions.client.blockentity.renderer.GenericBlockItemRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericBlockItem extends BlockItem implements GeoItem {

    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String name;

    public GenericBlockItem(Block pBlock, Properties pProperties, String name) {
        super(pBlock, pProperties);
        this.name = name;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GenericBlockItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
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
    public void appendHoverText(@NotNull ItemStack pStack, @Nullable Level pLevel, @NotNull List<Component> pTooltip, @NotNull TooltipFlag pFlag) {
        pTooltip.add(Component.translatable("tooltip.block.companions."+ name).withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY));
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) { ;; }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }


    @Override
    public Supplier<Object> getRenderProvider() {
        return this.renderProvider;
    }

}
