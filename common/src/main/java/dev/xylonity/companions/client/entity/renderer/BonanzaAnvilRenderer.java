package dev.xylonity.companions.client.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.xylonity.companions.common.entity.BonanzaAnvilEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BonanzaAnvilRenderer extends EntityRenderer<BonanzaAnvilEntity> {

    public BonanzaAnvilRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5f;
    }

    @Override
    public void render(@NotNull BonanzaAnvilEntity entity, float yaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        final BlockState state = entity.getBlockState();
        if (state.getRenderShape() != RenderShape.MODEL) {
            return;
        }

        final Level level = entity.level();
        if (state == level.getBlockState(entity.blockPosition())) {
            return;
        }

        poseStack.pushPose();

        final float scale = entity.getScale(partialTick);
        poseStack.scale(scale, scale, scale);

        final BlockPos position = BlockPos.containing(entity.getX(), entity.getBoundingBox().maxY, entity.getZ());
        poseStack.translate(-0.5, 0.0, -0.5);

        final BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        dispatcher.getModelRenderer().tesselateBlock(level, dispatcher.getBlockModel(state), state, position, poseStack,
                buffer.getBuffer(ItemBlockRenderTypes.getMovingBlockRenderType(state)), false, RandomSource.create(),
                state.getSeed(entity.getStartPos()), OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull BonanzaAnvilEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}