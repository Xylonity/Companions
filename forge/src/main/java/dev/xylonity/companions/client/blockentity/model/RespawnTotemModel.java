package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.CompanionsForge;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class RespawnTotemModel extends GeoModel<RespawnTotemBlockEntity> {

    @Override
    public ResourceLocation getModelResource(RespawnTotemBlockEntity animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "geo/respawn_totem_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RespawnTotemBlockEntity animatable) {
        if (animatable.getCharges() > 0) {
            return new ResourceLocation(CompanionsForge.MOD_ID, "textures/block/respawn_totem_block.png");
        }

        return new ResourceLocation(CompanionsForge.MOD_ID, "textures/block/respawn_totem_block_off.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RespawnTotemBlockEntity animatable) {
        return new ResourceLocation(CompanionsForge.MOD_ID, "animations/generic.animation.json");
    }

}