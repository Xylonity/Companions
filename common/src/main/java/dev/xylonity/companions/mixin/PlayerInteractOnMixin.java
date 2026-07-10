package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.item.HolyPorcelainPottery;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerInteractOnMixin {

    @Inject(method = "interactOn", at = @At("HEAD"), cancellable = true)
    private void companions$holyPotteryInteractionPriority(Entity target, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        final Player self = (Player) (Object) this;
        if (self.isSpectator()) {
            return;
        }
        if (!(target instanceof LivingEntity living)) {
            return;
        }

        final ItemStack stack = self.getItemInHand(hand);
        if (!(stack.getItem() instanceof HolyPorcelainPottery)) {
            return;
        }

        final InteractionResult result = stack.interactLivingEntity(self, living, hand);
        if (result.consumesAction()) {
            cir.setReturnValue(result);
        }

    }

}