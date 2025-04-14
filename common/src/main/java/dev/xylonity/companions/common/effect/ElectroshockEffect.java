package dev.xylonity.companions.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.jetbrains.annotations.NotNull;

public class ElectroshockEffect extends MobEffect {

    public ElectroshockEffect() {
        super(MobEffectCategory.HARMFUL, 0x303030);
    }

    public void addAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.addAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    public void removeAttributeModifiers(@NotNull LivingEntity pLivingEntity, @NotNull AttributeMap pAttributeMap, int pAmplifier) {
        super.removeAttributeModifiers(pLivingEntity, pAttributeMap, pAmplifier);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity livingEntity, int amp) {
        livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.015, 1, 0.015));
    }

    @Override
    public boolean isDurationEffectTick(int $$0, int $$1) {
        return true;
    }

}
