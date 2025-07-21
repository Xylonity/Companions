package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.IceShardBigModel;
import dev.xylonity.companions.common.entity.projectile.BigIceShardProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IceShardBigRenderer extends GeoEntityRenderer<BigIceShardProjectile> {

    public IceShardBigRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceShardBigModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BigIceShardProjectile animatable) {
        return ResourceLocation.fromNamespaceAndPath(CompanionsCommon.MOD_ID, "textures/entity/ice_shard_big.png");
    }

    @Override
    protected void applyRotations(BigIceShardProjectile animatable, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTick, float nativeScale) {
        Vec3 velocity = animatable.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-7) {
            super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
            return;
        }

        Vector3f velocityVec = new Vector3f((float) velocity.x, (float) velocity.y, (float) velocity.z);
        velocityVec.normalize();

        Vector3f defaultForward = new Vector3f(0.0F, 0.0F, 1.0F);

        float dot = defaultForward.dot(velocityVec);
        dot = Math.max(-1.0F, Math.min(1.0F, dot));
        float angle = (float) Math.acos(dot);

        Vector3f axis = defaultForward.cross(velocityVec);
        if (axis.length() < 1.0E-4F) {
            axis.set(0.0F, 1.0F, 0.0F);
        } else {
            axis.normalize();
        }

        Quaternionf rotationQuat = new Quaternionf().fromAxisAngleRad(axis, angle);

        poseStack.mulPose(rotationQuat);

        super.applyRotations(animatable, poseStack, ageInTicks, rotationYaw, partialTick, nativeScale);
    }

}