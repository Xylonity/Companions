package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RespawnTotemModel extends GeoModel<RespawnTotemBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RespawnTotemBlockEntity animatable) {
        return Companions.of("geo/respawn_totem_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RespawnTotemBlockEntity animatable) {
        if (animatable.getCharges() > 0) {
            return Companions.of("textures/block/respawn_totem_block.png");
        }

        return Companions.of("textures/block/respawn_totem_block_off.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RespawnTotemBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}