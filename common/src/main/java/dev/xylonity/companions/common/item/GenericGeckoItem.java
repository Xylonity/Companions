package dev.xylonity.companions.common.item;

import dev.xylonity.companions.client.item.renderer.GenericItemRenderer;
import dev.xylonity.companions.common.item.gecko.GeckoItem;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericGeckoItem extends GeckoItem implements GeoItem {

    public GenericGeckoItem(Properties properties, String resourceKey) {
        super(properties, resourceKey);
    }

    @Override
    protected Supplier<Object> createGeckoRenderer() {
        return () -> new GenericItemRenderer(resourceKey);
    }

}