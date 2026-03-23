package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.AbstractTeslaBlock;
import dev.xylonity.companions.common.blockentity.VoltaicRelayBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class VoltaicRelayModel extends GeoModel<VoltaicRelayBlockEntity> {

    @Override
    public ResourceLocation getModelResource(VoltaicRelayBlockEntity animatable) {
        String dirName = animatable.getBlockState().getValue(AbstractTeslaBlock.FACING).getName();
        return new ResourceLocation(Companions.MOD_ID, "geo/voltaic_relay_block_" + dirName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoltaicRelayBlockEntity animatable) {
        if (animatable.isActive()) {
            return new ResourceLocation(Companions.MOD_ID, "textures/block/voltaic_relay_block_on.png");
        }

        return new ResourceLocation(Companions.MOD_ID, "textures/block/voltaic_relay_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoltaicRelayBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}