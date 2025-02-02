package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntlionRenderer extends GeoEntityRenderer<AntlionEntity> {

    public AntlionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AntlionModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull AntlionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/antlion.png");
    }

}