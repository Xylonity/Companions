package dev.xylonity.companions.common.mixin;

import dev.xylonity.companions.common.accessor.TestEntityAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class TestEntity implements TestEntityAccessor {

    @Unique
    private static final EntityDataAccessor<Float> DARK_ABSORPTION_AMOUNT =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void defineDarkAbsorptionAmount(CallbackInfo ci) {
        ((Player) (Object) this).getEntityData().define(DARK_ABSORPTION_AMOUNT, 0.0f);
    }

    @Override
    public float getDarkAbsorptionAmount() {
        return ((Player) (Object) this).getEntityData().get(DARK_ABSORPTION_AMOUNT);
    }

    @Override
    public void setDarkAbsorptionAmount(float amount) {
        ((Player) (Object) this).getEntityData().set(DARK_ABSORPTION_AMOUNT, Math.max(0, amount));
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    private void saveDarkAbsorptionAmount(CompoundTag tag, CallbackInfo ci) {
        tag.putFloat("DarkAbsorptionAmount", getDarkAbsorptionAmount());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    private void loadDarkAbsorptionAmount(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("DarkAbsorptionAmount")) {
           setDarkAbsorptionAmount(tag.getFloat("DarkAbsorptionAmount"));
        }
    }

    //@ModifyVariable(
    //        method = "actuallyHurt",
    //        at = @At(
    //                value = "STORE",
    //                ordinal = 0
    //        ),
    //        argsOnly = true)
    //private float modifyDarkAbsorptionBeforeVanillaAbsorption(float value) {
    //    float darkAbsorption = ((TestEntityAccessor) this).getDarkAbsorptionAmount();
//
    //    float newDamage = Math.max(value - darkAbsorption, 0.0F);
    //    float consumedByDark = value - newDamage;
//
    //    ((TestEntityAccessor) this).setDarkAbsorptionAmount(darkAbsorption - consumedByDark);
//
    //    return newDamage;
    //}

}
