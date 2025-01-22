package dev.xylonity.companions.common.mixin;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.accessor.TestEntityAccessor;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(Gui.class)
public abstract class Test {

    //@Unique
    //private static final ResourceLocation TEDDY_HEART = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/gui/teddy_heart.png");
//
    //@Shadow protected abstract void renderHeart(GuiGraphics pGuiGraphics, Gui.HeartType pHeartType, int pX, int pY, int pYOffset, boolean pRenderHighlight, boolean pHalfHeart);
    //@Shadow
    //private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");
//
    //@Inject(method = "renderHearts", at = @At("TAIL"))
    //private void renderDarkAbsorptionHearts(GuiGraphics $$0, Player $$1, int $$2, int $$3, int $$4, int $$5, float $$6, int $$7, int $$8, int $$9, boolean $$10, CallbackInfo ci) {
    //    float darkAbs = ((TestEntityAccessor) $$1).getDarkAbsorptionAmount();
    //    int darkHearts = Mth.ceil(darkAbs / 2.0F);
//
    //    int healthHearts = Mth.ceil($$6 / 2.0F);
    //    int absorptionHearts = Mth.ceil((double) $$9 / 2.0);
    //    int totalBaseHearts = healthHearts + absorptionHearts;
//
    //    for (int index = totalBaseHearts; index < totalBaseHearts + darkHearts; index++) {
    //        int row = index / 10;
    //        int col = index % 10;
    //        int x = $$2 + col * 8;
    //        int y = $$3 - row * $$4;
//
    //        if ($$7 + $$9 <= 4) {
    //            y += new Random().nextInt(2);
    //        }
//
    //        if (index == $$5) y -= 2;
//
    //        boolean isHalfHeart = (index == totalBaseHearts + darkHearts - 1) && (darkAbs % 2 != 0);
//
    //        int isHardcore = 9 * ($$1.level().getLevelData().isHardcore() ? 5 : 0);
//
    //        if ($$1.hasEffect(MobEffects.WITHER)) {
    //            renderHeart($$0, Gui.HeartType.WITHERED, x, y, isHardcore, false, isHalfHeart);
    //        } else {
    //            $$0.blit(TEDDY_HEART, x, y, isHalfHeart ? 9 : 0, 0, 9, 9);
    //        }
//
    //    }
    //}

}
