package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.FroggyModel;
import dev.xylonity.companions.common.entity.FroggyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FroggyRenderer extends GeoEntityRenderer<FroggyEntity> {

    public FroggyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FroggyModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FroggyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/froggy.png");
    }

}