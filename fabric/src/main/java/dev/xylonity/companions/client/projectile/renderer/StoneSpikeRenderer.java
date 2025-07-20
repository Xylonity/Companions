package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.StoneSpikeModel;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class StoneSpikeRenderer extends GeoEntityRenderer<StoneSpikeProjectile> {

    public StoneSpikeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new StoneSpikeModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StoneSpikeProjectile animatable) {
        int l = animatable.getLifetime();
        int remaining = l - animatable.tickCount;

        return switch (remaining) {
            case 12, 11 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike1.png");
            case 10, 9 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike2.png");
            case 8, 7 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike3.png");
            case 6, 5 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike4.png");
            case 4, 3 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike5.png");
            case 2, 1 -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike6.png");
            default -> new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike0.png");
        };
    }

    @Override
    public RenderType getRenderType(StoneSpikeProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }

    @Override
    public void render(StoneSpikeProjectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.3F, 1.3F, 1.3F);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}