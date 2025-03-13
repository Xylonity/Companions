package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SoulFurnaceModel extends GeoModel<SoulFurnaceBlockEntity> {

    @Override
    public ResourceLocation getModelResource(SoulFurnaceBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/soul_furnace_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulFurnaceBlockEntity animatable) {
        if (animatable.getProgress() != 0)
            return new ResourceLocation(Companions.MOD_ID, "textures/block/soul_furnace_on_block.png");

        return new ResourceLocation(Companions.MOD_ID, "textures/block/soul_furnace_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SoulFurnaceBlockEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/generic.animation.json");
    }

}