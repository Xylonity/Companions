package dev.xylonity.companions.client.armor.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.item.armor.GenericArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GenericArmorItemModel  extends GeoModel<GenericArmorItem> {

    private final String resourceKey;

    public GenericArmorItemModel(String resourceKey) {
        this.resourceKey = resourceKey;
    }

    @Override
    public ResourceLocation getModelResource(GenericArmorItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/" + resourceKey + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GenericArmorItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/armor/" + resourceKey + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(GenericArmorItem animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}