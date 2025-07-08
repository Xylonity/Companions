package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.SoulMageModel;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SoulMageRenderer extends GeoEntityRenderer<SoulMageEntity> {

    public SoulMageRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SoulMageModel());
    }

}