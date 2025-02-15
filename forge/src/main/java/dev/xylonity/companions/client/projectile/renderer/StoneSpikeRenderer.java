package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.FireMarkRingModel;
import dev.xylonity.companions.client.projectile.model.StoneSpikeModel;
import dev.xylonity.companions.common.entity.projectile.FireMarkRingProjectile;
import dev.xylonity.companions.common.entity.projectile.StoneSpikeProjectile;
import dev.xylonity.companions.config.CompanionsConfig;
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
        if (animatable.tickCount <= 22) return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike.png");
        switch (animatable.tickCount) {
            case 23, 24: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike1.png");}
            case 25, 26, 27: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike2.png");}
            case 28, 29, 30: {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike3.png");}
            default:  {return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/stone_spike4.png");}
        }
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