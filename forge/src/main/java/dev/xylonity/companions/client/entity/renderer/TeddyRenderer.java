package dev.xylonity.companions.client.entity.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.TeddyModel;
import dev.xylonity.companions.common.entity.custom.TeddyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TeddyRenderer extends GeoEntityRenderer<TeddyEntity> {

    public TeddyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new TeddyModel());
    }

    private String prefix(TeddyEntity animatable) {
        return animatable.getPhase() == 1 ? "" : "mutated_";
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TeddyEntity animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/" + prefix(animatable) + "teddy.png");
    }

    @Override
    protected float getDeathMaxRotation(TeddyEntity animatable) {
        return animatable.getPhase() == 2 ? 0F : super.getDeathMaxRotation(animatable);
    }

}