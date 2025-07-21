package dev.xylonity.companions.compat.jei.category;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.ShadeMawAltarBlockEntity;
import dev.xylonity.companions.common.entity.projectile.ShadeAltarUpgradeHaloProjectile;
import dev.xylonity.companions.common.recipe.ShadeAltarRecipe;
import dev.xylonity.companions.registry.CompanionsBlocks;
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

public class ShadeAltarRecipeCategory implements IRecipeCategory<ShadeAltarRecipe> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "shade_altar_interaction");
    public static final RecipeType<ShadeAltarRecipe> TYPE = new RecipeType<>(UID, ShadeAltarRecipe.class);
    public static final ResourceLocation SHADOW = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/sprites.png");

    private ShadeMawAltarBlockEntity cachedBlockEntity;
    private ShadeAltarUpgradeHaloProjectile cachedEntity;

    private long lastUpdateTime = 0;
    private final IDrawable icon;

    private long lastEntityAppearTime = 0;
    private int spawnCooldown;

    public ShadeAltarRecipeCategory(IGuiHelper gui) {
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CompanionsItems.CRYSTALLIZED_BLOOD.get()));
        this.spawnCooldown = 0;
    }

    @Override
    public @NotNull RecipeType<ShadeAltarRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.companions.shade_altar_interaction.title");
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
    public void setRecipe(IRecipeLayoutBuilder builder, ShadeAltarRecipe rec, @NotNull IFocusGroup focuses) {
        this.cachedEntity = null;
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 5).addItemStack(rec.input());
    }

    private ShadeMawAltarBlockEntity getOrCreateBlockEntity() {
        if (cachedBlockEntity == null) {
            cachedBlockEntity = new ShadeMawAltarBlockEntity(BlockPos.ZERO, CompanionsBlocks.SHADE_MAW_ALTAR.get().defaultBlockState());
            cachedBlockEntity.addCharge();
        }

        return cachedBlockEntity;
    }

    private ShadeAltarUpgradeHaloProjectile getOrCreateEntity() {
        if (cachedEntity == null && spawnCooldown == 0) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                cachedEntity = new ShadeAltarUpgradeHaloProjectile(CompanionsEntities.SHADE_ALTAR_UPGRADE_HALO, mc.level);
                cachedEntity.setNoGravity(true);
                cachedEntity.tickCount = 0;
            }
        }

        return cachedEntity;
    }

    private void updateAnimation() {
        long currentTime = System.currentTimeMillis();

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTime;
            lastEntityAppearTime = currentTime;
        }

        if (currentTime - lastEntityAppearTime >= 1600) {
            lastEntityAppearTime = currentTime;
            cachedEntity = null;
            spawnCooldown = 0;
        }

        ShadeAltarUpgradeHaloProjectile entity = getOrCreateEntity();
        if (entity != null) {
            long delta = currentTime - lastEntityAppearTime;
            int ticks = (int)(delta / 50);
            entity.tickCount = ticks;

            if (ticks >= 16) {
                cachedEntity = null;
                spawnCooldown = 400;
            }
        }

        lastUpdateTime = currentTime;
        if (spawnCooldown > 0) spawnCooldown--;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void draw(@NotNull ShadeAltarRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        RenderSystem.setShaderTexture(0, SHADOW);
        guiGraphics.blit(SHADOW, 21, 55, 0, 0, 39, 17);
        // arrow down
        guiGraphics.blit(SHADOW, 33, 10, 46, 3, 33, 22);
        // item bg input
        guiGraphics.blit(SHADOW, 9, 4, 120, 0, 19, 19);

        updateAnimation();

        ShadeMawAltarBlockEntity blockEntity = getOrCreateBlockEntity();
        ShadeAltarUpgradeHaloProjectile entity = getOrCreateEntity();

        GeoBlockRenderer<ShadeMawAltarBlockEntity> blockRenderer = (GeoBlockRenderer<ShadeMawAltarBlockEntity>) Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(blockEntity);
        GeoEntityRenderer<ShadeAltarUpgradeHaloProjectile> entityRenderer = entity != null ? (GeoEntityRenderer<ShadeAltarUpgradeHaloProjectile>) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity) : null;

        if (blockRenderer == null) return;

        PoseStack pose = guiGraphics.pose();
        MultiBufferSource.BufferSource buffer = guiGraphics.bufferSource();

        // chalice
        pose.pushPose();
        pose.translate(60, 55, 20);
        pose.scale(24f, 24f, 24f);
        pose.mulPose(Axis.XP.rotationDegrees(-25f));
        pose.mulPose(Axis.YP.rotationDegrees(45f));
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
            blockRenderer.render(blockEntity, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY);
        }

        pose.popPose();

        // entity
        if (entity != null) {
            pose.pushPose();

            pose.translate(60, 63, 27.5);
            pose.scale(24f, 24f, 24f);
            pose.mulPose(Axis.XP.rotationDegrees(-25f));
            pose.mulPose(Axis.YP.rotationDegrees(45f));
            pose.mulPose(Axis.ZP.rotationDegrees(180f));

            entityRenderer.render(entity, 0, Minecraft.getInstance().getTimer().getGameTimeDeltaTicks(), pose, buffer, LightTexture.pack(15, 15));

            pose.popPose();
        }

        buffer.endBatch();
    }

}