package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.client.entity.model.SacredPontiffModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.hostile.SacredPontiffEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SacredPontiffRenderer extends GeoEntityRenderer<SacredPontiffEntity> {

    public SacredPontiffRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SacredPontiffModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SacredPontiffEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/sacred_pontiff.png");
    }

}