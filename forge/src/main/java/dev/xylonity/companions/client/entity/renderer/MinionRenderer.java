package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.HostileImpModel;
import dev.xylonity.companions.client.entity.model.MinionModel;
import dev.xylonity.companions.common.entity.custom.HostileImpEntity;
import dev.xylonity.companions.common.entity.custom.MinionEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MinionRenderer extends GeoEntityRenderer<MinionEntity> {

    public MinionRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MinionModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MinionEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + animatable.getVariant() + ".png");
    }

}