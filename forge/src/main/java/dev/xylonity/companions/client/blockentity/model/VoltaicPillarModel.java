package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoltaicPillarModel extends GeoModel<VoltaicPillarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(VoltaicPillarBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/voltaic_pillar_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoltaicPillarBlockEntity animatable) {
        if (animatable.isActive()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo_charge.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/entity/dinamo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoltaicPillarBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}