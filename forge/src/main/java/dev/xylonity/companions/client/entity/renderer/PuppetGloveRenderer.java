package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.PuppetGloveModel;
import dev.xylonity.companions.common.entity.companion.PuppetGloveEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PuppetGloveRenderer extends GeoEntityRenderer<PuppetGloveEntity> {

    public PuppetGloveRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PuppetGloveModel());
        this.shadowRadius = 0.5f;
    }

}