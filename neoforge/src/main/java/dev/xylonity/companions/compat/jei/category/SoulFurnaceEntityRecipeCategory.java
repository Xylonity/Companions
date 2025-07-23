package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.entity.summon.LivingCandleEntity;
import dev.xylonity.companions.common.recipe.SoulFurnaceEntityRecipe;
import dev.xylonity.companions.registry.CompanionsBlocks;
import dev.xylonity.companions.registry.CompanionsEntities;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class SoulFurnaceEntityRecipeCategory implements IRecipeCategory<SoulFurnaceEntityRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "soul_furnace_entity_interaction");
    public static final RecipeType<SoulFurnaceEntityRecipe> TYPE = new RecipeType<>(UID, SoulFurnaceEntityRecipe.class);

    public static final ResourceLocation SHADOW = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/sprites.png");

    private final IDrawable icon;

    private SoulFurnaceBlockEntity cachedBlockEntity;
    private LivingCandleEntity cachedEntity;

    private long lastUpdateTime = 0;

    public SoulFurnaceEntityRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsBlocks.SOUL_FURNACE.get()));
    }

    @Override
    public @NotNull RecipeType<SoulFurnaceEntityRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.soul_furnace_entity_interaction.title");
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public int getWidth() {
        return 160;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SoulFurnaceEntityRecipe rec, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 5).addItemStack(rec.input());
    }

    private SoulFurnaceBlockEntity getOrCreateBlockEntity() {
        if (cachedBlockEntity == null) {
            cachedBlockEntity = new SoulFurnaceBlockEntity(BlockPos.ZERO, CompanionsBlocks.SOUL_FURNACE.get().defaultBlockState());
        }

        return cachedBlockEntity;
    }

    private LivingCandleEntity getOrCreateEntity() {
        if (cachedEntity == null) {
            cachedEntity = new LivingCandleEntity(CompanionsEntities.LIVING_CANDLE.get(), Minecraft.getInstance().level);
            cachedEntity.setNoAi(true);
        }

        return cachedEntity;
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
        }

        if (currentTime - lastUpdateTime >= 50) {
            lastUpdateTime = currentTime;
        }

        if (cachedEntity != null) cachedEntity.tickCount = (int)(System.currentTimeMillis() / 50);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull SoulFurnaceEntityRecipe recipe, @NotNull IRecipeSlotsView slots, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // shadow
        guiGraphics.blit(SHADOW, 11, 60, 0, 41, 38, 18);
        // arrow down
        guiGraphics.blit(SHADOW, 32, 10, 56, 30, 24, 22);
        // arrow right
        guiGraphics.blit(SHADOW, 80, 48, 142, 6, 24, 12);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);
        // entity shadow
        guiGraphics.blit(SHADOW, 112, 60, 216, 5, 38, 16);

        updateAnimation();

        SoulFurnaceBlockEntity be = getOrCreateBlockEntity();
        LivingCandleEntity maw = getOrCreateEntity();

        GeoBlockRenderer<SoulFurnaceBlockEntity> renderer = (GeoBlockRenderer<SoulFurnaceBlockEntity>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);
        GeoEntityRenderer<LivingCandleEntity> mawRenderer = (GeoEntityRenderer<LivingCandleEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(maw);
        if (renderer == null) return;

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // furnace
        pose.pushPose();
        pose.translate(30, 68, 10);
        pose.scale(24f, 24f, 24f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(145f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat = pose.last().normal();

        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f front = new Vector3f(0, 0, -1);

        normalMat.transform(up).normalize();
        normalMat.transform(front).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up, front);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);

            renderer.render(be, partialTicks, pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        } catch (Exception e) {
            renderer.render(be, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }

        pose.popPose();

        // shade maw
        pose.pushPose();
        pose.translate(132, 65, 20);
        pose.scale(28, 28, 28);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(45f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat2 = pose.last().normal();

        Vector3f up2 = new Vector3f(1, 0, 0);
        Vector3f front2 = new Vector3f(0, 1, 0);

        normalMat2.transform(up2).normalize();
        normalMat2.transform(front2).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up2, front2);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);

            mawRenderer.render(maw, 0f, partialTicks, pose, buffer, LightTexture.pack(15, 15));
        } catch (Exception e) {
            mawRenderer.render(maw, 0f, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));
        }

        pose.popPose();

        buffer.endBatch();
    }

}