package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.client.projectile.model.FrogHealModel;
import dev.xylonity.companions.client.projectile.model.ScrollModel;
import dev.xylonity.companions.common.entity.projectile.FrogHealProjectile;
import dev.xylonity.companions.common.entity.projectile.ScrollProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FrogHealRenderer extends GeoEntityRenderer<FrogHealProjectile> {

    public FrogHealRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FrogHealModel());
    }

}