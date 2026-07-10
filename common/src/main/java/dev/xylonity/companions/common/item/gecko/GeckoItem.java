package dev.xylonity.companions.common.item.gecko;

import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GeckoItem extends Item implements GeoItem {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    protected final String resourceKey;

    public GeckoItem(Properties properties, String resourceKey) {
        super(properties);
        this.resourceKey = resourceKey;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    protected abstract Supplier<Object> createGeckoRenderer();

    @Override
    public void createGeoRenderer(Consumer<software.bernie.geckolib.animatable.client.GeoRenderProvider> consumer) {
        consumer.accept(new software.bernie.geckolib.animatable.client.GeoRenderProvider() {

            private net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer renderer;

            @Override
            public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getGeoItemRenderer() {
                if (this.renderer == null)
                    this.renderer = (net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer) createGeckoRenderer().get();

                return this.renderer;
            }

        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        ;;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

}