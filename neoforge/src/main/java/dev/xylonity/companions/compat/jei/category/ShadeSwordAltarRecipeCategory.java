package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.ShadeSwordAltarBlockEntity;
import dev.xylonity.companions.common.entity.companion.ShadeSwordEntity;
import dev.xylonity.companions.common.recipe.ShadeSwordAltarRecipe;
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

public final class ShadeSwordAltarRecipeCategory implements IRecipeCategory<ShadeSwordAltarRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "shade_sword_altar_interaction");
    public static final RecipeType<ShadeSwordAltarRecipe> TYPE = new RecipeType<>(UID, ShadeSwordAltarRecipe.class);

    public static final ResourceLocation SHADOW = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/sprites.png");

    private final IDrawable icon;

    private ShadeSwordAltarBlockEntity cachedBlockEntity;
    private ShadeSwordEntity cachedEntity;

    private long lastUpdateTime = 0;

    private double animationTicks = 0;
    private long lastSystemTimeMs = 0;

    public ShadeSwordAltarRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsBlocks.SHADE_SWORD_ALTAR.get()));
    }

    @Override
    public @NotNull RecipeType<ShadeSwordAltarRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.shade_sword_altar_interaction.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ShadeSwordAltarRecipe rec, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 5).addItemStack(rec.input());
    }

    private ShadeSwordAltarBlockEntity getOrCreateBlockEntity() {
        if (cachedBlockEntity == null) {
            cachedBlockEntity = new ShadeSwordAltarBlockEntity(BlockPos.ZERO, CompanionsBlocks.SHADE_SWORD_ALTAR.get().defaultBlockState());
            cachedBlockEntity.addCharge();
        }

        return cachedBlockEntity;
    }

    private ShadeSwordEntity getOrCreateEntity() {
        if (cachedEntity == null) {
            cachedEntity = new ShadeSwordEntity(CompanionsEntities.SHADE_SWORD.get(), Minecraft.getInstance().level);
            cachedEntity.setIsSpawning(false);
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

    private void updateAnimationTicks() {
        long now = System.currentTimeMillis();
        if (lastSystemTimeMs == 0) {
            lastSystemTimeMs = now;
            return;
        }

        long ms = now - lastSystemTimeMs;
        lastSystemTimeMs = now;
        animationTicks += ms / 50.0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull ShadeSwordAltarRecipe recipe, @NotNull IRecipeSlotsView slots, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // altar shadow
        guiGraphics.blit(SHADOW, 20, 58, 5, 28, 35, 9);
        // arrow down
        guiGraphics.blit(SHADOW, 32, 10, 46, 3, 33, 22);
        // arrow right
        guiGraphics.blit(SHADOW, 80, 45, 142, 6, 24, 12);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);
        // sword shadow
        guiGraphics.blit(SHADOW, 115, 55, 216, 5, 38, 16);

        updateAnimation();
        updateAnimationTicks();

        ShadeSwordAltarBlockEntity be = getOrCreateBlockEntity();
        ShadeSwordEntity sword = getOrCreateEntity();

        GeoBlockRenderer<ShadeSwordAltarBlockEntity> renderer = (GeoBlockRenderer<ShadeSwordAltarBlockEntity>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);
        GeoEntityRenderer<ShadeSwordEntity> swordRenderer = (GeoEntityRenderer<ShadeSwordEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(sword);
        if (renderer == null) return;

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // altar
        pose.pushPose();
        pose.translate(42, 65, 20);
        pose.scale(20f, 20f, 20f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(135f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat = pose.last().normal();

        Vector3f up = new Vector3f(1, 0, 0);
        Vector3f front = new Vector3f(0, 1, 0);

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

        // shade sword
        pose.pushPose();
        pose.translate(135, 62, 20);
        pose.scale(16, 16, 16);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(45f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat2 = pose.last().normal();

        Vector3f up2 = new Vector3f(-1, 100, -1);
        Vector3f front2 = new Vector3f(-1, 300, -1);

        normalMat2.transform(up2).normalize();
        normalMat2.transform(front2).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up2, front2);

        try {
            swordRenderer.render(sword, 0f, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));
        } catch (Exception e) {
            swordRenderer.render(sword, 0f, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));
        }

        pose.popPose();

        buffer.endBatch();
    }

}