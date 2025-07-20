package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.RespawnTotemBlockEntity;
import dev.xylonity.companions.common.entity.projectile.RespawnTotemRingProjectile;
import dev.xylonity.companions.common.recipe.HourglassRecipe;
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

public class RespawnTotemRecipeCategory implements IRecipeCategory<HourglassRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(Companions.MOD_ID, "respawn_totem_interaction");
    public static final RecipeType<HourglassRecipe> TYPE = new RecipeType<>(UID, HourglassRecipe.class);
    public static final ResourceLocation SHADOW = new ResourceLocation(Companions.MOD_ID, "textures/gui/sprites.png");

    private RespawnTotemBlockEntity cachedBlockEntity;
    private RespawnTotemRingProjectile cachedEntity;

    private long lastUpdateTime = 0;
    private final IDrawable icon;

    private long lastEntityAppearTime = 0;

    public RespawnTotemRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsBlocks.RESPAWN_TOTEM.get()));
    }

    @Override
    public @NotNull RecipeType<HourglassRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.respawn_totem_interaction.title");
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 100;
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

    private RespawnTotemBlockEntity getOrCreateBlockEntity() {
        if (cachedBlockEntity == null) {
            cachedBlockEntity = new RespawnTotemBlockEntity(BlockPos.ZERO, CompanionsBlocks.RESPAWN_TOTEM.get().defaultBlockState());
            cachedBlockEntity.setCharges(1);
        }

        return cachedBlockEntity;
    }

    private RespawnTotemRingProjectile getOrCreateEntity() {
        if (cachedEntity == null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                cachedEntity = new RespawnTotemRingProjectile(CompanionsEntities.RESPAWN_TOTEM_RING_PROJECTILE.get(), mc.level);
                cachedEntity.setNoGravity(true);
            }
        }

        return cachedEntity;
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
        }

        if (currentTime - lastEntityAppearTime >= 3900) {
            lastEntityAppearTime = currentTime;

            RespawnTotemRingProjectile entity = getOrCreateEntity();
            if (entity != null) {
                entity.tickCount = 0;
            }
        }

        lastUpdateTime = currentTime;
        if (cachedEntity != null) cachedEntity.tickCount = (int)(System.currentTimeMillis() / 50);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull HourglassRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        // shadow
        guiGraphics.blit(SHADOW, 21, 69, 170, 0, 42, 25);
        // arrow down
        guiGraphics.blit(SHADOW, 33, 10, 46, 3, 33, 22);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);

        updateAnimation();

        RespawnTotemBlockEntity blockEntity = getOrCreateBlockEntity();
        RespawnTotemRingProjectile entity = getOrCreateEntity();

        GeoBlockRenderer<RespawnTotemBlockEntity> blockRenderer = (GeoBlockRenderer<RespawnTotemBlockEntity>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        GeoEntityRenderer<RespawnTotemRingProjectile> entityRenderer = entity != null ? (GeoEntityRenderer<RespawnTotemRingProjectile>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) : null;

        if (blockRenderer == null) return;

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // chalice
        pose.pushPose();
        pose.translate(44, 80, 120);
        pose.scale(24f, 24f, 24f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(145f));
        pose.mulPose(Axis.ZP.rotationDegrees(180f));

        Matrix3f normalMat = pose.last().normal();
        Vector3f up = new Vector3f(-1, 10, -1);
        Vector3f front = new Vector3f(-1, 3, -1);
        normalMat.transform(up).normalize();
        normalMat.transform(front).normalize();

        RenderSystem.setupGui3DDiffuseLighting(up, front);

        try {
            float partialTicks = (float)((System.currentTimeMillis() - lastUpdateTime) / 50.0);
            blockRenderer.render(blockEntity, partialTicks, pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        } catch (Exception e) {
            blockRenderer.render(blockEntity, Minecraft.getInstance().getFrameTime(), pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }

        pose.popPose();

        // entity
        if (entity != null) {
            pose.pushPose();

            pose.translate(60, 80, 100);
            pose.scale(5f, 5f, 5f);
            pose.mulPose(Axis.XP.rotationDegrees(-25f));
            pose.mulPose(Axis.YP.rotationDegrees(45f));
            pose.mulPose(Axis.ZP.rotationDegrees(180f));

            entityRenderer.render(entity, 0, Minecraft.getInstance().getFrameTime(), pose, buffer, LightTexture.pack(15, 15));

            pose.popPose();
        }

        buffer.endBatch();
    }

}