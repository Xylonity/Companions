package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.entity.model.BrokenDinamoModel;
import dev.xylonity.companions.client.entity.model.IllagerGolemModel;
import dev.xylonity.companions.common.entity.custom.BrokenDinamoEntity;
import dev.xylonity.companions.common.entity.custom.IllagerGolemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BrokenDinamoRenderer extends GeoEntityRenderer<BrokenDinamoEntity> {

    public BrokenDinamoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BrokenDinamoModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BrokenDinamoEntity animatable) {
        if (animatable.getPhase() != 0 && animatable.getPhase() != 1) {
            return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/dinamo.png");
        }

        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/illager_golem.png");
    }

}
