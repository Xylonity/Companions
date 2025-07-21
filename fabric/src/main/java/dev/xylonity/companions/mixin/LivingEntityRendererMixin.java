package dev.xylonity.companions.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.common.entity.CompanionEntity;
import dev.xylonity.companions.common.util.PhantomVisibility;
import dev.xylonity.companions.common.util.interfaces.IPhantomEffectEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity> {

    @Unique
    private T companions$currentEntity;

    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    private void companions$handlePhantomRenderType(T pLivingEntity, boolean pBodyVisible, boolean pTranslucent, boolean pGlowing, CallbackInfoReturnable<RenderType> cir) {
        if (pLivingEntity instanceof IPhantomEffectEntity e && e.isPhantomEffectActive()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                switch (companions$getPhantomVisibility(pLivingEntity, player)) {
                    case TRANSLUCENT:
                        cir.setReturnValue(RenderType.entityTranslucent(((LivingEntityRenderer)(Object)this).getTextureLocation(pLivingEntity)));
                        break;
                    case INVISIBLE:
                        cir.setReturnValue(null);
                        break;
                    case NORMAL:
                        break;
                }
            }
        }

    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void companions$removeShadowForPhantom(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        if (pEntity instanceof IPhantomEffectEntity e && e.isPhantomEffectActive()) {
            ((EntityRendererAccessor) this).setShadowRadius(0);
        }

    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
    private void companions$captureEntity(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, CallbackInfo ci) {
        this.companions$currentEntity = pEntity;
    }

    @Redirect(
            method = "render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
            )
    )
    private void companions$redirectRenderToBuffer(
            LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>> instance,
            LivingEntity livingEntity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (livingEntity instanceof IPhantomEffectEntity e && e.isPhantomEffectActive()) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                ResourceLocation tex = instance.getTextureLocation(livingEntity);
                switch (companions$getPhantomVisibility(livingEntity, player)) {
                    case TRANSLUCENT -> {
                        VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucent(tex));
                        instance.getModel().renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);
                    }
                    case INVISIBLE -> {
                        return;
                    }
                    case NORMAL -> {
                        VertexConsumer vc = bufferSource.getBuffer(instance.getModel().renderType(tex));
                        instance.getModel().renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY);
                    }
                }
                return;
            }
        }
        instance.render(livingEntity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }



    @Unique
    private PhantomVisibility companions$getPhantomVisibility(LivingEntity entity, Player player) {
        if (!(entity instanceof IPhantomEffectEntity phantomEntity) || !phantomEntity.isPhantomEffectActive()) {
            return PhantomVisibility.NORMAL;
        }

        if (!(player instanceof IPhantomEffectEntity clientPhantom) || !clientPhantom.isPhantomEffectActive()) {
            return PhantomVisibility.INVISIBLE;
        }

        if (entity == player) {
            return PhantomVisibility.TRANSLUCENT;
        }

        if (player.getTeam() != null && player.isAlliedTo(entity)) {
            return PhantomVisibility.TRANSLUCENT;
        }

        if (entity instanceof CompanionEntity companion && companion.getOwner() == player) {
            return PhantomVisibility.TRANSLUCENT;
        }

        return PhantomVisibility.INVISIBLE;
    }

}