package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoltaicPillarModel extends GeoModel<VoltaicPillarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(VoltaicPillarBlockEntity animatable) {
        if (animatable.isTop() && !animatable.hasBlockOnTop()) {
            return Companions.of("geo/voltaic_pillar_top_block.geo.json");
        }

        return Companions.of("geo/voltaic_pillar_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoltaicPillarBlockEntity animatable) {
        if (animatable.isTop() && !animatable.hasBlockOnTop()) {
            if (animatable.isActive()) {
                return Companions.of("textures/block/voltaic_pillar_top_block_on.png");
            }

            return Companions.of("textures/block/voltaic_pillar_top_block.png");
        }

        if (animatable.isActive()) {
            return Companions.of("textures/block/voltaic_pillar_block_on.png");
        }

        return Companions.of("textures/block/voltaic_pillar_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoltaicPillarBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}