package dev.xylonity.companions.mixin;

import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityPhantomEffectMixin {

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void companions$phantomEffectInvisibility(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof LivingEntity livingEntity
                && livingEntity.hasEffect(CompanionsEffects.PHANTOM.get())) {
            cir.setReturnValue(true);
        }
    }

}
