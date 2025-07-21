package dev.xylonity.companions.client.blockentity.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.model.GeoModel;

public class SoulFurnaceModel extends GeoModel<SoulFurnaceBlockEntity> {

    @Override
    public ResourceLocation getModelResource(SoulFurnaceBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "geo/soul_furnace_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulFurnaceBlockEntity animatable) {
        if (animatable.getLevel() != null) {
            BlockState state = animatable.getLevel().getBlockState(animatable.getBlockPos());
            if (state.hasProperty(SoulFurnaceBlock.LIT) && state.getValue(SoulFurnaceBlock.LIT)) {
                return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/soul_furnace_on_block.png");
            }
        }

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/soul_furnace_block.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SoulFurnaceBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "animations/generic.animation.json");
    }

}