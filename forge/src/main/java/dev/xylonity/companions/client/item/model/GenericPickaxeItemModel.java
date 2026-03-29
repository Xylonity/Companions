package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.item.gecko.GeckoPickaxeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericPickaxeItemModel extends GeoModel<GeckoPickaxeItem> {

    private final String resourceKey;

    public GenericPickaxeItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GeckoPickaxeItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoPickaxeItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoPickaxeItem animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}