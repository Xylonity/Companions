package dev.xylonity.companions.client.item.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.generic.GenericGeckoPickAxeItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericPickaxeItemModel extends GeoModel<GenericGeckoPickAxeItem> {

    private final String resourceKey;

    public GenericPickaxeItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GenericGeckoPickAxeItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericGeckoPickAxeItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/item/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericGeckoPickAxeItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}