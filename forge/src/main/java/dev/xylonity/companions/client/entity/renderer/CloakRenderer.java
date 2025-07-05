package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.client.entity.model.CloakModel;
import dev.xylonity.companions.common.entity.custom.CloakEntity;
import dev.xylonity.companions.common.util.interfaces.IPhantomEffectEntity;
import dev.xylonity.companions.common.util.PhantomVisibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CloakRenderer extends GeoEntityRenderer<CloakEntity> {

    public CloakRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CloakModel());
    }

    @Override
    public void render(@NotNull CloakEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity instanceof IPhantomEffectEntity phantomEntity && phantomEntity.isPhantomEffectActive()) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                if (getPhantomVisibility(entity, player) == PhantomVisibility.INVISIBLE) {
                    return;
                }
            }

        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public RenderType getRenderType(CloakEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        if (animatable instanceof IPhantomEffectEntity phantomEntity && phantomEntity.isPhantomEffectActive()) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                switch (getPhantomVisibility(animatable, player)) {
                    case TRANSLUCENT:
                        return RenderType.entityTranslucent(getTextureLocation(animatable));
                    case INVISIBLE:
                        return null;
                    case NORMAL:
                    default:
                        break;
                }
            }
        }

        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, CloakEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (animatable instanceof IPhantomEffectEntity phantomEntity && phantomEntity.isPhantomEffectActive()) {
            Player player = Minecraft.getInstance().player;

            if (player != null) {
                PhantomVisibility visibility = getPhantomVisibility(animatable, player);

                if (visibility == PhantomVisibility.TRANSLUCENT) {
                    alpha = 0.35f;
                } else if (visibility == PhantomVisibility.INVISIBLE) {
                    return;
                }
            }
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private PhantomVisibility getPhantomVisibility(CloakEntity entity, Player clientPlayer) {
        if (!(entity instanceof IPhantomEffectEntity phantomEntity) || !phantomEntity.isPhantomEffectActive()) {
            return PhantomVisibility.NORMAL;
        }

        if (!(clientPlayer instanceof IPhantomEffectEntity clientPhantom) || !clientPhantom.isPhantomEffectActive()) {
            return PhantomVisibility.INVISIBLE;
        }

        if (clientPlayer.getTeam() != null && clientPlayer.isAlliedTo(entity)) {
            return PhantomVisibility.TRANSLUCENT;
        }

        if (entity.getOwner() == clientPlayer) {
            return PhantomVisibility.TRANSLUCENT;
        }

        return PhantomVisibility.INVISIBLE;
    }

}