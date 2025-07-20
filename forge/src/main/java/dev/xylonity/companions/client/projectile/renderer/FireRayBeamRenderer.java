package dev.xylonity.companions.client.projectile.renderer;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.FireRayBeamModel;
import dev.xylonity.companions.client.projectile.model.FireRayPieceModel;
import dev.xylonity.companions.client.projectile.model.GenericTriggerProjectileModel;
import dev.xylonity.companions.common.entity.projectile.FireRayPieceProjectile;
import dev.xylonity.companions.common.entity.projectile.trigger.FireRayBeamEntity;
import dev.xylonity.companions.common.entity.projectile.trigger.GenericTriggerProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireRayBeamRenderer extends GeoEntityRenderer<FireRayBeamEntity> {

    public FireRayBeamRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireRayBeamModel());
    }

}