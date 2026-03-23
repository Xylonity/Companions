package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.generic.GenericGeckoAxeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericAxeItemModel extends GeoModel<GenericGeckoAxeItem> {

    private final String resourceKey;

    public GenericAxeItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GenericGeckoAxeItem animatable) {
        return Companions.of("geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericGeckoAxeItem animatable) {
        return Companions.of("textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericGeckoAxeItem animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}