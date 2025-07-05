package dev.xylonity.companions.client.projectile.model;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.SoulMageEntity;
import dev.xylonity.companions.common.entity.projectile.SoulMageBookEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class SoulMageBookModel extends GeoModel<SoulMageBookEntity> {
    private float currentBookYaw = 0.0F;

    @Override
    public ResourceLocation getModelResource(SoulMageBookEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "geo/soul_mage_book.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SoulMageBookEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "textures/entity/soul_mage_book.png");
    }

    @Override
    public void setCustomAnimations(SoulMageBookEntity animatable, long instanceId, AnimationState<SoulMageBookEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone mainB = this.getAnimationProcessor().getBone("book2");
        if (mainB == null) return;

        float targetYaw = 0F;
        if (animatable.getOwner() instanceof SoulMageEntity mage) {
            Vec3 diff = mage.position().subtract(animatable.position());
            targetYaw = (float)(Math.atan2(diff.z, diff.x) * (180.0 / Math.PI)) - 90.0F;

            float oscillation = (float)(Math.sin(animatable.tickCount * 0.05) * 5.0F);
            targetYaw += oscillation;
        }

        float angleDiff = wrapDegrees(targetYaw - currentBookYaw);
        currentBookYaw += angleDiff * 0.005F;

        mainB.setRotY((float) Math.toRadians(currentBookYaw));
    }

    private float wrapDegrees(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) angle -= 360.0F;
        if (angle < -180.0F) angle += 360.0F;
        return angle;
    }

    @Override
    public ResourceLocation getAnimationResource(SoulMageBookEntity animatable) {
        return new ResourceLocation(Companions.MOD_ID, "animations/soul_mage_book.animation.json");
    }
}