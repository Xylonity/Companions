package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.FrogBonanzaBlockEntity;
import dev.xylonity.companions.common.recipe.FrogBonanzaRecipe;
import dev.xylonity.companions.registry.CompanionsBlocks;
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
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public final class FrogBonanzaRecipeCategory implements IRecipeCategory<FrogBonanzaRecipe> {

    public static final ResourceLocation UID = Companions.of("frog_bonanza_interaction");
    public static final RecipeType<FrogBonanzaRecipe> TYPE = new RecipeType<>(UID, FrogBonanzaRecipe.class);

    public static final ResourceLocation SHADOW = Companions.of("textures/gui/sprites.png");

    private final IDrawable icon;

    private FrogBonanzaBlockEntity cachedBlockEntity;
    private long lastUpdateTime = 0;
    private long nextAnimAt = 0;

    public FrogBonanzaRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsBlocks.FROG_BONANZA.get()));
    }

    @Override
    public @NotNull RecipeType<FrogBonanzaRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.frog_bonanza_interaction.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, FrogBonanzaRecipe rec, @NotNull IFocusGroup focuses) {
        this.cachedBlockEntity = null;
        if (!rec.coinInputs.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 31).addItemStacks(rec.coinInputs);
        }
    }

    private FrogBonanzaBlockEntity getOrCreateBlockEntity() {
        if (cachedBlockEntity == null) {
            cachedBlockEntity = new FrogBonanzaBlockEntity(BlockPos.ZERO, CompanionsBlocks.FROG_BONANZA.get().defaultBlockState());
        }

        return cachedBlockEntity;
    }

    private void triggerAnim(FrogBonanzaBlockEntity be) {
        final long now = System.currentTimeMillis();
        if (nextAnimAt == 0) {
            nextAnimAt = now + 2500;
            return;
        }
        if (now < nextAnimAt) {
            return;
        }

        nextAnimAt = now + 2500;

        final AnimatableManager<?> manager = be.getAnimatableInstanceCache().getManagerForId(0L);
        if (manager != null) {
            manager.tryTriggerAnimation("lever_controller", "lever");
        }

    }

    private void updateAnimation() {
        final long currentTime = System.currentTimeMillis();
        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
        }

        if (currentTime - lastUpdateTime >= 50) {
            lastUpdateTime = currentTime;
        }

    }

    @Override
    public void draw(@NotNull FrogBonanzaRecipe recipe, @NotNull IRecipeSlotsView slots, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // arrow 1
        guiGraphics.blit(SHADOW, 28, 34, 81, 6, 35, 12);
        // arrow 2
        guiGraphics.blit(SHADOW, 100, 34, 81, 6, 35, 12);
        // question mark
        guiGraphics.blit(SHADOW, 140, 25, 188, 32, 17, 25);
        // input bg
        guiGraphics.blit(SHADOW, 4, 30, 120, 0, 19, 19);

        updateAnimation();

        FrogBonanzaBlockEntity be = getOrCreateBlockEntity();
        triggerAnim(be);

        @SuppressWarnings("unchecked")
        GeoBlockRenderer<FrogBonanzaBlockEntity> renderer = (GeoBlockRenderer<FrogBonanzaBlockEntity>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);

        if (renderer == null) {
            return;
        }

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        pose.pushPose();
        pose.translate(70, 55, 10);
        pose.scale(18f, 18f, 18f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(-215));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat = pose.last().normal();

        Vector3f up = new Vector3f(-1, 10, -1);
        Vector3f front = new Vector3f(-1, 3, -1);

        normalMat.transform(up).normalize();
        normalMat.transform(front).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up, front);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);

            renderer.render(be, partialTicks, pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }
        catch (Exception e) {
            renderer.render(be, Minecraft.getInstance().getFrameTime(), pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }

        pose.popPose();
        buffer.endBatch();
    }

}
