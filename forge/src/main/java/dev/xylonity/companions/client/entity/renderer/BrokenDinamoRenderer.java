package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.BrokenDinamoModel;
import dev.xylonity.companions.common.entity.custom.BrokenDinamoEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BrokenDinamoRenderer extends GeoEntityRenderer<BrokenDinamoEntity> {

    public BrokenDinamoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BrokenDinamoModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BrokenDinamoEntity animatable) {
        if (animatable.getPhase() != 0 && animatable.getPhase() != 1) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/illager_golem.png");
    }

}
