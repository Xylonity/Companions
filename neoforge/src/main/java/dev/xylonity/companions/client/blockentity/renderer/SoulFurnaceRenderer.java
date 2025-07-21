package dev.xylonity.companions.client.blockentity.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.SoulFurnaceModel;
import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SoulFurnaceRenderer extends GeoBlockRenderer<SoulFurnaceBlockEntity> {

    public SoulFurnaceRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new SoulFurnaceModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulFurnaceBlockEntity animatable) {
        if (animatable.getLevel() != null) {
            BlockState state = animatable.getLevel().getBlockState(animatable.getBlockPos());
            if (state.hasProperty(SoulFurnaceBlock.LIT) && state.getValue(SoulFurnaceBlock.LIT)) {
                return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/soul_furnace_on_block.png");
            }
        }

        return ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/block/soul_furnace_block.png");
    }

}