package dev.xylonity.companions.mixin;

import dev.xylonity.companions.common.util.interfaces.IPhantomEffectEntity;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("all")
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements IPhantomEffectEntity {
    @Unique
    private static final EntityDataAccessor<Boolean> companions$PHANTOM_FLAG;

    @Unique
    private boolean companions$lastPhantomState = false;

    @Inject(method = "tick", at = @At("TAIL"))
    private void syncPhantomFlag(CallbackInfo ci) {
        boolean curr = ((LivingEntity)(Object) this).hasEffect(CompanionsEffects.PHANTOM.get());

        if (curr != companions$lastPhantomState) {
            ((LivingEntity) (Object) this).getEntityData().set(companions$PHANTOM_FLAG, curr);
            companions$lastPhantomState = curr;
        }
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void companions$definePhantomFlag(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(companions$PHANTOM_FLAG, false);
    }

    @Override
    public boolean isPhantomEffectActive() {
        return ((LivingEntity) (Object) this).getEntityData().get(companions$PHANTOM_FLAG);
    }

    @Override
    public void setPhantomEffectActive(boolean active) {
        ((LivingEntity) (Object) this).getEntityData().set(companions$PHANTOM_FLAG, active);
        companions$lastPhantomState = active;
    }

    static {
        companions$PHANTOM_FLAG = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);
    }

}