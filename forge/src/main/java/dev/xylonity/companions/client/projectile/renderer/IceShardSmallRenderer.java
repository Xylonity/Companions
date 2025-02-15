package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.projectile.model.IceShardSmallModel;
import dev.xylonity.companions.common.entity.projectile.SmallIceShardProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class IceShardSmallRenderer extends GeoEntityRenderer<SmallIceShardProjectile> {
    private static final Map<UUID, Quaternionf> LAST_ROTATION_MAP = new HashMap<>();
    private static final float ROTATION_SMOOTH_FACTOR = 0.01F;

    public IceShardSmallRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceShardSmallModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SmallIceShardProjectile animatable) {
        return new ResourceLocation(CompanionsCommon.MOD_ID, "textures/entity/ice_shard_small.png");
    }

    @Override
    protected void applyRotations(SmallIceShardProjectile entity, PoseStack poseStack, float ageInTicks, float rotationYaw, float partialTicks) {
        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() < 1.0E-7) {

            Quaternionf lastRotation = LAST_ROTATION_MAP.get(entity.getUUID());
            if (lastRotation != null) {
                poseStack.mulPose(lastRotation);
            }

            super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
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

        Quaternionf newRotation = new Quaternionf().fromAxisAngleRad(axis, angle);

        UUID uuid = entity.getUUID();
        Quaternionf oldRotation = LAST_ROTATION_MAP.get(uuid);
        if (oldRotation == null) {
            oldRotation = new Quaternionf(newRotation);
        }

        Quaternionf interpolated = new Quaternionf();
        oldRotation.slerp(newRotation, ROTATION_SMOOTH_FACTOR, interpolated);

        poseStack.mulPose(interpolated);

        LAST_ROTATION_MAP.put(uuid, new Quaternionf(interpolated));

        super.applyRotations(entity, poseStack, ageInTicks, rotationYaw, partialTicks);
    }

}
