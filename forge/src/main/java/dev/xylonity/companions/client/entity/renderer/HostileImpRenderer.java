package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.HostileImpModel;
import dev.xylonity.companions.common.entity.hostile.HostileImpEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HostileImpRenderer extends GeoEntityRenderer<HostileImpEntity> {

    public HostileImpRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HostileImpModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HostileImpEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/imp_hostile2.png");
    }

}