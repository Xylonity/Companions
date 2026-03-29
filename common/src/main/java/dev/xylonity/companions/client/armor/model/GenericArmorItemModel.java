package dev.xylonity.companions.client.armor.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.gecko.GeckoArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericArmorItemModel  extends GeoModel<GeckoArmorItem> {

    private final String resourceKey;

    public GenericArmorItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GeckoArmorItem animatable) {
        return Companions.of("geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GeckoArmorItem animatable) {
        return Companions.of("textures/armor/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GeckoArmorItem animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}