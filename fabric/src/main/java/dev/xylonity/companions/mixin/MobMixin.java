package dev.xylonity.companions.mixin;

import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void companions$setTarget(LivingEntity pTarget, CallbackInfo ci) {
        if (pTarget == null) return;
        if (pTarget.hasEffect(CompanionsEffects.PHANTOM.get())) {
            ci.cancel();
        }

    }

}
