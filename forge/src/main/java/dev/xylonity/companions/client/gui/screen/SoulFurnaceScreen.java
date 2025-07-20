package dev.xylonity.companions.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.xylonity.companions.Companions;
import dev.xylonity.companions.common.blockentity.SoulFurnaceBlockEntity;
import dev.xylonity.companions.common.container.SoulFurnaceContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class SoulFurnaceScreen extends AbstractContainerScreen<SoulFurnaceContainerMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Companions.MOD_ID, "textures/gui/soul_furnace_gui.png");

    private static final int FRAME_WIDTH = 10;
    private static final int FRAME_HEIGHT = 9;
    private static final int FRAME_COUNT = 6;
    private static final int FRAME_BASE_X = 176;
    private static final int FRAME_BASE_Y = 31;
    private static final int FRAME_Y_OFFSET = 9;

    private final int[] animationTicks;
    //private final int[] animationSpeeds;
    private final int animationSpeeds = 3;
    private final int[] animationOffsets;

    public SoulFurnaceScreen(SoulFurnaceContainerMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 179;

        int maxCharges = SoulFurnaceBlockEntity.getMaxCharges();
        animationTicks = new int[maxCharges];
        animationOffsets = new int[maxCharges];

        for (int i = 0; i < maxCharges; i++) {
            animationOffsets[i] = new Random().nextInt(FRAME_COUNT);
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        for (int i = 0; i < animationTicks.length; i++) {
            animationTicks[i]++;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

        if (menu.getProcessingTime() > 0) {
            int progressWidth = 24;
            int progressHeight = 17;
            int progress = (int) ((float) menu.getProgress() / menu.getProcessingTime() * progressWidth);
            guiGraphics.blit(TEXTURE, relX + 79, relY + 40, 176, 14, progress, progressHeight);
        }

        int chargeCount = menu.getCharge();
        for (int i = 0; i < chargeCount; i++) {
            int currentFrame = ((animationTicks[i] / animationSpeeds) + animationOffsets[i]) % FRAME_COUNT;
            int frameY = FRAME_BASE_Y + (currentFrame * FRAME_Y_OFFSET);

            int drawX;
            int drawY;

            if (i == 0) {
                drawX = relX + 9;
                drawY = relY + 61;
            } else if (i == SoulFurnaceBlockEntity.getMaxCharges() - 1) {
                drawX = relX + 157;
                drawY = relY + 61;
            } else {
                drawX = relX + 26 + (i - 1) * 19;
                drawY = relY + 69;
            }

            guiGraphics.blit(TEXTURE, drawX, drawY, FRAME_BASE_X, frameY, FRAME_WIDTH, FRAME_HEIGHT);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY + 2, 4210752, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY + 14, 4210752, false);
    }
}