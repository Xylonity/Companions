package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.gecko.GeckoSwordItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericSwordItemModel extends GeoModel<GeckoSwordItem> {

    private final String resourceKey;

    public GenericSwordItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GeckoSwordItem animatable) {
        return Companions.of("geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoSwordItem animatable) {
        return Companions.of("textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoSwordItem animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}