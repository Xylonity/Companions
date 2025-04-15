package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.HostilePuppetGloveModel;
import dev.xylonity.companions.client.entity.model.PuppetGloveModel;
import dev.xylonity.companions.common.entity.custom.HostilePuppetGlove;
import dev.xylonity.companions.common.entity.custom.PuppetGlove;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PuppetGloveRenderer extends GeoEntityRenderer<PuppetGlove> {

    public PuppetGloveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PuppetGloveModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PuppetGlove animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/puppet_glove.png");
    }

}