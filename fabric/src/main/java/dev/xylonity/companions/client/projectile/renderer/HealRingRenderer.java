package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.HealRingModel;
import dev.xylonity.companions.common.entity.projectile.HealRingProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HealRingRenderer extends GeoEntityRenderer<HealRingProjectile> {

    public HealRingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HealRingModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull HealRingProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/heal_ring.png");
    }

    @Override
    public RenderType getRenderType(HealRingProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

}