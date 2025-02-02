package dev.xylonity.companions.common.mixin;

import dev.xylonity.companions.CompanionsCommon;
import dev.xylonity.companions.common.accessor.TestEntityAccessor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Gui.class)
public abstract class TestHeartsLoop {

    @Unique
    private static final ResourceLocation TEDDY_HEART = new ResourceLocation(CompanionsCommon.MOD_ID, "textures/gui/teddy_heart.png");
//
    @Shadow
    protected abstract void renderHeart(GuiGraphics pGuiGraphics, Gui.HeartType pHeartType, int pX, int pY, int pYOffset, boolean pRenderHighlight, boolean pHalfHeart);

    @Inject(
            method = "renderHearts",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Gui;renderHeart(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Gui$HeartType;IIIZZ)V",
                    ordinal = 1
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    private void injectDarkAbsorption(
            GuiGraphics pGuiGraphics,
            Player pPlayer,
            int pX,
            int pY,
            int pHeight,
            int pOffsetHeartIndex,
            float pMaxHealth,
            int pCurrentHealth,
            int pDisplayHealth,
            int pAbsorptionAmount,
            boolean pRenderHighlight,
            CallbackInfo ci,
            Gui.HeartType guiHeartType,
            int i,
            int j,
            int k,
            int l,
            int i1,
            int j1,
            int k1,
            int l1,
            int i2,
            int j2,
            boolean flag,
            int k2,
            boolean flag1
    ) {
        TestEntityAccessor accessor = (TestEntityAccessor) pPlayer;
        float darkAbs = accessor.getDarkAbsorptionAmount();
        int darkHalfHearts = Mth.ceil(darkAbs);

        int firstDarkIndex = pAbsorptionAmount - darkHalfHearts;

        Gui.HeartType heartTypeToRender;
        if (k2 >= firstDarkIndex && k2 < pAbsorptionAmount) {
            heartTypeToRender = Gui.HeartType.FROZEN;
        } else {
            heartTypeToRender = (guiHeartType == Gui.HeartType.WITHERED) ? guiHeartType : Gui.HeartType.ABSORBING;
        }

        this.renderHeart(pGuiGraphics, heartTypeToRender, l1, i2, i, false, flag1);

        //ci.cancel();
    }

    //@Redirect(
    //        method = "renderHearts",
    //        at = @At(
    //                value = "INVOKE",
    //                target = "Lnet/minecraft/util/Mth;ceil(D)I",
    //                ordinal = 1
    //        )
    //)
    //private int redirectAbsorptionCeil(double originalValue, GuiGraphics guiGraphics, Player player, int x, int y, int spacing, int heartBlink, float health, int healthInt, int displayHealthInt, int absorption, boolean blinking) {
    //    int vanillaAbsorptionHearts = Mth.ceil(originalValue);
//
    //    int darkHearts = 0;
    //    darkHearts = Mth.ceil(((TestEntityAccessor) player).getDarkAbsorptionAmount() / 2.0f);
//
    //    return vanillaAbsorptionHearts + darkHearts;
    //}

}
