package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.AntlionEntity;
import dev.xylonity.companions.common.recipe.HourglassRecipe;
import dev.xylonity.companions.registry.CompanionsEntities;
import dev.xylonity.companions.registry.CompanionsItems;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AntlionRecipeCategory implements IRecipeCategory<HourglassRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(Companions.MOD_ID, "antlion_interaction");
    public static final RecipeType<HourglassRecipe> TYPE = new RecipeType<>(UID, HourglassRecipe.class);
    public static final ResourceLocation SHADOW = new ResourceLocation(Companions.MOD_ID, "textures/gui/sprites.png");

    private AntlionEntity cachedEntity;

    private final IDrawable icon;

    private int phaseCooldown;

    public AntlionRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsItems.HOURGLASS.get()));
    }

    @Override
    public @NotNull RecipeType<HourglassRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.antlion_interaction.title");
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 80;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HourglassRecipe rec, @NotNull IFocusGroup focuses) {
        this.cachedEntity = null;
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 5).addItemStack(rec.input);
    }

    private AntlionEntity getOrCreateEntity() {
        if (cachedEntity == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                cachedEntity = new AntlionEntity(CompanionsEntities.ANTLION, mc.level);
                cachedEntity.setNoGravity(true);
                this.phaseCooldown = 600;
            }
        }

        return cachedEntity;
    }

    private void updateAnimation() {
        if (cachedEntity != null) {
            cachedEntity.tickCount = (int)(System.currentTimeMillis() / 50);
            if (phaseCooldown <= 0) {
                cachedEntity.cycleVariant();
                phaseCooldown = 600;
            }
        }

        if (phaseCooldown > 0) phaseCooldown--;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull HourglassRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // shadow
        guiGraphics.blit(SHADOW, 40, 60, 216, 5, 38, 16);
        // arrow down
        guiGraphics.blit(SHADOW, 33, 10, 46, 3, 33, 22);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);

        updateAnimation();

        AntlionEntity entity = getOrCreateEntity();

        GeoEntityRenderer<AntlionEntity> entityRenderer = entity != null ? (GeoEntityRenderer<AntlionEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) : null;

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // entity
        if (entity != null) {
            pose.pushPose();

            pose.translate(60, 68, 27.5);
            pose.scale(24f, 24f, 24f);
            pose.mulPose(Axis.XP.rotationDegrees(-25f));
            pose.mulPose(Axis.YP.rotationDegrees(45f));
            pose.mulPose(Axis.ZP.rotationDegrees(180f));

            entityRenderer.render(entity, 0, Minecraft.getInstance().getFrameTime(), pose, buffer, LightTexture.pack(15, 15));

            pose.popPose();
        }

        buffer.endBatch();
    }

}