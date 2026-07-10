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
        return Companions.of("geo/voltaic_relay_block_" + dirName + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(VoltaicRelayBlockEntity animatable) {
        if (animatable.isActive()) {
            return Companions.of("textures/block/voltaic_relay_block_on.png");
        }

        return Companions.of("textures/block/voltaic_relay_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(VoltaicRelayBlockEntity animatable) {
        return Companions.of("animations/generic.animation.json");
    }

}