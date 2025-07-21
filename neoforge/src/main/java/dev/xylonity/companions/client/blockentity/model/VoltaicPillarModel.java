package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.VoltaicPillarBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoltaicPillarModel extends GeoModel<VoltaicPillarBlockEntity> {

    @Override
    public ResourceLocation getModelResource(VoltaicPillarBlockEntity animatable) {
        if (animatable.isTop()) {
            return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/voltaic_pillar_top_block.geo.json");
        }

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/voltaic_pillar_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoltaicPillarBlockEntity animatable) {
        if (animatable.isTop()) {
            if (animatable.isActive()) {
                return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/voltaic_pillar_top_block_on.png");
            }

            return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/voltaic_pillar_top_block.png");
        }

        if (animatable.isActive()) {
            return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/voltaic_pillar_block_on.png");
        }

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/voltaic_pillar_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoltaicPillarBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/generic.animation.json");
    }

}