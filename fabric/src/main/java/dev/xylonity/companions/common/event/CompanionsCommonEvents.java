package dev.xylonity.companions.common.event;

import dev.xylonity.companions.config.CompanionsConfig;
import dev.xylonity.companions.registry.CompanionsEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class CompanionsCommonEvents {
    public static void init() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((LivingEntity entity,
                                                        DamageSource source,
                                                        float amount) -> {
            if (entity.hasEffect(CompanionsEffects.FIRE_MARK.get())
                    && (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE))) {
                entity.level().explode(
                        entity,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        (float) CompanionsConfig.FIRE_MARK_EFFECT_RADIUS,
                        Level.ExplosionInteraction.MOB
                );
                entity.removeEffect(CompanionsEffects.FIRE_MARK.get());
            }
            return true;
        });
    }
}