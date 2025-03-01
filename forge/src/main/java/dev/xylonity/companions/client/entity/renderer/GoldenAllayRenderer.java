package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.client.entity.model.GoldenAllayModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.GoldenAllayEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class GoldenAllayRenderer extends GeoEntityRenderer<GoldenAllayEntity> {

    public GoldenAllayRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GoldenAllayModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GoldenAllayEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/golden_allay.png");
    }

}