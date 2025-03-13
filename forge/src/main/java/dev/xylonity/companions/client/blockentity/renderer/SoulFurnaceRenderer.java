package dev.xylonity.companions.client.blockentity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.client.blockentity.model.SoulFurnaceModel;
import dev.xylonity.companions.client.blockentity.model.TeslaReceiverModel;
import dev.xylonity.companions.common.block.SoulFurnaceBlock;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.blockentity.TeslaReceiverBlockEntity;
import dev.xylonity.companions.common.entity.ai.illagergolem.TeslaConnectionManager;
import dev.xylonity.companions.common.event.ClientEntityTracker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
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
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class SoulFurnaceRenderer extends GeoBlockRenderer<SoulFurnaceBlockEntity> {

    public SoulFurnaceRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {
        super(new SoulFurnaceModel());
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulFurnaceBlockEntity animatable) {
        if (animatable.getProgress() != 0)
            return new ResourceLocation(Companions.MOD_ID, "textures/block/soul_furnace_on_block.png");

        return new ResourceLocation(Companions.MOD_ID, "textures/block/soul_furnace_block.png");
    }

}