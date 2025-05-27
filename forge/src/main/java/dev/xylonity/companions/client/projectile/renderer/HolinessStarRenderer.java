package dev.xylonity.companions.client.projectile.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.projectile.model.BlackHoleModel;
import dev.xylonity.companions.client.projectile.model.HolinessStarModel;
import dev.xylonity.companions.common.entity.projectile.HolinessStartProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HolinessStarRenderer extends GeoEntityRenderer<HolinessStartProjectile> {

    public HolinessStarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HolinessStarModel());
    }

}