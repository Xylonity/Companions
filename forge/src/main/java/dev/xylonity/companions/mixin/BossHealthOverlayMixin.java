package dev.xylonity.companions.mixin;

import dev.xylonity.companions.Companions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.BossEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.spongepowered.asm.mixin.Unique;

import java.util.Map;
import java.util.UUID;

@Mixin(BossHealthOverlay.class)
public abstract class BossHealthOverlayMixin {

    @Unique
    private static final int COMPANIONS$PONTIFF_Y_PADDING = 24;
    @Unique
    private static final int COMPANIONS$HOLINESS_Y_PADDING = 24;

    @Unique
    private static final ResourceLocation COMPANIONS$PONTIFF_GUI_HEALTH;
    @Unique
    private static final ResourceLocation COMPANIONS$PONTIFF_GUI_BACKGROUND;
    @Unique
    private static final ResourceLocation COMPANIONS$PONTIFF_GUI_OVERLAY;
    @Unique
    private static final ResourceLocation COMPANIONS$HOLINESS_GUI_HEALTH;
    @Unique
    private static final ResourceLocation COMPANIONS$HOLINESS_GUI_BACKGROUND;
    @Unique
    private static final ResourceLocation COMPANIONS$HOLINESS_GUI_OVERLAY;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow @Final public Map<UUID, LerpingBossEvent> events;
    @Unique
    private boolean companions$lastWasPontiff = false;
    @Unique
    private boolean companions$lastWasHoliness = false;
    @Unique
    private boolean companions$skipNextNameDraw = false;

    @Redirect(method = "render", at = @At(value  = "INVOKE", target = "Lnet/minecraft/client/gui/components/BossHealthOverlay;drawBar(Lnet/minecraft/client/gui/GuiGraphics;IILnet/minecraft/world/BossEvent;)V"))
    private void companions$redirectDrawBar(BossHealthOverlay self, GuiGraphics gui, int x, int y, BossEvent event) {

        String name = event.getName().getString();

        if (name.equalsIgnoreCase("Sacred Pontiff Invisible")) {
            companions$skipNextNameDraw = true;
            return;
        }

        companions$lastWasPontiff = name.contains("Sacred Pontiff");
        companions$lastWasHoliness = name.contains("His Holiness");

        if (companions$lastWasPontiff) {
            companions$renderPontiffBossBar(gui, (LerpingBossEvent) event, x, y);
        } else if (companions$lastWasHoliness) {
            companions$renderHolinessBossBar(gui, (LerpingBossEvent) event, x, y);
        } else {
            self.drawBar(gui, x, y, event);
        }
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)I"))
    private int companions$skipPontiffInvisibleName(GuiGraphics instance, Font pFont, Component pText, int pX, int pY, int pColor) {

        if (companions$skipNextNameDraw) {
            companions$skipNextNameDraw = false;
            return pX;
        }

        instance.drawString(pFont, pText, pX, pY, pColor);
        return pX;
    }

    @ModifyVariable(method = "render", at = @At(value = "STORE", ordinal = 1), index = 3)
    private int companions$addExtraSpacing(int currentJ) {
        if (companions$lastWasPontiff) {
            companions$lastWasPontiff = false;
            return currentJ + COMPANIONS$PONTIFF_Y_PADDING;
        } else if (companions$lastWasHoliness) {
            companions$lastWasHoliness = false;
            return currentJ + COMPANIONS$HOLINESS_Y_PADDING;
        }

        return currentJ;
    }

    @Unique
    private void companions$renderPontiffBossBar(GuiGraphics gui, LerpingBossEvent boss, int x, int y) {
        int barWidth = 183;

        gui.blit(COMPANIONS$PONTIFF_GUI_BACKGROUND, x, y + 22, 0, 0, barWidth, 5);

        int healthWidth = (int) (boss.getProgress() * barWidth);
        if (healthWidth > 0) {
            gui.blit(COMPANIONS$PONTIFF_GUI_HEALTH, x, y, 0, 0, healthWidth, 27);
        }

        gui.blit(COMPANIONS$PONTIFF_GUI_OVERLAY, x - 3, y, 0, 0, 189, 30);

        Component title = boss.getName();
        int textX = minecraft.getWindow().getGuiScaledWidth() / 2 - minecraft.font.width(title) / 2;
        gui.drawString(minecraft.font, title, textX, y - 9, 0xFFFFFF);
    }

    @Unique
    private void companions$renderHolinessBossBar(GuiGraphics gui, LerpingBossEvent boss, int x, int y) {
        int barWidth = 199;

        gui.blit(COMPANIONS$HOLINESS_GUI_BACKGROUND, x - 2, y + 15, 0, 0, 185, 5);

        int healthWidth = (int) (boss.getProgress() * 185);
        if (healthWidth > 0) {
            gui.blit(COMPANIONS$HOLINESS_GUI_HEALTH, x - 2, y + 15, 0, 0, healthWidth, 28);
        }

        gui.blit(COMPANIONS$HOLINESS_GUI_OVERLAY, x - 23, y, 0, 0, 227, 42);

        Component title = boss.getName();
        int textX = minecraft.getWindow().getGuiScaledWidth() / 2 - minecraft.font.width(title) / 2;
        gui.drawString(minecraft.font, title, textX, y + 24, 0xFFFFFF);
    }

    static {
        COMPANIONS$PONTIFF_GUI_HEALTH = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_health.png");
        COMPANIONS$PONTIFF_GUI_BACKGROUND = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_background.png");
        COMPANIONS$PONTIFF_GUI_OVERLAY = new ResourceLocation(Companions.MOD_ID, "textures/gui/pontiff_bar_overlay.png");
        COMPANIONS$HOLINESS_GUI_HEALTH = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_health.png");
        COMPANIONS$HOLINESS_GUI_BACKGROUND = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_background.png");
        COMPANIONS$HOLINESS_GUI_OVERLAY = new ResourceLocation(Companions.MOD_ID, "textures/gui/holiness_bar_overlay.png");
    }

}
