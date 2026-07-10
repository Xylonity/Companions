package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.gecko.GeckoAxeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericAxeItemModel extends GeoModel<GeckoAxeItem> {

    private final String resourceKey;

    public GenericAxeItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GeckoAxeItem animatable) {
        return Companions.of("geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoAxeItem animatable) {
        return Companions.of("textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoAxeItem animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}