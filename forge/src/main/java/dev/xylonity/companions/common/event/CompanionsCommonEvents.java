package dev.xylonity.companions.common.event;

import dev.xylonity.companions.Companions;
import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = Companions.MOD_ID)
public class CompanionsCommonEvents {

    private static final int COMPANIONS_EXTRA = 24;

    private static final ResourceLocation GUI_HEALTH = new ResourceLocation(Companions.MOD_ID, "textures/gui/bar_health.png");
    private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(Companions.MOD_ID, "textures/gui/bar_background.png");
    private static final ResourceLocation GUI_OVERLAY = new ResourceLocation(Companions.MOD_ID, "textures/gui/bar_overlay.png");

    @SubscribeEvent
    public static void onRenderBossBars(CustomizeGuiOverlayEvent.BossEventProgress event) {
        Minecraft minecraft = Minecraft.getInstance();
        BossHealthOverlay bossHealthOverlay = minecraft.gui.getBossOverlay();
        Iterator<LerpingBossEvent> iterator = bossHealthOverlay.events.values().iterator();
        int y = 12;

        while (iterator.hasNext()) {
            LerpingBossEvent bossEvent = iterator.next();
            int x = event.getX();

            if (y >= event.getGuiGraphics().guiHeight() / 3) break;

            if (bossEvent.getName().getString().contains("sacred_pontiff")) {
                renderCustomBossBar(event.getGuiGraphics(), bossEvent, x, y);
                y += 19 + COMPANIONS_EXTRA;
            } else {
                bossHealthOverlay.drawBar(event.getGuiGraphics(), x, y, bossEvent);
                Component name = bossEvent.getName();
                int textWidth = minecraft.font.width(name);
                int textX = minecraft.getWindow().getGuiScaledWidth() / 2 - textWidth / 2;
                int textY = y - 9;
                event.getGuiGraphics().drawString(minecraft.font, name, textX, textY, 16777215);
                y += 19;
            }
        }

        event.setCanceled(true);
    }

    private static void renderCustomBossBar(GuiGraphics guiGraphics, LerpingBossEvent bossEvent, int x, int y) {
        Minecraft minecraft = Minecraft.getInstance();
        int barWidth = 182;
        int barHeight = 5;

        guiGraphics.blit(GUI_BACKGROUND, x, y + 15, 0, 0, barWidth, barHeight);

        int healthWidth = (int) (bossEvent.getProgress() * barWidth);
        guiGraphics.blit(GUI_HEALTH, x, y + 15, 0, 0, healthWidth, barHeight);

        guiGraphics.blit(GUI_OVERLAY, x, y, 0, 0, 184, 24);

        Component name = bossEvent.getName();
        int textWidth = minecraft.font.width(name);
        int textX = minecraft.getWindow().getGuiScaledWidth() / 2 - textWidth / 2;
        int textY = y - 9;
        guiGraphics.drawString(minecraft.font, name, textX, textY, 16777215);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.hasEffect(CompanionsEffects.FIRE_MARK.get()) && (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE))) {
            entity.level().explode(entity, entity.getX(), entity.getY(), entity.getZ(), (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS, Level.ExplosionInteraction.MOB);
            entity.removeEffect(CompanionsEffects.FIRE_MARK.get());
        }
    }

}
