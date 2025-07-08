package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.MinionModel;
import dev.xylonity.companions.common.entity.companion.MinionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MinionRenderer extends GeoEntityRenderer<MinionEntity> {

    public MinionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MinionModel());
    }

}