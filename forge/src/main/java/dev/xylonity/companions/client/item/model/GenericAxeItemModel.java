package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.CompanionsForge;
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
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoAxeItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoAxeItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}