package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.client.blockentity.model.TeslaCoilModel;
import dev.xylonity.companions.client.layer.ElectricConnectionLayer;
import dev.xylonity.companions.common.blockentity.AbstractTeslaBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaCoilBlockEntity;
import dev.xylonity.companions.common.tesla.TeslaConnectionManager;
import dev.xylonity.companions.common.event.CompanionsEntityTracker;
import dev.xylonity.companions.common.util.interfaces.ITeslaUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TeslaCoilRenderer extends GeoBlockRenderer<TeslaCoilBlockEntity> implements ITeslaUtil {

    public TeslaCoilRenderer(BlockEntityRendererProvider.Context rendererDispatcher, int totalFrames, int ticksPerFrame) {
        super(new TeslaCoilModel());
        addRenderLayer(new ElectricConnectionLayer<>(this, new ResourceLocation(Companions.MOD_ID, "textures/misc/electric_arch.png"), totalFrames, ticksPerFrame));
    }

    public TeslaCoilRenderer(BlockEntityRendererProvider.Context renderManager) {
        this(renderManager, 8, ELECTRICAL_CHARGE_DURATION / 8);
    }

    @Override
    protected void rotateBlock(Direction facing, PoseStack poseStack) {
        ;;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TeslaCoilBlockEntity pBlockEntity) {
        return true;
    }

}