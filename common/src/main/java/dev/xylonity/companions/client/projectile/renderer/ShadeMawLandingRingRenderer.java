package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.projectile.ShadeMawLandingRingProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ShadeMawLandingRingRenderer extends EntityRenderer<ShadeMawLandingRingProjectile> {

    public ShadeMawLandingRingRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ShadeMawLandingRingProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        ;;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ShadeMawLandingRingProjectile entity) {
        return Companions.of("textures/entity/fire_mark_ring.png");
    }

}
