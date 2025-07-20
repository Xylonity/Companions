package dev.xylonity.companions.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class ElectroshockEffect extends MobEffect {

    public ElectroshockEffect() {
        super(MobEffectCategory.HARMFUL, 0x88D1FF);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity livingEntity, int amp) {
        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.015, 1, 0.015));
        return true;
    }

}
