package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.item.gecko.GeckoItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericItemModel extends GeoModel<GeckoItem> {

    private final String resourceKey;

    public GenericItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GeckoItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}