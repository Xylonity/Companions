package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.client.entity.model.MankhModel;
import dev.xylonity.companions.common.entity.custom.MankhEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MankhRenderer extends GeoEntityRenderer<MankhEntity> {

    public MankhRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MankhModel());
    }

}