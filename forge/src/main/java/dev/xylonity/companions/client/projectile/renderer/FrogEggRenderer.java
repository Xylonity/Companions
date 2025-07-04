package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.client.projectile.model.FrogEggModel;
import dev.xylonity.companions.client.projectile.model.LaserModel;
import dev.xylonity.companions.common.entity.projectile.FrogEggProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.LaserTriggerProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogEggRenderer extends GeoEntityRenderer<FrogEggProjectile> {

    public FrogEggRenderer(EntityRendererProvider.Context context) {
        super(context, new FrogEggModel());
    }

    @Override
    public RenderType getRenderType(FrogEggProjectile animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(getTextureLocation(animatable));
    }
}
