package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.HostilePuppetGloveModel;
import dev.xylonity.companions.common.entity.custom.HostilePuppetGloveEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostilePuppetGloveRenderer extends GeoEntityRenderer<HostilePuppetGloveEntity> {

    public HostilePuppetGloveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostilePuppetGloveModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HostilePuppetGloveEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

}