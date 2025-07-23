package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.entity.companion.CroissantDragonEntity;
import dev.xylonity.companions.common.recipe.CroissantDragonArmorRecipe;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public final class CroissantDragonArmorRecipeCategory implements IRecipeCategory<CroissantDragonArmorRecipe> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "croissant_dragon_armor_interaction");
    public static final RecipeType<CroissantDragonArmorRecipe> TYPE = new RecipeType<>(UID, CroissantDragonArmorRecipe.class);

    public static final ResourceLocation SHADOW = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/sprites.png");

    private final IDrawable icon;

    private CroissantDragonEntity cachedEntity;
    private CroissantDragonEntity cachedEntity2;

    private long lastUpdateTime = 0;

    private CroissantDragonArmorRecipe currentRecipe;

    public CroissantDragonArmorRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsBlocks.CROISSANT_EGG.get()));
    }

    @Override
    public @NotNull RecipeType<CroissantDragonArmorRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.croissant_dragon_armor_interaction.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CroissantDragonArmorRecipe rec, @NotNull IFocusGroup focuses) {
        this.currentRecipe = rec;
        this.cachedEntity = null;
        this.cachedEntity2 = null;
        builder.addSlot(RecipeIngredientRole.INPUT, 5, 5).addItemStack(rec.input());
    }

    private CroissantDragonEntity getOrCreateEntity() {
        if (cachedEntity == null) {
            cachedEntity = new CroissantDragonEntity(CompanionsEntities.CROISSANT_DRAGON.get(), Minecraft.getInstance().level);
            cachedEntity.setMilkAmount(3);
            cachedEntity.setNoAi(true);
        }

        return cachedEntity;
    }

    private CroissantDragonEntity getOrCreateEntity2() {
        if (cachedEntity2 == null) {
            cachedEntity2 = new CroissantDragonEntity(CompanionsEntities.CROISSANT_DRAGON.get(), Minecraft.getInstance().level);
            cachedEntity2.setMilkAmount(3);

            String armorName;
            if (currentRecipe.input().getHoverName().getString().toLowerCase().contains("vanilla")) {
                armorName = "vanilla";
            } else if (currentRecipe.input().getHoverName().getString().toLowerCase().contains("chocolate")) {
                armorName = "chocolate";
            } else {
                armorName = "strawberry";
            }

            cachedEntity2.setArmorName(armorName);
            cachedEntity2.setNoAi(true);
        }

        return cachedEntity2;
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
        if (cachedEntity2 != null) cachedEntity2.tickCount = (int)(System.currentTimeMillis() / 50);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull CroissantDragonArmorRecipe recipe, @NotNull IRecipeSlotsView slots, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // dragon shadow
        guiGraphics.blit(SHADOW, 4, 53, 170, 0, 42, 25);
        // arrow down
        guiGraphics.blit(SHADOW, 26, 10, 62, 56, 19, 29);
        // arrow right
        guiGraphics.blit(SHADOW, 60, 50, 81, 6, 39, 12);
        // item bg input
        guiGraphics.blit(SHADOW, 4, 4, 120, 0, 19, 19);
        // dragon shadow
        guiGraphics.blit(SHADOW, 100, 53, 170, 0, 42, 25);

        updateAnimation();

        CroissantDragonEntity dragon1 = getOrCreateEntity();
        CroissantDragonEntity dragon2 = getOrCreateEntity2();

        GeoEntityRenderer<CroissantDragonEntity> dragonRenderer1 = (GeoEntityRenderer<CroissantDragonEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragon1);
        GeoEntityRenderer<CroissantDragonEntity> dragonRenderer2 = (GeoEntityRenderer<CroissantDragonEntity>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(dragon2);

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // dragon1
        pose.pushPose();
        pose.translate(35, 68, 20);
        pose.scale(17f, 17f, 17f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(38f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat = pose.last().normal();

        Vector3f up = new Vector3f(0, 1, 0);
        Vector3f front = new Vector3f(0, 0, -1);

        normalMat.transform(up).normalize();
        normalMat.transform(front).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up, front);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);

            dragonRenderer1.render(dragon1, 0f, partialTicks, pose, buffer, LightTexture.pack(15, 15));
        } catch (Exception e) {
            dragonRenderer1.render(dragon1, 0f, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));
        }

        pose.popPose();

        // dragon2
        pose.pushPose();
        pose.translate(130, 68, 20);
        pose.scale(18f, 18f, 18f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(38f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat2 = pose.last().normal();

        Vector3f up2 = new Vector3f(1, 0, 0);
        Vector3f front2 = new Vector3f(0, 1, 0);

        normalMat2.transform(up2).normalize();
        normalMat2.transform(front2).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up2, front2);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);

            dragonRenderer2.render(dragon2, 0f, partialTicks, pose, buffer, LightTexture.pack(15, 15));
        } catch (Exception e) {
            dragonRenderer2.render(dragon2, 0f, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));
        }

        pose.popPose();

        buffer.endBatch();
    }

}