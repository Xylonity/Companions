package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.AntlionModel;
import dev.xylonity.companions.client.entity.model.HostilePuppetGloveModel;
import dev.xylonity.companions.common.entity.custom.AntlionEntity;
import dev.xylonity.companions.common.entity.custom.HostilePuppetGlove;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostilePuppetGloveRenderer extends GeoEntityRenderer<HostilePuppetGlove> {

    public HostilePuppetGloveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostilePuppetGloveModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HostilePuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

}