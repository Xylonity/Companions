package dev.xylonity.companions.mixin;

import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void companions$preventPhantomTarget(LivingEntity target, CallbackInfo ci) {
        if (target != null && target.hasEffect(CompanionsEffects.PHANTOM.get())) {
            ci.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void companions$clearPhantomTarget(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        LivingEntity target = mob.getTarget();
        if (target != null && target.hasEffect(CompanionsEffects.PHANTOM.get())) {
            mob.setTarget(null);
        }
    }

}
